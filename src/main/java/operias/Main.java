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
	 * The third argument is the branch name
	 */
	public static void main(String[] args) {
		
		// If not enough arguments specified, exit
		if (args.length < 2) {
			System.exit(OperiasStatus.NO_ARGUMENTS_SPECIFIED.ordinal());
		}	
		
		// Set the arguments
		try {
			Configuration.setSourceDirectory(args[0]);
			Configuration.setRepositoryDirectory(args[1]);
		} catch (InvalidParameterException e) {
			System.exit(OperiasStatus.INVALID_ARGUMENTS.ordinal());
		}
	}
}
