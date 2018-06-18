package Servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import HelpingUntilities.HashCodeMaker;


/**
 * Servlet implementation class StudentsServlet
 */
@WebServlet("/StudentsServlet")
public class StudentsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public StudentsServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    
    String db_user="postgres";
    String db_pass="theodora";
    String db_url="jdbc:postgresql://localhost:5432/JavaProject";
     
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());	
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	  response.setContentType("text/html; charset=UTF-8");
	  response.setCharacterEncoding("UTF-8");
	  request.setCharacterEncoding("UTF-8");	
      String requestType= request.getParameter("requestType");
	  HttpSession session = request.getSession();
	  synchronized(session) {
		if (requestType == null) {
			createDynPage(response, "Invalid request type");
		}
		if (requestType.equalsIgnoreCase("loginstud")){
				String un=request.getParameter("username");
				String pw=request.getParameter("password");
				String hash = null;
				String salt = null;
				try {
					Class.forName("org.postgresql.Driver");
					// loads driver
					Connection c = null;
					try {
						c = DriverManager.getConnection(db_url,db_user,db_pass); // gets a new connection
					} 
					catch (SQLException e) {
						System.out.println("Connection Failed!");
						e.printStackTrace();
						return;
					}	
					//if we are in the login page(there is no value for the username & password fields)
					if (un != null && pw != null) {
						PreparedStatement ps = c.prepareStatement("SELECT id_users,hash,salt FROM Users WHERE username=? ");
						ps.setString(1, un);
						ResultSet rs = ps.executeQuery();
						if (rs.next())  {
							PreparedStatement ps1 = c.prepareStatement("SELECT id_students FROM Students WHERE id_students =?");
							ps1.setString(1, rs.getString("id_users"));
							ResultSet rs1 = ps1.executeQuery();
							if (rs1.next()) { 
								//αν το login γίνει επιτυχώς redirect στην σελίδα μαθητών
								hash = rs.getString("hash");
								salt = rs.getString("salt");
								if(HashCodeMaker.confirmpassword(pw, hash, salt)) {
									response.sendRedirect("selidafoithtwn.html");
									session.setAttribute("user_id",rs.getString("id_users"));
								}
								else {
									createDynPage(response,"Invalid password");
								}
								
							}
							else {
								createDynPage(response, "Ο χρήστης υπάρχει αλλα δεν πρόκειται για μαθητή");
							}
						}
						else {
							createDynPage(response, "Καμία data entry με τα στοιχέια που δώσατε,δοκιμάστε ξανα");
						}
						ps.close();
					}
					try {
						c.close();
					}
					catch (Exception e) {
						System.out.println("Connection failed to close!");
						e.printStackTrace();
					}
				}
				catch (ClassNotFoundException | SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		} else if (requestType.equalsIgnoreCase("viewlecture")) {
			String lesson_id=request.getParameter("lesson");
			try {
				Class.forName("org.postgresql.Driver");
				// loads driver
				Connection c = null;
				try {
					c = DriverManager.getConnection(db_url,db_user,db_pass); // gets a new connection
				} 
				catch (SQLException e) {
					System.out.println("Connection Failed!");
					e.printStackTrace();
					return;
				}	
				//if we are in the login page(there is no value for the username & password fields)
				if (lesson_id != null) {
					PreparedStatement ps = c.prepareStatement("SELECT grade_num FROM Grades WHERE id_courses=? and id_students=?");
					ps.setString(1, lesson_id);
					ps.setString(2, (String)session.getAttribute("user_id"));
					ResultSet rs = ps.executeQuery();
					if (rs.next())  {
						double grade = rs.getDouble("grade_num");
						createDynPage(response,"Ο βαθμός του φοιτητή με α.μ. "+(String)session.getAttribute("user_id")+" στο μάθημα με id "+lesson_id+" είναι "+ String.valueOf(grade) +"!");
					}
					else {
						createDynPage(response, "Δεν βρέθηκε καταχωρημένος βαθμός στον συγκεκριμένο φοιτητή για αυτό το μάθημα!");
					}
					ps.close();
				}
				try {
					c.close();
				}
				catch (Exception e) {
					System.out.println("Connection failed to close!");
					e.printStackTrace();
				}
			}
			catch (ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		else if (requestType.equalsIgnoreCase("viewsem")) {
			String sem=request.getParameter("sem");
			int semester =Integer.parseInt(sem);
			try {
				Class.forName("org.postgresql.Driver");
				// loads driver
				Connection c = null;
				try {
					c = DriverManager.getConnection(db_url,db_user,db_pass); // gets a new connection
				} 
				catch (SQLException e) {
					System.out.println("Connection Failed!");
					e.printStackTrace();
					return;
				}	
				PrintWriter out = response.getWriter();		
				
					PreparedStatement ps = c.prepareStatement("SELECT id_courses, grade_num FROM Grades WHERE grade_num IS NOT NULL and id_students=?");		
					ps.setString(1, (String)session.getAttribute("user_id")); 
					ResultSet rs = ps.executeQuery();
					String course_id;
					double gradee;
					//we make a list with the course id that the student has	
					if (rs.isBeforeFirst()) {
						out.println("<html>");
						out.println("<head><title></title></head>");
						out.println("<body>");
						out.println("<h1>Λίστα βαθμολογιών εξαμήνου "+ Integer.toString(semester) +" </h1>");
						out.println("<table border = '1'>");
						out.println("<tr>\n<th>ID ΜΑΘΗΜΑΤΟΣ</th>\n<th>ΒΑΘΜΟΣ</th></tr>");					
						PreparedStatement ps2;
						ResultSet rs2;												
						while (rs.next()) {						
							course_id = rs.getString("id_courses");	//everytime stores the next string of the resultset
							gradee = rs.getDouble("grade_num");
							ps2 = c.prepareStatement("SELECT semester FROM Courses WHERE id_courses=?");
							ps2.setString(1,course_id);
							rs2 = ps2.executeQuery();
							if(rs2.next()) {
								if (semester == rs2.getInt("semester")) {								
									out.println("<tr>\n<td>"+ course_id+"</td>\n<td>"+Double.toString(gradee)+"</td>\n</tr>");
								}
								else {
									out.println("<tr>\n<td colspan=2>Κανένας βαθμός για αυτό το εξάμηνο</td>\n</tr>");
								}
								rs2.close();
							}
					    }

					rs.close();			           					
				    }
					else {
						out.println("<p>Κανένας βαθμός καταχωρημένος για τον συγκεκριμένο μαθητή.</p>");
					}
					 out.println("</table>");
			         out.println("<a href='selidafoithtwn.html'>Επιστροφή στην αρχική σελίδα</a>");
			         out.println("</body></html>");  
				try {
					c.close();
				}
				catch (Exception e) {
					System.out.println("Connection failed to close!");
					e.printStackTrace();
				}
			}
			catch (ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		} else if (requestType.equalsIgnoreCase("viewtotal")) {
			try {
				Class.forName("org.postgresql.Driver");
				System.out.println("PostgreSQL JDBC Driver Registered!");
			} 
			catch (ClassNotFoundException e) {

				System.out.println("No correct path of the Driver (database connector)");
				e.printStackTrace();
			}

			//then load the connection
			Connection conn = null;
			try {
				conn = DriverManager.getConnection(db_url,db_user,db_pass);
			} 
			catch (SQLException e) {
				System.out.println("Connection Failed!");
				e.printStackTrace();
				return;
			}		
			PrintWriter out = response.getWriter();		
			out.println("<html>");
			out.println("<head><title></title></head>");
			out.println("<body>");
			out.println("<h1Λίστα βαθμολογιών</h1>");
			try {			
				PreparedStatement ps1 = conn.prepareStatement("SELECT id_courses, grade_num FROM Grades WHERE id_students =? ;");
				ps1.setString(1, (String)session.getAttribute("user_id"));
				ResultSet rs = ps1.executeQuery();
				boolean flag=false;
				out.println("<table border = '1'>");
				out.println("<tr>\n<th>ΙD ΜΑΘΗΜΑΤΟΣ</th>\n<th>ΒΑΘΜΟΣ</th>\n</tr>");
				while(rs.next()) {
					flag=true;									
					out.println("<tr>\n<td>"+ rs.getString("id_courses") +"</td>\n<td>"+Double.toString(rs.getDouble("grade_num"))+"</td>\n</tr>");										
				}
				ps1.close();
				if (!flag) {//an o mathitis den einai eggegramenos se kanena mathima
					out.println("<tr>\n<td colspan=3>Καμία εγγραφή μαθήματος διαθέσιμη!</td>\n</tr>");				
				}
	            out.println("</table>");
	            out.println("<a href='selidafoithtwn.html'>Επιστροφή στην αρχική σελίδα</a>");
	            out.println("</body></html>");  
			}
			catch (Exception e) {
				e.printStackTrace();
			}		
			try {
				conn.close();
			} 
			catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	  }//end of synchronised session
	}
	
	private void createDynPage(HttpServletResponse response, String message) throws IOException {
		response.setContentType("text/html; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.println("<html>");
		out.println("<head><title>Εισαγωγή στοιχείων </title></head>");
		out.println("<body>");
		out.println("<p>" + message + "</p>");
		out.println("<a href='index.html'>Επιστροφη στην αρχική σελίδα</a>");
		out.println("</body></html>");
	}

}
