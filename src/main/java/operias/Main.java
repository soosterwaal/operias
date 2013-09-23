package operias;

/**
 * Main class of operias
 * @author soosterwaal
 *
 */
public class Main {
	
	/**
	 * Start up the operias tool
	 * @param args Contains serveral arguments, for example the source directories which
	 * needs to be compared and to which branch it must be compared.
	 * 
	 * The first argument is the directory of the new source files
	 * The second argument is the directory of the github repository
	 * The third argument is the branch name
	 */
	public static void main(String[] args) {
		
		// If not enough arguments specified, exit
		if (args.length < 3) {
			System.exit(OperiasStatus.NO_ARGUMENTS_SPECIFIED.ordinal());
		}
		
		
	}
}
