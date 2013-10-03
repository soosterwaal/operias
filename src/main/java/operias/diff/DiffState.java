package operias.diff;

/**
 * Diff states of a file
 * @author soosterwaal
 *
 */
public enum DiffState {
	
	/**
	 * New file
	 */
	NEW,
	
	/**
	 * Changed file
	 */
	CHANGED,
	
	/**
	 * Deleted file
	 */
	DELETED, 
	
	/**
	 * No changes
	 */
	SAME;	
}