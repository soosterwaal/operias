package operias.report.change;

import java.util.LinkedList;

import difflib.DeleteDelta;

/**
 * Contains the coverage information about a piece of code which was deleted.
 * @author soosterwaal
 *
 */
public class DeleteSourceChange extends SourceChange {
	
	public DeleteSourceChange(int originalLineNumber, int revisedLineNumber, DeleteDelta additions) {
		this.originalLineNumber = originalLineNumber;
		this.revisedLineNumber = revisedLineNumber;
		this.originalCoverage = new LinkedList<Boolean>();
		this.revisedCoverage = new LinkedList<Boolean>();
		this.sourceDiffDelta = additions;
		
	}
	
	
}
