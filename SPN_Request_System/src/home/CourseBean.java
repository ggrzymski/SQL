package home;
 
//Data Encapsulation using Getters and Setters
public class CourseBean 
{
   public String course_id;
   public String name;
   public String ru_id;
   public String section;
   public String building_code;
   public String semester;
   public String room;
   public static String netID;
   public String lastname;
   public String pid;
   
   public String getPid() {
	return pid;
}

public void setPid(String pid) {
	this.pid = pid;
}

public String getLastname() {
	return lastname;
}

public void setLastname(String lastname) {
	this.lastname = lastname;
}
public String net_id;
   
   
   public String getNet_id() {
	return net_id;
}

public void setNet_id(String net_id) {
	this.net_id = net_id;
}

public static String getNetID() {
	return netID;
}

public static void setNetID(String netID) {
	CourseBean.netID = netID;
}
public static boolean registered;
   
   public static boolean isRegistered() {
	return registered;
}

public static void setRegistered(boolean isRegistered) 
{
	registered = isRegistered;
}

public static void setnetid(String input)
   {
	   netID= input;
   }
   
   public String getnetid()
   {
	   return netID;
   }
   
   public String getRoom() {
	return room;
}
public void setRoom(String room) {
	this.room = room;
}
public String year_date;
   
public String getCourse_id() {
	return course_id;
}
public void setCourse_id(String course_id) {
	this.course_id = course_id;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getRu_id() {
	return ru_id;
}
public void setRu_id(String ru_id) {
	this.ru_id = ru_id;
}
public String getSection() {
	return section;
}
public void setSection(String section) {
	this.section = section;
}
public String getBuilding_code() {
	return building_code;
}
public void setBuilding_code(String building_code) {
	this.building_code = building_code;
}
public String getSemester() {
	return semester;
}
public void setSemester(String semester) {
	this.semester = semester;
}
public String getYear_date() {
	return year_date;
}
public void setYear_date(String year_date) {
	this.year_date = year_date;
}
}
