package operias.cobertura;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;

import operias.OperiasStatus;
import operias.test.general.ExitException;
import operias.test.general.NoExitSecurityManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CoberturaReportTest {

	/**
	 * Set up the security manager
	 */
	@Before
	public void setUp() {
		System.setSecurityManager(new NoExitSecurityManager());	
	}
	
	/**
	 * Tear down the security manager
	 */
	@After
	public void tearDown() {
		System.setSecurityManager(null);	
	}
	
	/**
	 * Test invalid coverage XML files
	 */
	@Test
	public void testInvalidCoverageXML() {
		boolean exceptionThrown = false;
		
		try {
			new CoberturaReport(null);
		} catch(ExitException e) {
			exceptionThrown = true;
			assertEquals("Wrong exit code", OperiasStatus.ERROR_COBERTURA_INVALID_XML.ordinal(), e.status);
		}
		assertTrue("No exception was thrown", exceptionThrown);
		
		exceptionThrown = false;
		
		try {
			new CoberturaReport(new File(""));
		} catch(ExitException e) {
			exceptionThrown = true;
			assertEquals("Wrong exit code", OperiasStatus.ERROR_COBERTURA_INVALID_XML.ordinal(), e.status);
		}
		assertTrue("No exception was thrown", exceptionThrown);
		
		exceptionThrown = false;
		
		try {
			new CoberturaReport(new File("src/test/resources/nonExistingFile.xml"));
		} catch(ExitException e) {
			exceptionThrown = true;
			assertEquals("Wrong exit code", OperiasStatus.ERROR_COBERTURA_INVALID_XML.ordinal(), e.status);
		}
		assertTrue("No exception was thrown", exceptionThrown);
	}
	
	/**
	 * Test the loading of a coverage xml
	 */
	@Test
	public void testLoadXML() {
		double delta = 0.0001;
		File file = new File("src/test/resources/coverage.xml");
		
		CoberturaReport report = new CoberturaReport(file);
		
		assertEquals("Line rate not equal", 0.7578125, report.getLineRate(),  delta);
		assertEquals(0.75,report.getBranchRate(), delta);
		
		
		ArrayList<CoberturaPackage> packages = (ArrayList<CoberturaPackage>) report.getPackages();
		assertEquals(2, packages.size());
		
		CoberturaPackage cPackage = packages.get(0);
		
		assertEquals("operias", cPackage.getName());
		assertEquals(0.6376811594202898, cPackage.getLineRate(), delta);
		assertEquals(0.6, cPackage.getBranchRate(), delta);
		
		
		// Check classes
		ArrayList<CoberturaClass> classes = (ArrayList<CoberturaClass>) cPackage.getClasses();
		
		assertEquals(4, classes.size());
		
		CoberturaClass cClass = classes.get(0);
		assertEquals("operias.Configuration", cClass.getName());
		assertEquals("operias/Configuration.java", cClass.getFileName());
		assertEquals(0.9230769230769231, cClass.getLineRate(), delta);
		assertEquals(1.0, cClass.getBranchRate(), delta);
		
		
		// Check methods
		ArrayList<CoberturaMethod> methods = (ArrayList<CoberturaMethod>) cClass.getMethods();
		
		assertEquals(8, methods.size());
		
		CoberturaMethod cMethod = methods.get(2);

		assertEquals("checkValidDirectory", cMethod.getName());
		assertEquals(1.0, cMethod.getLineRate(), delta);
		assertEquals(1.0, cMethod.getBranchRate(), delta);
		
		// Check lines in method
		ArrayList<CoberturaLine> lines = (ArrayList<CoberturaLine>) cMethod.getLines();
		
		assertEquals(9, lines.size());
		
		CoberturaLine cNoConditionLine = lines.get(0);
		
		assertEquals(64, cNoConditionLine.getNumber());
		assertEquals(40, cNoConditionLine.getHits());
		assertFalse(cNoConditionLine.isCondition());
		
		CoberturaLine cConditionLine = lines.get(5);
	
		assertEquals(72, cConditionLine.getNumber());
		assertEquals(30, cConditionLine.getHits());
		assertTrue(cConditionLine.isCondition());
		
		// Check conditions
		ArrayList<CoberturaCondition> conditions = (ArrayList<CoberturaCondition>) cConditionLine.getConditions();
		
		assertEquals(1, conditions.size());
		
		CoberturaCondition cCondition = conditions.get(0);
		
		assertEquals(0, cCondition.getNumber());
		assertEquals("100%" , cCondition.getCoverage());
		
		// Check lines in class
		ArrayList<CoberturaLine> classLines = (ArrayList<CoberturaLine>) cClass.getLines();
		
		assertEquals(26, classLines.size());
		
		assertEquals(cNoConditionLine, classLines.get(12));
		assertEquals(cConditionLine, classLines.get(17));
		
	}
}
