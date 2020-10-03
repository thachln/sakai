<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="r" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<br />
<div>
<div class="row">
  <div class="col-md-3 col-sm-6 col-xs-3">
    <p>Click on the Question Pool to select it:</p><br/>
    <div id="jstree">
    </div>
  </div>

  <div class="col-md-9 col-sm-6 col-xs-9">
     <form:form id="formImportQuestion" name="formImportQuestion" action="save" method="POST" modelAttribute="model" enctype="multipart/form-data" accept-charset="utf-8">
        <div class="row">
            <div class="col-md-6 col-sm-6 col-xs-6">
                <%-- Store selected Pool Question Id from the tree --%>
                Selected the Question Pool: <input readonly="readonly" id="poolName" size="30"/>
                <input type="hidden" id="poolId"/>
            </div>
            <div class="col-md-6 col-sm-6 col-xs-6">
                    <a href="./resources/template/Template_Import-Questions-into-Sakai-11.x.xlsx">Download Excel Template and Guideline</a>
            </div>
        </div>

        <div class="row">
            <div class="col-md-6 col-sm-6 col-xs-6">
                <p class="instruction">Upload Excel file to import the question:</p>
            </div>
            <div class="col-md-6 col-sm-6 col-xs-6 instruction">
                <input name="attachment" class="input-file" type="file" id="attachment"/>
            </div>
        </div>

        <strong>Or</strong> fill the question(s) into below table:<br/>
        <p class="instruction">In case you uploaded file <strong>and</strong> input question(s) into below tables, all questions in the <strong>both file and grid table</strong> will be imported.</p>
        <button id="saveBtn" type="submit" class="btn btn-primary">Save</button><button id="resetBtn" type="reset" class="btn btn-default">Reset</button>
        <div class="table-responsive scroll-container">
            <div id="question">
            </div>
        </div>
        <br/>
        Settings:<br/>
        <input type="checkbox" name="setQuestionColor"/> Color for question: <input type="color" name="questionColor" value="#0000ff">
        <input type="checkbox" name="setQuestionBold"/> <strong>Format bold for question</strong>.
         
        <br/>
        <button id="saveBtn" type="submit" class="btn btn-primary">Save</button><button id="resetBtn" type="reset" class="btn btn-default">Reset</button>
     </form:form>
  </div>
  
</div>
</div>
