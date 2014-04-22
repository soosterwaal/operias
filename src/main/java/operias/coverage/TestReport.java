package operias.coverage;

public class TestReport {

	/**
	 * Class name
	 */
	private String className;
	
	/**
	 * Test method name
	 */
	private String caseName;
	
	/**
	 * Result type
	 */
	private TestResultType result;
	
	/**
	 * Error of failure message
	 */
	private String message;
	
	/**
	 * Error or failure type
	 */
	private String type;
	
	/**
	 * Error or failure trace
	 */
	private String trace;

	/**
	 * Create a succesfully executed test case
	 * @param className
	 * @param caseName
	 */
	public TestReport(String className, String caseName) {
		this.className = className;
		this.caseName = caseName;
		this.result = TestResultType.SUCCESS;
	}
	
	/**
	 * Construct a tst case report
	 * @param className Class name
	 * @param caseName Method/Case anme
	 * @param result Result type
	 * @param message Error/Failure message
	 * @param type Error/Failure type
	 * @param trace Error/Failure trace
	 */
	public TestReport(String className, String caseName, TestResultType result, String message, String type, String trace) {
		this.className = className;
		this.caseName = caseName;
		this.result = result;
		this.message = message;
		this.type = type;
		this.trace = trace;
	}
	
	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * @return the caseName
	 */
	public String getCaseName() {
		return caseName;
	}

	/**
	 * @return the result
	 */
	public TestResultType getResult() {
		return result;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return the trace
	 */
	public String getTrace() {
		return trace;
	}
}
