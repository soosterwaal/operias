package operias.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GitServletHandler extends AbstractHandler {

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
		
		
		Thread operiasThread = new Thread("Operias") { 
			public void run() { 
				Operias op = new Operias(object);
				if(op.execute()) {
					
					String message = op.constructMessage();

					JsonObject data = new JsonObject();
					data.addProperty("body", message);
					
					ProcessBuilder builder = new ProcessBuilder(
							"curl", 
							"-u", Configuration.getGitHubUsername()+":" + Configuration.getGitHubPassword(),
							op.GetCommentsURL(),
							"-d", data.toString());
					Process process = null;
					try {
						process = builder.start();
						process.waitFor();
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					process.destroy();
				}
			}
		};
		operiasThread.start();
		
		
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
	}
}
