package Servlets;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import HelpingUntilities.HashCodeMaker;

/**
 * Servlet implementation class ProfessorsServlet
 */
@WebServlet("/ProfessorsServlet")
public class ProfessorsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ProfessorsServlet() {
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
		// TODO Auto-generated method stub
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
		
		if (requestType.equalsIgnoreCase("loginprof")){
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
							String professor_id = rs.getString("id_users");
							PreparedStatement ps1 = c.prepareStatement("SELECT id_professors FROM Professors WHERE id_professors =?");
							ps1.setString(1, professor_id);
							ResultSet rs1 = ps1.executeQuery();
							if (rs1.next()) { 
								//αν το login γίνει επιτυχώς redirect στην σελίδα μαθητών
								hash = rs.getString("hash");
								salt = rs.getString("salt");
								if(HashCodeMaker.confirmpassword(pw, hash, salt)) {
									response.sendRedirect("selidakathigitwn.html");
									session.setAttribute("id_user", professor_id);
								}
								else {
									createDynPage(response, "Invalid password!");
								}
								
							}
							else {
								createDynPage(response, "Ο χρήστης υπάρχει αλλα δεν πρόκειται για καθηγητή");
							}
						}
						else {
							createDynPage(response, "Καμία data entry με τα στοιχέια που δώσατε,δοκιμάστε ξανα");
						}
						..3
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
		  } else if (requestType.equalsIgnoreCase("submitgrades")) {//kataxwrhshvathmologias
			  String course_ID=request.getParameter("course");
				String gradee =request.getParameter("grade");
				double grade = Double.parseDouble(gradee);
				String student_id = request.getParameter("id_stud");
					try {
						Class.forName("org.postgresql.Driver");
						// loads driver
						Connection c = DriverManager.getConnection(db_url,db_user,db_pass); // gets a new connection
						boolean flag = true;
						//check no.1 
						PreparedStatement ps = c.prepareStatement("SELECT id_students FROM Students WHERE id_students=?");
						ps.setString(1, student_id);
						ResultSet rs = ps.executeQuery();
						
						if (!rs.next()) {//if there is no student with this id
							createDynPage(response, "Κανένας μαθητής με τα στοιχεία που δώσατε. Αποτυχία σύνδεσης!");
							flag = false;
						}
						//check no.2
						PreparedStatement ps0 = c.prepareStatement("SELECT id_courses FROM Courses WHERE id_courses=? AND id_professors=?");
						ps0.setString(1, course_ID);
						ps0.setString(2,  (String)session.getAttribute("id_user"));
						Res-----------------------------------------------------------------------------------------------------ultSet rs0 = ps0.executeQuery();
						
						if (!rs0.next()) {//if there is no lecture with this id
							createDynPage(response, "Κανένα μάθημα που να αντιστοιχεί στον συγκεκριμένο καθηγητή. Αποτυχία σύνδεσης!");
							flag = false;
						}
						
						//check no.3
						PreparedStatement ps1 = c.prepareStatement("SELECT * FROM Grades WHERE id_students=?");
						ps1.setString(1, student_id);
						ResultSet rs1 = ps1.executeQuery();
						
						if (rs1.next()) {//if there is an already existing grade
							PreparedStatement ps3= c.prepareStatement("DELETE FROM Grades WHERE id_students=?");
							ps3.setString(1, student_id);
							ps3.execute();
						}
						
						if (flag == true){//Proceed to the assignment of the lecture			
							PreparedStatement ps2 = c.prepareStatement("INSERT INTO Grades VALUES (?,?,?)");
							ps2.setString(1, course_ID);
							ps2.setString(2, student_id);
							ps2.setDouble(3,  grade);
							ps2.executeUpdate();
							createDynPage(response, "Επιτυχής ανάθεση μαθήματος!");
							ps2.close();
						}
						ps.close();
						
						try {
							c.close();
						}
						catch (Exception e) {
							System.out.println("Connection failed to close!");
							e.printStackTrace();
						}
					}
					catch (ClassNotFoundException |SQLException e )  {
						e.printStackTrace();
					}
		  }
		  else if (requestType.equalsIgnoreCase("viewgrades")){
				//first load the driver
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
				out.println("<head><title>Βαθμολογία</title></head>");
				out.println("<body>");
				out.println("<h1>Λίστα βαθμολογιών</h1>");
				out.println("<table border = '1'>");
				try {			
					PreparedStatement ps1 = conn.prepareStatement("SELECT id_courses, course_name FROM Courses WHERE id_professors =?");
					ps1.setString(1, (String)session.getAttribute("id_user"));
					ResultSet rs = ps1.executeQuery();
					boolean flag1=false;
					boolean flag2=false;
					PreparedStatement ps2;
					ResultSet rs2;
					while (rs.next()) {
						flag1=true;
						out.println("<tr>\n<td colspan=2>Μάθημα: "+rs.getString("course_name")+" με id "+rs.getString("id_courses")+"</td>\n</tr>");
			            ps2 = conn.prepareStatement("SELECT id_students, grade_num FROM Grades WHERE id_courses=?");
						ps2.setString(1, rs.getString("id_courses"));
			            rs2 = ps2.executeQuery();
			            out.println("<tr>\n<th>Α.Μ. ΜΑΘΗΤΗ</th><th>ΒΑΘΜΟΣ</th>\n</tr>");//we want this to be printed only 1 time
			            while (rs2.next()) {
			               flag2=true;
		            	   out.println("<tr>\n<td>"+rs2.getString("id_students")+"</td>\n<td>"+rs2.getDouble("grade_num")+"</td>\n</tr>");
		            	}          
					}
					if(!flag1) {
						 out.println("<p>Κανένα μάθημα καταχωρημένο στον καθηγητή!</p>");						 
					}
					if(!flag2 && flag1) {
						out.println("<tr>\n<td> Κανένας βαθμολογημένος </td>\n<td> - <\td>\n<\tr>");
					}
		            out.println("</table>");
		            out.println("<a href='selidakathigitwn.html'>Επιστροφή στην σελίδα καθηγητή</a>");
		            out.println("</body></html>"); 
							
					rs.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}			
				

				try {
					conn.close();
				} 
				catch (SQLException e) {
					//  
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
		out.println("<head><title>Εισαγωγή στοιχείων</title></head>");
		out.println("<body>");
		out.println("<p>" + message + "</p>");
		out.println("<a href='selidakathigitwn.html'>Επιστροφή στην αρχική σελίδα</a>");
		out.println("</body></html>");
	}
}
