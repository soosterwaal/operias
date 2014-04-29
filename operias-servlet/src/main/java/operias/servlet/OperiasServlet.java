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

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class OperiasServlet extends AbstractHandler {

	/**
	 * Run the servlet
	 * @param args
	 */
	public static void main(String[] args) {
		int serverPort = 8080;
		if (args.length > 0) {
			serverPort = Integer.parseInt(args[0]);
		}
		
		Server server = new Server(serverPort);
	    server.setHandler(new OperiasServlet());
	    
	    try {
	    	server.start();
	    } catch (Exception e) {
	    	e.printStackTrace();
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
		JsonObject object = parser.parse(inputData).getAsJsonObject();
	
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
	}

}
