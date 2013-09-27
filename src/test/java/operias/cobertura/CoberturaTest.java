package operias.cobertura;

import static org.junit.Assert.*;

import operias.OperiasStatus;

import org.junit.*;
import operias.test.general.*;

public class CoberturaTest {

    private Cobertura cobertura;
    
	/**
	 * Simple test which executes a mvn project
	 */
	@Test
	public void testCoberturaExecution(){
		cobertura = new Cobertura("src/test/resources/simpleMavenProject");
		cobertura.setOutputDirectory("target/simpleMavenProject");
		assertTrue("Executing cobertura failed", cobertura.executeCobertura());
	}
	
	/**
	 * Simple failure test when no correct maven project exists in the given folder
	 */
	@Test
	public void testFailedCoberturaExecution() {
		cobertura = new Cobertura("src/test/resources/noMavenProject");
		assertFalse("Executing cobertura failed", cobertura.executeCobertura());
	}
	
	/**
	 * Proper execution of the maven project, but the coverage file was not found, can be test by giving a random non existing output directory
	 */
	@Test
	public void testCoverageXMLNotFound() {
        System.setSecurityManager(new NoExitSecurityManager());
		
        cobertura = new Cobertura("src/test/resources/simpleMavenProject");
		cobertura.setOutputDirectory("target/randomFolder");
		try {
			cobertura.executeCobertura();
		}
	    catch (ExitException e) {
            assertEquals("Exit status invalid", OperiasStatus.COVERAGE_XML_NOT_FOUND.ordinal(), e.status);
	    }
		System.setSecurityManager(null);
		
	}
}
