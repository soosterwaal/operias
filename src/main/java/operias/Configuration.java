package operias;

import java.io.File;
import java.security.InvalidParameterException;


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
	 * Source directory, containing the source files which needs to be compared to the branch in the repository directory
	 */
	private static String revisedDirectory = null;
	
	/**
	 * Repository directory, containing the sources files which are used as a base for the comparison
	 */
	private static String originalDirectory = null;
	
	/**
	 * Sets and checks the source directory, will throw an exception if it is an invalid directory
	 * A valid directory must also contain a pom.xml file to ensure that it is a maven project.
	 * @param revisedDirectory Valid local source directory.
	 */
	public static void setRevisedDirectory(String revisedDirectory) {
		if (checkValidDirectory(revisedDirectory)) {
			Configuration.revisedDirectory = revisedDirectory;
		} else {
			throw new InvalidParameterException("Error: '" + revisedDirectory + "' is not a valid directory for Operias");
		}
	}
		
	/**
	 * Sets and checks the repository directory, will throw an exception if it is an invalid directory or does not contain a git repo
	 * A valid directory must also contain a pom.xml file to ensure that it is a maven project.
	 * @param originalDirectory
	 */
	public static void setOriginalDirectory(String originalDirectory) {
		if (checkValidDirectory(originalDirectory)) {
			Configuration.originalDirectory = originalDirectory;
		} else {
			throw new InvalidParameterException("Error: '" + originalDirectory + "' is not a valid directory for Operias");
		}
	}
	
	/**
	 * Check if the directory is valid, should contain a pom.xml file
	 * @param directory Directory
	 * @return True if it is a valid directory, false otherwise
	 */
	private static boolean checkValidDirectory(String directory) {
		File file = null;
		
		try {
			file = new File(directory);
		} catch (NullPointerException e) {
			return false;
		}
		
		if (!file.isDirectory()){
			// invalid source directory
			return false;
		}
		
		File pomXML = new File(file.getAbsolutePath() +  "/pom.xml");
		
		
		return pomXML.exists();
	}
		
	/**
	 * Get the source directory name
	 * @return Source directory
	 */
	public static String getRevisedDirectory(){
		return revisedDirectory;
	}
	
	/**
	 * Get the repository directory name
	 * @return Repository directory name
	 */
	public static String getOriginalDirectory() {
		return originalDirectory;
	}
	
	/**
	 * Reset the configuration, used for testing mostly
	 */
	public static void resetConfiguration() {
		revisedDirectory = null;
		originalDirectory = null;
	}
}
