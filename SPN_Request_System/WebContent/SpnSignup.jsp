<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<form name="actionForm" action="StudentServlet" method ="GET">
<input type="hidden" name="formID" value="5">
<table>
		<tr><td>SPN #:</td><td><input type="text" name="spn_num"></td></tr>
		<tr><td>Course ID#:</td><td><input type="text" name="course_id"></td></tr>
		<tr><td>Section #:</td><td><input type="text" name="section"></td></tr>
		<tr><td></td><td><input type="submit" value="Submit"></td></tr>
</table>
</form>
</body>
</html>