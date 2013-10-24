package operias.report;

import java.util.List;

import difflib.Delta;

public class OperiasChange {

	/**
	 * Contains changes on the source, either a ChangeDelta or an InsertDelta
	 */
	private Delta sourceDiffDelta;
	
	/**
	 * Original line number (same as sourceDiffDelte.getOriginal().getPosition() if set)
	 */
	private int originalLineNumber;
	
	/**
	 * New line number (same as sourceDiffDelta.getRevision().getPosition() if set)
	 */
	private int revisedLineNumber;
	
	private List<Boolean> originalCoverage;
	
	private List<Boolean> revisedCoverage;
	
}
