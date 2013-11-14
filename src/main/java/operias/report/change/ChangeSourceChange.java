package operias.report.change;

import java.util.LinkedList;

import difflib.ChangeDelta;

/**
 * Contains information for a change in the source and the coverage for both pieces of code
 * @author soosterwaal
 *
 */
public class ChangeSourceChange extends SourceChange {
	
	public ChangeSourceChange(int originalLineNumber, int revisedLineNumber, ChangeDelta additions) {
		this.originalLineNumber = originalLineNumber;
		this.revisedLineNumber = revisedLineNumber;
		this.originalCoverage = new LinkedList<Boolean>();
		this.revisedCoverage = new LinkedList<Boolean>();
		this.sourceDiffDelta = additions;
		
	}
	
}
