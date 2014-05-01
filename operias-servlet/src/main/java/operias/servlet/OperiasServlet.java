package operias.servlet;


import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;


public class OperiasServlet  {

	static String username, password;
	/**
	 * Run the servlet
	 * @param args
	 */
	public static void main(String[] args) {
		if (Configuration.parseArguments(args)) {
		

	        System.setSecurityManager(new NoExitSecurityManager());
			Server gitServer = new Server(Configuration.getGitServerPort());
		    gitServer.setHandler(new GitServletHandler());
		    
		    ResourceHandler resourceHandler = new ResourceHandler();
		    resourceHandler.setWelcomeFiles(null);
		    resourceHandler.setDirectoriesListed(false);
		    resourceHandler.setResourceBase(Configuration.getResultDirectory());
		    
			Server htmlServer = new Server(Configuration.getHtmlServerPort());
			htmlServer.setHandler(resourceHandler);
		    
		    
		    try {
		    	htmlServer.start();
		    	gitServer.start();
		    	htmlServer.join();
		    	gitServer.join();
		    } catch (Exception e) {
		    	
		    	e.printStackTrace();
		    }
		}
		else {
			
			System.exit(1);
			
		}
		
	}


}
