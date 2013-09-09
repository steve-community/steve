package html;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CPS_Servlet_Log extends HttpServlet {
	
	private static final Logger LOG = LoggerFactory.getLogger(CPS_Servlet_Log.class);
	private static final long serialVersionUID = 8576766110806723303L;	

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		PrintWriter writer = response.getWriter();				
		response.setContentType("text/plain");			
		printLogFile(writer);
		writer.close();	
	}

	private void printLogFile(PrintWriter writer){
		File logDir = new File(System.getProperty("catalina.base"), "logs");
		File cxfLog = new File(logDir, "cxf.log");
		BufferedReader bufferedReader = null;
		try {
			String sCurrentLine;
			bufferedReader = new BufferedReader(new FileReader(cxfLog));
			while ((sCurrentLine = bufferedReader.readLine()) != null) {
				writer.println(sCurrentLine);
			}
		} catch (Exception ex) {
			LOG.error(ex.getMessage());
		} finally {
			try {
				bufferedReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
