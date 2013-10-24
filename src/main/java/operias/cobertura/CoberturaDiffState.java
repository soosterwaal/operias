package operias.cobertura;

public enum CoberturaDiffState {

	/**
	 * No coverage information known
	 */
	UNKNOWN,
	
	/**
	 * The coverage was the same
	 */
	SAME,
	
	/**
	 * Coverage was increased
	 */
	INCREASED,
	
	/**
	 * Coverage was decreased
	 */
	DECREASED;
}
