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
	
	/**
	 * Returns the amount of relevant lines in the original piece of code.
	 * A relevant line is a line of which the coverage can be measured.
	 * @return
	 */
	public int countOriginalRelevantLines() {
		return countRelevantLines(originalCoverage);
	}

	/**
	 * Returns the amount of relevant lines in the original piece of code.
	 * A relevant line is a line of which the coverage can be measured.
	 * @return
	 */
	public int countRevisedRelevantLines() {
		return countRelevantLines(revisedCoverage);
	}
	
	/**
	 * Count the amount of relevant lines in a list of booleans.
	 * A line is relevant if the value is not null, so that there is information about the coverage
	 * @param lines
	 * @return
	 */
	private int countRelevantLines(List<Boolean> lines) {
		int count = 0 ;
		
		for(Boolean covered : lines) {
			if (covered != null) {
				count++;
			}
		}
		
		return count;
	}
	
	
	/**
	 * Count the amount of lines which were coverge in the original piece of code
	 * @return
	 */
	public int countOriginalLinesCovered() {
		return countLinesCovered(originalCoverage);
	}
	
	/**
	 * Count the amount of lines which were coverge in the revised piece of code
	 * @return
	 */
	public int countRevisedLinesCovered() {
		return countLinesCovered(revisedCoverage);
	}
	
	/**
	 * Count the amount of lines which were coverge in a piece of code, thus the amount of true's in the list
	 * @return
	 */
	private int countLinesCovered(List<Boolean> lines) {
		int count = 0 ;
		
		for(Boolean covered : lines) {
			if (covered != null && covered == true) {
				count++;
			}
		}
		
		return count;
	}
}
