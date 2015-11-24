package registration;
 
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
 
/**
 * Servlet implementation class LoginServlet
 */
public class ProfessorRegServlet extends HttpServlet 
{
    private static final long serialVersionUID = 1L;
 
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ProfessorRegServlet() 
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
            System.out.println("In the Registration Servlet");
            
            RegBean user = new RegBean();
            
            user.setRUID(request.getParameter("RU_ID"));
            user.setNet_id(request.getParameter("NET_ID"));
            user.setPassword(request.getParameter("PASSWORD"));
            user.setFirstName(request.getParameter("FIRSTNAME"));
            user.setLastName(request.getParameter("LASTNAME"));
            user.setGrad_year(request.getParameter("DEPARTMENT"));
            
            user = RegDAO.registerProfessor(user);
            
            if(user.isAdded())
            {
                HttpSession session = request.getSession(true);
                session.setAttribute("currentSessionUser",user);
                response.sendRedirect("RegSuccess.jsp");
            }
            
            else
            {
                response.sendRedirect("RegFailed.jsp");
            }
            
        } 
        
        catch (Throwable exc)
        {
            System.out.println(exc);
        }
    }
 
    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
    }
 
}
