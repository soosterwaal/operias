package operias.report;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import operias.cobertura.CoberturaReport;
import operias.diff.DiffReport;

import org.junit.Test;

public class OperiasReportTest {

	@Test
	public void testSimpleOperiasReport() {
		CoberturaReport originalCoverage = new CoberturaReport(new File("src/test/resources/coverageMavenProject1.xml"));
		CoberturaReport revisedCoverage = new CoberturaReport(new File("src/test/resources/coverageMavenProject2.xml"));
		DiffReport diffReport = null;
		try {
			diffReport = new DiffReport("src/test/resources/mavenProject1", "src/test/resources/mavenProject2");
		} catch (IOException e) {
			fail();
		}
		
		OperiasReport report = new OperiasReport(originalCoverage, revisedCoverage, diffReport);
		
		assertEquals(1, 1);
		
	}
}
