package html;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CPS_Servlet extends HttpServlet {

	private static final long serialVersionUID = 8576766110806723303L;
	String contextPath;	

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		// Get the request details			
		contextPath = request.getContextPath();

		PrintWriter writer = response.getWriter();		
		response.setContentType("text/html");
		writer.println(CPS_Common.printHead(contextPath));
		
		printHomepage(writer);
		writer.println("</div>");
		writer.println("</body></html>");
		writer.close();	
	}


	private void printHomepage(PrintWriter writer) {
		writer.println("<b>Welcome!</b><hr>");
		writer.println("<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Qui ita affectus, beatum esse numquam probabis; Nihil opus est exemplis hoc facere longius. Itaque ab his ordiamur. Quid turpius quam sapientis vitam ex insipientium sermone pendere? Nam adhuc, meo fortasse vitio, quid ego quaeram non perspicis. Non igitur bene. Dic in quovis conventu te omnia facere, ne doleas. Duo Reges: constructio interrete. Iam enim adesse poterit.</p>");
		writer.println("<p>Ergo, inquit, tibi Q. Neque solum ea communia, verum etiam paria esse dixerunt. Itaque nostrum est-quod nostrum dico, artis est-ad ea principia, quae accepimus. In qua quid est boni praeter summam voluptatem, et eam sempiternam? Qui enim voluptatem ipsam contemnunt, iis licet dicere se acupenserem maenae non anteponere. Nos quidem Virtutes sic natae sumus, ut tibi serviremus, aliud negotii nihil habemus. Mihi vero, inquit, placet agi subtilius et, ut ipse dixisti, pressius. Esse enim quam vellet iniquus iustus poterat inpune. Ut in geometria, prima si dederis, danda sunt omnia.</p>");
		writer.println("<p>Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat.</p>");
		writer.println("<p>Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi.</p>");
	}

}
