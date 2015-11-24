package home;
 
import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class LoginServlet
 */
public class StudentServlet extends HttpServlet 
{
	
	public static String NET_ID;
	
	public static String courses_id;
	public static String sections;
	
	public static void setID(String input)
	{
		NET_ID=input;
	}
    private static final long serialVersionUID = 1L;
 
    /**
     * @see HttpServlet#HttpServlet()
     */
    public StudentServlet() 
    {
        super();
        // TODO Auto-generated constructor stub
    }
 
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
        try
        {	
        	System.out.println("In the Home Servlet");
        	
        	int formID = Integer.parseInt(request.getParameter("formID"));
        	
        	if(formID==5)
        	{
        		String spn = request.getParameter("spn_num");
        		String cid = request.getParameter("course_id");
        		String sect= request.getParameter("section");
        		
        		try
        		{
	        		int spn_parse = Integer.parseInt(spn);
	        		int cid_parse = Integer.parseInt(cid);
	        		int sect_parse = Integer.parseInt(sect);
	        		
	        		HomeDAO dao = new HomeDAO();
	        		
	        		// register SPN for course
	        		
	        		boolean check_spn = dao.registerWithSpn(spn_parse, cid_parse, sect_parse);
	        		
	        		if(check_spn)
	        		{
	        			response.sendRedirect("RegSuccess.jsp");
	        		}
	        		else
	        		{
	        			response.sendRedirect("RegFailed.jsp");
	        		}
        		}
        		catch(Exception e)
        		{
        			response.sendRedirect("RegFailed.jsp");
        			return;
        		}
        	}
        	if(formID==4)
        	{
        		// getting the status messages
        		
        		HomeDAO dao = new HomeDAO();
        		
        		ArrayList list=dao.getStatus(NET_ID);
        		
        		request.setAttribute("resList", list);
            	request.setAttribute("formAction", "lookupRequests");
            	
            	RequestDispatcher rd = request.getRequestDispatcher("/CheckStatus.jsp");
                rd.forward(request, response);
        	}
        	if(formID==3)
        	{
        		String courseid = request.getParameter("coursefield");
        		String section = request.getParameter("section");
        		String responsed = request.getParameter("required");
        		
        		int required =0;
        		
        		if(responsed==null)
        		{
        			required=0;
        		}
        		else
        		{
        			required=1;
        		}
        		
        		String comment = request.getParameter("comment");
        		
        		HomeDAO dao = new HomeDAO();
        		
        		boolean check =dao.processSPN(courseid, required, comment,section);
        		
        		if (check==true)
        		{
        			response.sendRedirect("ReqSuccess.jsp");
        		}
        		else
        		{
        			response.sendRedirect("Reqfail.jsp");
        		}
        	}
        	
        	if(formID==2)
        	{
        		// Handle course registration
        		
        		HomeDAO dao = new HomeDAO();
        		CourseBean course = new CourseBean();
        		
        		 course.setCourse_id((request.getParameter("course_id")));
        		 course.setSection((request.getParameter("section")));
        		 
        		 dao.registerCourse(course.getCourse_id(), course.getSection());
        		 
        		 if(CourseBean.registered==true)
        		 {
        			 System.out.println("Successfully enrolled "+NET_ID+" for "+course.getCourse_id());
        			 response.sendRedirect("RegSuccess.jsp");
        		 }
        		 else
        		 {
        			 if(dao.spec_permission==true)
        			 {
        				 courses_id=course.getCourse_id();
        				 sections=course.getSection();
        				 response.sendRedirect("Request.jsp");
        				 return;
        			 }
        			 if(dao.taken==false)
        			 {
        				 response.sendRedirect("PreReqFail.jsp");
        			 }
        			 else
        			 {
        				 response.sendRedirect("RegFailed.jsp");
        			 }
        		 }
        		
        		return;
        	}
       
        	// Look up Courses
        	
        	if(formID==1)
        	{
        	
	        	HomeDAO dao = new HomeDAO();
	        	
	        	ArrayList list = dao.lookupCourses(request.getParameter("semester"));
	        	
	        	request.setAttribute("resList", list);
	        	request.setAttribute("formAction", "lookupCourses");
	        	
	        	RequestDispatcher rd = request.getRequestDispatcher("/StudentHome.jsp");
	            rd.forward(request, response); 
        	}
        	
        } catch (Throwable exc)
        {
            System.out.println(exc);
        }
    }
 
    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
    	
    }
 
}
