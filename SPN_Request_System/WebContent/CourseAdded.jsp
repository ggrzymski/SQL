<html>
<head>
<title>Adding a Course</title>
</head>
<body>
<form name="actionForm" action="ProfessorServlet" method ="GET">
<input type="hidden" name="formID" value="1">
	<table>
		<tr><td>Course ID:</td><td><input type="text" name="cid"></td></tr>
		<tr><td>Course Name:</td><td><input type="text" name="cname"></td></tr>
		<tr><td>Course Section:</td><td><input type="text" name="section"></td></tr>
		<tr><td>Building Code:</td><td><input type="text" name="building"></td></tr>
		<tr><td>Room #:</td><td><input type="text" name="roomnum"></td></tr>
		<tr><td>Room Size:</td><td><input type="text" name="roomsize"></td></tr>
		<tr><td></td><td><input type="submit" value="Submit"></td></tr>
	</table>
	</form>
</body>
</html>