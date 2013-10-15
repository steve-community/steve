package de.rwth.idsg.steve.html;

/**
* This exception is fired when an input field in the Web interface (HTTP servlet) 
* does not match the expected input. It is catched by the ServletError which prints
* an error message in the browser.
* 
* @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
* 
*/
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
