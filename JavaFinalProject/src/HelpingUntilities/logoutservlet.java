package HelpingUntilities;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;



/**
 * Servlet implementation class logoutservlet
 */
@WebServlet("/logoutservlet")
public class logoutservlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public logoutservlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/html");           
                         
    	Cookie loginCookie = null;
    	Cookie[] cookies = request.getCookies();
    	if(cookies != null){
    	for(Cookie cookie : cookies){
    		if(cookie.getName().equals("user_id")){
    			loginCookie = cookie;
    			break;
    		}
    	}
    	}
    	if(loginCookie != null){
    		loginCookie.setMaxAge(0);//to cookie epomenws tha diagrafei se 0 deyterolepta, dhladh twra pou ginetai to logout
        	response.addCookie(loginCookie);
    	}
    	HttpSession session=request.getSession();  
        session.invalidate();  
    	RequestDispatcher view = request.getRequestDispatcher("index.html");
        view.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
