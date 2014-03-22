package operias;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

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
				} else if (args[i].equals("-ru") || args[i].equals("--repository-url")) {
					Configuration.setOriginalRepositoryURL(args[i + 1]);
					Configuration.setRevisedRepositoryURL(args[i + 1]);
					i += 2;
				} else if (args[i].equals("-oru") || args[i].equals("--original-repository-url")) {
					Configuration.setOriginalRepositoryURL(args[i + 1]);
					i += 2;
				} else if (args[i].equals("-rru") || args[i].equals("--revised-repository-url")) {
					Configuration.setRevisedRepositoryURL(args[i + 1]);
					i += 2;
				} else if (args[i].equals("-oc") || args[i].equals("--original-commit-id")) {
					Configuration.setOriginalCommitID(args[i + 1]);
					i += 2;
				} else if (args[i].equals("-rc") || args[i].equals("--revised-commit-id")) {
					Configuration.setRevisedCommitID(args[i + 1]);
					i += 2;
				} else if (args[i].equals("-obn") || args[i].equals("--original-branch-name")) {
					Configuration.setOriginalBranchName(args[i + 1]);
					i += 2;
				} else if (args[i].equals("-rbn") || args[i].equals("--revised-branch-name")) {
					Configuration.setRevisedBranchName(args[i + 1]);
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

		try {
			setUpDirectoriesThroughGit();
		} catch (Exception e) {
			System.out.println("[Error] Error setting up directory through git");
			System.exit(OperiasStatus.INVALID_ARGUMENTS.ordinal());
		}
		
		// Check if the directories were set
		if (Configuration.getOriginalDirectory() == null || Configuration.getRevisedDirectory() == null) {
			System.out.println("[Error] Missing either the original or the revised directory");
			System.exit(OperiasStatus.MISSING_ARGUMENTS.ordinal());
		}
	}
	
	/**
	 * Set up the original and/or revised directories according to the provided arguments
	 * @throws Exception 
	 */
	private static void setUpDirectoriesThroughGit() throws Exception {

		if (Configuration.getOriginalDirectory() == null) {
			// No original directory found, check for git
			if (Configuration.getOriginalRepositoryURL() != null) {

				Configuration.setOriginalDirectory(Git.clone(Configuration.getOriginalRepositoryURL()));
			
				if (Configuration.getOriginalBranchName() != null) {
					Git.checkout(Configuration.getOriginalDirectory(), Configuration.getOriginalBranchName());
				}
				
				if (Configuration.getOriginalCommitID() != null) {
					Git.checkout(Configuration.getOriginalDirectory(), Configuration.getOriginalCommitID());
				}
			}
		}
		
		if (Configuration.getRevisedDirectory() == null) {
			// No original directory found, check for git
			if (Configuration.getRevisedRepositoryURL() != null) {

				Configuration.setRevisedDirectory(Git.clone(Configuration.getRevisedRepositoryURL()));
			
				if (Configuration.getRevisedBranchName() != null) {
					Git.checkout(Configuration.getRevisedDirectory(), Configuration.getRevisedBranchName());
				}
				
				if (Configuration.getRevisedCommitID() != null) {
					Git.checkout(Configuration.getRevisedDirectory(), Configuration.getRevisedCommitID());
				}
			}
		}
	}
}
