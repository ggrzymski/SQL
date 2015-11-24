<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<div align="center">
	<h1>Admin Home Page</h1>
</div>
<form name="actionForm" action="AdminServlet" method ="GET">
<input type="hidden" name="formID" value="1">
<input value="Write Database into File" type="submit">
</form>
&nbsp;
<form name="actionForm" action="AdminServlet" method ="GET">
<input type="hidden" name="formID" value="2">
<input value="Read Database From File" type="submit">
</form>
</body>
</html>