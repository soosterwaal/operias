package operias.report.change;

import java.util.List;

import difflib.Delta;

/**
 * Contains the coverage information about a piece of code, and if neccessary also the source diff information
 * @author soosterwaal
 *
 */
public class OperiasChange {

	/**
	 * Contains changes on the source, either a ChangeDelta or an InsertDelta
	 */
	protected Delta sourceDiffDelta;
	
	/**
	 * Original line number (same as sourceDiffDelte.getOriginal().getPosition() if set)
	 */
	protected int originalLineNumber;
	
	/**
	 * New line number (same as sourceDiffDelta.getRevision().getPosition() if set)
	 */
	protected int revisedLineNumber;
	
	/**
	 * A list of booleans with the coverage information about the line of the original file
	 */
	protected List<Boolean> originalCoverage;
	
	/**
	 * A list of booleans with the coverage information about the of the revised file
	 */
	protected List<Boolean> revisedCoverage;

	/**
	 * @return the sourceDiffDelta
	 */
	public Delta getSourceDiffDelta() {
		return sourceDiffDelta;
	}

	/**
	 * @return the originalLineNumber
	 */
	public int getOriginalLineNumber() {
		return originalLineNumber;
	}

	/**
	 * @return the revisedLineNumber
	 */
	public int getRevisedLineNumber() {
		return revisedLineNumber;
	}

	/**
	 * @return the originalCoverage
	 */
	public List<Boolean> getOriginalCoverage() {
		return originalCoverage;
	}

	/**
	 * @return the revisedCoverage
	 */
	public List<Boolean> getRevisedCoverage() {
		return revisedCoverage;
	}
	
}
