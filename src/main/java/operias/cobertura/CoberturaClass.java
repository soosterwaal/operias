package operias.cobertura;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds information about the coverage of a certain class
 * @author soosterwaal
 *
 */
public class CoberturaClass {

	/**
	 * The name of the class
	 */
	private String name;
	
	/**
	 * File name of the class
	 */
	private String fileName;
	
	/**
	 * The line rate of the class
	 */
	private double lineRate;
	
	/**
	 * The branch rate of the class
	 */
	private double branchRate;
	
	/**
	 * A list of methods within this class
	 */
	private List<CoberturaMethod> methods;
	

	/**
	 * A list of all lines within this class
	 */
	private List<CoberturaLine> lines;
	
	
	/**
	 * Construct a cobertura class definition
	 * @param name			Name of the class
	 * @param fileName		File name of the class
	 * @param lineRate		Line rate of the class
	 * @param branchRate	Branch rate of the class
	 */
	public CoberturaClass(String name, String fileName, double lineRate, double branchRate) {
		this.name = name;
		this.fileName = fileName;
		this.lineRate = lineRate;
		this.branchRate = branchRate;
		this.methods = new ArrayList<CoberturaMethod>();
		this.lines = new ArrayList<CoberturaLine>();
	}
	
	/**
	 * Add a new line to the class
	 * @param line Cobertura coverage line
	 */
	public void addLine(CoberturaLine line) {
		lines.add(line);
	}
	
	/**
	 * Add a new method to the class
	 * @param method Cobertura coverage method
	 */
	public void addMethod(CoberturaMethod method) {
		methods.add(method);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
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
	 * @return the methods
	 */
	public List<CoberturaMethod> getMethods() {
		return methods;
	}

	/**
	 * @return the lines
	 */
	public List<CoberturaLine> getLines() {
		return lines;
	}
}
