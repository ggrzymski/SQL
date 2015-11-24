<%@ page language="java" contentType="text/html; charset=windows-1256" pageEncoding="windows-1256" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=windows-1256">
<title>Student Registration</title> </head>
<body>
<form name="actionForm" action="ProfessorRegServlet" method ="GET">
<p> Professor Registration Form </p>
<table>
<tr><td>  RUID: </td><td><input type="text" name="RU_ID"/></td></tr>
<tr><td>  Net_ID: </td><td><input type="text" name="NET_ID"/></td></tr>
<tr><td> Password: </td><td><input type="text" name="PASSWORD"/></td></tr>
<tr><td> First Name: </td><td><input type="text" name="FIRSTNAME"/></td></tr>
<tr><td> Last Name: </td><td><input type="text" name="LASTNAME"/></td></tr>
<tr><td> Department: </td><td><input type="text" name="DEPARTMENT"/></td></tr>
<tr><td colspan="2" align="center"><input type="submit" value="submit"> </td></tr>
<tr><td colspan="2" align="center"><input type="submit" value="cancel"> </td></tr>
</table>
</form>
</body>
</html>