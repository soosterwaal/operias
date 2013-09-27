package operias;

import static org.junit.Assert.*;

import operias.test.general.*;

import org.junit.After;
import org.junit.Before;

public class MainTest {
	
    Main main;
	
	/**
	 * Set up the test case
	 */
	@Before
	public void setUp() {		
		main = new Main();
        System.setSecurityManager(new NoExitSecurityManager());
        Configuration.resetConfiguration();
	}
	
	/**
	 * Reset the security manager
	 */
	@After
	public void tearDown() {
        System.setSecurityManager(null);		
	}
	
	/**
	 * Test the main with invalid count of arguments
	 */
	public void testInvalidArgumentCount() {
	    try {
	    	Main.main(new String[1]);
	    }
	    catch (ExitException e) {
            assertEquals("Exit status", OperiasStatus.NO_ARGUMENTS_SPECIFIED.ordinal(), e.status);
	    }
	}
	
	/**
	 * Test invalid arguments
	 */
	public void testInvalidArguments() {

	    try {
	    	String[] args = new String[2];
	    	args[0] = "non-existing-folder/";
	    	args[1] = "src/";
	    	
	    	Main.main(args);
	    }
	    catch (ExitException e) {
            assertEquals("Exit status", OperiasStatus.INVALID_ARGUMENTS.ordinal(), e.status);
	    }
	}
	
	/**
	 * Test the main with invalid count of arguments
	 */
	public void testValidArgumentCount() {
		boolean exitExceptionThrown = false;
	    try {
	    	String[] args = new String[2];
	    	args[0] = "src/test/resources/validMavenDirectory";
	    	args[1] = "src/test/resources/validMavenDirectory";
	    	
	    	Main.main(args);
	    }
	    catch (ExitException e) {
	    	exitExceptionThrown = true;
	    }
	    
	    assertFalse(exitExceptionThrown);
	}
}
