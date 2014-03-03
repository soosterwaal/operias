package operias;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.security.InvalidParameterException;
import java.util.Collections;

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
	 
	 * 
	 */
	public static void main(String[] args) {
		
		parseArguments(args);

		new Operias().constructReport().writeSite();


		System.out.println("[Info] Cleaning up!");
		// Remove temporary directory
		try {
			FileUtils.deleteDirectory(new File(Configuration.getTemporaryDirectory()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		

		System.out.println("[Info] Execution of operias was a great success!");
	}
	
	/**
	 * Parse the arguments passed by the command line
	 * @param args
	 */
	private static void parseArguments(String[] args) {
		boolean enableOutput = false;
		try {
			int i = 0;
			while(i < args.length) {
				if (args[i].equals("-d") || args[i].equals("--destination-directory")) {
					// Destination folder
					Configuration.setDestinationDirectory(args[i + 1]);
					i += 2;
				} else if (args[i].equals("-rd") || args[i].equals("--revised-directory")) {
					Configuration.setRevisedDirectory(args[i + 1]);
					i += 2;
				} else if (args[i].equals("-od") || args[i].equals("--original-directory")) {
					Configuration.setOriginalDirectory(args[i + 1]);
					i += 2;
				} else if (args[i].equals("-rpd") || args[i].equals("--repository-directory")) {
					Configuration.setGitDirectory(args[i + 1]);
					i += 2;
				} else if (args[i].equals("-ru") || args[i].equals("--repository-url")) {
					Configuration.setRepositoryURL(args[i + 1]);
					i += 2;
				} else if (args[i].equals("-oc") || args[i].equals("--original-commit-id")) {
					Configuration.setOriginalCommitID(args[i + 1]);
					i += 2;
				} else if (args[i].equals("-rc") || args[i].equals("--revised-commit-id")) {
					Configuration.setRevisedCommitID(args[i + 1]);
					i += 2;
				} else if (args[i].equals("-td") || args[i].equals("--temp-directory")) {
					Configuration.setTemporaryDirectory(args[i + 1]);
					i += 2;
				} else if (args[i].equals("-v") || args[i].equals("--verbose")) {
					enableOutput = true;
					i++;
				} else {
					System.out.println("[Error] Unknown option \"" + args[i] + "\"");
					System.exit(OperiasStatus.INVALID_ARGUMENTS.ordinal());
				}
				
			}

		} catch (Exception e) {
			System.exit(OperiasStatus.INVALID_ARGUMENTS.ordinal());
		}
		
		// Disable the output if needed
		if (!enableOutput) {
			OutputStream out = new OutputStream() {
		          public void write(int b) {
		          }
			};
			System.setOut(new PrintStream(out));
		}

		setUpGitDirectories();
		
		// Check if the directories were set
		if (Configuration.getOriginalDirectory() == null || Configuration.getRevisedDirectory() == null) {
			System.out.println("[Error] Missing either the original or the revised directory");
			System.exit(OperiasStatus.MISSING_ARGUMENTS.ordinal());
		}
	}
	
	/**
	 * Set up the original and/or revised directories according to the provided arguments
	 */
	private static void setUpGitDirectories() {

		if (Configuration.getRepositoryURL() != null) {
			try {
				Configuration.setGitDirectory(Git.clone(Configuration.getRepositoryURL()));
			} catch (Exception e) {
				System.out.println("[Error] Unable to clone \"" + Configuration.getRepositoryURL() + "\"");
				System.exit(OperiasStatus.ERROR_CLONE_GIT.ordinal());
			}
		}
		
		// initialy set the derived directory to the git directory
		String derivedDirectory = Configuration.getGitDirectory();
		
		String originalCommitID = Configuration.getOriginalCommitID();
		if (originalCommitID != null) {
			// Got an original commit id, now either use the git directory, or else the revised directory
		
			// If not found, use the revised direcoty
			if (derivedDirectory == null) {
				derivedDirectory = Configuration.getRevisedDirectory();
			} 

			// If still not found, we cannot execute operias!
			if (derivedDirectory == null) {
				System.out.println("[Error] Unable to find a directory to checkout commit \""  + originalCommitID +"\"");
				System.exit(OperiasStatus.NO_REVISED_DIRECTORY.ordinal());
			}
			
			// Checkout!
			try {
				Configuration.setOriginalDirectory(Git.checkoutCommit(derivedDirectory, originalCommitID));
			} catch (Exception e) {
				System.out.println("[Error] Unable to checkout commit \"" + originalCommitID + "\"");
				System.exit(OperiasStatus.INVALID_GIT_COMMIT.ordinal());
			}
		}
		
		String revisedCommitID = Configuration.getRevisedCommitID();
		if (revisedCommitID != null) {
			// Got an original commit id, now either use the git directory, or else the revised directory
		
			// If not found, use the original direcoty
			if (derivedDirectory == null) {
				derivedDirectory = Configuration.getOriginalDirectory();
			} 

			// If still not found, we cannot execute operias!
			if (derivedDirectory == null) {
				System.out.println("[Error] Unable to find a directory to checkout commit \""  + revisedCommitID +"\"");
				System.exit(OperiasStatus.NO_ORIGINAL_DIRECTORY.ordinal());
			}
			
			// Checkout!
			try {
				Configuration.setRevisedDirectory(Git.checkoutCommit(derivedDirectory, revisedCommitID));
			} catch (Exception e) {
				System.out.println("[Error] Unable to checkout commit \"" + revisedCommitID + "\"");
				System.exit(OperiasStatus.INVALID_GIT_COMMIT.ordinal());
			}
		}	
	}
	
}
