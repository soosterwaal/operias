package operias.output.xml;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import operias.cobertura.CoberturaReport;
import operias.diff.DiffReport;
import operias.report.OperiasReport;

import org.junit.Before;
import org.junit.Test;

public class XMLReportTest {

	CoberturaReport originalCoverage,revisedCoverage ;
	DiffReport diffReport = null;
	
	@Before
	public void setUp() {
		originalCoverage = new CoberturaReport(new File("src/test/resources/coverageMavenProject1.xml"));
		revisedCoverage = new CoberturaReport(new File("src/test/resources/coverageMavenProject2.xml"));

		try {
			diffReport = new DiffReport("src/test/resources/mavenProject1", "src/test/resources/mavenProject2");
		} catch (IOException e) {
			fail();
		}
		
	}
	@Test
	public void testBasicXMLReport()  {
		
		OperiasReport report = new OperiasReport(originalCoverage, revisedCoverage, diffReport);
		
		XMLReport xmlReport = new XMLReport(report);
		
		try {
			xmlReport.generateReport();
		} catch (ParserConfigurationException e) {
			fail(e.getMessage());
		} catch (TransformerException e) {
			fail(e.getMessage());	
		} catch (IOException e) {
			fail(e.getMessage());	
		}
		
	}
}
