package operias.report;

import static org.junit.Assert.*;

import java.util.LinkedList;

import operias.cobertura.*;
import operias.diff.DiffFile;
import operias.diff.SourceDiffState;

import org.junit.Test;

public class OperiasFileTest {

	@Test
	public void simpleTest() {
		
		CoberturaClass originalClass = new CoberturaClass("Simple" , "simple/Simple.java", "Simple", 1, 1);
		CoberturaClass revisedClass = new CoberturaClass("Simple" , "simple/Simple.java", "Simple", 1, 1);
		
		CoberturaLine line1 = new CoberturaLine(1, 1, false, false);
		CoberturaLine line2_1 = new CoberturaLine(2, 0, false, false);
		CoberturaLine line2_2 = new CoberturaLine(2, 5, false, false);
		
		CoberturaLine line3 = new CoberturaLine(3, 1, false, false);
		
		originalClass.addLine(line1);
		originalClass.addLine(line2_1);
		originalClass.addLine(line3);
		
		revisedClass.addLine(line1);
		revisedClass.addLine(line2_2);
		revisedClass.addLine(line3);
		
		DiffFile sourceDiff = new DiffFile("simple/Simple.java", SourceDiffState.SAME);
		
		OperiasFile oFile = new OperiasFile(originalClass, revisedClass, sourceDiff);
		
		LinkedList<OperiasChange> changes = (LinkedList<OperiasChange>) oFile.getChanges();
		
		assertEquals(1, changes.size());
		
	}
}
