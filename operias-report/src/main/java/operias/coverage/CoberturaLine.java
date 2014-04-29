package operias.coverage;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains coverage information for a line of code
 * @author sebas
 *
 */
public class CoberturaLine {

	/**
	 * Line numer
	 */
	private int number;
	
	/**
	 * Number of hits
	 */
	private int hits;
	
	/**
	 * True if the line has a condition
	 */
	private boolean condition;
	
	/**
	 * True if the branches were covered
	 */
	private boolean conditionCompletelyCovered;
	
	/**
	 * List of conditions on this line
	 */
	private List<CoberturaCondition> conditions;
	
	/**
	 * Construct a new coberture line instance
	 * @param number		Line number
	 * @param hits			Number of hits
	 * @param condition		Condition in line
	 */
	public CoberturaLine(int number, int hits, boolean condition, boolean conditionCompletelyCovered){
		this.number = number;
		this.hits = hits;
		this.condition = condition;
		this.conditionCompletelyCovered = conditionCompletelyCovered;
		this.conditions = new ArrayList<CoberturaCondition>();
	}
	
	

	/**
	 * @return the number
	 */
	public int getNumber() {
		return number;
	}

	/**
	 * @return the hits
	 */
	public int getHits() {
		return hits;
	}

	/**
	 * @return the condition
	 */
	public boolean isCondition() {
		return condition;
	}

	/**
	 * @return the conditionCompletelyCovered
	 */
	public boolean isConditionCompletelyCovered() {
		return conditionCompletelyCovered;
	}
	
	
	/**
	 * Override the equals implementation
	 */
	public boolean equals(Object other) {
		if (!(other instanceof CoberturaLine)) {
			return false;
		}
		CoberturaLine otherLine = (CoberturaLine) other;
		
		
		return otherLine.getHits() == getHits() && 
				otherLine.getNumber() == getNumber() && 
				otherLine.isCondition() == isCondition();
	}



	/**
	 * Check if the line can be considered covered
	 */
	public Boolean isCovered() {
		return condition ? conditionCompletelyCovered : hits > 0;
	}



	/**
	 * @return the conditions
	 */
	public List<CoberturaCondition> getConditions() {
		return conditions;
	}


	/**
	 * Add a condition to the lines
	 * @param cCondition
	 */
	public void addCondition(CoberturaCondition cCondition) {
		conditions.add(cCondition);
	}
	
	
}
