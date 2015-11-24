<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@page import="java.util.ArrayList"%> 
<%@page import="home.RequestBean"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<%
request.setCharacterEncoding("UTF-8");
ArrayList list = (ArrayList)request.getAttribute("resList");
String formAction = (String)request.getAttribute("formAction");
%>
<center>
<form name="actionForm" action="StudentServlet" method ="GET">
<input type="hidden" name="formID" value="4">
	Select Semester 
	<select name="semester" size="1">
	<option>Fall</option>
	</select>
	<input value="Lookup Requests" type="submit">
</form>
<%
if (list != null) {
	if (formAction.equals("lookupRequests")) {
	%>
<table border=1>
	<tr>
		<td>SPN Number</td>
		<td>Course ID</td>
		<td>Section</td>
		<td>Net ID</td>
		<td>Status</td>
	</tr>
	<%
		for (int i = 0; i < list.size(); ++i) {
			RequestBean ri = (RequestBean)list.get(i);
			%>
	<tr>
		<td><%= ri.getSpn() %></td>
		<td><%= ri.getCourse_id() %></td>
		<td><%= ri.getSection() %></td>
		<td><%= ri.getNet_id() %></td>
		<td><%= ri.getStatus() %></td>
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