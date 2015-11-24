<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@page import="java.util.ArrayList"%> 
<%@page import="home.CourseBean"%> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<div align="center">
	<h1>Course Home Page</h1>
</div>
<%
request.setCharacterEncoding("UTF-8");
ArrayList list = (ArrayList)request.getAttribute("resList");
String formAction = (String)request.getAttribute("formAction");
%>
<form name="actionForm" action="ProfessorServlet" method ="GET">
<input type="hidden" name="formID" value="6">
	Select Semester 
	<select name="semester" size="1">
	<option>Fall</option>
	</select>
	<input value="Lookup Courses" type="submit">
</form>
<%
if (list != null) {
	if (formAction.equals("lookupCourses")) {
	%>
<table border=1>
	<tr>
		<td>Course ID</td>
		<td>Course Name</td>
		<td>Course Professor</td>
		<td>Course Section</td>
		<td>Course Building</td>
		<td>Course Room</td>
		<td>Course Semester</td>
		<td>Course Year</td>
	</tr>
	<%
		for (int i = 0; i < list.size(); ++i) {
			CourseBean ri = (CourseBean)list.get(i);
			%>
	<tr>
		<td><%= ri.getCourse_id() %></td>
		<td><%= ri.getName() %></td>
		<td><%= ri.getLastname() %></td>
		<td><%= ri.getSection() %></td>
		<td><%= ri.getBuilding_code() %></td>
		<td><%= ri.getRoom() %></td>
		<td><%= ri.getSemester() %></td>
		<td><%= ri.getYear_date() %></td>
	</tr>
			<%
		}
	%>
</table>
	<%	
	}
}
%>
&nbsp;
<form name="actionForm" action="ProfessorServlet" method ="GET">
<input type="hidden" name="formID" value="5">
<table>
		<tr><td>Course ID#:</td><td><input type="text" name="course_id"></td></tr>
		<tr><td>Section #:</td><td><input type="text" name="section"></td></tr>
		<tr><td></td><td><input type="submit" value="Submit"></td></tr>
</table>
</form>
</body>
</html>