package login;
 
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
 
public class LoginDAO
{
	static Connection currentCon = null;
	static ResultSet rs = null;
	
	public static LoginBean login(LoginBean bean)
	{
		Statement stmt = null;
		String username = bean.getUsername();
		String password = bean.getPassword();
		String searchQuery = "select * from USERS where NET_ID='" + username + "' AND PASSWORD='" + password + "'";
		String searchQuerys = "select * from STUDENT where NET_ID='" + username + "' AND PASSWORD='" + password + "'";

		try
		{
			//connecting to the DB
			currentCon = ConnectionManager.getConnection();
			stmt=currentCon.createStatement();
			
			rs = stmt.executeQuery(searchQuery);
			
			boolean userExists = rs.next(); // Result Set
			
			rs = stmt.executeQuery(searchQuerys);
			
			boolean studentExists = rs.next();
			
			if(studentExists)
			{
				bean.setStudent(true);
			}
	 
			if (!userExists)
			{
				bean.setValid(false);
			}
			else if (userExists)
			{
				bean.setValid(true);
			}
			 
		}
		catch (Exception ex)
		{
			System.out.println("Log In failed: An Exception has occurred! ");
		}
		
		return bean;
	}
}
