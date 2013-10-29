package operias.diff;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import operias.diff.SourceDiffState;

import org.junit.Test;

import difflib.Delta;

public class DiffFileTest {

	/**
	 * Check if it correctly notes that teh file is deleted
	 * 
	 */
	@Test
	public void testFileDiffDeleted() {
		DiffFile diffFile = null;
		
		try {
			diffFile = DiffFile.compareFile("src/test/resources/diffFile/file1.txt", null);
		} catch (Exception e) {
			fail();
		}
		
		assertEquals(SourceDiffState.DELETED, diffFile.getSourceState());
		assertEquals("src/test/resources/diffFile/file1.txt", diffFile.getFileName());
		//1 change, the complete file
		assertEquals(1, diffFile.getChanges().size());

		try {
			diffFile = DiffFile.compareFile("src/test/resources/diffFile/file1.txt", "src/test/resources/diffFile/nonExistingFile.txt");
		} catch (Exception e) {
			fail();
		}
		
		assertEquals(SourceDiffState.DELETED, diffFile.getSourceState());
		assertEquals("src/test/resources/diffFile/file1.txt", diffFile.getFileName());
		//1 change, the complete file
		assertEquals(1, diffFile.getChanges().size());
	}
	
	/**
	 * Check if it correctly notes that the file is new
	 * This is either when the original file is null or not found
	 */
	@Test
	public void testfileDiffNew() {
		DiffFile diffFile = null;
		try {
			diffFile = DiffFile.compareFile(null, "src/test/resources/diffFile/file2.txt");
		} catch (Exception e) {
			fail();
		}
		
		assertEquals(SourceDiffState.NEW, diffFile.getSourceState());
		assertEquals("src/test/resources/diffFile/file2.txt", diffFile.getFileName());
		//1 change, the complete file
		assertEquals(1, diffFile.getChanges().size());
		
		
		try {
			diffFile = DiffFile.compareFile("src/test/resources/diffFile/nonExistingFile.txt", "src/test/resources/diffFile/file2.txt");
		} catch (Exception e) {
			fail();
		}
		
		assertEquals(SourceDiffState.NEW, diffFile.getSourceState());
		assertEquals("src/test/resources/diffFile/file2.txt", diffFile.getFileName());
		//1 change, the complete file
		assertEquals(1, diffFile.getChanges().size());
	}
	
	/**
	 * Check if it correctly note that the files are the same
	 */
	@Test
	public void testFileDiffEqualFile() {
		DiffFile diffFile = null;
		try {
			diffFile = DiffFile.compareFile("src/test/resources/diffFile/file1.txt", "src/test/resources/diffFile/file1.txt");
		} catch (Exception e) {
			fail();
		}
		
		assertEquals(SourceDiffState.SAME, diffFile.getSourceState());
		assertEquals("src/test/resources/diffFile/file1.txt", diffFile.getFileName());
		assertEquals(0, diffFile.getChanges().size());
	}
	
	/**
	 * Compare two files to each other
	 */
	@Test
	public void testFileDiff()  {
		DiffFile diffFile = null;
		try {
			diffFile = DiffFile.compareFile("src/test/resources/diffFile/file1.txt", "src/test/resources/diffFile/file2.txt");
		} catch (Exception e) {
			fail();
		}
		
		assertEquals(SourceDiffState.CHANGED, diffFile.getSourceState());
		assertEquals("src/test/resources/diffFile/file2.txt", diffFile.getFileName());
		assertEquals(3, diffFile.getChanges().size());
		
		List<Delta> changes = diffFile.getChanges();
		
		assertEquals(Delta.TYPE.CHANGE, changes.get(0).getType());
		assertEquals(3, changes.get(0).getOriginal().getPosition());
		List<String> changed_1 = new ArrayList<String>();
		changed_1.add("		int i = 1;");
		assertEquals(changed_1, changes.get(0).getOriginal().getLines());
		List<String> changed_2 = new ArrayList<String>();
		changed_2.add("		int i = 2;");
		assertEquals(changed_2, changes.get(0).getRevised().getLines());
		
		assertEquals(Delta.TYPE.DELETE, changes.get(1).getType());
		assertEquals(6, changes.get(1).getOriginal().getPosition());
		List<String> deleted = new ArrayList<String>();
		deleted.add("		int b = 4;");
		assertEquals(deleted, changes.get(1).getOriginal().getLines());
		
		assertEquals(Delta.TYPE.INSERT, changes.get(2).getType());
		assertEquals(9, changes.get(2).getOriginal().getPosition());
		List<String> insert = new ArrayList<String>();
		insert.add("");
		insert.add("	public void fail() { System.exit(0); }");
		assertEquals(insert, changes.get(2).getRevised().getLines());
	}
}
