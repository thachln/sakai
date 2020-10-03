/**
 * 
 */
    var tableGeoArg;     // đối tượng handsontable của Thông số hình học
    var tableKineArg1;   // đối tượng handsontable của Thông số động học 1
//    var tableKineArg2;   // đối tượng handsontable của Thông số động học 2
    var tableMotionRule;  // đối tượng handsontable của Quy luật chuyển động
    var tableResult;      // đối tượng handsontable của Kết quả

    /**
     * Định dạng màu nền cho các ô nhập liệu.
     */
    function formatInputColor(instance, td, row, col, prop, value, cellProperties) {
        Handsontable.renderers.TextRenderer.apply(this, arguments);
        td.style.background = '#ffff00';
    }

    /**
     * Định dạng màu nền cho các ô kết quả.
     */
    function formatResultColor(instance, td, row, col, prop, value, cellProperties) {
        Handsontable.renderers.TextRenderer.apply(this, arguments);
        td.style.background = '#339933';
        td.style.fontWeight = 'bold';
        td.style.color = 'black';
    }

    /**
     * Bảng thông số hình học.
     */
    $(document).ready(function () {
        var data = [
                    ["Bán kính cơ sở:", "Rp", "", "mm"],
                    ["Khoảng lệch tâm:", "e", 0.00, "mm"],
                    ["", "", "", "", ""],
                    ["Góc áp lực cho phép:", "α", 35, "deg"]
                ];

        // Setting table for Thông số hình học
        var container = document.getElementById('geoArg');
        tableGeoArg = new Handsontable(container, {
            data: data,
            rowHeaders: false,
            colHeaders: false,
            columns: [
                {
                    readOnly: true
                },
                {
                    readOnly: true
                },
                {

                },
                {
                    readOnly: true
                }
            ],
            mergeCells: [
                {row: 2, col: 0, rowspan: 1, colspan: 4}
            ],
            // Format color for cell
            cells: function (row, col, prop) {
                var cellProperties = {};

                if ((col === 2) && (row !== 2) && (row > 0)) { // row 3: separator
                    cellProperties.renderer = formatInputColor; // uses function directly
                } else if ((col === 2) && (row === 0)) { // Bán kính cơ sở
                	cellProperties.renderer = formatResultColor;
                }

                return cellProperties;
            }
        });
    });

    /**
     * Bảng thông số động học: Hàm chuyển động, STT Pha.
     */
    $(document).ready(function () {
        var data =
                [
                    ["Hàm chuyển động:", "Gia tốc hình sin"]
//                    ,
//                    ["", ""],
//                    ["STT Pha:", "1"]
                ];



        // Setting table for Thông số động học
        var container = document.getElementById('kine-arg1');
        tableKineArg1 = new Handsontable(container, {
            data: data,
            rowHeaders: false,
            colHeaders: false,
            columns: [
                {
                    readOnly: true
                },
                {

                }
            ],
//            mergeCells: [
//                {row: 1, col: 0, rowspan: 1, colspan: 2}
//            ],

            // Định dạng dropbox
            cells: function (row, col, prop) {
                if ((row === 0) && (col === 1)) { // Hàm chuyển động
                    this.type = 'dropdown';
                }

//                if ((row === 2) && (col === 1)) { // STT Pha 
//                    this.type = 'dropdown';
//                    this.source = [1, 2, 3, 4, 5];
//                }
            }
        });
    });

    /**
     * Bảng thông số động học: Góc kết thúc pha, Độ cao tương ứng.
     */
//    $(document).ready(function () {
//        var data =
//                [
//                    ["Góc kết thúc pha:", "360", "deg"],
//                    ["Độ cao tương ứng:", "0", "mm"]
//                ];
//
//        // Setting table for Thông số động học
//        var container = document.getElementById('kine-arg2');
//        tableKineArg2 = new Handsontable(container, {
//            data: data,
//            rowHeaders: false,
//            colHeaders: false,
//            columns: [
//                {
//                    readOnly: true
//                },
//                {
//                    type: 'numeric'
//                },
//                {
//                    readOnly: true
//                }
//            ],
//
//            // Format color for cell
//            cells: function (row, col, prop) {
//                var cellProperties = {};
//
//                if (col === 1) {
//                    cellProperties.renderer = formatInputColor; // uses function directly
//                }
//
//                return cellProperties;
//            }
//        });
//    });
    /**
     * Bảng quy luật chuyển động.
     */
    $(document).ready(function () {
        var data =
                [
                    ["0", "0"],
                    ["30", "2"],
                    ["60", "2"],
                    ["90", "0"],
                    ["360", "0"]
                ];

        // Setting table for Thông số hình học
        var container = document.getElementById('motionRule');
        tableMotionRule = new Handsontable(container, {
            colHeaders: [
                'Góc kết<br/>thúc pha<br/>(deg)',
                'Độ cao<br/>tương ứng<br/>(mm)'
            ],
            rowHeaders: true,
            minSpareRows: 1,
            data: data,
            columns: [
                {
                    type: 'numeric'
                },
                {
                    type: 'numeric'
                }
            ],
            
            // Format color for cell
            cells: function (row, col, prop) {
                var cellProperties = {};
                cellProperties.renderer = formatInputColor;
                
//                if ((col === 2) && (row !== 2) && (row > 0)) { // row 3: separator
//                    cellProperties.renderer = formatInputColor; // uses function directly
//                } else if ((col === 2) && (row === 0)) { // Bán kính cơ sở
//                	cellProperties.renderer = formatResultColor;
//                }

                return cellProperties;
            }
        });
    });

    
    /**
     * Bảng kết quả.
     */
    $(document).ready(function () {
        var data =
                [
                    ["d", "", "mm"],
                    ["Góc áp lực max:", "", "deg"],
                    ["Góc áp lực min:", "", "deg"]
                ];

        // Setting table for Kết quả
        var container = document.getElementById('result');
        tableResult = new Handsontable(container, {
            data: data,
            rowHeaders: false,
            colHeaders: false,
            columns: [
                {
                    readOnly: true
                },
                {
                    readOnly: true
                },

                {
                    readOnly: true
                }
            ],
            // Format color for cell
            cells: function (row, col, prop) {
                var cellProperties = {};

                if (col === 1) {
                    cellProperties.renderer = formatResultColor; // uses function directly
                }

                return cellProperties;
            }
        });
        
        var exportPlugin = tableResult.getPlugin('exportFile');
        var buttons = {
        	    exportCSV: document.getElementById('ExportCSV')
        	  };
        
        buttons.exportCSV.addEventListener('click', function() {
        	exportPlugin.downloadFile('csv', {filename: 'Cam'});
          });

    });

    /**
     * Processing submit form
     */
    $(document).ready(function () {
        // Lưu tên nút bấm
        var event;

        // Process events for button "Apply"
//        $('#Apply').click(function (e) {
//            event = "Apply";
//        });

        $('#Clear').click(function (e) {
            //alert("Clear is clicked.");
            clearInputData();
        });

        $('#Run').click(function (e) {
            event = "Run";
        });

        $('#defaultForm').submit(function (e) {
            e.preventDefault();
            var frm = $('#defaultForm');

            // Get data from Thông số hình học, Thông số động học
            var geoArgData = tableGeoArg.getData();
            var kineArg1Data = tableKineArg1.getData();
//            var kineArg2Data = tableKineArg2.getData();
            var motionRuleData = tableMotionRule.getData();

            // Prepare data to submit
            var jsonData = {
                "event": event, // event: reserved keyword
                "rp": geoArgData[0][2], // Bán kính cơ sở 
                "e": geoArgData[1][2], // Khoảng lệch tâm
                "alpha": geoArgData[3][2], // Góc áp lực cho phép
                "motionFunc": kineArg1Data[0][1], // Hàm chuyển 
//                "phaseNo": kineArg1Data[2][1], // Số thứ tự pha
//                "endAnglePhase": kineArg2Data[0][1], // Góc kết thúc pha
//                "high": kineArg2Data[1][1], // Độ cao tương ứng
                "listItemKine": JSON.stringify(motionRuleData)
            };

            // Submit về server
            $.ajax({
                url: 'processCam',
                type: $(this).attr('method'),
                enctype: $(this).attr('enctype'),
                data: jsonData,
                //processData : false,
                //contentType : false,
                success: function (result) {
                    var jsonResult = JSON.parse(result);
                    //console.log("jsonResult=" + jsonResult);
                    console.log("result=" + jsonResult.status);
                    // Draw graph
                    // plotScatter(transposeGraph, jsonResult.transposeData);
                    drawTransposeGraph(jsonResult.data.calData.listS, jsonResult.data.transposeAxis);
                    drawSpeedGraph(jsonResult.data.calData.listV, jsonResult.data.speedAxis);
                    drawAccelerationGraph(jsonResult.data.calData.listA, jsonResult.data.accelerationAxis);
                    drawCamGraph(jsonResult.data.calData.listX, jsonResult.data.calData.listY, jsonResult.data.camAxis);
                    updateResult(jsonResult.data);
                },
                error: function () {
                    console.log("Error!");
                }
            });
        });
    });
    
    /**
     * Xóa dữ liệu đã nhập.
     */
    function clearInputData() {
    	// Bán kính cơ s
    	tableGeoArg.setDataAtCell(0, 2, "");
    	
    	// Khoảng lệch tâm
    	tableGeoArg.setDataAtCell(1, 2, "");
    	// Góc áp lực cho phép
    	tableGeoArg.setDataAtCell(3, 2, "");
    	
    	// Bảng quy luật chuyển động
    	tableMotionRule.clear();
    	
    	// Bảng kết quả
    	for (var i = 0; i < 3; i++) {
    		tableResult.setDataAtCell(i, 1, "");
    	}
    }

    /**
     * @result Model of m.k.s.devlib.model.out.OutCamModel
     */
    function updateResult(result) {
    	// Cập nhật Bán kính cơ sở
    	tableGeoArg.setDataAtCell(0, 2, result.calData.rp);

    	// Cập nhật bảng kết quả: d, Góc áp lực max, Góc áp lực min
    	tableResult.setDataAtCell(0, 1, result.calData.d.toFixed(2));
    	tableResult.setDataAtCell(1, 1, result.calData.alphaMax.toFixed(2));
    	tableResult.setDataAtCell(2, 1, result.calData.alphaMin.toFixed(2));
    	
    }