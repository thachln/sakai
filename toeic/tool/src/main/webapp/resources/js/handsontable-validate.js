
// Mẫu để kiểm tra số thực
var floatPattern = /^[\d]*[\.]{0,1}[\d]*$/;
var intPattern = /^[\d]*$/;
var dotPattern = /^[.]$/;

// English and vietnamese name pattern for testing.
//var namePattern = /^[a-zA-Z_àáãạảăắằẳẵặâấầẩẫậèéẹẻẽêềếểễệđìíĩỉịòóõọỏôốồổỗộơớờởỡợùúũụủưứừửữựỳỵỷỹýÀÁÃẠẢĂẮẰẲẴẶÂẤẦẨẪẬÈÉẸẺẼÊỀẾỂỄỆĐÌÍĨỈỊÒÓÕỌỎÔỐỒỔỖỘƠỚỜỞỠỢÙÚŨỤỦƯỨỪỬỮỰỲỴỶỸÝ\\s]+$/;
var namePattern = "^[a-zA-Z_àáãạảăắằẳẵặâấầẩẫậèéẹẻẽêềếểễệđìíĩỉịòóõọỏôốồổỗộơớờởỡợùúũụủưứừửữựỳỵỷỹý" +
						"ÀÁÃẠẢĂẮẰẲẴẶÂẤẦẨẪẬÈÉẸẺẼÊỀẾỂỄỆĐÌÍĨỈỊÒÓÕỌỎÔỐỒỔỖỘƠỚỜỞỠỢÙÚŨỤỦƯỨỪỬỮỰỲỴỶỸÝ\\s]+$";

/**
 * Check value is a positive integer.
 * 
 * @param value number to be checked
 * @param callback function of caller.
 */
intValidator = function (value, callback) {
	var pattern = /^[\d]*$/;
	
	if (pattern.test(value)) {
	  callback(true);
	} else {
	  callback(false);
	}
};

intValidator4 = function (value, callback) {
	var pattern = /^[\d]*$/;
	
	if (pattern.test(value) && value.length <= 4) {
	  callback(true);
	} else {
	  callback(false);
	}
};

intValidator5 = function (value, callback) {
	var pattern = /^[\d]*$/;
	
	if (pattern.test(value) && value.length <= 5) {
	  callback(true);
	} else {
	  callback(false);
	}
};

floatValidator = function (value, callback) {
	if (floatPattern.test(value)) {
	  callback(true);
	} else {
	  callback(false);
	}
};



lengthValidateName = function(value, callback) {
	(value.length <= lenName) ? ($('#errorName').hide() , callback(true)) : ($('#errorName').show() , callback(false));
};

lengthValidateDes = function(value, callback) {
	(value.length <= lenDes) ? ($('#errorDes').hide() , callback(true)) : ($('#errorDes').show() , callback(true));
};

/**
 * Kiểm tra số thực hợp lệ và phần nguyên có tối đa 4 kí số.
 */
floatValidator4 = function (value, callback) {
	
	
	// trunk the int part
	var dotIdx = value.indexOf(".");
	
	if (dotIdx > -1) {
		var leftDot = value.substring(0, dotIdx - 1);
	} else {
		leftDot = value
	}
	
	
	if (floatPattern.test(leftDot) && leftDot.length <= 4) {
	  callback(true);
	} else {
	  callback(false);
	}
};


/**
 * Check if value is a positive integer number and within limit if there is limit.
 * @param handsontable - instance of handsontable
 * @param row - checking cell's row
 * @param col - checking cell's col
 * @param newVal - the new value user type in.
 * @param limit - limit of newVal's length
 * @returns
 */
function integerValidator (handsontable, row, col, newVal, limit) {
    var valid; 

    if (newVal) {
	    if (limit == undefined) {
	        valid = (intPattern.test(newVal));
	    } else {
	    	valid = (intPattern.test(newVal) && newVal.length <= limit);
	    }
    } else {
    	valid = true;
    }

    handsontable.getCellMeta(row, col).valid = valid;
    
    handsontable.render();
}

/**
 * Check if value is a positive decimal number and within limit if there is limit.
 * @param handsontable - instance of handsontable
 * @param row - checking cell's row
 * @param col - checking cell's col
 * @param newVal - the new value user type in.
 * @param limit - limit of newVal's length
 */
function decimalValidator (handsontable, row, col, newVal, limit) {
    var valid; 

	// trunk the Integer part
    if (newVal) {
		var dotIdx = newVal.indexOf(".");
		
		if (dotIdx > -1) {
			var leftDot = newVal.substring(0, dotIdx);
		} else {
			leftDot = newVal;
		}
	
	    if (limit == undefined) {
	        valid = (floatPattern.test(newVal));
	    } else {
	    	valid = (floatPattern.test(newVal) && leftDot.length <= limit);
	    }
    } else {
    	valid = true;
    }

	handsontable.getCellMeta(row, col).valid = valid;
 
    handsontable.render();
}

/**
 * Check if text length within litmit (default 500 if undefined) if there is.
 * @param handsontable
 * @param row
 * @param col
 * @param newVal
 * @param limit
 */
function textValidator (handsontable, row, col, newVal, limit) {
    var valid; 

    if (newVal) {
	    if (limit == undefined) {
	        valid = (newVal.length <= 500);
	    } else {
	    	valid = (newVal.length <= limit);
	    }
    } else {
    	valid = true;
    }

    handsontable.getCellMeta(row, col).valid = valid;
    
    handsontable.render();	
}

/**
 * Check if value is a vietnamese phone number.
 * @param handsontable
 * @param row
 * @param col
 * @param newVal
 * @returns
 */
function phoneValidator (handsontable, row, col, newVal) {
    var valid; 

    if (newVal) {
    	valid = (intPattern.test(newVal) && (10 <= newVal.length && newVal.length <= 11));
    } else {
    	valid = true;
    }

    handsontable.getCellMeta(row, col).valid = valid;
 
    handsontable.render();	
}

/**
 * Check if value is a english or vietnamse name (no special characters, number) and within limit (default 500).
 * 
 * @param handsontable
 * @param row
 * @param col
 * @param newVal
 * @param limit
 * @returns
 */
function nameValidator (handsontable, row, col, newVal, limit) {
    var valid; 

    if (newVal) {
	    if (limit == undefined) {
	        valid = (newVal.match(namePattern) && newVal.length <= 100);
	    } else {
	    	valid = (newVal.match(namePattern) && newVal.length <= limit);
	    }
    } else {
    	valid = true;
    }
    
    if (valid) {
    	handsontable.getCellMeta(row, col).valid = true;
    } else {
    	handsontable.getCellMeta(row, col).valid = false;
    }
    
    handsontable.render();		
}

/**
 * function check if list of wanted to check column equal specific column
 * @param columns
 * @param col
 * @returns
 */
function isCheckCol(columns, col) {
	for (var i = 0; i < columns.length; i++) {
		if (columns[i] == col) {
			return true;
		}
	}
	
	return false;
}


/**
 * function check if list of unwanted to check column equal specific column
 * @param exceptColumns
 * @param col
 * @returns
 */
function isExceptCol(exceptColumns, col) {
	for (var i = 0; i < exceptColumns.length; i++) {
		if (exceptColumns[i] == col) {
			return true;
		}
	}
	
	return false;	
}

/**
 * Check if row have at least one column have value but others are empty.
 * If columns undefined check whole row.
 * @param handsontable - instance of handsontable
 * @param columns - list of column you want to check.
 * @returns
 */
function areEmptyColumns (handsontable, columns) {
	var inValid = false;
	
	if (columns) {
	    for (var row = 0; row < handsontable.countRows(); row++) {
	        if (!handsontable.isEmptyRow(row)) {
	            for (var col = 0; col < handsontable.countCols(); col++) {
	            	if (isCheckCol(columns, col)) {
		                if (handsontable.getDataAtCell(row, col) === null || handsontable.getDataAtCell(row, col) === "") {
		                	inValid = true;                    
		                    handsontable.getCellMeta(row, col).valid = false;
		                }
	            	}
	            }
	        }
	    }
	} else {
	    for (var row = 0; row < handsontable.countRows(); row++) {
	        if (!handsontable.isEmptyRow(row)) {
	            for (var col = 0; col < handsontable.countCols(); col++) {
	                if (handsontable.getDataAtCell(row, col) === null || handsontable.getDataAtCell(row, col) === "") {
	                	inValid = true;                    
	                    handsontable.getCellMeta(row, col).valid = false;
	                }
	            }
	        }
	    }		
	}
    
    handsontable.render();
    
    return inValid;
}

/**
 * Check if row with specific column empty or not.
 * @param handsontable
 * @returns
 */
function isEmptyColumn (handsontable, column) {
	var inValid = false;
	
    for (var row = 0; row < handsontable.countRows(); row++) {
        if (!handsontable.isEmptyRow(row)) {
            for (var col = 0; col < handsontable.countCols(); col++) { 
            	if (col == column){
	                if (handsontable.getDataAtCell(row, col) === null || handsontable.getDataAtCell(row, col) === "") {
	                	inValid = true;                    
	                    handsontable.getCellMeta(row, col).valid = false;
	                } 
            	}
            }
        }
    }
    
    handsontable.render();
    
    return inValid;	
}

/**
 * Check if handsontable table have any invalid cell.
 * @param handsontable
 * @returns
 */
function isValidTable (handsontable) {
	var valid = true;
	
	for (var row = 0; row < handsontable.countRows(); row++){
		if (!handsontable.isEmptyRow(row)) {
			for (var col = 0; col < handsontable.countCols(); col++){
				if (handsontable.getCellMeta(row, col).valid == false) {
					valid = false;
					break;
				}
			}
		} else {
			for (var col2 = 0; col2 < handsontable.countCols(); col2++) {
				if (handsontable.getCellMeta(row, col2).valid == false) {
					handsontable.getCellMeta(row, col2).valid = true;
				}
			}
		}
	}
	
	handsontable.render();
	
	return valid;
}

/**
 * Check if handsontable empty or not.
 * @param handsontable
 * @returns
 */
function isEmptyTable (handsontable) {
	
	return (handsontable.countEmptyRows() == handsontable.countRows());
}

/**
 * Check if cell's value within limit or not.
 * use this method if you want to show error alert message at specific column
 * @param handsontable
 * @param row
 * @param col
 * @param value
 * @param limit
 * @returns 
 */
function isWithinLimit (handsontable, row, col, value, limit) {
    var valid; 
    
    if (value) {
	    if (limit == undefined) {
	        valid = (value.length <= 500);
	    } else {
	    	valid = (value.length <= limit);
	    }
    } else {
    	valid = true;
    }

	handsontable.getCellMeta(row, col).valid = valid;
    handsontable.render();	

    return valid;  
}

/**
 * Check if value is a decimal number,
 * use this method if you want to show error alert message at specific column.
 * @param handsontable
 * @param row
 * @param col
 * @param value
 * @param limit
 * @returns
 */
function isDecimalNumber (handsontable, row, col, value, limit) {
    var valid; 
    
    if (value) { 
    	
		// trunk the Integer part
		var dotIdx = value.indexOf(".");
		
		if (dotIdx > -1) {
			var leftDot = value.substring(0, dotIdx);
		} else {
			leftDot = value;
		}
	
	    if (limit == undefined) {
	        valid = (floatPattern.test(value));
	    } else {
	    	valid = (floatPattern.test(value) && leftDot.length <= limit);
	    }
    } else {
    	valid = true;
    }

	handsontable.getCellMeta(row, col).valid = valid;
    handsontable.render();
    
    return valid;
}

/**
 * Check if value is a integer number,
 * use this method if you want to show error alert message at specific column.
 * @param handsontable
 * @param row
 * @param col
 * @param value
 * @param limit
 * @returns
 */
function isIntegerNumber (handsontable, row, col, value, limit) {
    var valid; 

    if (value) {
	    if (limit == undefined) {
	        valid = (intPattern.test(value));
	    } else {
	    	valid = (intPattern.test(value) && value.length <= limit);
	    }
    } else {
    	valid = true;
    }

	handsontable.getCellMeta(row, col).valid = valid;
    handsontable.render();
    
    return valid;
}

/**
 * get all invalid cells in handsontable.
 * use this for refresh table after choose data from datatable,
 * or show error message for specific column.
 * @param handsontable
 * @returns
 */
function getInvalidCells (handsontable) {
	var invalidCells = [];
	
	for (var row = 0; row < handsontable.countRows(); row++){
		if (!handsontable.isEmptyRow(row)) {
			for (var col = 0; col < handsontable.countCols(); col++){
				if (handsontable.getCellMeta(row, col).valid == false) {
					
					invalidCells.push({'row': row, 'col': col});
				}
			}
		}
	}

	return invalidCells;
}
















