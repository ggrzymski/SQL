package home;

public class RequestBean 
{
	String sp_request_id;
	int section;
	String course_id;
	String net_id;
	String time_stamp;
	String note_req;
	float rating;
	String status;
	public String spn =null;
	
	public String getSpn() {
		return spn;
	}
	public void setSpn(String spn) {
		this.spn = spn;
	}
	
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public float getRating() {
		return rating;
	}
	public void setRating(float rating) {
		this.rating = rating;
	}
	public String getSp_request_id() {
		return sp_request_id;
	}
	public void setSp_request_id(String sp_request_id) {
		this.sp_request_id = sp_request_id;
	}
	public int getSection() {
		return section;
	}
	public void setSection(int section) {
		this.section = section;
	}
	public String getCourse_id() {
		return course_id;
	}
	public void setCourse_id(String course_id) {
		this.course_id = course_id;
	}
	public String getNet_id() {
		return net_id;
	}
	public void setNet_id(String net_id) {
		this.net_id = net_id;
	}
	public String getTime_stamp() {
		return time_stamp;
	}
	public void setTime_stamp(String time_stamp) {
		this.time_stamp = time_stamp;
	}
	public String getNote_req() {
		return note_req;
	}
	public void setNote_req(String note_req) {
		this.note_req = note_req;
	}
	public String getRequired() {
		return required;
	}
	public void setRequired(String required) {
		this.required = required;
	}
	String required;
}
