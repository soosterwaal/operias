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
	 * Private class, so private constructor
	 */
	private Configuration() {
		
	}
	
	/**
	 * Source directory, containing the source files which needs to be compared to the branch in the repository directory
	 */
	private static String sourceDirectory = null;
	
	/**
	 * Repository directory, containing the sources files which are used as a base for the comparison
	 */
	private static String repositoryDirectory = null;
	
	
	/**
	 * Sets and checks the source directory, will throw an exception if it is an invalid directory
	 * @param sourceDirectory Valid local source directory.
	 */
	public static void setSourceDirectory(String sourceDirectory) {
		File file = null;
		
		try {
			file = new File(sourceDirectory);
		} catch (NullPointerException e) {
			// invalid source directory
			throw new InvalidParameterException("'" + sourceDirectory + "' is not a valid directory");
		}
		
		if (!file.isDirectory()){
			// invalid source directory
			throw new InvalidParameterException("'" + sourceDirectory + "' is not a valid directory");
		}
		
		Configuration.sourceDirectory = sourceDirectory;
	}
	
	/**
	 * Sets and checks the repository directory, will throw an exception if it is an invalid directory or does not contain a git repo
	 * @param repositoryDirectory
	 */
	public static void setRepositoryDirectory(String repositoryDirectory) {
		// First check if its a valid repository
		File file = null;
		
		try {
			file = new File(repositoryDirectory);
		} catch (NullPointerException e) {
			// invalid source directory
			throw new InvalidParameterException("'" + repositoryDirectory + "' is not a valid directory");
		}
		
		if (!file.isDirectory()){
			// invalid source directory
			throw new InvalidParameterException("'" + repositoryDirectory + "' is not a valid directory");
		}
		
		Configuration.repositoryDirectory = repositoryDirectory;
	}
	
	
	/**
	 * Get the source directory name
	 * @return Source directory
	 */
	public static String getSourceDirectory(){
		return sourceDirectory;
	}
	
	/**
	 * Get the repository directory name
	 * @return Repository directory name
	 */
	public static String getRepositoryDirectory() {
		return repositoryDirectory;
	}

	/**
	 * Reset the configuration, used for testing mostly
	 */
	public static void resetConfiguration() {
		sourceDirectory = null;
		repositoryDirectory = null;
	}
}
