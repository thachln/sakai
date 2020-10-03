<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<script src="/sakai-content-tool/js/jstree/dist/jstree.min.js"></script>
<script type="text/javascript">
  var tableQuestion;
  var container;
  /**
   * Lưới nhập liệu câu hỏi trắc nghiệm.
   */
  $(document).ready(function () {
      container = document.getElementById('question');

      initQuestionTable();
      $("#resetBtn").click(function() {
          // Clear selected Pool name
          $('#poolName').val('');
          $('#poolId').val('');

          // Delete the current table
    	  tableQuestion.destroy();
    	  initQuestionTable();
      })
      
  });

    function initQuestionTable() {
        tableQuestion = new Handsontable(container, {
          data: [['', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '']],
          rowHeaders: true,
            minSpareRows: 1,
            startCols: 19,
            colHeaders: ['Question', 'Level', 'Score', 'IsNotRandom (x)', 'Question type', 'Correct<br/>answer(s)', 
              'A', 'B', 'C', 'D', 'E', 'FA', 'FB', 'FC', 'FD', 'FE', 'Correct<br/>feedback', 'Incorrect<br/>feedback',
              'Objective'],
            colWidths: [400, 50, 50, 50, 50, 75, 60, 60, 60, 60, 60, 100, 100, 100, 100, 100, 100, 100, 100],
            manualColumnResize: true,
            contextMenu: { // Refer code: https://handsontable.com/blog/articles/customize-handsontable-context-menu; https://docs.handsontable.com/pro/1.8.2/Core.html#alter
                items : {
                      "insert_answer" : {
                        name : "Add Column",
                        callback: function(key, options) {
                            var selection = this.getSelectedRange()[0];

                              // console.log("selection=" + JSON.stringify(selection));

                            // var selectedCol = selection.from.col;
                            var selectedCol = selection.highlight.col;
                              // console.log("selectedCol: " + selectedCol);
                              var colHeaderData =  this.getColHeader();
                              // console.log("colHeaderData: " + colHeaderData); 

                              var selectedColumnName = colHeaderData[selectedCol];

                              if (isAnswerCol(selectedColumnName)) {
                                  // Refer: https://docs.handsontable.com/pro/1.8.2/Core.html#alter
                                  var curCols = this.countCols();

                                  // Insert in right of selected column
                                  this.alter('insert_col', selectedCol + 1); 

                                  colHeaderData =  this.getColHeader();
                                  // make sure to update your hot instance to trigger the re-render the grid
                                  // console.log("New colHeaderData=" + colHeaderData);

                                  // Re-update the column names
                                  colHeaderData = updateAnswerColumnName(colHeaderData);
                                  this.updateSettings({
                                      colHeaders: colHeaderData
                                  });
                              } else if (isAnswerFeedbackColumn(selectedColumnName)) {
                                  // Add more answer feedback
                                  var curCols = this.countCols();

                                  // Insert in right of selected column
                                  this.alter('insert_col', selectedCol + 1); 

                                  colHeaderData =  this.getColHeader();
                                  // make sure to update your hot instance to trigger the re-render the grid
                                  // console.log("New colHeaderData=" + colHeaderData);

                                  // Re-update the column names
                                  colHeaderData = updateAnswerFeedbackColumnName(colHeaderData);
                                  this.updateSettings({
                                      colHeaders: colHeaderData
                                  });
                              } else {
                                  alert("Suport to right-click on the columns A,B,C... or FA, FB, FC,... to add columns for Answer Options or Feekback for Answer Options.");
                              }
                        }
                      },
                      "remove_col" : {
                          name : "Remove Column",
                          callback: function(key, options) {
                            var selection = this.getSelectedRange()[0];
                            var selectedCol = selection.highlight.col;
                              // console.log("selectedCol: " + selectedCol);
                              var colHeaderData =  this.getColHeader();
                              // console.log("colHeaderData: " + colHeaderData); 

                              var selectedColumnName = colHeaderData[selectedCol];

                              if (isAnswerCol(selectedColumnName)) {
                                  // Refer: https://docs.handsontable.com/pro/1.8.2/Core.html#alter
                                  var curCols = this.countCols();
                                  this.alter('remove_col', selectedCol); 

                                  // Re-update the column names
                                  colHeaderData =  this.getColHeader();
                                  colHeaderData = updateAnswerColumnName(colHeaderData);
                                  this.updateSettings({
                                      colHeaders: colHeaderData
                                  });
                              } else if (isAnswerFeedbackColumn(selectedColumnName) && ("FA" != selectedColumnName)) {
                                  // alert("Add more answer feedback.");
                                  console.log("Delete column:" + selectedColumnName);
                                  var curCols = this.countCols();
                                  this.alter('remove_col', selectedCol); 

                                  // Re-update the column names
                                  colHeaderData =  this.getColHeader();
                                  colHeaderData = updateAnswerFeedbackColumnName(colHeaderData);
                                  this.updateSettings({
                                      colHeaders: colHeaderData
                                  });
                              } else {
                                alert("In this version, only suport to remove columns A,B,C... or FA, FB, FC,...");
                              }
                          }
                      }
                  }
            }
        });

    }
    /**
    * Check a column name is a answer column or not. The answer column includes one character such as: A, B, C,....
    */
    function isAnswerCol(colName) {
      return (colName.length == 1);
    };
    
    /**
    * Check a column name is a answer column or not. The answer column includes one character such as: FA, FB, FC,....
    */
    function isAnswerFeedbackColumn(colName) {
      return colName.length == 2;
    }
    
    /**
     * Rename column names of Answer as: A, B,....
     */
    function updateAnswerColumnName(colNames) {
      var INDEX_START_A = 6;
    
      var i = INDEX_START_A;
      // Rename answer column as A, B, C...
      var charA = 'A';
      while (colNames[i].length == 1) {
          newColName = String.fromCharCode(charA.charCodeAt() + (i - INDEX_START_A));
          colNames[i] = newColName;
          i++;
      }
    
      return colNames;
    }

    /**
     * Rename column names of Answer Feedback as: FA, FB,....
     */
    function updateAnswerFeedbackColumnName(colNames) {

        var indexFA = colNames.indexOf("FA");
        // console.log("Index of column FA=" + indexFA);

        if ((-1 < indexFA) && (indexFA < colNames.length)) {
            var i = indexFA;
            var charA = 'A';
            while (colNames[i].length <= 2) {
                newColName = String.fromCharCode(charA.charCodeAt() + (i - indexFA));
                colNames[i] = "F" + newColName;
                i++;
            }
        } else {
            alert("Unknown error.");
        }

        return colNames;
    }
</script>

<script>
  /**
   * Display tree of Question Pools.
   * Events:
   * When select a Pool, the name and id of the selected pool will be set to inputs: poolName, poolId in home.jsp
   */
  $(function () {
    // 6 create an instance when the DOM is ready
    $('#jstree').jstree({
    	"core" : {

    		  'multiple' : false,
              'data' : {
                'url' : function(node) {
                  return node.id === '#' ? 'getNodeRoot' : 'getNodeChildren';
                },
                'dataType' : 'JSON',
                'data' : function(node) {
                  if (node.id === '#') {
                    return {
                      'id' : -1
                    };
                  } else {
                    return {
                      'id' : node.id,
                      'description' : node.a_attr["description"]
                    };
                  }
                }
              }
              ,"plugins" : [ "unique", "search", "state", "types", "wholerow" ]
    	}	
    });
    // 7 bind to events triggered on the tree
    $('#jstree').on("changed.jstree", function (e, data) {
      var id = data.selected[0];

      console.log('data.selected=' + data.selected + ';id=' + id);
      
      if (typeof data.node !== "undefined") {
    	  var title = data.node["a_attr"].title;

    	  console.log("title=" + title);
          
    	  $('#poolName').val(title);
      } else {
    	  console.log("Could not get the Title of Question Pool.");
      }

      // Set hidden input
      $('#poolId').val(id);
    });
    // 8 interact with the tree - either way is OK
    $('jstree').on('click', function () {
      $('#jstree').jstree(true).select_node('child_node_1');
      $('#jstree').jstree('select_node', 'child_node_1');
      $.jstree.reference('#jstree').select_node('child_node_1');
    });
  });
  </script>
  
  <script>
  function disableSaveBtn() {
	  $("#saveBtn").attr("disabled", true);
	  
  }
  function enableSaveBtn() {
	  $("#saveBtn").removeAttr("disabled");
  }
  /**
   * Processing events of question table.
   */
  $(document).ready(function() {

      $('#formImportQuestion').submit(function(e) {
          e.preventDefault();

          // Disable Save button to avoid twice click.
          disableSaveBtn();

          var frm = $('#formImportQuestion');
          var selectPoolId = $('#poolId').val();
          var frmData = new FormData(this);

          // Get header name
          var colHeaderData =  tableQuestion.getColHeader();
          
          // Get data from Handsontable
          var questionData = tableQuestion.getData();

          var questionTableData = {header: colHeaderData, data: questionData};

          var formDataJson = JSON.stringify(questionTableData);

          // Get column header
//           var colHeaderNames = JSON.stringify(); //for row headers
//           console.log("colHeader" + colHeader);

          frmData.append("questionList", formDataJson);
          frmData.append("poolId", selectPoolId);
//           frmData.append("colHeaderNames", colHeaderNames);

          console.log("formDataJson=" + formDataJson);
          //frmData.append("noteData", "Demo"); 
//           console.log(frmData);
          //           var thisForm = new FormData();
          //           thisFormo.append("file", files[0])
          
          // Data validation
          if (selectPoolId === '') {
              alert("You must select a Pool Question in the left panel.");

              // Re-enable the Save button
              enableSaveBtn();
              return;
          }
          
          $.ajax({
              url : frm.attr('action'),
              type : frm.attr('method'),
              enctype : frm.attr('enctype'),
              data : frmData,
              processData : false,
              contentType : false,
              success : function(result) {
                  result = JSON.parse(result);
                  console.log("Result:" + result.status);

                  if (result.status == "FAILED") {
                	  alert("Result status: " + result.status + "\n"
                             + "Invalid data at question " + result.invalidQuestionIdx + "\n"
                             + ", column " + result.invalidColIdx + "\n"
                             + ", error message: " +  result.errorMessage
                            );  
                  } else if (result.status == "ERROR") {
                      alert("Error: " + result.errorMessage);
                  } else {
                	  alert("Result status: " + result.status);
                      // Clear selected Pool to avoid twice save
                      $('#poolName').val('');
                      $('#poolId').val('');
                  }

                  // Re-enable the Save button
                  enableSaveBtn();
              },
              error : function() {
                  console.log("Error!");

                  // Re-enable the Save button
                  enableSaveBtn();
              }
          });
      });
  });
  </script>