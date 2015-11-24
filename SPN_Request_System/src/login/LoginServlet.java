package login;
 
import home.CourseBean;
import home.ProfessorServlet;
import home.StudentServlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
 
/**
 * Servlet implementation class LoginServlet
 */
public class LoginServlet extends HttpServlet 
{
    private static final long serialVersionUID = 1L;
 
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() 
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
        	System.out.println("In the Login Servlet");
            LoginBean user = new LoginBean();
            user.setUserName(request.getParameter("NET_ID"));
            user.setPassword(request.getParameter("PASSWORD"));
            user = LoginDAO.login(user);
            
            if(user.getUsername().compareTo("admin")==0)
            {
            	response.sendRedirect("AdminPage.jsp");
            	return;
            }
            
            if(user.isValid())
            {
                HttpSession session = request.getSession(true);
                session.setAttribute("currentSessionUser",user);
                
                // need to differentiate between student and professor 
                 
                if(user.isStudent)
                {
                	StudentServlet.setID(user.getUsername());
                	response.sendRedirect("StudentHome.jsp");
                }
                else
                {
                	ProfessorServlet.setID(user.getUsername());
                	response.sendRedirect("ProfessorHome.jsp");
                }
            }
            else
            {
                response.sendRedirect("LoginFailed.jsp");
            }
        } 
        catch (Exception e)
        {
            System.out.println("Couldn't log in properly");
        }
    }
 
    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
    	
    }
 
}
