package operias;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;

import org.apache.commons.io.FileUtils;

import operias.git.*;



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
	 * There are two ways to execute operias, using the -d command you can enter a original and revised directory and compare these folders
	 * Using -g command, operias will copy the directory and checkout the given commit.
	 * 
	 * Examples:
	 * 
	 *  -d /dir1 /dir2
	 *  -g /dir commit
	 * 
	 */
	public static void main(String[] args) {
		
		parseArguments(args);

		new Operias().constructReport().writeSite();

		// Remove temporary directory
		if (args[0].equals("-g")) {
			try {
				FileUtils.deleteDirectory(new File(Configuration.getOriginalDirectory()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
		/**
	 * Parse the arguments passed by the command line
	 * @param args
	 */
	private static void parseArguments(String[] args) {
		// If not enough arguments specified, exit
		if (args.length < 3) {
			System.exit(OperiasStatus.NO_ARGUMENTS_SPECIFIED.ordinal());
		}	
		
		if (args[0].equals("-d")) {
			// Set the arguments
			try {
				Configuration.setOriginalDirectory(args[1].toString());
				Configuration.setRevisedDirectory(args[2].toString());
			} catch (InvalidParameterException e) {
				System.exit(OperiasStatus.INVALID_ARGUMENTS.ordinal());
			}
		} else if (args[0].equals("-g")) {
			// Checkout the commit before setting the directory
			try {
				Configuration.setRevisedDirectory(args[1]);
				Git git = new Git(Configuration.getRevisedDirectory());
				
				if(git.checkoutCommit(args[2])) {
					Configuration.setOriginalDirectory(git.getDirectory());
				} else {
					// checkout failed
					System.exit(OperiasStatus.INVALID_GIT_COMMIT.ordinal());
				}
			} catch (InvalidParameterException e) {
				System.out.println("hier3");
				System.exit(OperiasStatus.INVALID_ARGUMENTS.ordinal());
			}
			
		}
	}
}
