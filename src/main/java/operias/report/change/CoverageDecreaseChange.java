package operias.report.change;

import java.util.LinkedList;

/**
 * Contains the information for a line of which the coverage was decreased
 * @author soosterwaal
 *
 */
public class CoverageDecreaseChange extends OperiasChange {

	public CoverageDecreaseChange(int originalLineNumber, int revisedLineNumber) {
		this.originalLineNumber = originalLineNumber;
		this.revisedLineNumber = revisedLineNumber;
		this.originalCoverage = new LinkedList<Boolean>();
		this.revisedCoverage = new LinkedList<Boolean>();
		this.sourceDiffDelta = null;
		
		this.originalCoverage.add(true);
		this.revisedCoverage.add(false);
	}
	
}
