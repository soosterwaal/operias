package operias.output.xml;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import operias.Configuration;
import operias.cobertura.CoberturaReport;
import operias.diff.DiffReport;
import operias.report.OperiasReport;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

public class XMLReportTest {

	CoberturaReport originalCoverage,revisedCoverage ;
	DiffReport diffReport = null;
	
	@Before
	public void setUp() {
		
		Configuration.setDestinationDirectory(new File("").getAbsolutePath() + "/target");
		
		originalCoverage = new CoberturaReport(new File("src/test/resources/coverageMavenProject1.xml"));
		revisedCoverage = new CoberturaReport(new File("src/test/resources/coverageMavenProject2.xml"));

		try {
			diffReport = new DiffReport("src/test/resources/mavenProject1", "src/test/resources/mavenProject2");
		} catch (IOException e) {
			fail();
		}
		
	}
	
	@After
	public void tearDown() {
		Configuration.setDestinationDirectory(new File("").getAbsolutePath()+ "/site");
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
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		
		try {
			dBuilder = dbFactory.newDocumentBuilder();
		
			Document doc = dBuilder.parse(new File(Configuration.getDestinationDirectory(), "operias.xml"));
			
			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpath = xPathfactory.newXPath();
			
			assertEquals("9" , xpath.compile("/operias/summary/classChanges/coverageChanges/totalRelevantLinesAdded/lineCount").evaluate(doc));	
			assertEquals("0.33" , xpath.compile("/operias/summary/classChanges/coverageChanges/totalRelevantLinesAdded/lineRate").evaluate(doc));	

			assertEquals("6" , xpath.compile("/operias/summary/classChanges/coverageChanges/totalRelevantLinesChanged/originalLineCount").evaluate(doc));	
			assertEquals("0.67" , xpath.compile("/operias/summary/classChanges/coverageChanges/totalRelevantLinesChanged/originalLineRate").evaluate(doc));	
			assertEquals("6" , xpath.compile("/operias/summary/classChanges/coverageChanges/totalRelevantLinesChanged/revisedLineCount").evaluate(doc));	
			assertEquals("0.6" , xpath.compile("/operias/summary/classChanges/coverageChanges/totalRelevantLinesChanged/revisedLineRate").evaluate(doc));	

			assertEquals("21 (43.75%)" , xpath.compile("/operias/summary/classChanges/sourceChanges/addedLineCount").evaluate(doc));	
			assertEquals("7 (14.58%)" , xpath.compile("/operias/summary/classChanges/sourceChanges/removedLineCount").evaluate(doc));	
			assertEquals("6 to 11 (12.5%)" , xpath.compile("/operias/summary/classChanges/sourceChanges/changedLineCount").evaluate(doc));	
			
			assertEquals("26 (55.32%)" , xpath.compile("/operias/summary/testChanges/sourceChanges/addedLineCount").evaluate(doc));	
			assertEquals("16 (34.04%)" , xpath.compile("/operias/summary/testChanges/sourceChanges/removedLineCount").evaluate(doc));	
			assertEquals("0 to 0 (0.0%)" , xpath.compile("/operias/summary/testChanges/sourceChanges/changedLineCount").evaluate(doc));	

			
			// Check all classes
			assertEquals("example.Calculations" , xpath.compile("/operias/changedFiles/changedClasses/classFile[1]/@classname").evaluate(doc));	
			assertEquals("1.0" , xpath.compile("/operias/changedFiles/changedClasses/classFile[1]/@conditionCoverageOriginal").evaluate(doc));	
			assertEquals("0.75" , xpath.compile("/operias/changedFiles/changedClasses/classFile[1]/@conditionCoverageRevised").evaluate(doc));	
			assertEquals("0.75" , xpath.compile("/operias/changedFiles/changedClasses/classFile[1]/@lineCoverageOriginal").evaluate(doc));	
			assertEquals("0.875" , xpath.compile("/operias/changedFiles/changedClasses/classFile[1]/@lineCoverageRevised").evaluate(doc));		
			assertEquals("CHANGED" , xpath.compile("/operias/changedFiles/changedClasses/classFile[1]/@sourceState").evaluate(doc));		
			assertEquals("/src/main/java/example/Calculations.java" , xpath.compile("/operias/changedFiles/changedClasses/classFile[1]/@filename").evaluate(doc));	
			
			assertEquals("1" , xpath.compile("/operias/changedFiles/changedClasses/classFile[1]/coverageChanges/relevantLinesChanged/originalLineCount").evaluate(doc));	
			assertEquals("0.5" , xpath.compile("/operias/changedFiles/changedClasses/classFile[1]/coverageChanges/relevantLinesChanged/originalLineRate").evaluate(doc));		
			assertEquals("4" , xpath.compile("/operias/changedFiles/changedClasses/classFile[1]/coverageChanges/relevantLinesChanged/revisedLineCount").evaluate(doc));		
			assertEquals("0.67" , xpath.compile("/operias/changedFiles/changedClasses/classFile[1]/coverageChanges/relevantLinesChanged/revisedLineRate").evaluate(doc));	
		
			assertEquals("8 (57.14%)" , xpath.compile("/operias/changedFiles/changedClasses/classFile[1]/sourceChanges/@sizeChange").evaluate(doc));	
			assertEquals("2 into 10 (14.29%)" , xpath.compile("/operias/changedFiles/changedClasses/classFile[1]/sourceChanges/changedLineCount").evaluate(doc));		
			

			assertEquals("example.Loops" , xpath.compile("/operias/changedFiles/changedClasses/classFile[2]/@classname").evaluate(doc));	
			assertEquals("0.5" , xpath.compile("/operias/changedFiles/changedClasses/classFile[2]/@conditionCoverageOriginal").evaluate(doc));	
			assertEquals("1.0" , xpath.compile("/operias/changedFiles/changedClasses/classFile[2]/@conditionCoverageRevised").evaluate(doc));	
			assertEquals("0.8" , xpath.compile("/operias/changedFiles/changedClasses/classFile[2]/@lineCoverageOriginal").evaluate(doc));	
			assertEquals("1.0" , xpath.compile("/operias/changedFiles/changedClasses/classFile[2]/@lineCoverageRevised").evaluate(doc));		
			assertEquals("SAME" , xpath.compile("/operias/changedFiles/changedClasses/classFile[2]/@sourceState").evaluate(doc));		
			assertEquals("/src/main/java/example/Loops.java" , xpath.compile("/operias/changedFiles/changedClasses/classFile[2]/@filename").evaluate(doc));	
			
			assertEquals("0" , xpath.compile("/operias/changedFiles/changedClasses/classFile[2]/coverageChanges/relevantLinesChanged/originalLineCount").evaluate(doc));	
			assertEquals("0.0" , xpath.compile("/operias/changedFiles/changedClasses/classFile[2]/coverageChanges/relevantLinesChanged/originalLineRate").evaluate(doc));		
			assertEquals("2" , xpath.compile("/operias/changedFiles/changedClasses/classFile[2]/coverageChanges/relevantLinesChanged/revisedLineCount").evaluate(doc));		
			assertEquals("1.0" , xpath.compile("/operias/changedFiles/changedClasses/classFile[2]/coverageChanges/relevantLinesChanged/revisedLineRate").evaluate(doc));	
		
			assertEquals("0 (0.0%)" , xpath.compile("/operias/changedFiles/changedClasses/classFile[2]/sourceChanges/@sizeChange").evaluate(doc));	
			
			
			assertEquals("example.Music" , xpath.compile("/operias/changedFiles/changedClasses/classFile[3]/@classname").evaluate(doc));	
			assertEquals("1.0" , xpath.compile("/operias/changedFiles/changedClasses/classFile[3]/@conditionCoverageOriginal").evaluate(doc));	
			assertEquals("1.0" , xpath.compile("/operias/changedFiles/changedClasses/classFile[3]/@conditionCoverageRevised").evaluate(doc));	
			assertEquals("1.0" , xpath.compile("/operias/changedFiles/changedClasses/classFile[3]/@lineCoverageOriginal").evaluate(doc));	
			assertEquals("0.0" , xpath.compile("/operias/changedFiles/changedClasses/classFile[3]/@lineCoverageRevised").evaluate(doc));		
			assertEquals("CHANGED" , xpath.compile("/operias/changedFiles/changedClasses/classFile[3]/@sourceState").evaluate(doc));		
			assertEquals("/src/main/java/example/Music.java" , xpath.compile("/operias/changedFiles/changedClasses/classFile[3]/@filename").evaluate(doc));	
			
			assertEquals("5" , xpath.compile("/operias/changedFiles/changedClasses/classFile[3]/coverageChanges/relevantLinesChanged/originalLineCount").evaluate(doc));	
			assertEquals("1.0" , xpath.compile("/operias/changedFiles/changedClasses/classFile[3]/coverageChanges/relevantLinesChanged/originalLineRate").evaluate(doc));		
			assertEquals("0" , xpath.compile("/operias/changedFiles/changedClasses/classFile[3]/coverageChanges/relevantLinesChanged/revisedLineCount").evaluate(doc));		
			assertEquals("0.0" , xpath.compile("/operias/changedFiles/changedClasses/classFile[3]/coverageChanges/relevantLinesChanged/revisedLineRate").evaluate(doc));	
		
			assertEquals("-5 (-35.71%)" , xpath.compile("/operias/changedFiles/changedClasses/classFile[3]/sourceChanges/@sizeChange").evaluate(doc));	
			assertEquals("4 into 1 (28.57%)" , xpath.compile("/operias/changedFiles/changedClasses/classFile[3]/sourceChanges/changedLineCount").evaluate(doc));	
			assertEquals("2 (14.29%)" , xpath.compile("/operias/changedFiles/changedClasses/classFile[3]/sourceChanges/removedLineCount").evaluate(doc));	
			
			
			assertEquals("example.deletablePackage.DeletableClass" , xpath.compile("/operias/changedFiles/changedClasses/classFile[4]/@classname").evaluate(doc));	
			assertEquals("1.0" , xpath.compile("/operias/changedFiles/changedClasses/classFile[4]/@conditionCoverageOriginal").evaluate(doc));	
			assertEquals("0.0" , xpath.compile("/operias/changedFiles/changedClasses/classFile[4]/@lineCoverageOriginal").evaluate(doc));	
			assertEquals("DELETED" , xpath.compile("/operias/changedFiles/changedClasses/classFile[4]/@sourceState").evaluate(doc));		
			assertEquals("/src/main/java/example/deletablePackage/DeletableClass.java" , xpath.compile("/operias/changedFiles/changedClasses/classFile[4]/@filename").evaluate(doc));	
					
			assertEquals("-5 (-100.0%)" , xpath.compile("/operias/changedFiles/changedClasses/classFile[4]/sourceChanges/@sizeChange").evaluate(doc));	
			assertEquals("5 (100.0%)" , xpath.compile("/operias/changedFiles/changedClasses/classFile[4]/sourceChanges/removedLineCount").evaluate(doc));	
			
			assertEquals("example.NewClass" , xpath.compile("/operias/changedFiles/changedClasses/classFile[5]/@classname").evaluate(doc));	
			assertEquals("1.0" , xpath.compile("/operias/changedFiles/changedClasses/classFile[5]/@conditionCoverageRevised").evaluate(doc));	
			assertEquals("0.0" , xpath.compile("/operias/changedFiles/changedClasses/classFile[5]/@lineCoverageRevised").evaluate(doc));		
			assertEquals("NEW" , xpath.compile("/operias/changedFiles/changedClasses/classFile[5]/@sourceState").evaluate(doc));		
			assertEquals("/src/main/java/example/NewClass.java" , xpath.compile("/operias/changedFiles/changedClasses/classFile[5]/@filename").evaluate(doc));	
			
			assertEquals("4" , xpath.compile("/operias/changedFiles/changedClasses/classFile[5]/coverageChanges/relevantLinesAdded/lineCount").evaluate(doc));	
			assertEquals("0.0" , xpath.compile("/operias/changedFiles/changedClasses/classFile[5]/coverageChanges/relevantLinesAdded/lineRate").evaluate(doc));			
		
			assertEquals("9 (100.0%)" , xpath.compile("/operias/changedFiles/changedClasses/classFile[5]/sourceChanges/@sizeChange").evaluate(doc));	
			assertEquals("9 (100.0%)" , xpath.compile("/operias/changedFiles/changedClasses/classFile[5]/sourceChanges/addedLineCount").evaluate(doc));	
		
			assertEquals("moreExamples.Switch" , xpath.compile("/operias/changedFiles/changedClasses/classFile[6]/@classname").evaluate(doc));	
			assertEquals("0.6666666666666666" , xpath.compile("/operias/changedFiles/changedClasses/classFile[6]/@conditionCoverageRevised").evaluate(doc));	
			assertEquals("0.8" , xpath.compile("/operias/changedFiles/changedClasses/classFile[6]/@lineCoverageRevised").evaluate(doc));		
			assertEquals("NEW" , xpath.compile("/operias/changedFiles/changedClasses/classFile[6]/@sourceState").evaluate(doc));		
			assertEquals("/src/main/java/moreExamples/Switch.java" , xpath.compile("/operias/changedFiles/changedClasses/classFile[6]/@filename").evaluate(doc));	
			
			assertEquals("5" , xpath.compile("/operias/changedFiles/changedClasses/classFile[6]/coverageChanges/relevantLinesAdded/lineCount").evaluate(doc));	
			assertEquals("1.0" , xpath.compile("/operias/changedFiles/changedClasses/classFile[6]/coverageChanges/relevantLinesAdded/lineRate").evaluate(doc));			
		
			assertEquals("12 (100.0%)" , xpath.compile("/operias/changedFiles/changedClasses/classFile[6]/sourceChanges/@sizeChange").evaluate(doc));	
			assertEquals("12 (100.0%)" , xpath.compile("/operias/changedFiles/changedClasses/classFile[6]/sourceChanges/addedLineCount").evaluate(doc));	

			//Test files
			assertEquals("CHANGED" , xpath.compile("/operias/changedFiles/changedTests/testFile[1]/@sourceState").evaluate(doc));	
			assertEquals("8 (53.33%)" , xpath.compile("/operias/changedFiles/changedTests/testFile[1]/@sizeChange").evaluate(doc));	
			assertEquals("/src/test/java/example/CalculationsTest.java" , xpath.compile("/operias/changedFiles/changedTests/testFile[1]/@filename").evaluate(doc));		
			assertEquals("8 (53.33%)" , xpath.compile("/operias/changedFiles/changedTests/testFile[1]/addedLineCount").evaluate(doc));	

			assertEquals("CHANGED" , xpath.compile("/operias/changedFiles/changedTests/testFile[2]/@sourceState").evaluate(doc));	
			assertEquals("1 (6.25%)" , xpath.compile("/operias/changedFiles/changedTests/testFile[2]/@sizeChange").evaluate(doc));	
			assertEquals("/src/test/java/example/LoopsTest.java" , xpath.compile("/operias/changedFiles/changedTests/testFile[2]/@filename").evaluate(doc));		
			assertEquals("1 (6.25%)" , xpath.compile("/operias/changedFiles/changedTests/testFile[2]/addedLineCount").evaluate(doc));	

			assertEquals("DELETED" , xpath.compile("/operias/changedFiles/changedTests/testFile[3]/@sourceState").evaluate(doc));	
			assertEquals("-16 (-100.0%)" , xpath.compile("/operias/changedFiles/changedTests/testFile[3]/@sizeChange").evaluate(doc));	
			assertEquals("/src/test/java/example/MusicTest.java" , xpath.compile("/operias/changedFiles/changedTests/testFile[3]/@filename").evaluate(doc));		
			assertEquals("16 (100.0%)" , xpath.compile("/operias/changedFiles/changedTests/testFile[3]/removedLineCount").evaluate(doc));	

			assertEquals("NEW" , xpath.compile("/operias/changedFiles/changedTests/testFile[4]/@sourceState").evaluate(doc));	
			assertEquals("17 (100.0%)" , xpath.compile("/operias/changedFiles/changedTests/testFile[4]/@sizeChange").evaluate(doc));	
			assertEquals("/src/test/java/moreExamples/SwitchTest.java" , xpath.compile("/operias/changedFiles/changedTests/testFile[4]/@filename").evaluate(doc));		
			assertEquals("17 (100.0%)" , xpath.compile("/operias/changedFiles/changedTests/testFile[4]/addedLineCount").evaluate(doc));	
} catch (Exception e) {
			fail(e.getMessage());
		}
		
	}
}
