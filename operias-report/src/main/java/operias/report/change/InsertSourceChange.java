package operias.report.change;

import java.util.LinkedList;

import difflib.InsertDelta;

/**
 * Contains the coverage information about a piece of code which was inserted 
 * @author soosterwaal
 *
 */
public class InsertSourceChange extends SourceChange {

	public InsertSourceChange(int originalLineNumber, int revisedLineNumber, InsertDelta additions) {
		this.originalLineNumber = originalLineNumber;
		this.revisedLineNumber = revisedLineNumber;
		this.originalCoverage = new LinkedList<Boolean>();
		this.revisedCoverage = new LinkedList<Boolean>();
		this.sourceDiffDelta = additions;
		
	}
	
}
