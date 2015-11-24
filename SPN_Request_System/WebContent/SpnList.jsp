<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@page import="java.util.ArrayList"%> 
<%@page import="home.SpnBean"%>
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
<form name="actionForm" action="ProfessorServlet" method ="GET">
<input type="hidden" name="formID" value="9">
	Select Semester 
	<select name="semester" size="1">
	<option>Fall</option>
	</select>
	<input value="Lookup SPN" type="submit">
</form>
<%
if (list != null) {
	if (formAction.equals("lookupSPN")) {
	%>
	<table border=1>
	<tr>
		<td>Course ID</td>
		<td>Course Section</td>
		<td>SPN ID</td>
		<td>Net ID</td>
		<td>Prof ID</td>
	</tr>
	<%
		for (int i = 0; i < list.size(); ++i) {
			SpnBean ri = (SpnBean)list.get(i);
			%>
	<tr>
		<td><%= ri.getCourse_id() %></td>
		<td><%= ri.getSection() %></td>
		<td><%= ri.getSpn_id() %></td>
		<td><%= ri.getNet_id() %></td>
		<td><%= ri.getProf_id() %></td>
	</tr>
			<%
		}
	%>
</table>
	<%	
	}
}
%>
</body>
</html>