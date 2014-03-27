package operias;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;



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

		
		// Check if the directories were set
		if (Configuration.getOriginalDirectory() == null || Configuration.getRevisedDirectory() == null) {
			// if not, try to set up directories through git
			Configuration.setUpDirectoriesThroughGit();
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
	 * Print a line to the console, it will only be printed if the verbose argument was given
	 * @param line
	 */
	public static void printLine(String line) {
		if(Configuration.isOutputEnabled()) {
			System.out.println(line);
		}
	}
}
