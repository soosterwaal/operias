package operias.coverage;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

import operias.OperiasStatus;
import operias.coverage.CoberturaClass;
import operias.coverage.CoberturaLine;
import operias.coverage.CoberturaPackage;
import operias.coverage.CoverageReport;
import operias.test.general.ExitException;
import operias.test.general.NoExitSecurityManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CoverageReportTest {

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
			new CoverageReport((File)null, "").constructReport();;
		} catch(ExitException e) {
			exceptionThrown = true;
			assertEquals("Wrong exit code", OperiasStatus.ERROR_COBERTURA_INVALID_XML.ordinal(), e.status);
		}
		assertTrue("No exception was thrown", exceptionThrown);
		
		exceptionThrown = false;
		
		try {
			new CoverageReport(new File(""), "").constructReport();;
		} catch(ExitException e) {
			exceptionThrown = true;
			assertEquals("Wrong exit code", OperiasStatus.ERROR_COBERTURA_INVALID_XML.ordinal(), e.status);
		}
		assertTrue("No exception was thrown", exceptionThrown);
		
		exceptionThrown = false;
		
		try {
			new CoverageReport(new File("src/test/resources/nonExistingFile.xml"), "").constructReport();;
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

		CoverageReport report = new CoverageReport(file, "").constructReport();
		
		assertEquals("Line rate not equal", 0.7578125, report.getLineRate(),  delta);
		assertEquals(0.75,report.getConditionRate(), delta);
		
		
		ArrayList<CoberturaPackage> packages = (ArrayList<CoberturaPackage>) report.getPackages();
		assertEquals(2, packages.size());
		
		CoberturaPackage cPackage = packages.get(0);
		
		assertEquals("operias", cPackage.getName());
		assertEquals(0.6376811594202898, cPackage.getLineRate(), delta);
		assertEquals(0.6, cPackage.getConditionRate(), delta);
		
		
		// Check classes
		ArrayList<CoberturaClass> classes = (ArrayList<CoberturaClass>) cPackage.getClasses();
		
		assertEquals(4, classes.size());
		
		CoberturaClass cClass = classes.get(0);
		assertEquals("operias.Configuration", cClass.getName());
		assertEquals("operias/Configuration.java", cClass.getFileName());
		assertEquals(0.9230769230769231, cClass.getLineRate(), delta);
		assertEquals(1.0, cClass.getConditionRate(), delta);
		
		// Check lines in class
		LinkedList<CoberturaLine> classLines = (LinkedList<CoberturaLine>) cClass.getLines();
		
		assertEquals(26, classLines.size());
		
		
	}
}
