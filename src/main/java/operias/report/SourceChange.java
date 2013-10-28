package operias.report;

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
