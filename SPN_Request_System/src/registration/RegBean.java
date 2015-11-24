package registration;
 
//Data Encapsulation using Getters and Setters
public class RegBean {
    private String ru_id;
    private String net_id;
    private String grad_year;
    public String department;
    public String getGpa() {
		return gpa;
	}
	public void setGpa(String gpa) {
		this.gpa = gpa;
	}
	public String getMajor() {
		return major;
	}
	public void setMajor(String major) {
		this.major = major;
	}
	public String getCredits() {
		return credits;
	}
	public void setCredits(String credits) {
		this.credits = credits;
	}
	public String gpa;
    public String major;
    public String credits;
    
    public String getDepartment() 
    {
		return department;
	}
	public void setDepartment(String department) 
	{
		this.department = department;
	}
	private String password;
    private String firstName;
    private String lastName;
    public boolean added=false;
    
	public String getNet_id() 
	{
		return net_id;
	}
	public void setNet_id(String net_id) 
	{
		this.net_id = net_id;
	}
	public String getGrad_year() 
	{
		return grad_year;
	}
	public void setGrad_year(String grad_year) 
	{
		this.grad_year = grad_year;
	}
	public String getFirstName()
    {
        return firstName;
    }
    public void setFirstName(String newFirstName)
    {
        firstName = newFirstName;
    }
    public String getLastName()
    {
        return lastName;
    }
    public void setLastName(String newLastName)
    {
        lastName = newLastName;
    }
    public String getPassword()
    {
        return password;
    }
    public void setPassword(String newPassword)
    {
        password = newPassword;
    }
    public String getRUID()
    {
        return ru_id;
    }
    public void setRUID(String newUsername)
    {
        ru_id = newUsername;
    }
    public boolean isAdded()
    {
        return added;
    }
    public void setAdded(boolean newValid)
    {
        added = newValid;
    }
}
