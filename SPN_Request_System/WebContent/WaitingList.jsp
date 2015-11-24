<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@page import="java.util.ArrayList"%> 
<%@page import="home.RequestBean"%>
<%@page import="home.CourseBean"%>  
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<div align="center">
	<h1>Waiting List Home Page</h1>
</div>
<%
request.setCharacterEncoding("UTF-8");
ArrayList list = (ArrayList)request.getAttribute("resList");
ArrayList lists = (ArrayList)request.getAttribute("resLists");
String formAction = (String)request.getAttribute("formAction");
String formActions = (String)request.getAttribute("formActions");
%>
<%
if (list != null) {
	if (formAction.equals("lookupEnrolled")) {
	%>
<table border=1>
	<tr>
		<td>SP Request ID</td>
		<td>NET ID</td>
		<td>Course ID</td>
		<td>Course Section</td>
		<td>Course Required</td>
		<td>Student Rating</td>
		<td>Time Stamp</td>
		<td>Status</td>
	</tr>
	<%
		for (int i = 0; i < list.size(); ++i) {
			RequestBean ri = (RequestBean)list.get(i);
			%>
	<tr>
		<td><%= ri.getSp_request_id() %></td>
		<td><%= ri.getNet_id() %></td>
		<td><%= ri.getCourse_id() %></td>
		<td><%= ri.getSection() %></td>
		<td><%= ri.getRequired() %></td>
		<td><%= ri.getRating() %></td>
		<td><%= ri.getTime_stamp() %></td>
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
<%
if (lists != null) {
	if (formActions.equals("lookupPrereq")) {
	%>
<table border=1>
	<tr>
		<td>Prerequisite ID</td>
		<td>Prerequisite Name</td>
		<td>Parent Course ID</td>
	</tr>
	<%
		for (int i = 0; i < lists.size(); ++i) {
			CourseBean ri = (CourseBean)lists.get(i);
			%>
	<tr>
		<td><%= ri.getPid() %></td>
		<td><%= ri.getName() %></td>
		<td><%= ri.getCourse_id() %></td>
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
<input type="hidden" name="formID" value="8">
<input value="Assign SPN #" type="submit">
</form>
&nbsp;
<form name="actionForm" action="ProfessorServlet" method ="GET">
<input type="hidden" name="formID" value="7">
<table>
		<tr><td>Net ID#:</td><td><input type="text" name="net_id"></td></tr>
		<tr><td>Comment:</td><td><textarea name="comment" rows="5" cols="40"></textarea></td></tr>
		<tr><td></td><td><input type="submit" value="Submit"></td></tr>
</table>
</form>
</body>
</html>