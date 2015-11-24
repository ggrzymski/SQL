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
	<h1>Professor Home Page</h1>
</div>
<form name="input" method="post" action="CourseAdded.jsp">
<input type="submit" value="Add Course" />
</form>
&nbsp;
<form name="input" method="post" action="AddPreReq.jsp">
<input type="submit" value="Add Prerequisite" />
</form>
&nbsp;
<form name="actionForm" action="ProfessorServlet" method ="GET">
<input type="hidden" name="formID" value="4">
<input value="Select a Course" type="submit">
</form>
&nbsp;
<form name="input" method="post" action="SpnList.jsp">
<input type="submit" value="Get Assigned SPN # List" />
</form>
&nbsp;
<form name="input" method="post" action="LoginPage.jsp">
<input type="submit" value="Logout" />
</form>
</body>
</html>