package operias.report.change;

import java.util.LinkedList;

/**
 * Contains the information for a line of which the coverage was increased
 * @author soosterwaal
 *
 */
public class CoverageIncreaseChange extends OperiasChange {

	public CoverageIncreaseChange(int originalLineNumber, int revisedLineNumber) {
		this.originalLineNumber = originalLineNumber;
		this.revisedLineNumber = revisedLineNumber;
		this.originalCoverage = new LinkedList<Boolean>();
		this.revisedCoverage = new LinkedList<Boolean>();
		this.sourceDiffDelta = null;
		
		this.originalCoverage.add(false);
		this.revisedCoverage.add(true);
	}
	
	public CoverageIncreaseChange(int revisedLineNumber) {
		this(-1, revisedLineNumber);
	}
}
