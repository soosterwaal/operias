package operias;

/**
 * Containing error codes of operias
 * @author soosterwaal
 *
 */
public enum OperiasStatus {

	/**
	 * Everything was OK
	 */
	OK,
	
	/**
	 * No Arguments were specified
	 */
	NO_ARGUMENTS_SPECIFIED, 
	
	/**
	 * Invalid argument supplied
	 */
	INVALID_ARGUMENTS,

	/**
	 * There was an error during the execution of a cobertura task
	 */
	ERROR_COBERTURA_TASK_CREATION,
	
	/**
	 * There was an error during the execution of a cobertura task
	 */
	ERROR_COBERTURA_TASK_EXECUTION,
	
	/**
	 * Error in constructing the clean task for the project
	 
	 */
	ERROR_COBERTURA_CLEAN_TASK_CREATION,
	
	/**
	 * Coverage xml file was not, probably a wrong or costum target directory
	 */
	COVERAGE_XML_NOT_FOUND, 
	
	/**
	 * Given if operias is run on it self, can create a loop
	 */
	
	ERROR_COBERTURA_TASK_OPERIAS_EXECUTION, 
	
	/**
	 * XML file was either non readable, or the path was invalid
	 */
	ERROR_COBERTURA_INVALID_XML, 
	
	/**
	 * There was an error during the construction of the file diff report
	 */
	ERROR_FILE_DIFF_REPORT_GENERATION, 
	
	/**
	 * The source report diff says the files were the same, but the class was not found in the new cobertura erport
	 */
	ERROR_COBERTURA_CLASS_REPORT_NOT_FOUND, 
	
	/**
	 * Two different class were compared
	 */
	ERROR_OPERIAS_DIFF_INVALID_CLASS_COMPARISON, 
	
	/**
	 * Invalid line comparison
	 */
	ERROR_OPERIAS_INVALID_LINE_COMPARISON, 
	
	/**
	 * Error when joining the threads
	 */
	ERROR_THREAD_JOINING, 
	
	/**
	 * Checking out a commit from git was not possible
	 */
	INVALID_GIT_COMMIT, 
	
	/**
	 * Either the revised folder or the original folder is missing
	 */
	MISSING_ARGUMENTS, 
	
	/**
	 * No revised directory was found
	 */
	NO_REVISED_DIRECTORY, 
	
	/**
	 * No original directory was found
	 */
	NO_ORIGINAL_DIRECTORY, 
	
	/**
	 * Error during the cloning process
	 */
	ERROR_CLONE_GIT;
}
