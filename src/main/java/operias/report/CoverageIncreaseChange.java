package operias.report;

import java.util.LinkedList;

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
