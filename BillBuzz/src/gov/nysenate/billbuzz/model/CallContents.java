package gov.nysenate.billbuzz.model;

public class CallContents {
	private String message = "";
	private String code = "";
	private String succeeded = "";
	public CallContents(String m, String c, String s) {
		message = m;
		code = c;
		succeeded = s;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getMessage() {
		return message;
	}
	public String getCode() {
		return code;
	}
	public String getSucceeded() {
		return succeeded;
	}
}
