package operias.report;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import operias.cobertura.CoberturaReport;
import operias.diff.DiffReport;
import operias.output.html.HTMLReport;
import operias.report.change.ChangeSourceChange;
import operias.report.change.CoverageIncreaseChange;
import operias.report.change.InsertSourceChange;

import org.junit.Test;

public class OperiasReportTest {

	/**
	 * Test the creation of a simple operias report
	 */
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
		
		LinkedList<OperiasFile> changedClasses = (LinkedList<OperiasFile>)report.getChangedClasses();
			
		assertEquals(6, changedClasses.size());
		
		OperiasFile firstClass = changedClasses.get(0);
		
		assertEquals("example", firstClass.getPackageName());
		assertEquals("example.Calculations", firstClass.getClassName());
		assertEquals((new File("")).getAbsolutePath() + "/src/test/resources/mavenProject2/src/main/java/example/Calculations.java", firstClass.getSourceDiff().getRevisedFileName());
		
		
		assertEquals(2, firstClass.getChanges().size());
		
		assertTrue(firstClass.getChanges().getFirst() instanceof ChangeSourceChange);
		ChangeSourceChange change1 = (ChangeSourceChange)firstClass.getChanges().getFirst();
		
		assertEquals(8, change1.getOriginalLineNumber());
		assertEquals(8, change1.getRevisedLineNumber());
		
		assertTrue(change1.getOriginalCoverage().get(0));
		
		assertFalse(change1.getRevisedCoverage().get(0));
		assertTrue(change1.getRevisedCoverage().get(1));
		assertNull(change1.getRevisedCoverage().get(2));
		assertFalse(change1.getRevisedCoverage().get(3));
		assertNull(change1.getRevisedCoverage().get(4));

		assertTrue(firstClass.getChanges().get(1) instanceof ChangeSourceChange);
		ChangeSourceChange change2 = (ChangeSourceChange)firstClass.getChanges().get(1);
		
		assertEquals(12, change2.getOriginalLineNumber());
		assertEquals(16, change2.getRevisedLineNumber());
		
		assertFalse(change2.getOriginalCoverage().get(0));
		
		assertTrue(change2.getRevisedCoverage().get(0));
		assertTrue(change2.getRevisedCoverage().get(1));
		assertNull(change2.getRevisedCoverage().get(2));
		assertTrue(change2.getRevisedCoverage().get(3));
		assertNull(change2.getRevisedCoverage().get(4));
		
		OperiasFile secondClass = changedClasses.get(1);
		assertEquals("example", secondClass.getPackageName());
		assertEquals("example.Loops", secondClass.getClassName());
		assertEquals((new File("")).getAbsolutePath() + "/src/test/resources/mavenProject2/src/main/java/example/Loops.java", secondClass.getSourceDiff().getRevisedFileName());
		
		assertEquals(2, secondClass.getChanges().size());
		
		assertTrue(secondClass.getChanges().get(0) instanceof CoverageIncreaseChange);
		CoverageIncreaseChange change3 = (CoverageIncreaseChange) secondClass.getChanges().get(0);
		assertEquals(7, change3.getRevisedLineNumber());
		
		assertTrue(secondClass.getChanges().get(1) instanceof CoverageIncreaseChange);
		CoverageIncreaseChange change4 = (CoverageIncreaseChange) secondClass.getChanges().get(1);
		assertEquals(8, change4.getRevisedLineNumber());

		OperiasFile thirdClass = changedClasses.get(2);
		assertEquals("example", thirdClass.getPackageName());
		assertEquals("example.Music", thirdClass.getClassName());
		assertEquals((new File("")).getAbsolutePath() + "/src/test/resources/mavenProject2/src/main/java/example/Music.java", thirdClass.getSourceDiff().getRevisedFileName());
	
		

		OperiasFile fourthClass = changedClasses.get(3);
		
		assertEquals("example.deletablePackage", fourthClass.getPackageName());
		
		OperiasFile fifthClass = changedClasses.get(4);
		assertEquals("example", fifthClass.getPackageName());
		assertEquals("example.NewClass", fifthClass.getClassName());
		assertEquals((new File("")).getAbsolutePath() + "/src/test/resources/mavenProject2/src/main/java/example/NewClass.java", fifthClass.getSourceDiff().getRevisedFileName());
		
		assertEquals(1, fifthClass.getChanges().size());
		assertTrue(fifthClass.getChanges().get(0) instanceof InsertSourceChange);

		assertEquals(9, fifthClass.getChanges().get(0).getRevisedCoverage().size());
		
		assertNull(fifthClass.getChanges().get(0).getRevisedCoverage().get(0));
		assertNull(fifthClass.getChanges().get(0).getRevisedCoverage().get(1));
		assertFalse(fifthClass.getChanges().get(0).getRevisedCoverage().get(2));
		assertNull(fifthClass.getChanges().get(0).getRevisedCoverage().get(3));
		assertNull(fifthClass.getChanges().get(0).getRevisedCoverage().get(4));
		assertFalse(fifthClass.getChanges().get(0).getRevisedCoverage().get(5));
		assertFalse(fifthClass.getChanges().get(0).getRevisedCoverage().get(6));
		assertFalse(fifthClass.getChanges().get(0).getRevisedCoverage().get(7));
		assertNull(fifthClass.getChanges().get(0).getRevisedCoverage().get(8));
		
		OperiasFile sixthClass = changedClasses.get(5);
		assertEquals("moreExamples", sixthClass.getPackageName());
		assertEquals("moreExamples.Switch", sixthClass.getClassName());
		assertEquals((new File("")).getAbsolutePath() + "/src/test/resources/mavenProject2/src/main/java/moreExamples/Switch.java", sixthClass.getSourceDiff().getRevisedFileName());
		
		
		assertEquals(1, sixthClass.getChanges().size());
		assertTrue(sixthClass.getChanges().get(0) instanceof InsertSourceChange);

		assertEquals(12, sixthClass.getChanges().get(0).getRevisedCoverage().size());
		
		assertNull(sixthClass.getChanges().get(0).getRevisedCoverage().get(0));
		assertNull(sixthClass.getChanges().get(0).getRevisedCoverage().get(1));
		assertTrue(sixthClass.getChanges().get(0).getRevisedCoverage().get(2));
		assertNull(sixthClass.getChanges().get(0).getRevisedCoverage().get(3));
		assertNull(sixthClass.getChanges().get(0).getRevisedCoverage().get(4));
		assertFalse(sixthClass.getChanges().get(0).getRevisedCoverage().get(5));
		assertTrue(sixthClass.getChanges().get(0).getRevisedCoverage().get(6));
		assertTrue(sixthClass.getChanges().get(0).getRevisedCoverage().get(7));
		assertFalse(sixthClass.getChanges().get(0).getRevisedCoverage().get(8));
		assertNull(sixthClass.getChanges().get(0).getRevisedCoverage().get(9));
		assertNull(sixthClass.getChanges().get(0).getRevisedCoverage().get(10));
		assertNull(sixthClass.getChanges().get(0).getRevisedCoverage().get(11));
	}
}
