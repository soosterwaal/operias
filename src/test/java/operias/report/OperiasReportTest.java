package operias.report;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import operias.cobertura.CoberturaReport;
import operias.diff.DiffReport;
import operias.report.change.ChangeSourceChange;
import operias.report.change.CoverageDecreaseChange;
import operias.report.change.CoverageIncreaseChange;

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
		
		LinkedList<OperiasFile> changedClasses = (LinkedList<OperiasFile>)report.getChangedClasses();
		
		assertEquals(4, changedClasses.size());
		
		OperiasFile firstClass = changedClasses.getFirst();
		
		assertEquals("example", firstClass.getPackageName());
		assertEquals("example.Calculations", firstClass.getClassName());
		assertEquals((new File("")).getAbsolutePath() + "/src/test/resources/mavenProject2/src/main/java/example/Calculations.java", firstClass.getFileName());
		
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
		assertEquals((new File("")).getAbsolutePath() + "/src/test/resources/mavenProject2/src/main/java/example/Loops.java", secondClass.getFileName());
		
		assertEquals(2, secondClass.getChanges().size());
		
		assertTrue(secondClass.getChanges().get(0) instanceof CoverageIncreaseChange);
		CoverageIncreaseChange change3 = (CoverageIncreaseChange) secondClass.getChanges().get(0);
		assertEquals(7, change3.getRevisedLineNumber());
		
		assertTrue(secondClass.getChanges().get(1) instanceof CoverageIncreaseChange);
		CoverageIncreaseChange change4 = (CoverageIncreaseChange) secondClass.getChanges().get(1);
		assertEquals(8, change4.getRevisedLineNumber());
		
		OperiasFile thirdClass = changedClasses.get(2);
		assertEquals("example", thirdClass.getPackageName());
		assertEquals("example.NewClass", thirdClass.getClassName());
		assertEquals("example/NewClass.java", thirdClass.getFileName());
		
		assertEquals(4, thirdClass.getChanges().size());
		assertTrue(thirdClass.getChanges().get(0) instanceof CoverageDecreaseChange);
		CoverageDecreaseChange change5 = (CoverageDecreaseChange) thirdClass.getChanges().get(0);
		assertEquals(3, change5.getRevisedLineNumber());
		
		assertTrue(thirdClass.getChanges().get(1) instanceof CoverageDecreaseChange);
		CoverageDecreaseChange change6 = (CoverageDecreaseChange) thirdClass.getChanges().get(1);
		assertEquals(6, change6.getRevisedLineNumber());
		
		assertTrue(thirdClass.getChanges().get(2) instanceof CoverageDecreaseChange);
		CoverageDecreaseChange change7 = (CoverageDecreaseChange) thirdClass.getChanges().get(2);
		assertEquals(7, change7.getRevisedLineNumber());
		
		assertTrue(thirdClass.getChanges().get(3) instanceof CoverageDecreaseChange);
		CoverageDecreaseChange change8 = (CoverageDecreaseChange) thirdClass.getChanges().get(3);
		assertEquals(8, change8.getRevisedLineNumber());
		
		OperiasFile fourthClass = changedClasses.get(3);
		assertEquals("moreExamples", fourthClass.getPackageName());
		assertEquals("moreExamples.Switch", fourthClass.getClassName());
		assertEquals("moreExamples/Switch.java", fourthClass.getFileName());
		
		assertEquals(5, fourthClass.getChanges().size());
		
		assertTrue(fourthClass.getChanges().get(0) instanceof CoverageIncreaseChange);
		CoverageIncreaseChange change9 = (CoverageIncreaseChange) fourthClass.getChanges().get(0);
		assertEquals(3, change9.getRevisedLineNumber());
		
		assertTrue(fourthClass.getChanges().get(1) instanceof CoverageDecreaseChange);
		CoverageDecreaseChange change10 = (CoverageDecreaseChange) fourthClass.getChanges().get(1);
		assertEquals(6, change10.getRevisedLineNumber());
		
		assertTrue(fourthClass.getChanges().get(2) instanceof CoverageIncreaseChange);
		CoverageIncreaseChange change11 = (CoverageIncreaseChange) fourthClass.getChanges().get(2);
		assertEquals(7, change11.getRevisedLineNumber());
		
		assertTrue(fourthClass.getChanges().get(3) instanceof CoverageIncreaseChange);
		CoverageIncreaseChange change12 = (CoverageIncreaseChange) fourthClass.getChanges().get(3);
		assertEquals(8, change12.getRevisedLineNumber());
		
		assertTrue(fourthClass.getChanges().get(4) instanceof CoverageDecreaseChange);
		CoverageDecreaseChange change13 = (CoverageDecreaseChange) fourthClass.getChanges().get(4);
		assertEquals(9, change13.getRevisedLineNumber());
		
	}
}
