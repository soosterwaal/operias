package operias.servlet;

import java.io.File;


/**
 * Configuration of operias, checks and sets the source and repository directories and a branch
 * @author soosterwaal
 *
 */
public class Configuration {

	/**
	 * Static class, so private constructor
	 */
	private Configuration() {
		
	}
	
	/**
	 * Git hub username
	 */
	private static String gitHubUsername;
	
	/**
	 * Git hub password
	 */
	private static String gitHubPassword;
	
	/**
	 * Server IP, so we know from were we serve the files
	 */
	private static String htmlServerIP;
	
	/**
	 * Server Port
	 */
	private static int gitServerPort = 8081;

	/**
	 * Server Port
	 */
	private static int htmlServerPort = 8080;
	
	/**
	 * Directory where the result will be stored, is relative to working directory
	 */
	private static String resultDirectory = new File("results/").getAbsolutePath();
	
	/**
	 * Temporary directory, is an absolute path 
	 */
	private static String temporaryDirectory = new File("temp/").getAbsolutePath();
	
	/**
	 * Parse the arguments passed by the command line
	 * @param args
	 * @return True if arguments are parsed succesfully, false otherwise
	 */
	public static boolean parseArguments(String[] args) {
		try {
			int i = 0;
			while(i < args.length) {
				if (args[i].equals("-ip") || args[i].equals("--server-ip")) {
					// Destination folder
					Configuration.setServerIP(args[i + 1]);
					i += 2;
				} else if (args[i].equals("-gp") || args[i].equals("--git-server-port")) {
					Configuration.setGitServerPort(Integer.parseInt(args[i + 1]));
					i += 2;
				} else if (args[i].equals("-hp") || args[i].equals("--html-server-port")) {
					Configuration.setGitServerPort(Integer.parseInt(args[i + 1]));
					i += 2;
				}  else if (args[i].equals("-td") || args[i].equals("--temporary-directory")) {
					Configuration.setTemporaryDirectory(args[i + 1]);
					i += 2;
				} else if (args[i].equals("-rd") || args[i].equals("--results-directory")) {
					Configuration.setResultDirectory(args[i + 1]);
					i += 2;
				} else if (args[i].equals("-u") || args[i].equals("--username")) {
					Configuration.setGitHubUsername(args[i + 1]);
					i += 2;
				} else if (args[i].equals("-p") || args[i].equals("--password")) {
					Configuration.setGitHubPassword(args[i + 1]);
					i += 2;
				} else {
					System.out.println("[Error] Unknown option \"" + args[i] + "\"");
					return false;
				}
			}
		} catch (Exception e) {
			System.out.println("[Error] Invalid arguments ");
			return false;
		}
		
		if (htmlServerIP == null) {
			System.out.println("[Error] Missing argument --server-ip");
			return false;
		}

		if (gitHubPassword == null) {
			System.out.println("[Error] Missing argument --password");
			return false;
		}
		if (gitHubUsername == null) {
			System.out.println("[Error] Missing argument --username");
			return false;
		}
		
		return true;
	}

	/**
	 * @return the gitHubUsername
	 */
	public static String getGitHubUsername() {
		return gitHubUsername;
	}

	/**
	 * @param gitHubUsername the gitHubUsername to set
	 */
	public static void setGitHubUsername(String gitHubUsername) {
		Configuration.gitHubUsername = gitHubUsername;
	}

	/**
	 * @return the gitHubPassword
	 */
	public static String getGitHubPassword() {
		return gitHubPassword;
	}

	/**
	 * @param gitHubPassword the gitHubPassword to set
	 */
	public static void setGitHubPassword(String gitHubPassword) {
		Configuration.gitHubPassword = gitHubPassword;
	}

	/**
	 * @return the serverIP
	 */
	public static String getServerIP() {
		return htmlServerIP;
	}

	/**
	 * @param serverIP the serverIP to set
	 */
	public static void setServerIP(String serverIP) {
		Configuration.htmlServerIP = serverIP;
	}

	

	/**
	 * @return the resultDirectory
	 */
	public static String getResultDirectory() {
		return resultDirectory;
	}

	/**
	 * @param resultDirectory the resultDirectory to set
	 */
	public static void setResultDirectory(String resultDirectory) {
		Configuration.resultDirectory = resultDirectory;
	}

	/**
	 * @return the temporaryDirectory
	 */
	public static String getTemporaryDirectory() {
		return temporaryDirectory;
	}

	/**
	 * @param temporaryDirectory the temporaryDirectory to set
	 */
	public static void setTemporaryDirectory(String temporaryDirectory) {
		Configuration.temporaryDirectory = temporaryDirectory;
	}

	/**
	 * @return the gitServerPort
	 */
	public static int getGitServerPort() {
		return gitServerPort;
	}

	/**
	 * @param gitServerPort the gitServerPort to set
	 */
	public static void setGitServerPort(int gitServerPort) {
		Configuration.gitServerPort = gitServerPort;
	}

	/**
	 * @return the htmlServerPort
	 */
	public static int getHtmlServerPort() {
		return htmlServerPort;
	}

	/**
	 * @param htmlServerPort the htmlServerPort to set
	 */
	public static void setHtmlServerPort(int htmlServerPort) {
		Configuration.htmlServerPort = htmlServerPort;
	}
}
