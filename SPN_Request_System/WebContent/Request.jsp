<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<h2>Special Permission Request</h2>

	<form name="actionForm" action="StudentServlet" method ="GET">
	<input type="hidden" name="formID" value="3">
	<table>
		<tr><td>Course ID#:</td><td><input type="text" name="coursefield"></td></tr>
		<tr><td>Section #:</td><td><input type="text" name="section"></td></tr>
		<tr><td>Is this required for Major?:</td><td><input type="checkbox" name="required" value="true"></td></tr>
		<tr><td>Comment:</td><td><textarea name="comment" rows="5" cols="40"></textarea></td></tr>
		<tr><td></td><td><input type="submit" value="Submit"></td></tr>
	</table>
	</form>
</body>
</html>