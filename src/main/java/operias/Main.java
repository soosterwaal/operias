package operias;

import java.security.InvalidParameterException;

/**
 * Main class of operias
 * @author soosterwaal
 *
 */
public class Main {
	
	/**
	 * Start up the operias tool
	 * @param args Contains serveral arguments, for example the source directories which
	 * needs to be compared and to which branch it must be compared.
	 * 
	 * The first argument is the directory of the new source files
	 * The second argument is the directory of the github repository
	 */
	public static void main(String[] args) {
		parseArguments(args);
		
		new Operias().constructReport().writeSite();
	}
	
	/**
	 * Parse the arguments passed by the command line
	 * @param args
	 */
	private static void parseArguments(String[] args) {
		// If not enough arguments specified, exit
		if (args.length < 2) {
			System.exit(OperiasStatus.NO_ARGUMENTS_SPECIFIED.ordinal());
		}	
				
		// Set the arguments
		try {
			Configuration.setRevisedDirectory(args[1]);
			Configuration.setOriginalDirectory(args[0]);
		} catch (InvalidParameterException e) {
			System.exit(OperiasStatus.INVALID_ARGUMENTS.ordinal());
		}
	}
}
