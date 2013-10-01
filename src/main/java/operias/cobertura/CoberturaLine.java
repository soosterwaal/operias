package operias.cobertura;

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
	 * List of conditions
	 */
	private List<CoberturaCondition> conditions;
	
	/**
	 * Construct a new coberture line instance
	 * @param number		Line number
	 * @param hits			Number of hits
	 * @param condition		Condition in line
	 */
	public CoberturaLine(int number, int hits, boolean condition){
		this.number = number;
		this.hits = hits;
		this.condition = condition;
		this.conditions = new ArrayList<CoberturaCondition>();
	}
	
	/**
	 * Add a condition to the line
	 * @param condition Condition
	 */
	public void addCondition(CoberturaCondition condition) {
		conditions.add(condition);
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
	 * @return the conditions
	 */
	public List<CoberturaCondition> getConditions() {
		return conditions;
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
}
