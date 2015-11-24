package registration;
 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

 
public class RegDAO
{
	static Connection currentCon = null;
	static ResultSet rs = null;
	
	public static RegBean registerStudent(RegBean bean)
	{		
		String ru_id = bean.getRUID();
		String net_id = bean.getNet_id();
		String firstname = bean.getFirstName();
		String lastname = bean.getLastName();
		String grad_year = bean.getGrad_year();
		String password = bean.getPassword();
		String gpa = bean.getGpa();
		String major = bean.getMajor();
		String credits = bean.getCredits();
						
		try
		{
			int parse_ru = Integer.parseInt(ru_id);
			int parse_grad_year = Integer.parseInt(grad_year);
			float parse_gpa = Float.parseFloat(gpa);
			int parse_credits = Integer.parseInt(credits);
			
			//connecting to the DB
			
			currentCon = ConnectionManager.getConnection();
	
			// First insert into users
			
			String query_user = "INSERT INTO USERS(RU_ID, NET_ID, PASSWORD) VALUES(?, ?, ?)";

		    PreparedStatement pstmt = null;
		    pstmt = currentCon.prepareStatement(query_user); 
		    
		    pstmt.setString(1, ru_id); // set input parameter 1
		    pstmt.setString(2, net_id); // set input parameter 2
		    pstmt.setString(3, password); // set input parameter 3
		    
		    pstmt.executeUpdate();
		    
		    String query_student = "INSERT INTO STUDENT(RU_ID, NET_ID, PASSWORD, GRAD_YEAR, FIRSTNAME, LASTNAME,GPA,MAJOR,CREDITS) VALUES(?, ?, ?,?,?,?,?,?,?)";
		    
		    PreparedStatement pstmt_two = null;
		    pstmt_two = currentCon.prepareStatement(query_student); 
		    
		    pstmt_two.setString(1, ru_id); // set input parameter 1
		    pstmt_two.setString(2, net_id); // set input parameter 2
		    pstmt_two.setString(3, password); // set input parameter 3
		    pstmt_two.setInt(4, parse_grad_year);
		    pstmt_two.setString(5, firstname);
		    pstmt_two.setString(6, lastname);
		    pstmt_two.setFloat(7, parse_gpa);
		    pstmt_two.setString(8, major);
		    pstmt_two.setInt(9, parse_credits);
		    
		    pstmt_two.executeUpdate();
		
			System.out.println("Student was successfully registered to the database.");
			
			bean.setAdded(true);
			 
		}
		
		catch (Exception e)
		{
			System.out.println("Registration failed");
			bean.setAdded(false);
		}
		
		return bean;
	}
	
	public static RegBean registerProfessor(RegBean bean)
	{
		Statement stmt = null;
		
		String ru_id = bean.getRUID();
		String net_id = bean.getNet_id();
		String firstname = bean.getFirstName();
		String lastname = bean.getLastName();
		String department = bean.getDepartment();
		String password = bean.getPassword();
						
		try
		{
			//connecting to the DB
			
			int parse_ru_id= Integer.parseInt(ru_id);
			
			currentCon = ConnectionManager.getConnection();
	
			// First insert into users
			
			String query_user = "INSERT INTO USERS(RU_ID, NET_ID, PASSWORD) VALUES(?, ?, ?)";

		    PreparedStatement pstmt = null;
		    pstmt = currentCon.prepareStatement(query_user); 
		    
		    pstmt.setString(1, ru_id); // set input parameter 1
		    pstmt.setString(2, net_id); // set input parameter 2
		    pstmt.setString(3, password); // set input parameter 3
		    
		    pstmt.executeUpdate();
			
		    String query_professor = "INSERT INTO PROFESSOR(RU_ID, NET_ID, PASSWORD, FIRSTNAME, LASTNAME, DEPARTMENT) values(?, ?, ?,?,?,?)";
		    
		    PreparedStatement pstmt_two = null;
		    pstmt_two = currentCon.prepareStatement(query_professor); 
		    
		    pstmt_two.setString(1, ru_id); // set input parameter 1
		    pstmt_two.setString(2, net_id); // set input parameter 2
		    pstmt_two.setString(3, password); // set input parameter 3
		    pstmt_two.setString(4, firstname);
		    pstmt_two.setString(5, lastname);
		    pstmt_two.setString(6, department);
		    
		    pstmt_two.executeUpdate();
			
			System.out.println("Professor was successfully registered to the database.");
			
			bean.setAdded(true);
			 
		}
		
		catch (Exception ex)
		{
			System.out.println("Registration failed");
			bean.setAdded(false);
		}
		
		return bean;
	}
}
