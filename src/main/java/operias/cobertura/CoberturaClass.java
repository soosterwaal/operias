package operias.cobertura;

import java.util.ArrayList;
import java.util.LinkedList;
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
	 * Name of the package the class is in
	 */
	private String packageName;
	
	
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
	 * A list of all lines within this class
	 */
	private LinkedList<CoberturaLine> lines;
	
	
	/**
	 * Construct a cobertura class definition
	 * @param name			Name of the class
	 * @param fileName		File name of the class
	 * @param lineRate		Line rate of the class
	 * @param branchRate	Branch rate of the class
	 */
	public CoberturaClass(String name, String fileName, String packageName, double lineRate, double branchRate) {
		this.name = name;
		this.fileName = fileName;
		this.packageName = packageName;
		this.lineRate = lineRate;
		this.branchRate = branchRate;
		this.lines = new LinkedList<CoberturaLine>();
	}
	
	/**
	 * Add a new line to the class
	 * @param line Cobertura coverage line
	 */
	public void addLine(CoberturaLine line) {
		lines.add(line);
	}
	
	/**
	 * Get the information of the give line number
	 * @param lineNumber
	 */
	public CoberturaLine tryGetLine(int lineNumber) {
		for(CoberturaLine line : lines) {
			if (line.getNumber() == lineNumber) {
				return line;
			} else if (line.getNumber() > lineNumber) {
				break;
			}
		}
		
		return null;
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
	 * @return the lines
	 */
	public List<CoberturaLine> getLines() {
		return lines;
	}

	/**
	 * Get the max line number
	 * @return
	 */
	public int getMaxLineNumber() {
		return lines.getLast().getNumber();
	}
}
