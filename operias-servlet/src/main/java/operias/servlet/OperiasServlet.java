package operias.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class OperiasServlet extends AbstractHandler {

	static String username, password;
	/**
	 * Run the servlet
	 * @param args
	 */
	public static void main(String[] args) {
		if (Configuration.parseArguments(args)) {
		

	        
			Server server = new Server(Configuration.getServerPort());
		    server.setHandler(new OperiasServlet());
		    
		    try {
		    	server.start();
		    } catch (Exception e) {
		    	e.printStackTrace();
		    }
		}
		else {
			System.exit(1);
		}
		
	}

	/**
	 * Handle requests
	 */
	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		
		InputStream instream = request.getInputStream();
		StringWriter writer = new StringWriter();
		IOUtils.copy(instream, writer);
		String inputData = writer.toString();
		
		JsonParser parser = new JsonParser();
		
		// Get the object containig all information
		final JsonObject object = parser.parse(inputData).getAsJsonObject();
		
		Thread operiasThread = new Thread("Operias") { public void run() { new Operias(object); }};
		operiasThread.start();
		
		
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
	}

}
