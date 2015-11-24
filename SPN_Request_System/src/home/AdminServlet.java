package home;
 
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.*;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class LoginServlet
 */
public class AdminServlet extends HttpServlet 
{
	
	public static String NET_ID;
	
	public static void setID(String input)
	{
		NET_ID=input;
	}
    private static final long serialVersionUID = 1L;
 
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AdminServlet() 
    {
        super();
        // TODO Auto-generated constructor stub
    }
 
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
        	System.out.println("In the Admin Servlet");
        	
        	int formID = Integer.parseInt(request.getParameter("formID"));
        	
        	if(formID==1)
        	{
        		HashMap<String,ArrayList<BigBean>> store_db = new HashMap<String,ArrayList<BigBean>>();
        		HomeDAO dao = new HomeDAO();
        		
        		ArrayList<BigBean> users = new ArrayList<BigBean>();
        		ArrayList<BigBean> professors = new ArrayList<BigBean>();
        		ArrayList<BigBean> students = new ArrayList<BigBean>();
        		ArrayList<BigBean> classrooms = new ArrayList<BigBean>();
        		ArrayList<BigBean> courses = new ArrayList<BigBean>();
        		ArrayList<BigBean> student_info = new ArrayList<BigBean>();
        		ArrayList<BigBean> enrolled = new ArrayList<BigBean>();
        		ArrayList<BigBean> prerequisites = new ArrayList<BigBean>();
        		ArrayList<BigBean> spnrequests = new ArrayList<BigBean>();
        		ArrayList<BigBean> spn = new ArrayList<BigBean>();
        		ArrayList<BigBean> user_type = new ArrayList<BigBean>();
        		
        		users = dao.getUsers();
        		students = dao.getStudents();
        		professors = dao.getProfessors();
        		classrooms = dao.getClassroom();
        		courses = dao.getCourse();
        		student_info = dao.getStdnt_info();
        		enrolled = dao.getENROLLED();
        		prerequisites = dao.getPrereq();
        		spnrequests = dao.getSPNRequest();
        		spn = dao.getSPN();
        		user_type = dao.getUserTypes();
        		
        		store_db.put("USERS", users);
        		store_db.put("STUDENT", students);
        		store_db.put("PROFESSOR", professors);
        		store_db.put("CLASSROOM", classrooms);
        		store_db.put("COURSE", courses);
        		store_db.put("STUDENT_INFO", student_info);
        		store_db.put("ENROLLED", enrolled);
        		store_db.put("PREREQUISITE", prerequisites);
        		store_db.put("SPNREQUEST", spnrequests);
        		store_db.put("SPN", spn);
        		store_db.put("USERTYPE", user_type);
        		
        		try
        		{
        			File file = new File("temp.txt");
        	        FileOutputStream f = new FileOutputStream(file);
        	        ObjectOutputStream s = new ObjectOutputStream(f);
        	        s.writeObject(store_db);
        	        s.close();
        		}
        		catch(Exception e)
        		{
        			System.out.println("Couldn't store database in a file"+e);
        		}
        	}
        	
        	if(formID==2)
        	{
        		// Read the database file
        		
        		HashMap<String,ArrayList<BigBean>> store_db = new HashMap<String,ArrayList<BigBean>>();
        		HomeDAO dao = new HomeDAO();
        		
        		try
        		{
        			File file = new File("temp.txt");
        		    FileInputStream f = new FileInputStream(file);
        		    ObjectInputStream s = new ObjectInputStream(f);
        		    store_db = (HashMap<String, ArrayList<BigBean>>) s.readObject();
        		    s.close();
        		    
        		    // Users
        		    
        		    dao.insertUsers(store_db.get("USERS"));
        		    dao.insertStudents(store_db.get("STUDENT"));
        		    dao.insertProfessors(store_db.get("PROFESSOR"));
        		    dao.insertClassroom(store_db.get("CLASSROOM"));
        		    dao.insertCourses(store_db.get("COURSE"));
        		    dao.insertStudentInfo(store_db.get("STUDENT_INFO"));
        		    dao.insertEnrolled(store_db.get("ENROLLED"));
        		    dao.insertPrereq(store_db.get("PREREQUISITE"));
        		    dao.insertSpnRequest(store_db.get("SPNREQUEST"));
        		    dao.insertSpn(store_db.get("SPN"));
        		    dao.insertUserType(store_db.get("USERTYPE"));
        		}
        		catch(Exception e)
        		{
        			System.out.println("Couldn't read file to retrieve database");
        		}
        	}
    }
 
    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
    	
    }
 
}
