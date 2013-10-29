package operias.report.change;

import java.util.LinkedList;

import difflib.InsertDelta;

public class InsertSourceChange extends SourceChange {

	public InsertSourceChange(int originalLineNumber, int revisedLineNumber, InsertDelta additions) {
		this.originalLineNumber = originalLineNumber;
		this.revisedLineNumber = revisedLineNumber;
		this.originalCoverage = new LinkedList<Boolean>();
		this.revisedCoverage = new LinkedList<Boolean>();
		this.sourceDiffDelta = additions;
		
	}
	
}
