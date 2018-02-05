
public class Issue {
	String status;
	String assignee;
	String resolution;
	String testscope;
	String subject;
	String category;
	public String getCategory() {
		return category;
	}
	public Issue() {
		super();
		this.category="unknown";
	}
	public void setCategory(String category) {
		this.category = category;
	}

	String number;
	
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getAssignee() {
		return assignee;
	}
	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}
	public String getResolution() {
		return resolution;
	}
	public void setResolution(String resolution) {
		this.resolution = resolution;
	}
	public String getTestscope() {
		return testscope;
	}
	public void setTestscope(String testscope) {
		this.testscope = testscope;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public String toString() {
		String issueformat="#"+this.number +"\t"+ this.subject;
//		if(this.testscope.equals("")){
//			 
//		}else{
//			String textArea=this.testscope;					
//			issueformat+="\n testscope:\n ["+textArea+"]";
//		}
	    return issueformat;
	}
}
