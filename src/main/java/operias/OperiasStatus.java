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
	ERROR_FILE_DIFF_REPORT_GENERATION;
}
