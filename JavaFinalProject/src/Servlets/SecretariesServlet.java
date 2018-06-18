package Servlets;

import java.io.IOException;
import java.io.PrintWriter;
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

import java.util.Random; 
import java.security.*;

/**
 * Servlet implementation class SecretariesServlet
 */
@WebServlet("/SecretariesServlet")
public class SecretariesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SecretariesServlet() {
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
		// TODO Auto-generated method stub
	   response.setContentType("text/html; charset=UTF-8");
	   response.setCharacterEncoding("UTF-8");
	   request.setCharacterEncoding("UTF-8");	
	   HttpSession session = request.getSession();
	   synchronized(session) {
	   String requestType= request.getParameter("requestType");
		if (requestType == null) {
			createDynPage(response, "Invalid request type");
		}		
		if (requestType.equalsIgnoreCase("loginsecr")) {
			
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
							hash = rs.getString("hash");
							salt = rs.getString("salt");
							PreparedStatement ps1 = c.prepareStatement("SELECT id_secretaries FROM Secretaries WHERE id_secretaries =?");
							ps1.setString(1, rs.getString("id_users"));
							ResultSet rs1 = ps1.executeQuery();
							if (rs1.next()) { 
								//αν το login γίνει επιτυχώς redirect στην σελίδα 							
								
								if(HashCodeMaker.confirmpassword(pw, hash, salt)) {
									response.sendRedirect("selidagrammateias.html");								
									session.setAttribute("user_id",rs.getString("id_users"));
								}
								else {
									createDynPage(response, "Invalid password");
								}
							}
							else {
								createDynPage(response, "Ο χρήστης υπάρχει αλλα δεν πρόκειται για γραμματεα");
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
			
			
			//}
			
		} 
		else if (requestType.equalsIgnoreCase("anathesi")){
			//if this is about making a professor to teach a class
				String prof_id = request.getParameter("code");
				String course_id = request.getParameter("course");
				try {
					Class.forName("org.postgresql.Driver");
					// loads driver
					Connection c = DriverManager.getConnection(db_url,db_user,db_pass); // gets a new connection
					//if we are in the login page(there is no value for the username & password fields)
					if (prof_id != null && course_id != null) {
						PreparedStatement ps = c.prepareStatement("SELECT id_professors FROM Professors WHERE id_professors=?");
						ps.setString(1, prof_id);
						ResultSet rs = ps.executeQuery();
						if (!rs.next()) {//if there is no professor with this id
							createDynPage(response, "Κανένας καθηγητής με τα στοιχεία που δώσατε. Αποτυχία σύνδεσης!");
						}
						else {//if the professor exists, check if course exists
							PreparedStatement ps2 = c.prepareStatement("SELECT id_courses FROM Courses WHERE id_courses=?");
							ps2.setString(1, course_id);
							ResultSet rs2 = ps2.executeQuery();
							if (rs2.next()) {//if course exists then proceed
								PreparedStatement ps3 = c.prepareStatement("UPDATE Courses SET id_professors=? WHERE id_courses=?");
								ps3.setString(1, prof_id);
								ps3.setString(2, course_id);
								ps3.executeQuery();		
								String message = "Επιτυχής ανάθεση μαθήματος!";								
								createDynPage(response, message);
							}
							else {
								String message = "Δεν υπάρχει μάθημα με αυτό το ID!";								
								createDynPage(response, message);
							}
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
	     else if (requestType.equalsIgnoreCase("insertstud"))  {
	    	 String name=request.getParameter("name");
	    	 String surname =request.getParameter("surname");
			 String department = request.getParameter("department");
			 String username = request.getParameter("username");
			 String password = request.getParameter("password");
			 try {
				Class.forName("org.postgresql.Driver");
				// loads driver
				Connection c = DriverManager.getConnection(db_url,db_user,db_pass); // gets a new connection
				
				Random rand = new Random(); 
				long id_int;
				String new_id="";
				boolean flag=false;
				while (!flag) {
					id_int = 100000 + rand.nextInt(900000);//we only want max 6 digits
					new_id = "F" + String.valueOf(id_int);//f symbolizes foitites
					//now we check if there is another record with this number just in case
					PreparedStatement ps2 = c.prepareStatement("SELECT id_students FROM Students WHERE id_students=? ");
					ps2.setString(1, new_id);
					ResultSet rs2 = ps2.executeQuery();
					if (!rs2.next()) {//if a user with this id does not yet exist
						//create one
						PreparedStatement ps3 = c.prepareStatement("INSERT INTO Users VALUES (?,?,?,?,?,?,?)");
						ps3.setString(1, new_id);
						ps3.setString(2, name);
						ps3.setString(3, surname);
						ps3.setString(4, username);
						String[] hashedandsalted = HashCodeMaker.SHA512hashpass(password, HashCodeMaker.create_new_salt());						
						ps3.setString(5, hashedandsalted[0]);
						ps3.setString(6, hashedandsalted[1]);
						ps3.setString(7, department);
						ps3.executeUpdate();
						PreparedStatement ps4 = c.prepareStatement("INSERT INTO Students VALUES(?,?)");
						ps4.setString(1, new_id);
						ps4.setInt(2, 1);//student is registered in the first semester
						ps4.executeUpdate();
						flag = true;//and exit loop
						ps3.close();
						ps4.close();
					}
					ps2.close();
				}				
				//and now we check if the professor is succesfully registered
				PreparedStatement ps5 = c.prepareStatement("SELECT * FROM Students WHERE id_students=?");
				ps5.setString(1, new_id);
				ResultSet rs5 = ps5.executeQuery();
				if (rs5.next()) {
					createDynPage(response, "H καταχώρηση έγινε με επιτυχία του φοιτητή με νέο Α.Μ. "+ new_id);
				}
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
	     else if (requestType.equalsIgnoreCase("insertprof")) {
	    	 String name=request.getParameter("name");
	    	 String surname =request.getParameter("surname");
			 String department = request.getParameter("department");
			 String username = request.getParameter("username");
			 String password = request.getParameter("password");
			 String officenum = request.getParameter("officeNum");
			 int officeNum = Integer.parseInt(officenum);
			 String salaryy = request.getParameter("salary");
			 int salary = Integer.parseInt(salaryy);
			 try {
				Class.forName("org.postgresql.Driver");
				// loads driver
				Connection c = DriverManager.getConnection(db_url,db_user,db_pass); // gets a new connection

				Random rand = new Random(); 
				long id_int;
				String new_id="";
				
				boolean flag=false;
				while (!flag) {
					id_int = 100000 + rand.nextInt(900000);//we only want max 6 digits
					new_id = "K" + String.valueOf(id_int);//f symbolizes foitites
					//now we check if there is another record with this number just in case
					PreparedStatement ps2 = c.prepareStatement("SELECT id_professors FROM Professors WHERE id_professors=? ");
					ps2.setString(1, new_id);
					ResultSet rs2 = ps2.executeQuery();
					if (!rs2.next()) {//if a user with this id does not yet exist
						//create one
						PreparedStatement ps3 = c.prepareStatement("INSERT INTO Users VALUES (?,?,?,?,?,?,?)");
						ps3.setString(1, new_id);
						ps3.setString(2, name);
						ps3.setString(3, surname);
						ps3.setString(4, username);
						String[] hashedandsalted = HashCodeMaker.SHA512hashpass(password, HashCodeMaker.create_new_salt());						
						ps3.setString(5, hashedandsalted[0]);
						ps3.setString(6, hashedandsalted[1]);
						ps3.setString(7, department);
						ps3.executeUpdate();
						PreparedStatement ps4 = c.prepareStatement("INSERT INTO Professors VALUES(?,?,?)");
						ps4.setString(1, new_id);
						ps4.setInt(2, officeNum);
						ps4.setInt(3, salary);
						ps4.executeUpdate();
						flag = true;//and exit loop
						ps3.close();
						ps4.close();
					}
					ps2.close();
				}					
				
				//and now we check if the professor is succesfully registered
				PreparedStatement ps5 = c.prepareStatement("SELECT * FROM Professors WHERE id_professors=?");
				ps5.setString(1, new_id);
				ResultSet rs5 = ps5.executeQuery();
				if (rs5.next()) {
					createDynPage(response, "H καταχώρηση έγινε με επιτυχία του καθηγητή με νέο Α.Μ. "+ new_id);
				}
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
	     } else if (requestType.equalsIgnoreCase("insertsecr")) {
	    	 String name=request.getParameter("name");
	    	 String surname =request.getParameter("surname");
			 String department = request.getParameter("department");
			 String username = request.getParameter("username");
			 String password = request.getParameter("password");
			 String officenum = request.getParameter("officeNum");
			 int officeNum = Integer.parseInt(officenum);
			 String salaryy = request.getParameter("salary");
			 int salary = Integer.parseInt(salaryy);
			 try {
				Class.forName("org.postgresql.Driver");
				// loads driver
				Connection c = DriverManager.getConnection(db_url,db_user,db_pass); // gets a new connection

				Random rand = new Random(); 
				long id_int;
				String new_id="";
				boolean flag=false;
				while (!flag) {
					id_int = 100000 + rand.nextInt(900000);//we only want max 6 digits
					new_id = "K" + String.valueOf(id_int);//f symbolizes foitites
					//now we check if there is another record with this number just in case
					PreparedStatement ps2 = c.prepareStatement("SELECT id_secretaries FROM Secretaries WHERE id_secretaries=? ");
					ps2.setString(1, new_id);
					ResultSet rs2 = ps2.executeQuery();
					if (!rs2.next()) {//if a user with this id does not yet exist
						//create one
						PreparedStatement ps3 = c.prepareStatement("INSERT INTO Users VALUES (?,?,?,?,?,?,?)");
						ps3.setString(1, new_id);
						ps3.setString(2, name);
						ps3.setString(3, surname);
						ps3.setString(4, username);
						String[] hashedandsalted = HashCodeMaker.SHA512hashpass(password, HashCodeMaker.create_new_salt());						
						ps3.setString(5, hashedandsalted[0]);
						ps3.setString(6, hashedandsalted[1]);
						ps3.setString(7, department);
						ps3.executeUpdate();
						PreparedStatement ps4 = c.prepareStatement("INSERT INTO Secretaries VALUES(?,?,?)");
						ps4.setString(1, new_id);
						ps4.setInt(2, officeNum);
						ps4.setInt(3, salary);
						ps4.executeUpdate();
						flag = true;//and exit loop
						ps3.close();
						ps4.close();
					}
					ps2.close();
				}					
				
				//and now we check if the secretary is succesfully registered
				PreparedStatement ps5 = c.prepareStatement("SELECT * FROM Secretaries WHERE id_secretaries=?");
				ps5.setString(1, new_id);
				ResultSet rs5 = ps5.executeQuery();
				if (rs5.next()) {
					createDynPage(response, "H καταχώρηση έγινε με επιτυχία του γραμματεα με νέο Α.Μ. "+ new_id);
				}
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
	     } else if (requestType.equalsIgnoreCase("viewall")){
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
			    try {
			    	PreparedStatement ps = conn.prepareStatement("SELECT * FROM Courses;");
			    	ResultSet rs = ps.executeQuery();
			    	if (rs.isBeforeFirst()) {
			    		out.println("<html>");
				    	out.println("<head><title>Σύνολο μαθημάτων</title></head>");
				    	out.println("<body>");
			            out.println("<tr>");
			            out.println("<table border=\"1\">");
					    out.println("<tr>");
					    out.println("<th>Όνομα Μαθήματος</th>");
					    out.println("<th>ID Μαθήματος</th>");
					    out.println("<th>Τμήμα</th>");	
					    out.println("<th>Εξάμηνο</th>");
					    out.println("<th>ID Καθηγητή</th>");
					    out.println("</tr>");
			    	}
			    	else {
			    		createDynPage(response,"Καμία καταχώρηση μαθήματος ακόμη!");
			    	}		    	
			    	while(rs.next()) {		    		
			    		out.println("<tr><td>" + rs.getString("id_courses") + "</td><td>" +  rs.getString("course_name") + "</td><td>" + rs.getString("dept_name") + "</td><td>" +
			    		Integer.toString( rs.getInt("semester")) + "</td><td>" + rs.getString("id_professors") + "</td>\n</tr>");		    	
			    	}
			    	rs.close();
			    	out.println("<a href='selidagrammateias'>Επιστροφή στην αρχική σελίδα γραμματείας</a> ");
			    	out.println("</table>");
			    	out.println("</body>");
			    	out.println("</html>");
			    }
			    catch (Exception e) {
			    	e.printStackTrace();
			    }			
			    try {
			    	conn.close();
			    } 
			    catch (SQLException e) {
			    	e.printStackTrace();
			    }
		    }	
	   }//closing aynchronised session
   }
	
	private void createDynPage(HttpServletResponse response, String message) throws IOException {
		response.setContentType("text/html; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.println("<html>");
		out.println("<head><title>Εισαγωγή στοιχείων</title></head>");
		out.println("<body>");
		out.println("<p>" + message + "</p>");
		out.println("<a href=\"/JavaFinalProject/selidagrammateias.html\">Επιστροφή στην αρχική σελίδα</a>");
		out.println("</body></html>");
	}
}

