package operias.report.change;

import java.util.LinkedList;

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
	
	public CoverageDecreaseChange(int revisedLineNumber) {
		this(-1, revisedLineNumber);
	}
}
