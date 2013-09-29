package operias.cobertura;

import static org.junit.Assert.*;

import operias.OperiasStatus;

import org.junit.*;
import operias.test.general.*;

public class CoberturaTest {

    private Cobertura cobertura;
    
	/**
	 * Simple test which executes a mvn project, retrieves the report and cleans up afterwards
	 */
	@Test
	public void testCoberturaExecution(){
		cobertura = new Cobertura("src/test/resources/simpleMavenProject");
		cobertura.setOutputDirectory("target/simpleMavenProject");
		assertNotNull("Executing cobertura failed", cobertura.executeCobertura());
		
	}
	
	/**
	 * Simple failure test when no correct maven project exists in the given folder
	 */
	@Test
	public void testFailedCoberturaExecution() {
		cobertura = new Cobertura("src/test/resources/noMavenProject");
		assertNull("Executing cobertura failed", cobertura.executeCobertura());
	}
	
	/**
	 * Proper execution of the maven project, but the coverage file was not found, can be test by giving a random non existing output directory
	 */
	@Test
	public void testCoverageXMLNotFound() {
        System.setSecurityManager(new NoExitSecurityManager());
		boolean exceptionThrown = false;
		
        cobertura = new Cobertura("src/test/resources/simpleMavenProject");
		cobertura.setOutputDirectory("target/randomFolder");
		try {
			cobertura.executeCobertura();
		}
	    catch (ExitException e) {
	    	exceptionThrown = true;
            assertEquals("Exit status invalid", OperiasStatus.COVERAGE_XML_NOT_FOUND.ordinal(), e.status);
	    }
		System.setSecurityManager(null);
		assertTrue("No exception was thrown", exceptionThrown);
	}
	
	/**
	 * Test an invalid directory name, see if an error occurs
	 */
	@Test
	public void testInvalidDirectory() {
		System.setSecurityManager(new NoExitSecurityManager());
		boolean exceptionThrown = false;
		
        cobertura = new Cobertura(null);
		try {
			cobertura.executeCobertura();
		}
	    catch (ExitException e) {
	    	exceptionThrown = true;
            assertEquals("Exit status invalid", OperiasStatus.ERROR_COBERTURA_TASK_CREATION.ordinal(), e.status);
	    }
		
		exceptionThrown = false;
		
		cobertura = new Cobertura("src/../");
		try {
			cobertura.executeCobertura();
		}
	    catch (ExitException e) {
	    	exceptionThrown = true;
            assertEquals("Exit status invalid", OperiasStatus.ERROR_COBERTURA_TASK_OPERIAS_EXECUTION.ordinal(), e.status);
	    }

		assertTrue("No exception was thrown", exceptionThrown);
		
		System.setSecurityManager(null);
	}
	
}
