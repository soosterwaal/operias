package operias;

/**
 * Main class of operias
 * @author sebas
 *
 */
public class Main {
	
	
	/**
	 * Start up the operias tool
	 * @param args Contains serveral arguments, for example the source directories which
	 * needs to be compared and to which branch it must be compared.
	 */
	public static void main(String[] args) {
		
		// If not enough arguments specified, exit
		if (args.length < 3) {
			System.exit(OperiasStatus.NO_ARGUMENTS_SPECIFIED.ordinal());
		}
		
	}
}
