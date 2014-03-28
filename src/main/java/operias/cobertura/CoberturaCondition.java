package operias.cobertura;

public class CoberturaCondition {

	
	/**
	 * Number of the condition
	 */
	private int number;
	
	/**
	 * Type of the condition
	 */
	private String type;
	
	/**
	 * Percentage that the condition was covered
	 */
	private String coverage;
	
	
	/**
	 * Construct a new cobertura condition
	 * @param number
	 * @param type
	 * @param coverage
	 */
	public CoberturaCondition(int number, String type, String coverage) {
		this.number = number;
		this.type = type;
		this.coverage = coverage;
	}
}
