package operias.cobertura;

/**
 * Contains information about a condition
 * @author soosterwaal
 *
 */
public class CoberturaCondition {

	/**
	 * Condition number
	 */
	private int number;
	
	/**
	 * Amount of condition covered
	 */
	private String coverage;
	
	/**
	 * Construct a new definition of coverage for a condition
	 * @param number	Condition number
	 * @param coverage	Amount of condition covered
	 */
	public CoberturaCondition(int number, String coverage) {
		this.number = number;
		this.coverage = coverage;
	}

	/**
	 * @return the number
	 */
	public int getNumber() {
		return number;
	}

	/**
	 * @return the coverage
	 */
	public String getCoverage() {
		return coverage;
	}
	
	
}
