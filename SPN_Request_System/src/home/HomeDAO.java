package home;
 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import login.ConnectionManager;
 
public class HomeDAO
{
	static Connection currentCon = null;
	static ResultSet rs = null;
	ArrayList<CourseBean> list = new ArrayList<CourseBean>();
	ArrayList<RequestBean> lists = new ArrayList<RequestBean>();
	ArrayList<SpnBean> listss = new ArrayList<SpnBean>();
	public boolean taken =false;
	public boolean spec_permission=false;
	
	public boolean confirmInfo(int cit, int sid)
	{
		Statement stmt = null;
		
		try
		{
			currentCon = ConnectionManager.getConnection();
			stmt=currentCon.createStatement();
			
			String query = "SELECT * FROM COURSE WHERE COURSE_ID="+cit+" AND SECTION="+sid+" AND NET_ID='"+ProfessorServlet.NET_ID+"'";
			rs=stmt.executeQuery(query);
			
			if(rs.next())
			{
				return true;
			}
		}
		catch(Exception e)
		{
			System.out.println("Invalid course id or section number");
		}
		
		return false;
	}
	
	public boolean registerWithSpn(int spn, int cid, int sect)
	{
		// Make sure he is using the right SPN
		
		Statement stmt = null;
		
		try
		{
			currentCon = ConnectionManager.getConnection();
			stmt=currentCon.createStatement();
			
			String query = "SELECT * FROM SPN WHERE SPN_ID="+spn+" AND COURSE_ID="+cid+" AND SECTION="+sect;
			rs=stmt.executeQuery(query);
			
			if(rs.next())
			{
				// You verified the information, now sign up
				
				String insert = "insert into ENROLLED(net_id, section, course_id, semester, year_date,grade) values(?, ?, ?,?,?,?)";

				PreparedStatement pstmt_two = null;
				
			    pstmt_two = currentCon.prepareStatement(insert); 	
			    
			    pstmt_two.setString(1, StudentServlet.NET_ID); // set input parameter 1
			    pstmt_two.setInt(2, sect); // set input parameter 2
			    pstmt_two.setInt(3, cid); // set input parameter 3
			    pstmt_two.setString(4, "FALL");
			    pstmt_two.setString(5, "2014");
			    pstmt_two.setString(6, null);
			    
			    pstmt_two.executeUpdate();
			    taken=true;
				CourseBean.registered=true;
				
				// Need to Remove SPN From the list
				
				String remove_spn = "DELETE FROM SPN WHERE SPN_ID="+spn;
				stmt.executeUpdate(remove_spn);
				
				// Need to remove from SPN Request list as well
				
				String remove_spnrequest = "DELETE FROM SPNREQUEST WHERE COURSE_ID="+cid+" AND SECTION="+sect+" AND NET_ID='"+StudentServlet.NET_ID+"'";
				stmt.executeUpdate(remove_spnrequest);
				
				return true;
			}
			
			return false;
		}
		catch(Exception e)
		{
			System.out.println("Couldn't sign up for course with SPN");
			return false;
		}
		 
	}
	public ArrayList<SpnBean> getAssigned(String prof_id)
	{
		Statement stmt = null;
		
		try
		{
			currentCon = ConnectionManager.getConnection();
			stmt=currentCon.createStatement();
			
			String find_assigned = "SELECT * FROM SPN WHERE PROF_ID='"+prof_id+"'";
			rs = stmt.executeQuery(find_assigned);
			
			while(rs.next())
			{
				String courseid = rs.getString(5);
				String net_id = rs.getString(2);
				String profid = rs.getString(3);
				String section = rs.getString(6);
				String spn_id = rs.getString(1);
				
				SpnBean bean = new SpnBean();
				bean.setCourse_id(courseid);
				bean.setNet_id(net_id);
				bean.setProf_id(profid);
				bean.setSection(section);
				bean.setSpn_id(spn_id);
				
				listss.add(bean);
			}
		}
		catch(Exception e)
		{
			System.out.println("Couldn't retrieve status messages");
			return null;
		}
		
		return listss;
	}
	
	public boolean assignSPN(int cid, int sect)
	{
		// Get the highest rated one from the waiting list.
		
		Statement stmt = null;
		
		try
		{
			currentCon = ConnectionManager.getConnection();
			stmt=currentCon.createStatement();
			
			String sql = "SELECT * FROM SPNREQUEST WHERE COURSE_ID="+cid+" AND SECTION="+sect;
			rs=stmt.executeQuery(sql);
			
			while(rs.next())
			{
				RequestBean bean = new RequestBean();
				
				String coursey = Integer.toString(cid);
				
				bean.setCourse_id(coursey);
				bean.setSp_request_id(rs.getString("sp_request_id"));
				bean.setNet_id(rs.getString("net_id"));
				bean.setRequired(rs.getString("required"));
				bean.setNote_req(rs.getString("note_req"));
				bean.setTime_stamp(rs.getString("time_stamp"));
				bean.setStatus(rs.getString("status"));
				bean.setSection(sect);
				bean.setRating(rs.getFloat("rating"));
				
				lists.add(bean);
			}
			
			// now pluck out the bean with the highest rating
			
			float max=0;
			
			ArrayList<String> track_netid= new ArrayList<String>();
			
			for(int i=0; i<lists.size();i++)
			{
				RequestBean temp = lists.get(i);
				
				if(temp.getRating()>max)
				{
					track_netid.add(temp.getNet_id());
					max=temp.getRating();
				}
			}
			
			// update the status and assign spn
			
			String update_status = "SELECT * FROM SPNREQUEST WHERE COURSE_ID="+cid+" AND SECTION="+sect+" AND RATING="+max;
			rs=stmt.executeQuery(sql);
			
			if(rs.next())
			{
				String net_id = track_netid.get(track_netid.size()-1);
				float user_rating = max;
				
				// check to see if you have enough to give out
				
				String check_amount = "SELECT SPN_AMOUNT FROM COURSE WHERE COURSE_ID="+cid+" AND SECTION="+sect;
			    rs=stmt.executeQuery(check_amount);
			    
			    if(rs.next())
			    {
			    	int checkers = rs.getInt(1);
			    	
			    	if(checkers<1) // Denied Message
			    	{
			    		String denied="DENIED";
					    String query_status = "UPDATE SPNREQUEST SET STATUS='"+denied+"' WHERE COURSE_ID="+cid+" AND SECTION="+sect+" AND NET_ID='"+net_id+"'";
						stmt.executeUpdate(query_status);
			    		return false;
			    	}
			    }
				
				// Create a new SPN
				
				String insert_spn = "insert into SPN(net_id, course_id, section, prof_id) values(?,?,?,?)";
				    
			    PreparedStatement pstmt_one = null;
			    pstmt_one = currentCon.prepareStatement(insert_spn); 
			    
			    pstmt_one.setString(1, net_id);
			    pstmt_one.setInt(2, cid);
			    pstmt_one.setInt(3, sect);
			    pstmt_one.setString(4, ProfessorServlet.NET_ID);
			    
			    pstmt_one.executeUpdate();
			    
			    // Deecrement the SPN queue by 1
			    
			    String decrement_num = "SELECT SPN_AMOUNT FROM COURSE WHERE COURSE_ID="+cid+" AND SECTION="+sect;
			    rs=stmt.executeQuery(decrement_num);
			    
			    if(rs.next())
			    {
			    	int amount = rs.getInt(1);
			    	amount--;
			    	
			    	// Insert decremented amount into the table
			    	
				    String update_amount = "UPDATE COURSE SET SPN_AMOUNT="+amount+" WHERE COURSE_ID="+cid+" AND SECTION="+sect;
				    stmt.executeUpdate(update_amount);	
			    }
			    
			    // Update the Status of the SPN Request to ACCEPTED
			    
			    String accepted="ACCEPTED";
			    String query_status = "UPDATE SPNREQUEST SET STATUS='"+accepted+"' WHERE COURSE_ID="+cid+" AND SECTION="+sect+" AND NET_ID='"+net_id+"'";
				stmt.executeUpdate(query_status);
			}
			
			return true;
		}
		catch(Exception e)
		{
			System.out.println("Couldn't assign SPN #"+e);
			return false;
		}
	}
	
	public void sendComment(String netid, String comment)
	{
		Statement stmt = null;
		
		// Need to make sure it's for that specific course
		
		String sql = "SELECT * FROM SPNREQUEST WHERE NET_ID='"+netid+"' AND COURSE_ID="+ProfessorServlet.courseid;
		
		try
		{
			currentCon = ConnectionManager.getConnection();
			stmt=currentCon.createStatement();
			rs=stmt.executeQuery(sql);
			
			while(rs.next())
			{
			    String send_comment = "UPDATE SPNREQUEST SET NOTE_FV='"+comment+"' WHERE NET_ID='"+netid+"'";
			    
			    Statement pstmt_one = null;
			    
			    pstmt_one = currentCon.createStatement(); 
			    
			    pstmt_one.executeUpdate(send_comment);
			}
		}
		catch(Exception e)
		{
			System.out.println("Couldn't send comment to user.");
		}
	}
	public ArrayList<CourseBean> getPrereq(String courseid)
	{
		Statement stmt = null;
		int cid= Integer.parseInt(courseid);
		
		try
		{
			currentCon = ConnectionManager.getConnection();
			stmt=currentCon.createStatement();
			
			String sql = "SELECT * FROM PREREQUISITE WHERE COURSE_ID="+cid;
			rs=stmt.executeQuery(sql);
			
			while(rs.next())
			{
				CourseBean bean = new CourseBean();
				
				bean.setPid(rs.getString(2));
				bean.setName(rs.getString(1));
				bean.setCourse_id(rs.getString(7));
				
				list.add(bean);
			}
		}
		catch(Exception e)
		{
			System.out.println("Couldn't pull up prereqs."+e);
			return null;
		}
		
		return list;
	}
	public ArrayList<RequestBean> getStatus(String netid)
	{
		Statement stmt = null;
		
		try
		{
			currentCon = ConnectionManager.getConnection();
			stmt=currentCon.createStatement();
			
			String find_status = "SELECT * FROM SPNREQUEST WHERE NET_ID='"+netid+"'";
			rs = stmt.executeQuery(find_status);
			
			while(rs.next())
			{
				String spn_request = rs.getString(1);
				String net_id = rs.getString(2);
				float rating = rs.getFloat(11);
				String status = rs.getString(10);
				String cid = rs.getString(4);
				int sect = Integer.parseInt(rs.getString(5));
				int coursey = Integer.parseInt(cid);
				
				// Check if it has a SPN
				
				Statement stmt_two = null;
				stmt_two=currentCon.createStatement();
				ResultSet rd =null;
				
				String find_spn = "SELECT * FROM SPN WHERE NET_ID='"+netid+"' AND COURSE_ID="+coursey+" AND SECTION="+sect;
				rd = stmt_two.executeQuery(find_spn);
				String spn = null;
				
				RequestBean bean = new RequestBean();
				
				if(rd.next())
				{
					spn = rd.getString(1);
					bean.setSpn(spn);
				}
				bean.setCourse_id(cid);
				bean.setSection(sect);
				bean.setSp_request_id(spn_request);
				bean.setNet_id(net_id);
				bean.setRating(rating);
				bean.setStatus(status);
				
				lists.add(bean);
			}
		}
		catch(Exception e)
		{
			System.out.println("Couldn't retrieve status messages");
			return null;
		}
		
		return lists;
	}
	public ArrayList<RequestBean> getEnrolled(String cid, String sect)
	{
		Statement stmt = null;
		Statement stmt_two = null;
		Statement stmt_three = null;
		int course= Integer.parseInt(cid);
		int parse_section= Integer.parseInt(sect);
		
		// Find the section
		
		String find_section = "SELECT SECTION FROM COURSE WHERE COURSE_ID="+course+" AND NET_ID='"+ProfessorServlet.NET_ID+"'";
		
		try
		{
			//connecting to the DB
			currentCon = ConnectionManager.getConnection();
			
			stmt=currentCon.createStatement();
			stmt_two=currentCon.createStatement();
			stmt_three=currentCon.createStatement();
			
			rs = stmt.executeQuery(find_section);
			rs.next();
			
			int section_num = parse_section;
			
			String sql = "select * from SPNREQUEST WHERE COURSE_ID="+course+" AND SECTION="+section_num;
			
			rs = stmt.executeQuery(sql);
			
			ResultSet rd = null;
			
			while(rs.next())
			{
				RequestBean bean = new RequestBean();
				
				bean.setCourse_id(cid);
				bean.setSp_request_id(rs.getString("sp_request_id"));
				bean.setNet_id(rs.getString("net_id"));
				bean.setRequired(rs.getString("required"));
				bean.setNote_req(rs.getString("note_req"));
				bean.setTime_stamp(rs.getString("time_stamp"));
				bean.setStatus(rs.getString("status"));
				bean.setSection(section_num);
				
				// need to calculate the rating based on gpa, credits, grad_year.
				
				String query_student = "SELECT * FROM STUDENT WHERE NET_ID='"+rs.getString("net_id")+"'";
				rd = stmt_two.executeQuery(query_student);
				
				while(rd.next())
				{
					float gpa = rd.getFloat(7);
					int grad_year = rd.getInt(4);
					int credits = rd.getInt(9);
					float sum =0;
					
					float weight_gpa = 3;
					float weight_credits= 2;
					float weight_grad_year = 1;
					
					int required = rs.getInt("required");
					
					if(required==1)
					{
						sum+=1;
					}
					
					sum += ((gpa/4.0)*weight_gpa);
					sum += (credits/120.0)*weight_credits;
					sum += (weight_grad_year)/((grad_year - 2014) + 1);
					
					bean.setRating(sum);
					
					int request_id = Integer.parseInt(rs.getString("sp_request_id"));
					
				    String query_rating = "UPDATE SPNREQUEST SET RATING="+sum+" WHERE SP_REQUEST_ID="+request_id;
				    stmt_three.executeUpdate(query_rating);
				}
				
				lists.add(bean);
			}
		}
		catch(Exception e)
		{
			System.out.println("Couldn't retrieve active waiting list.");
			return null;
		}
		
		Collections.sort(lists, new RatingCompare());
		Collections.reverse(lists);
		return lists;
	}
	public ArrayList<CourseBean> getCourses(String semester)
	{
		Statement stmt = null;
		String sql = "select * from COURSE where SEMESTER='" + semester + "' AND NET_ID='"+ProfessorServlet.NET_ID+"'";
		
		try
		{
			//connecting to the DB
			currentCon = ConnectionManager.getConnection();
			
			stmt=currentCon.createStatement();
			
			rs = stmt.executeQuery(sql);
			
			while (rs.next()) 
			{
				CourseBean course = new CourseBean();
				
				course.setCourse_id(rs.getString("course_id"));
				course.setName(rs.getString("name"));
				course.setLastname(rs.getString("lastname"));
				course.setBuilding_code(rs.getString("building_code"));
				course.setRoom(rs.getString("room"));
				course.setSection(rs.getString("section"));
				course.setSemester(rs.getString("semester"));
				course.setYear_date(rs.getString("year_date"));
				list.add(course);
			}
		}
		catch(Exception e)
		{
			System.out.println("Couldn't execute query");
		}
		
		return list;
	}
	public void maintainList(ArrayList<String> maintain_list)
	{
		Statement stmt = null;
		try
		{
			currentCon = ConnectionManager.getConnection();
			stmt=currentCon.createStatement();
			
			// Pull up the last name.
			
			String find_last_name = "SELECT P.LASTNAME FROM PROFESSOR P WHERE NET_ID='"+ProfessorServlet.NET_ID+"'";
		    rs= stmt.executeQuery(find_last_name);
		    rs.next();
		    
		    String last_name = rs.getString(1);
		    
		    String get_courses = "SELECT COURSE_ID FROM COURSE WHERE LASTNAME='"+last_name+"'";
		    rs= stmt.executeQuery(get_courses);
		    
		    ArrayList<Integer> courses = new ArrayList<Integer>();
		    
		    while(rs.next())
		    {
		    	courses.add(rs.getInt(1));
		    }
		    
		    ArrayList<String> waiting_peers = new ArrayList<String>();
		    
		    for(int i =0; i<courses.size();i++)
		    {
		    	String get_waiting = "SELECT NET_ID FROM SPNREQUEST WHERE COURSE_ID='"+courses.get(i)+"'";
		    	rs= stmt.executeQuery(get_waiting);
		   
		    	while(rs.next())
		    	{
		    		String check_enrolled = "SELECT NET_ID FROM ENROLLED WHERE NET_ID='"+courses.get(i)+"'";
		    		rs= stmt.executeQuery(check_enrolled);
		    		
		    		if(rs.next())
		    		{
		    			String delete_spn = "DELETE FROM SPNREQUEST WHERE NET_ID='"+courses.get(i)+"'";
		    			stmt.executeUpdate(delete_spn);
		    		}
		    	}
		    }
		}
		catch(Exception e)
		{
			System.out.println("Couldn't maintain list of students."+e);
			return;
		}
		
		
	}
	
	public boolean addPrereq(String pid, String cid)
	{
		// check that the prereq doesn't already exist
		
		Statement stmt = null;
		
		try
		{
			int pre = Integer.parseInt(pid);
			int course_id = Integer.parseInt(cid);
			
			currentCon = ConnectionManager.getConnection();
			stmt=currentCon.createStatement();
			
			String query = "SELECT P.pid FROM PREREQUISITE P WHERE P.pid="+pre+" AND P.COURSE_ID="+course_id;
			rs=stmt.executeQuery(query);
			
			if(rs.next())
			{
				return false;
			}
			
			// Check that the course exists
			
			String query_course = "SELECT * FROM COURSE WHERE COURSE_ID="+course_id;
			rs=stmt.executeQuery(query_course);
			
			if(!rs.next())
			{
				return false;
			}
			
			// Now, add the legit values.
			
			String get_name = "SELECT C.name FROM COURSE C WHERE C.COURSE_ID="+pre;
			rs=stmt.executeQuery(get_name);
			rs.next();
			
			String course_name = rs.getString(1);
			
			// Now add a new prereq with the values
			
		    String insert_prereq = "insert into PREREQUISITE(name, pid, course_id) values(?,?,?)";
		    
		    PreparedStatement pstmt_one = null;
		    pstmt_one = currentCon.prepareStatement(insert_prereq); 
		    
		    pstmt_one.setString(1, course_name);
		    pstmt_one.setInt(2, pre);
		    pstmt_one.setInt(3, course_id);
		    
		    pstmt_one.executeUpdate();
		    
		    return true;
		}
		catch(Exception e)
		{
			System.out.println("Couldn't add new prerequisite.");
			return false;
		}
	}
	
	public boolean addCourse(String course, String section, String cname, String building, String roomnum, String roomsize)
	{
		// Insert the professor's name, derived from the net_id, into the section and course
		
		Statement stmt = null;
		
		try
		{
			int course_num = Integer.parseInt(course);
			int section_num = Integer.parseInt(section);
			int room_size = Integer.parseInt(roomsize);
			
			currentCon = ConnectionManager.getConnection();
			stmt=currentCon.createStatement();
			
			// Find the last name
			String find_last_name = "SELECT P.LASTNAME FROM PROFESSOR P WHERE NET_ID='"+ProfessorServlet.NET_ID+"'";
		    rs= stmt.executeQuery(find_last_name);
		    rs.next();
		    
		    String last_name = rs.getString(1);
		    
			String compare_course_names = "SELECT NAME FROM COURSE WHERE COURSE_ID="+course_num;
			rs= stmt.executeQuery(compare_course_names);
			
			if(rs.next())
			{
				if(rs.getString(1).compareTo(cname)!=0)
				{
					return false;
				}
			}
			
		    // You need to enter the classroom
		    
		    // Check for duplicates:
		    
		    String check_build = "SELECT B.building_code, B.room FROM CLASSROOM B WHERE B.building_code='"+building+"' AND B.room='"+roomnum+"'";
		    rs=stmt.executeQuery(check_build);
		    
		    if(rs.next())
		    {
		    	return false;
		    }
		    
		    String insert_classroom = "insert into CLASSROOM(seat_limit, building_code, room) values(?,?,?)";
		    
		    PreparedStatement pstmt_one = null;
		    pstmt_one = currentCon.prepareStatement(insert_classroom); 
		    
		    pstmt_one.setInt(1, room_size);
		    pstmt_one.setString(2, building);
		    pstmt_one.setString(3, roomnum);
		    
		    pstmt_one.executeUpdate();
		    
		    // Now enter the course
		    
		    //Check for duplicates
		    
		    String check_course = "SELECT C.COURSE_ID FROM COURSE C WHERE C.COURSE_ID="+course_num+" AND C.SECTION="+section_num;
		    rs=stmt.executeQuery(check_course);
		    
		    if(rs.next())
		    {
		    	return false;
		    }
		    
		    String insert_course = "insert into COURSE(course_id, name, lastname, section, building_code, room,semester,year_date, net_id,spn_amount) values(?,?,?,?,?,?,?,?,?,?)";
		    
		    PreparedStatement pstmt_two = null;
		    pstmt_two = currentCon.prepareStatement(insert_course); 
		    
		    pstmt_two.setInt(1, course_num); // set input parameter 1
		    pstmt_two.setString(2, cname); // set input parameter 2
		    pstmt_two.setString(3, last_name); // set input parameter 3
		    pstmt_two.setInt(4, section_num);
		    pstmt_two.setString(5, building);
		    pstmt_two.setString(6, roomnum);
		    pstmt_two.setString(7, "FALL");
		    pstmt_two.setInt(8, 2014);
		    pstmt_two.setString(9, ProfessorServlet.NET_ID);
		    pstmt_two.setInt(10, 10);
		    pstmt_two.executeUpdate();
		    
		    return true;
		}
		catch(Exception e)
		{
			System.out.println("Couldn't add professor to course.");
		}
		
		return false;
	}
	
	public ArrayList<CourseBean> lookupCourses(String qsname) // semester
	{
		Statement stmt = null;
		String sql = "select * from COURSE where SEMESTER='" + qsname + "'";
		
		try
		{
			//connecting to the DB
			currentCon = ConnectionManager.getConnection();
			
			stmt=currentCon.createStatement();
			
			rs = stmt.executeQuery(sql);
			
			while (rs.next()) 
			{
				CourseBean course = new CourseBean();
				
				course.setCourse_id(rs.getString("course_id"));
				course.setName(rs.getString("name"));
				course.setLastname(rs.getString("lastname"));
				course.setBuilding_code(rs.getString("building_code"));
				course.setRoom(rs.getString("room"));
				course.setSection(rs.getString("section"));
				course.setSemester(rs.getString("semester"));
				course.setYear_date(rs.getString("year_date"));
				list.add(course);
			}
		}
		catch(Exception e)
		{
			System.out.println("Couldn't execute query");
		}
		
		return list;
	}
	
	public boolean registerCourse(String courseid, String section)
	{
		// Need to look up the course in the database.
		
		Statement stmt = null;
		
		try
		{
			int course_id = Integer.parseInt(courseid);
			
			String sql = "SELECT C.COURSE_ID	 from COURSE C WHERE COURSE_ID='"+course_id+"'";
			
			currentCon = ConnectionManager.getConnection();
			stmt=currentCon.createStatement();
			rs = stmt.executeQuery(sql);
			
			// couldn't find course
			
			if(!rs.next())
			{
				taken=true;
				return false;
			}
			
			// Check if already taken
			
			rs = stmt.executeQuery("SELECT *	 FROM STUDENT_INFO E where NET_ID='"+StudentServlet.NET_ID+"'");
			
			int dup_check =0;
			
			while(rs.next())
			{
				String course = rs.getString(2);
				
				if(course.compareTo(courseid)==0)
				{
					dup_check=1;
				}
			}
			
			if(dup_check==1)
			{
				taken=true;
				CourseBean.registered=false;
				return false;
			}
			
			// Check for already enrolled
			
			rs = stmt.executeQuery("SELECT *	 from ENROLLED E where COURSE_ID='"+course_id+"'");	
			
			int check =0;
			
			while(rs.next())
			{
				String net_id = rs.getString(1);
				
				if(net_id.compareTo(StudentServlet.NET_ID)==0)
				{
					check=1;
				}
			}
			
			if(check==1)
			{
				taken=true;
				CourseBean.registered=false;
				return false;
			}
			
			rs = stmt.executeQuery("SELECT C.NAME	 from COURSE C where COURSE_ID='"+course_id+"'");	
			rs.next();
			String course_title = rs.getString(1);
			
			// Check PreReqs
			
			rs = stmt.executeQuery("SELECT pid	 from PREREQUISITE where COURSE_ID='"+course_id+"'");
			
			while(rs.next())
			{
				int preid = rs.getInt(1);
				
				Statement stmt2 = currentCon.createStatement();
				ResultSet rs2;
				
				rs2 = stmt2.executeQuery("SELECT course_id,grade from STUDENT_INFO where NET_ID='"+StudentServlet.NET_ID+"'"+" AND COURSE_ID="+preid);
				
				if(rs2.next()) 
				{
					int tempcid = rs2.getInt(1);
					String grade = rs2.getString(2);
					
					int convert_grade = convertGrade(grade);
					
					if (tempcid == preid) 
					{
						if (convert_grade > 1) 
						{ 
							taken = true;
						} 
						else 
						{
							taken=false;
							CourseBean.registered=false;
							return false;
						}
					}
				}
				else
				{
					taken=true;
					CourseBean.registered=false;
					return false;
				}
				
				rs2.close();
				stmt2.close();
			}
			
			// Check Capacity
			
			rs = stmt.executeQuery("SELECT room	 from COURSE where COURSE_ID='"+course_id+"'");
			rs.next();
			
			String room_id=rs.getString(1);
			
			rs = stmt.executeQuery("SELECT seat_limit	 from CLASSROOM where room='"+room_id+"'");
			rs.next();
			
			int space = rs.getInt(1);
			
			if(space>0)
			{
				String insert = "insert into ENROLLED(NET_ID, SECTION, COURSE_ID, SEMESTER, YEAR_DATE,GRADE) values(?, ?, ?,?,?,?)";

				PreparedStatement pstmt_two = null;
				
			    pstmt_two = currentCon.prepareStatement(insert); 	
			    
			    pstmt_two.setString(1, StudentServlet.NET_ID); // set input parameter 1
			    pstmt_two.setString(2, section); // set input parameter 2
			    pstmt_two.setString(3, courseid); // set input parameter 3
			    pstmt_two.setString(4, "FALL");
			    pstmt_two.setString(5, "2014");
			    pstmt_two.setString(6, null);
			    
			    pstmt_two.executeUpdate();
			    taken=true;
				CourseBean.registered=true;
				
				// decrement seat limit
				rs = stmt.executeQuery("SELECT BUILDING_CODE	 from COURSE where COURSE_ID='"+course_id+"'");
				rs.next();
				String building = rs.getString(1); 
				
				space--;
				
			    String query_space = "UPDATE CLASSROOM SET SEAT_LIMIT="+space+" WHERE BUILDING_CODE='"+building+"'"+" AND ROOM='"+room_id+"'";
			    
				stmt.executeUpdate(query_space);
				
				return true;
			}
			else
			{
				// Send them for special permission
				spec_permission=true;
				CourseBean.registered=false;
				taken=false;
				return false;
			}
			
			   
		}
		catch(Exception e)
		{
			System.out.println("Couldn't register course");
			CourseBean.registered=false;
			taken=true;
		}
		
		return true;
	}
	
	public boolean processSPN(String course, int response, String comment, String section)
	{
		if(course.compareTo(StudentServlet.courses_id)!=0 || section.compareTo(StudentServlet.sections)!=0)
		{
			return false;
		}
		
		try
		{
			currentCon = ConnectionManager.getConnection();
			
			int cid = Integer.parseInt(course);
			// Make sure you don't send duplicates
			
			Statement stmt = null;
			stmt=currentCon.createStatement();
			String check = "SELECT * FROM SPNREQUEST WHERE NET_ID='"+StudentServlet.NET_ID+"'"+" AND COURSE_ID="+cid;
			rs=stmt.executeQuery(check);
			
			if(rs.next())
			{
				return false;
			}
			
			String time_stamp =GetTime();
			
		    String sql = "insert into SPNREQUEST(net_id, course_id, required, note_req,time_stamp,section, status) values(?,?, ?, ?,?,?,?)";
		    
		    PreparedStatement pstmt_two = null;
		    pstmt_two = currentCon.prepareStatement(sql);
		    
		    pstmt_two.setString(1, StudentServlet.NET_ID); // set input parameter 1
		    pstmt_two.setString(2, course); // set input parameter 2
		    pstmt_two.setInt(3, response); // set input parameter 3
		    pstmt_two.setString(4, comment);
		    pstmt_two.setString(5, time_stamp);
		    pstmt_two.setString(6, section);
		    pstmt_two.setString(7, "PENDING");
		    
		    pstmt_two.executeUpdate();
		    
		    return true;
		}
		catch(Exception e)
		{
			System.out.println("Couldn't process Special Permission Number.");
			return false;
		}
		
		
	}
	
	private int convertGrade(String grade)
	{
		String temp;
		
		switch(grade)
		{
			case "A":
			{
				return 4;
			}
			case "B":
			{
				return 3;
			}
			case "C":
			{
				return 2;
			}
			case "D":
			{
				return 1;
			}
		}
		
		return 0;
	}
	
	public String GetTime()
	{
	    	Calendar cal = Calendar.getInstance();
	    	SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    	String return_date = sf.format(cal.getTime());
	    	
	    	return return_date;
	}
	
	public class RatingCompare implements Comparator<RequestBean> 
	{
	    @Override
	    public int compare(RequestBean o1, RequestBean o2) 
	    {
	        Float rating_one = new Float(o1.getRating());
	        Float rating_two = new Float(o2.getRating());
	        
	        int retVal = rating_one.compareTo(rating_two);
	        
	        return retVal;
	        
	    }
	}
	
	public void insertUsers(ArrayList<BigBean> users)
	{
		for(int i =0; i<users.size();i++)
		{
		    String insert_users = "insert into USERS(ru_id, net_id, password) values(?,?,?)";
		    
		    try
		    {
			    PreparedStatement pstmt_one = null;
			    
			    pstmt_one = currentCon.prepareStatement(insert_users); 
			    
			    pstmt_one.setInt(1, users.get(i).getRu_id());
			    pstmt_one.setString(2, users.get(i).getNetID());
			    pstmt_one.setString(3, users.get(i).getPassword());
			    
			    pstmt_one.executeUpdate();
		    }
		    catch(Exception e)
		    {
		    	
		    }
		}
	}
	
	public void insertStudents(ArrayList<BigBean> students)
	{
		for(int i =0; i<students.size();i++)
		{
		    String insert_users = "insert into STUDENT(ru_id, net_id, password,grad_year,firstname,lastname,gpa,major,credits) values(?,?,?,?,?,?,?,?,?)";
		    
		    try
		    {
			    PreparedStatement pstmt_one = null;
			    
			    pstmt_one = currentCon.prepareStatement(insert_users); 
			    
			    pstmt_one.setInt(1, students.get(i).getRu_id());
			    pstmt_one.setString(2, students.get(i).getNetID());
			    pstmt_one.setString(3, students.get(i).getPassword());
			    pstmt_one.setString(4, students.get(i).getGrad_year());
			    pstmt_one.setString(5, students.get(i).getFirstname());
			    pstmt_one.setString(6, students.get(i).getLastname());
			    pstmt_one.setString(7, students.get(i).getGpa());
			    pstmt_one.setString(8, students.get(i).getMajor());
			    pstmt_one.setString(9, students.get(i).getCredits());
			    pstmt_one.executeUpdate();
		    }
		    catch(Exception e)
		    {
		    	
		    }
		}
	}
	
	public void insertProfessors(ArrayList<BigBean> professors)
	{
		for(int i =0; i<professors.size();i++)
		{
		    String insert_users = "insert into PROFESSOR(ru_id, net_id, password,firstname,lastname,department) values(?,?,?,?,?,?)";
		    
		    try
		    {
			    PreparedStatement pstmt_one = null;
			    
			    pstmt_one = currentCon.prepareStatement(insert_users); 
			    
			    pstmt_one.setInt(1, professors.get(i).getRu_id());
			    pstmt_one.setString(2, professors.get(i).getNetID());
			    pstmt_one.setString(3, professors.get(i).getPassword());
			    pstmt_one.setString(4, professors.get(i).getFirstname());
			    pstmt_one.setString(5, professors.get(i).getLastname());
			    pstmt_one.setString(6, professors.get(i).getDepartment());
			    
			    pstmt_one.executeUpdate();
		    }
		    catch(Exception e)
		    {
		    	
		    }
		}
	}
	
	public void insertClassroom(ArrayList<BigBean> classroom)
	{
		for(int i =0; i<classroom.size();i++)
		{
		    String insert_users = "insert into CLASSROOM(seat_limit, building_code, room) values(?,?,?)";
		    
		    try
		    {
			    PreparedStatement pstmt_one = null;
			    
			    pstmt_one = currentCon.prepareStatement(insert_users); 
			    
			    pstmt_one.setString(1, classroom.get(i).getSeat_limit());
			    pstmt_one.setString(2, classroom.get(i).getBuilding_code());
			    pstmt_one.setString(3, classroom.get(i).getRoom());
			    
			    pstmt_one.executeUpdate();
		    }
		    catch(Exception e)
		    {
		    	
		    }
		}
	}
	
	public void insertCourses(ArrayList<BigBean> courses)
	{
		for(int i =0; i<courses.size();i++)
		{
		    String insert_users = "insert into COURSE(course_id, net_id, name,lastname,section,spn_amount,building_code,room,semester,year_date) values(?,?,?,?,?,?,?,?,?,?)";
		    
		    try
		    {
			    PreparedStatement pstmt_one = null;
			    
			    pstmt_one = currentCon.prepareStatement(insert_users); 
			    
			    pstmt_one.setString(1, courses.get(i).getCourse_id());
			    pstmt_one.setString(2, courses.get(i).getNetID());
			    pstmt_one.setString(3, courses.get(i).getCourse_name());
			    pstmt_one.setString(4, courses.get(i).getLastname());
			    pstmt_one.setString(5, courses.get(i).getSection());
			    pstmt_one.setString(6, courses.get(i).getSpn_amount());
			    pstmt_one.setString(7, courses.get(i).getBuilding_code());
			    pstmt_one.setString(8, courses.get(i).getRoom());
			    pstmt_one.setString(9, courses.get(i).getSemester());
			    pstmt_one.setString(10, courses.get(i).getYear_date());


			    pstmt_one.executeUpdate();
		    }
		    catch(Exception e)
		    {
		    	
		    }
		}
	}
	
	public void insertStudentInfo(ArrayList<BigBean> info)
	{
		for(int i =0; i<info.size();i++)
		{
		    String insert_users = "insert into STUDENT_INFO(net_id, course_id, grade) values(?,?,?)";
		    
		    try
		    {
			    PreparedStatement pstmt_one = null;
			    
			    pstmt_one = currentCon.prepareStatement(insert_users); 
			    
			    pstmt_one.setString(1, info.get(i).getNetID());
			    pstmt_one.setString(2, info.get(i).getCourse_id());
			    pstmt_one.setString(3, info.get(i).getGrade());
			   
			    pstmt_one.executeUpdate();
		    }
		    catch(Exception e)
		    {
		    	
		    }
		}
	}
	
	public void insertEnrolled(ArrayList<BigBean> enrolled)
	{
		for(int i =0; i<enrolled.size();i++)
		{
		    String insert_users = "insert into ENROLLED(net_id, section, course_id, semester, year_date, grade) values(?,?,?,?,?,?)";
		    
		    try
		    {
			    PreparedStatement pstmt_one = null;
			    
			    pstmt_one = currentCon.prepareStatement(insert_users); 
			    
			    pstmt_one.setString(1, enrolled.get(i).getNetID());
			    pstmt_one.setString(2, enrolled.get(i).getSection());
			    pstmt_one.setString(3, enrolled.get(i).getCourse_id());
			    pstmt_one.setString(4, enrolled.get(i).getSemester());
			    pstmt_one.setString(5, enrolled.get(i).getYear_date());
			    pstmt_one.setString(6, enrolled.get(i).getGrade());
			    
			    pstmt_one.executeUpdate();
		    }
		    catch(Exception e)
		    {
		    	
		    }
		}
	}
	
	public void insertPrereq(ArrayList<BigBean> prereq)
	{
		for(int i =0; i<prereq.size();i++)
		{
		    String insert_users = "insert into PREREQUISITE(name, pid, section, semester, year_date,course_id) values(?,?,?,?,?,?)";
		    
		    try
		    {
			    PreparedStatement pstmt_one = null;
			    
			    pstmt_one = currentCon.prepareStatement(insert_users); 
			    
			    pstmt_one.setString(1, prereq.get(i).getCourse_name());
			    pstmt_one.setString(2, prereq.get(i).getPid());
			    pstmt_one.setString(3, prereq.get(i).getSection());
			    pstmt_one.setString(4, prereq.get(i).getSemester());
			    pstmt_one.setString(5, prereq.get(i).getYear_date());
			    pstmt_one.setString(6, prereq.get(i).getCourse_id());
			    
			    pstmt_one.executeUpdate();
		    }
		    catch(Exception e)
		    {
		    	
		    }
		}
	}
	
	public void insertSpnRequest(ArrayList<BigBean> request)
	{
		for(int i =0; i<request.size();i++)
		{
		    String insert_users = "insert into SPNREQUEST(sp_request_id, net_id, major_id, course_id, section,semester,required,time_stamp,year_date,status,rating,note_req,note_fv ) values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
		    
		    try
		    {
			    PreparedStatement pstmt_one = null;
			    
			    pstmt_one = currentCon.prepareStatement(insert_users); 
			    
			    pstmt_one.setString(1, request.get(i).getSp_request_id());
			    pstmt_one.setString(2, request.get(i).getNetID());
			    pstmt_one.setString(3, request.get(i).getMajor_id());
			    pstmt_one.setString(4, request.get(i).getCourse_id());
			    pstmt_one.setString(5, request.get(i).getSection());
			    pstmt_one.setString(6, request.get(i).getSemester());
			    pstmt_one.setString(7, request.get(i).getRequired());
			    pstmt_one.setString(8, request.get(i).getTime_stamp());
			    pstmt_one.setString(9, request.get(i).getYear_date());
			    pstmt_one.setString(10, request.get(i).getStatus());
			    pstmt_one.setString(11, request.get(i).getRating());
			    pstmt_one.setString(12, request.get(i).getNote_req());
			    pstmt_one.setString(13, request.get(i).getNote_fv());
			    
			    
			    pstmt_one.executeUpdate();
		    }
		    catch(Exception e)
		    {
		    	
		    }
		}
	}
	
	public void insertSpn(ArrayList<BigBean> spn)
	{
		for(int i =0; i<spn.size();i++)
		{
		    String insert_users = "insert into SPN(spn_id, net_id, prof_id, major, course_id,section,semester,year_date,sp_request_id) values(?,?,?,?,?,?,?,?,?)";
		    
		    try
		    {
			    PreparedStatement pstmt_one = null;
			    
			    pstmt_one = currentCon.prepareStatement(insert_users); 
			    
			    pstmt_one.setString(1, spn.get(i).getSpn_id());
			    pstmt_one.setString(2, spn.get(i).getNetID());
			    pstmt_one.setString(3, spn.get(i).getProf_id());
			    pstmt_one.setString(4, spn.get(i).getMajor());
			    pstmt_one.setString(5, spn.get(i).getCourse_id());
			    pstmt_one.setString(6, spn.get(i).getSection());
			    pstmt_one.setString(7, spn.get(i).getSemester());
			    pstmt_one.setString(8, spn.get(i).getYear_date());
			    pstmt_one.setString(9, spn.get(i).getSp_request_id());
			     
			    pstmt_one.executeUpdate();
		    }
		    catch(Exception e)
		    {
		    	
		    }
		}
	}
	
	public void insertUserType(ArrayList<BigBean> usertpe)
	{
		for(int i =0; i<usertpe.size();i++)
		{
		    String insert_users = "insert into USERTYPE(typeid,typename) values(?,?)";
		    
		    try
		    {
			    PreparedStatement pstmt_one = null;
			    
			    pstmt_one = currentCon.prepareStatement(insert_users); 
			    
			    pstmt_one.setString(1, usertpe.get(i).getTypeid());
			    pstmt_one.setString(2, usertpe.get(i).getTypename());
			     
			    pstmt_one.executeUpdate();
		    }
		    catch(Exception e)
		    {
		    	
		    }
		}
	}
	
	public ArrayList<BigBean> getUsers()
	{
		ArrayList<BigBean> bigList = new ArrayList<BigBean>();
	 	Statement stmt = null;
	 	try
	 	{
	 	currentCon = ConnectionManager.getConnection();
	 	stmt=currentCon.createStatement();
	 	 
	 	String find_assigned = "SELECT * FROM USERS";
	 	rs = stmt.executeQuery(find_assigned);
	 	 
	 	while(rs.next())
	 	{

	 	String ruid = rs.getString(1);
	 	String net_id = rs.getString(2);
	 	String password = rs.getString(3);
	 	 
	 	 
	 	BigBean bean = new BigBean();
	 	bean.setRu_id(Integer.parseInt(ruid));
	 	bean.setNetID(net_id);
	 	bean.setPassword(password);

	 	 
	 	bigList.add(bean);
	 	}
	 	 
	 	 
	 	}
	 	catch(Exception e)
	 	{
	 	System.out.println("GET USERS FAILED");
	 	return null;
	 	}
	 	  
	 	return bigList;
	 	 
	 }
	public ArrayList<BigBean> getUserTypes()
	{
		ArrayList<BigBean> bigList = new ArrayList<BigBean>();
	 	Statement stmt = null;
	 	try
	 	{
	 		currentCon = ConnectionManager.getConnection();
	 		stmt=currentCon.createStatement();
	 	 
	 		String find_assigned = "SELECT * FROM USERTYPE";
	 		rs = stmt.executeQuery(find_assigned);
	 	 
	 	while(rs.next())
	 	{

		 	String typeid = rs.getString(1);
		 	String typename = rs.getString(2);
		 	
		 	BigBean bean = new BigBean();
		 	bean.setTypeid(typeid);
		 	bean.setTypename(typename);
		 	
		 	bigList.add(bean);
	 	}
	 	 
	 	 
	 	}
	 	catch(Exception e)
	 	{
	 	System.out.println("GET USERS FAILED");
	 	return null;
	 	}
	 	  
	 	return bigList;
	 	 
	 }
	public ArrayList<BigBean> getProfessors()
	{
		ArrayList<BigBean> bigList = new ArrayList<BigBean>();
		
		Statement stmt = null;
		
	 	try
	 	{
	 		currentCon = ConnectionManager.getConnection();
	 		stmt=currentCon.createStatement();
	 	 
	 		String find_assigned = "SELECT * FROM PROFESSOR";
	 		rs = stmt.executeQuery(find_assigned);
	 		
	 		while(rs.next())
		 	{
			 	String ruid = rs.getString(1);
			 	String net_id = rs.getString(2);
			 	String password = rs.getString(3);
			 	String firstname = rs.getString(4);
			 	String lastname = rs.getString(5);
			 	String department = rs.getString(6);
			 	
			 	BigBean bean = new BigBean();
			 	bean.setRu_id(Integer.parseInt(ruid));
			 	bean.setNetID(net_id);
			 	bean.setPassword(password);
			 	bean.setFirstname(firstname);
			 	bean.setLastname(lastname);
			 	bean.setDepartment(department);
			 	bigList.add(bean);
		 	}
	 		
	 		
	 	}
	 	catch(Exception e)
	 	{
	 		System.out.println("GET PROFESSOR FAILED");
	 	}
	 	
	 	return bigList;
	}
	
	public ArrayList<BigBean> getENROLLED()
	{
		ArrayList<BigBean> bigList = new ArrayList<BigBean>();
		
		Statement stmt = null;
		
	 	try
	 	{
	 		currentCon = ConnectionManager.getConnection();
	 		stmt=currentCon.createStatement();
	 	 
	 		String find_assigned = "SELECT * FROM ENROLLED";
	 		rs = stmt.executeQuery(find_assigned);
	 		
	 		while(rs.next())
		 	{
			 	String net_id = rs.getString(1);
			 	String section = rs.getString(2);
			 	String courseid = rs.getString(3);
			 	String semester = rs.getString(4);
			 	String year_date = rs.getString(5);
			 	String grade = rs.getString(6);
			 	
			 	BigBean bean = new BigBean();
			 	
			 	bean.setNetID(rs.getString(1));
			 	bean.setSection(rs.getString(2));
			 	bean.setCourse_id(rs.getString(3));
			 	bean.setSemester(rs.getString(4));
			 	bean.setYear_date(rs.getString(5));
			 	bean.setGrade(rs.getString(6));
			 	
			 	bigList.add(bean);
		 	}
	 		
	 		
	 	}
	 	catch(Exception e)
	 	{
	 		System.out.println("GET PROFESSOR FAILED");
	 	}
	 	
	 	return bigList;
	}
	 public  ArrayList<BigBean> getStudents(){
         ArrayList<BigBean> bigList = new ArrayList<BigBean>();

         Statement stmt = null;
         try{
                 currentCon = ConnectionManager.getConnection();
                 stmt=currentCon.createStatement();

                 String find_assigned = "SELECT * FROM STUDENT";
                 rs = stmt.executeQuery(find_assigned);

                 while(rs.next())
                 {

                         String ruid = rs.getString(1);
                         String net_id = rs.getString(2);
                         String password = rs.getString(3);
                         String grad_year = rs.getString(4);
                         String firstname = rs.getString(5);
                         String lastname = rs.getString(6);
                         String gpa = rs.getString(7);
                         String major = rs.getString(8);
                         String credits = rs.getString(9);

                         BigBean bean = new BigBean();
                         bean.setRu_id(Integer.parseInt(ruid));
                         bean.setNetID(net_id);
                         bean.setPassword(password);
                         bean.setGrad_year(grad_year);
                         bean.setFirstname(firstname);
                         bean.setLastname(lastname);
                         bean.setGpa(gpa);
                         bean.setMajor(major);
                         bean.setCredits(credits);
                         bigList.add(bean);
                 }


         }
         catch(Exception e){
                 System.out.println("GET STUDENT FAILED");
                 return null;
         }

         

         return bigList;




 }

 public  ArrayList<BigBean> getClassroom(){
         ArrayList<BigBean> bigList = new ArrayList<BigBean>();




         Statement stmt = null;
         try{
                 currentCon = ConnectionManager.getConnection();
                 stmt=currentCon.createStatement();

                 String find_assigned = "SELECT * FROM CLASSROOM";
                 rs = stmt.executeQuery(find_assigned);

                 while(rs.next())
                 {

                         String seat_limit = rs.getString(1);
                         String building_code = rs.getString(2);
                         String room = rs.getString(3);


                         BigBean bean = new BigBean();
                         bean.setSeat_limit(seat_limit);
                         bean.setBuilding_code(building_code);
                         bean.setRoom(room);


                         bigList.add(bean);
                 }


         }
         catch(Exception e){
                 System.out.println("GET CLASSROOM FAILED");
                 return null;
         }

        

         return bigList;

 }

 public  ArrayList<BigBean> getCourse(){
         ArrayList<BigBean> bigList = new ArrayList<BigBean>();
         Statement stmt = null;


         try{
                 currentCon = ConnectionManager.getConnection();
                 stmt=currentCon.createStatement();

                 String find_assigned = "SELECT * FROM COURSE";
                 rs = stmt.executeQuery(find_assigned);

                 while(rs.next())
                 {

                         String course_id = rs.getString(1);
                         String net_id = rs.getString(2);
                         String name = rs.getString(3);
                         String lastname = rs.getString(4);
                         String section = rs.getString(5);
                         String spn_amount = rs.getString(6);
                         String building_code = rs.getString(7);
                         String room = rs.getString(8);
                         String semester = rs.getString(9);
                         String year_date = rs.getString(10);


                         BigBean bean = new BigBean();
                         bean.setCourse_id(course_id);
                         bean.setNetID(net_id);
                         bean.setCourse_name(name);
                         bean.setLastname(lastname);
                         bean.setSection(section);
                         bean.setSpn_amount(spn_amount);
                         bean.setBuilding_code(building_code);
                         bean.setRoom(room);
                         bean.setSemester(semester);
                         bean.setYear_date(year_date);
                         bigList.add(bean);
                 }


         }
         catch(Exception e){
                 System.out.println("GET COURSE FAILED");
                 return null;
         }

        

         return bigList;

 }
 public  ArrayList<BigBean> getStdnt_info(){
         ArrayList<BigBean> bigList = new ArrayList<BigBean>();

         Statement stmt = null;
         try{
                 currentCon = ConnectionManager.getConnection();
                 stmt=currentCon.createStatement();

                 String find_assigned = "SELECT * FROM STUDENT_INFO";
                 rs = stmt.executeQuery(find_assigned);

                 while(rs.next())
                 {

                         String net_id = rs.getString(1);
                         String course_id = rs.getString(2);
                         String grade = rs.getString(3);


                         BigBean bean = new BigBean();
                         bean.setNetID(net_id);
                         bean.setCourse_id(course_id);
                         bean.setGrade(grade);

                         bigList.add(bean);
                 }


         }
         catch(Exception e){
                 System.out.println("GET STUDENT INFO FAILED");
                 return null;
         }

        

         return bigList;

 }


 public  ArrayList<BigBean> getPrereq(){
         ArrayList<BigBean> bigList = new ArrayList<BigBean>();


         Statement stmt = null;


         try{
                 currentCon = ConnectionManager.getConnection();
                 stmt=currentCon.createStatement();

                 String find_assigned = "SELECT * FROM PREREQUISITE";
                 rs = stmt.executeQuery(find_assigned);

                 while(rs.next())
                 {
                	 	 
   
                         BigBean bean = new BigBean();
                         bean.setCourse_name(rs.getString(1));
                         bean.setPid(rs.getString(2));
                         bean.setSection(rs.getString(3));
                         bean.setSemester(rs.getString(4));
                         bean.setYear_date(rs.getString(6));
                         bean.setCourse_id(rs.getString(7));
                         bigList.add(bean);
                 }


         }
         catch(Exception e){
                 System.out.println("GET PREREQUISITE FAILED");
                 return null;
         }

         

         return bigList;

 }

 public  ArrayList<BigBean> getSPN(){
         ArrayList<BigBean> bigList = new ArrayList<BigBean>();

         Statement stmt = null;
         try{
                 currentCon = ConnectionManager.getConnection();
                 stmt=currentCon.createStatement();

                 String find_assigned = "SELECT * FROM SPN";
                 rs = stmt.executeQuery(find_assigned);

                 while(rs.next())
                 {

                         String spn_id = rs.getString(1);
                         String net_id = rs.getString(2);
                         String prof_id = rs.getString(3);
                         String major = rs.getString(4);
                         String course_id = rs.getString(5);
                         String section = rs.getString(6);
                         String semester = rs.getString(7);
                         String year_date = rs.getString(8);
                         String sp_request_id = rs.getString(9);

                         BigBean bean = new BigBean();
                         bean.setSpn_id(spn_id);
                         bean.setNetID(net_id);
                         bean.setProf_id(prof_id);
                         bean.setMajor(major);
                         bean.setCourse_id(course_id);
                         bean.setSection(section);
                         bean.setSemester(semester);
                         bean.setYear_date(year_date);
                         bean.setSp_request_id(sp_request_id);

                         bigList.add(bean);
                 }


         }
         catch(Exception e){
                 System.out.println("GET SPN FAILED");
                 return null;
         }

        

         return bigList;

 }
 public  ArrayList<BigBean> getSPNRequest(){
         ArrayList<BigBean> bigList = new ArrayList<BigBean>();


         /*/
          *      SP_REQUEST_ID INTEGER AUTO_INCREMENT,
                                                         NET_ID       CHAR(9),
                                                         MAJOR_ID    CHAR(3),
                                                         COURSE_ID        INTEGER,
                                                         SECTION       INTEGER,
                                                         SEMESTER   CHAR(10),
                                                         REQUIRED        INTEGER,
                                                         TIME_STAMP              CHAR(255),
                                                         YEAR_DATE      INTEGER,
                                                         STATUS                  CHAR(20),
                                                         RATING                  REAL,
                                                         NOTE_REQ   VARCHAR(256),
                                                         NOTE_FV     VARCHAR(256),
          */


         Statement stmt = null;
         try{
                 currentCon = ConnectionManager.getConnection();
                 stmt=currentCon.createStatement();

                 String find_assigned = "SELECT * FROM SPNREQUEST";
                 rs = stmt.executeQuery(find_assigned);

                 while(rs.next())
                 {

                         String sp_request_id = rs.getString(1);
                         String net_id = rs.getString(2);
                         String major_id = rs.getString(3);
                         String course_id = rs.getString(4);
                         String section = rs.getString(5);
                         String semester = rs.getString(6);
                         String required = rs.getString(7);
                         String time_stamp = rs.getString(8);
                         String year_date = rs.getString(9);
                         String status = rs.getString(10);
                         String rating = rs.getString(11);
                         String note_req = rs.getString(12);
                         String note_fv = rs.getString(13);



                         BigBean bean = new BigBean();

                         bean.setSp_request_id(sp_request_id);
                         bean.setNetID(net_id);
                         bean.setMajor_id(major_id);
                         bean.setCourse_id(course_id);
                         bean.setSection(section);
                         bean.setRequired(required);
                         bean.setTime_stamp(time_stamp);
                         bean.setYear_date(year_date);
                         bean.setSemester(semester);
                         bean.setNote_req(note_req);
                         bean.setNote_fv(note_fv);
                         bean.setStatus(status);
                         bean.setRating(rating);


                         bigList.add(bean);
                 }


         }
         catch(Exception e){
                 System.out.println("GET spnrequest FAILED");
                 return null;
         }

        

         return bigList;

 }
}
