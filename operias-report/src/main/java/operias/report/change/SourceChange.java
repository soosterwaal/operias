package operias.report.change;

/**
 * Contains both the coverage and source information for a piece of code
 * @author soosterwaal
 *
 */
public abstract class SourceChange extends OperiasChange {
	
	/**
	 * Add a boolean to the list of the revised coverage information
	 * @param covered
	 */
	public void addOriginalCoverageLine(Boolean covered) {
		originalCoverage.add(covered);
	}
	
	/**
	 * Add a boolean to the list of the revised coverage information
	 * @param covered
	 */
	public void addRevisedCoverageLine(Boolean covered) {
		revisedCoverage.add(covered);
	}
}
