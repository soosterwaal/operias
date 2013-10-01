package operias.cobertura;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds coverage information about a method
 * @author soosterwaal
 *
 */
public class CoberturaMethod {

	/**
	 * The name of the method
	 */
	private String name;
	
	/**
	 * The line rate of the method
	 */
	private double lineRate;
	
	/**
	 * The branch rate of the method
	 */
	private double branchRate;
	
	/**
	 * A list of lines within this method
	 */
	private List<CoberturaLine> lines;
	
	/**
	 * Construct a new method definition
	 * @param name			Name of the method
	 * @param lineRate		Line rate of the method
	 * @param branchRate	Branch rate of the method
	 */
	public CoberturaMethod(String name, double lineRate, double branchRate) {
		this.name = name;
		this.lineRate = lineRate;
		this.branchRate = branchRate;
		this.lines = new ArrayList<CoberturaLine>();
	}
	
	/**
	 * Add a new cobertura line to the method
	 * @param line Cobertura Line
	 */
	public void addLine(CoberturaLine line) {
		lines.add(line);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the lineRate
	 */
	public double getLineRate() {
		return lineRate;
	}

	/**
	 * @return the branchRate
	 */
	public double getBranchRate() {
		return branchRate;
	}

	/**
	 * @return the lines
	 */
	public List<CoberturaLine> getLines() {
		return lines;
	}
}
