package operias;

import java.io.File;
import java.io.IOException;

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
		
		Configuration.parseArguments(args);

		try {
			setUpDirectoriesThroughGit();
		} catch (Exception e) {
			Main.printLine("[Error] Error setting up directory through git");
			System.exit(OperiasStatus.INVALID_ARGUMENTS.ordinal());
		}
		
		// Check if the directories were set
		if (Configuration.getOriginalDirectory() == null || Configuration.getRevisedDirectory() == null) {
			Main.printLine("[Error] Missing either the original or the revised directory");
			System.exit(OperiasStatus.MISSING_ARGUMENTS.ordinal());
		}
		
		new Operias().constructReport().writeHTMLReport().writeXMLReport();


		Main.printLine("[Info] Cleaning up!");
		// Remove temporary directory
		try {
			FileUtils.deleteDirectory(new File(Configuration.getTemporaryDirectory()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Main.printLine("[Info] Execution of operias was a great success!");
	}
	
	
	/**
	 * Set up the original and/or revised directories according to the provided arguments
	 * @throws Exception 
	 */
	public static void setUpDirectoriesThroughGit() throws Exception {

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
	
	/**
	 * Print a line to the console, it will only be printed if the verbose argument was given
	 * @param line
	 */
	public static void printLine(String line) {
		if(Configuration.isOutputEnabled()) {
			System.out.println(line);
		}
	}
}
