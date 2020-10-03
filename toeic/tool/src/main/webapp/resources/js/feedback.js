var fbHot;
var dHot;

var deletedIds = [];
var fbList = [];
var dList = [];
var changed = false;

var dData = [];
var fbData = [];

var gNull = {
    "id": null,
    "minPoint": null,
    "maxPoint": null,
    "cefrLevel": null,
    "levelText": null,
    "levelDescription": null
};
var dNull = {
    "id": null,
    "kind": null,
    "levelPoint": null,
    "strength": null,
    "weakness": null
};
$(function () {
    waitingAlert(wait);

    $('#nav-home-tab').click();

    $('#nav-tab a').on('click', function (event) {
        if (changed) {
            event.preventDefault();
            el = document.createElement('span');
            el.innerHTML = `<p>The changes you have made will not be saved.</p>`;
            swal({
                title: 'There are unsaved changes!',
                content: {
                    element: el,
                },
                icon: "warning",
                buttons: {
                    cancel: {
                        text: "Cancel",
                        value: null,
                        visible: true,
                        className: "btn-warning",
                        closeModal: true,
                    },
                    confirm: {
                        text: "Delete unsaved changes",
                        value: true,
                        visible: true,
                        className: "",
                        closeModal: false
                    }
                }
            }).then(isConfirm => {
                if (isConfirm) {
                    deletedIds = [];
                    changed = false;
                    loadGeneralFeedback();
                    loadDetailFeedback();
                    swal.close();
                }
            });

            return false;
        }
    });

    $('#nav-tab a').on('shown.bs.tab', function (event) {
        fbHot.render();
        dHot.render();
    });

    $('#fbSave').on('click', function (e) {
        saveGeneral();
    });

    $('#dSave').on('click', function (e) {
        saveDetail();
    });

    if (fbHot == null) {
        var containerGeneral = document.getElementById('feedBackHot');

        fbHot = new Handsontable(containerGeneral, {
            data: fbData,
            colHeaders: ["Id", "Min Point", "Max Point", "CEFR Level", "Level Text", "Level Description"],
            stretchH: 'all',
            rowHeaders: true,
            autoWrapRow: true,
            manualColumnResize: true,
            manualRowResize: true,
            minRows: 4,
            minSpareRows: 1,
            currentRowClassName: 'currentRow',
            currentColClassName: 'currentCol',
            contextMenu: ['row_above', 'row_below', 'remove_row', '---------', 'copy', 'cut', 'undo', 'redo', '---------', 'alignment'],
            columns: [{
                data: 'id',
                readOnly: true
            },
            {
                data: 'minPoint',
                validator: /^[1-9][0-9]*$/
            },
            {
                data: 'maxPoint',
                validator: /^[1-9][0-9]*$/
            },
            {
                data: 'cefrLevel',
                validator: /.*/
            },
            {
                data: 'levelText',
                validator: /.*/
            },
            {
                data: 'levelDescription',
                validator: /.*/
            }
            ],
            afterChange: (changes, source) => {
                if (changes && source != "loadData") {
                    if (!changed) {
                        changed = true;
                    }
                }
            },
            beforeRemoveRow: function (index, amount) {
                var lastIndex = index + amount;
                var i;

                for (i = index; i < lastIndex; i++) {
                    var deletedRecord = fbHot.getSourceDataAtRow(i);

                    if (deletedRecord.id) {
                        deletedIds.push(deletedRecord.id)
                    }
                }
            }

        });
    }

    if (dHot == null) {
        var containerDetail = document.getElementById('detailHot');

        dHot = new Handsontable(containerDetail, {
            data: dData,
            colHeaders: ["Id", "Kind", "Level Point", "Strength", "Weakness"],
            stretchH: 'all',
            rowHeaders: true,
            autoWrapRow: true,
            manualColumnResize: true,
            manualRowResize: true,
            minRows: 4,
            minSpareRows: 1,
            currentRowClassName: 'currentRow',
            currentColClassName: 'currentCol',
            contextMenu: ['row_above', 'row_below', 'remove_row', '---------', 'copy', 'cut', 'undo', 'redo', '---------', 'alignment'],
            columns: [{
                data: 'id',
                readOnly: true
            },
            {
                data: 'kind',
                validator: /^[0-1]$/
            },
            {
                data: 'levelPoint',
                validator: /^[1-9][0-9]*$/
            },
            {
                data: 'strength'
            },
            {
                data: 'weakness'
            }
            ],
            afterChange: (changes, source) => {
                if (changes && source != "loadData") {
                    if (!changed) {
                        changed = true;
                    }
                }
            },
            beforeRemoveRow: function (index, amount) {
                var lastIndex = index + amount;
                var i;

                for (i = index; i < lastIndex; i++) {
                    var deletedRecord = dHot.getSourceDataAtRow(i);

                    if (deletedRecord.id) {
                        deletedIds.push(deletedRecord.id)
                    }
                }
            }

        });
    }

    loadGeneralFeedback();
    loadDetailFeedback();
});

function loadGeneralFeedback() {
    // Get data Assessment
    $.ajax({
        type: "POST",
        url: "get-general-feedback-list",
        dataType: 'json',
        contentType: "application/json",
        success: function (data) {
            console.log("loadGeneralFeedback(): ", data);
            fbData = data;
            fbHot.loadData(fbData);
            successAlert();
        },
        error: function (er) {
            console.log("ERROR: ", er);
            errorAlert("unexpected error occurred, please try refresh this page...");
        }
    });
}

function loadDetailFeedback() {
    // Get data Assessment
    $.ajax({
        type: "POST",
        url: "get-detail-feedback-list",
        dataType: 'json',
        contentType: "application/json",
        success: function (data) {
            console.log("loadDetailFeedback(): ", data);
            dData = data;
            dHot.loadData(dData);
        },
        error: function (er) {
            console.log("ERROR: ", er);
            errorAlert("unexpected error occurred, please try refresh this page...");
        }
    });
}

function saveGeneral() {
    areEmptyColumns(fbHot, [1, 2, 3, 4, 5]);
    if (isValidTable(fbHot) && !isEmptyTable(fbHot)) {
        waitingAlert(wait);
        let list = [];
        var dIds = [];
        for (g in fbData)
            if (JSON.stringify(fbData[g]) != JSON.stringify(gNull)) {
                list.push(fbData[g])
            }

        for (index in deletedIds) {
            let i = list.find(item => item.id == deletedIds[index]);
            if (!i) {
                dIds.push(deletedIds[index]);
            }
        }
        let data = {
            "generalList": list,
            "deletedIdList": dIds
        };

        $.ajax({
            type: "POST",
            url: "save-general-feedback",
            data: JSON.stringify(data),
            dataType: 'json',
            contentType: "application/json",
            success: function () {
                changed = false;
                deletedIds = [];
                loadGeneralFeedback();
            },
            error: function (er) {
                console.log("ERROR: ", er);
                toastr.remove();
                errorAlert("unexpected error occurred while saving general feedback, please try again...");
            }
        });
    } else {
        swal("Invalid", "has cells with invalid data", "error");
    }
}

function saveDetail() {
    areEmptyColumns(dHot, [1, 2]);
    if (isValidTable(dHot) && !isEmptyTable(dHot)) {
        waitingAlert(wait);
        let list = [];
        let dIds = [];
        for (d in dData)
            if (JSON.stringify(dData[d]) != JSON.stringify(dNull)) {
                list.push(dData[d])
            }

        for (index in deletedIds) {
            let i = list.find(item => item.id == deletedIds[index]);
            if (!i) {
                dIds.push(deletedIds[index]);
            }
        }

        let data = {
            "detailList": list,
            "deletedIdList": dIds
        };

        $.ajax({
            type: "POST",
            url: "save-detail-feedback",
            data: JSON.stringify(data),
            dataType: 'json',
            contentType: "application/json",
            success: function () {
                changed = false;
                deletedIds = [];
                loadDetailFeedback();
                successAlert();
            },
            error: function (er) {
                console.log("ERROR: ", er);
                toastr.remove();
                errorAlert("unexpected error occurred while saving detail feedback, please try again...");
            }
        });
    } else {
        swal("Invalid", "has cells with invalid data", "error");
    }
}