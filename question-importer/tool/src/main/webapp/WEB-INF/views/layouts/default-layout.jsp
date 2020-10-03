<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="r" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Default layout</title>
<meta content='width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no' name='viewport'/>

<!-- Bootstrap Core CSS -->
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="/library/webjars/bootstrap/3.3.7/css/bootstrap.min.css">
<link rel="stylesheet" media="screen" href="${pageContext.servletContext.contextPath}/resources/hansontable/handsontable.full.css"/>

<tiles:insertAttribute name="css" flush="true"></tiles:insertAttribute>

</head>
<body>
  <tiles:insertAttribute name="body" flush="true"></tiles:insertAttribute>
  <tiles:insertAttribute name="footer" flush="true"></tiles:insertAttribute>
<!-- jQuery library -->
<script src="/library/webjars/jquery/3.3.1/dist/jquery.min.js"></script>

<!-- Latest compiled JavaScript -->
<script src="/library/webjars/bootstrap/3.3.7/js/bootstrap.min.js"></script>

<!--  Define Library Handsontable -->
<script src="${pageContext.servletContext.contextPath}/resources/hansontable/handsontable.full.min.js" type="text/javascript">
</script>

<tiles:insertAttribute name="js" flush="true"></tiles:insertAttribute>

</body>
</html>


