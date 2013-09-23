package operias;

import static org.junit.Assert.*;

import java.security.Permission;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
public class MainTest {
	
	protected static class ExitException extends SecurityException 
    {
        public final int status;
        public ExitException(int status) 
        {
            super("There is no escape!");
            this.status = status;
        }
    }

    private static class NoExitSecurityManager extends SecurityManager 
    {
        @Override
        public void checkPermission(Permission perm) 
        {
            // allow anything.
        }
        @Override
        public void checkPermission(Permission perm, Object context) 
        {
            // allow anything.
        }
        @Override
        public void checkExit(int status) 
        {
            super.checkExit(status);
            throw new ExitException(status);
        }
    }
	Main main;
	
	/**
	 * Set up the test case
	 */
	@Before
	public void setUp() {		
		main = new Main();
        System.setSecurityManager(new NoExitSecurityManager());
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
	@Test
	public void testInvalidArgumentCount() {
	    try {
	    	Main.main(new String[2]);
	    }
	    catch (ExitException e) {
            assertEquals("Exit status", OperiasStatus.NO_ARGUMENTS_SPECIFIED.ordinal(), e.status);
	    }
	}
	
	/**
	 * Test the main with invalid count of arguments
	 */
	@Test
	public void testValidArgumentCount() {
		boolean exitExceptionThrown = false;
	    try {
	    	Main.main(new String[3]);
	    }
	    catch (ExitException e) {
	    	exitExceptionThrown = true;
	    }
	    
	    assertFalse(exitExceptionThrown);
	}
}
