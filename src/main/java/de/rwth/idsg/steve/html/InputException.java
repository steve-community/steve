package de.rwth.idsg.steve.html;

public class InputException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	private String htmlMessage;
	
	public InputException(String msg){
		super();
		this.htmlMessage = msg;
	}
	
	public String getHTMLMessage(){
		return htmlMessage;
	}
	
//	// Do not print the stacktrace for this exception
//    @Override
//    public Throwable fillInStackTrace() {
//        return null;
//    } 

}
