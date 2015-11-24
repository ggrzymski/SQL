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
public class ProfessorServlet extends HttpServlet 
{
	
	public static String NET_ID;
	
	public static int courseid;
	
	public static int sect;
	
	ArrayList<String> waiting_list = new ArrayList<String>();
	
	public static void setID(String input)
	{
		NET_ID=input;
	}
    private static final long serialVersionUID = 1L;
 
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ProfessorServlet() 
    {
        super();
        // TODO Auto-generated constructor stub
    }
 
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {	
    	System.out.println("In the Home Servlet");
    	
    	int formID = Integer.parseInt(request.getParameter("formID"));
    	
    	if(formID==9)
    	{
    		// Printing out the list of approved SPNs by this Instructor
    		
    		HomeDAO dao = new HomeDAO();
    		
    		ArrayList list=dao.getAssigned(NET_ID);
    		
    		request.setAttribute("resList", list);
        	request.setAttribute("formAction", "lookupSPN");
        	
        	RequestDispatcher rd = request.getRequestDispatcher("/SpnList.jsp");
            rd.forward(request, response);
    	}
    	if(formID==8)
    	{
    		// Need to assign SPN # to highest rated one
    		
    		HomeDAO dao = new HomeDAO();
    		boolean check_spn =dao.assignSPN(courseid, sect);
    		
    		if(check_spn)
    		{
    			response.sendRedirect("AssignSpnGood.jsp");
    		}
    		else
    		{
    			response.sendRedirect("AssignSpnBad.jsp");
    		}
    	}
    	
    	if(formID==7)
    	{
    		// Sending a comment into the SPN request
    		
    		String net_id = request.getParameter("net_id");
    		String comment = request.getParameter("comment");
    		
    		HomeDAO dao = new HomeDAO();
    		
    		dao.sendComment(net_id, comment);
    		
    		response.sendRedirect("CommentSent.jsp");
    	}
    	
    	if(formID==6)
    	{
    		// Just pull up the courses
    		
    		HomeDAO dao = new HomeDAO();
    		
    		ArrayList list=dao.getCourses(request.getParameter("semester"));
    		
    		request.setAttribute("resList", list);
        	request.setAttribute("formAction", "lookupCourses");
        	
        	RequestDispatcher rd = request.getRequestDispatcher("/CoursePage.jsp");
            rd.forward(request, response);
    		
    	}
    	if(formID==5)
    	{
    		// He entered a course to look at waiting list
    		
    		String course_id = request.getParameter("course_id");
    		String section = request.getParameter("section");
    		
    		try
    		{
	    		courseid = Integer.parseInt(course_id);
	    		sect = Integer.parseInt(section);
	    		
	    		HomeDAO dao = new HomeDAO();
	    		
	    		boolean check_input = dao.confirmInfo(courseid, sect);
	    		
	    		if(check_input==false)
	    		{
	    			response.sendRedirect("StatusDenied.jsp");
	    			return;
	    		}
	    		
	    		ArrayList list = dao.getEnrolled(course_id, section);
	    		ArrayList lists = dao.getPrereq(course_id);
	    		
	    		request.setAttribute("resList", list);
	        	request.setAttribute("formAction", "lookupEnrolled");
	        	
	        	request.setAttribute("resLists", lists);
	        	request.setAttribute("formActions", "lookupPrereq");
	        	
	        	RequestDispatcher rd = request.getRequestDispatcher("/WaitingList.jsp");
	            rd.forward(request, response);
    		}
    		catch(Exception e)
    		{
    			System.out.println("Entered wrong values for course and section");
    			response.sendRedirect("StatusDenied.jsp");
    			return;
    		}
    	}
    	if(formID==4)
    	{
    		// redirect the person to course page
    		
    		response.sendRedirect("CoursePage.jsp");
    	}
    	
    	if(formID==3)
    	{
    		// Maintain up-to-date waiting list
    		
    		HomeDAO dao = new HomeDAO();
    		
    		dao.maintainList(waiting_list);
    	}
    	
    	if(formID==2)
    	{
    		// Add a Prerequisite. I don't recommend names because the user can easily screw it up.
    		
    		HomeDAO dao = new HomeDAO();
    		
    		String prereq = request.getParameter("pid");
    		String course_id = request.getParameter("cid");
    		
    		boolean check = dao.addPrereq(prereq, course_id);
    		
    		if(check==false)
    		{
    			response.sendRedirect("AddPreReqFail.jsp");
    			return;
    		}
    		else
    		{
    			response.sendRedirect("AddPreReqSuccess.jsp");
    		}
    	}
    	
    	if(formID==1)
    	{
    		// Add a Course
    		
    		HomeDAO dao = new HomeDAO();
    		
    		String course_id = request.getParameter("cid");
    		String course_name = request.getParameter("cname");
    		String section = request.getParameter("section");
    		String building = request.getParameter("building");
    		String roomnum = request.getParameter("roomnum");
    		String roomsize = request.getParameter("roomsize");
    		
    		boolean check = dao.addCourse(course_id,section, course_name, building, roomnum, roomsize);
    		
    		if(check==true)
    		{
    			response.sendRedirect("CourseSuccess.jsp");
    		}
    		else
    		{
    			response.sendRedirect("CourseFail.jsp");
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
