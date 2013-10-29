package operias.report.change;

import java.util.LinkedList;

import difflib.DeleteDelta;

public class DeleteSourceChange extends SourceChange {
	
	public DeleteSourceChange(int originalLineNumber, int revisedLineNumber, DeleteDelta additions) {
		this.originalLineNumber = originalLineNumber;
		this.revisedLineNumber = revisedLineNumber;
		this.originalCoverage = new LinkedList<Boolean>();
		this.revisedCoverage = new LinkedList<Boolean>();
		this.sourceDiffDelta = additions;
		
	}
	
	
}
