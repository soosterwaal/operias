package operias.diff;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

public class FileDiffReportTest {

	/**
	 * Test the same directory
	 * @throws IOException 
	 */
	@Test
	public void testSameDirectory() {
		try {
			FileDiffReport diffReport = new FileDiffReport("src/test/resources/simpleMavenProject", "src/test/resources/simpleMavenProject");

			assertEquals("src/test/resources/simpleMavenProject", diffReport.getOriginalDirectory());
			assertEquals("src/test/resources/simpleMavenProject", diffReport.getNewDirectory());
			
			FileDiffDirectory diffDir = diffReport.getChangedFiles();
				
			checkStatus(diffDir, DiffState.SAME);
			
		} catch (IOException e) {
			fail();		
		}
	}
	
	/**
	 * Check if the status of the directory and all its sub directories and files are the same, 
	 * can be used for DELETED, NEW and SAME
	 * @param diffDir
	 * @param state
	 */
	private void checkStatus(FileDiffDirectory diffDir, DiffState state) {

		assertEquals(state, diffDir.getState());
		assertFalse(diffDir.isEmpty());
	
		List<FileDiffDirectory> dirs = diffDir.getDirectories();
		List<FileDiffFile> files = diffDir.getFiles();
		
		FileDiffDirectory diffDir_1 = dirs.get(0);
		FileDiffFile diffFile = files.get(0);
		
		assertFalse(diffDir_1.isEmpty());
		assertEquals((new File("")).getAbsolutePath() + "/src/test/resources/simpleMavenProject/src", diffDir_1.getDirectoryName());
		assertEquals(state, diffDir_1.getState());
		
		assertEquals((new File("")).getAbsolutePath() + "/src/test/resources/simpleMavenProject/pom.xml", diffFile.getFileName());
		assertEquals(state, diffFile.getState());
		
		// Check the src folder, should contain a main and test folder
		FileDiffDirectory diffDir_2 = diffDir_1.getDirectories().get(0);
		FileDiffDirectory diffDir_3 = diffDir_1.getDirectories().get(1);
		
		//Check main folder
		assertFalse(diffDir_2.isEmpty());
		assertEquals((new File("")).getAbsolutePath() + "/src/test/resources/simpleMavenProject/src/main", diffDir_2.getDirectoryName());
		assertEquals(state, diffDir_2.getState());

		FileDiffDirectory mainJavaFolder = diffDir_2.getDirectories().get(0);
		FileDiffDirectory mainResourceFolder = diffDir_2.getDirectories().get(1);
		
		// Check main/java folder
		assertFalse(mainJavaFolder.isEmpty());
		assertEquals((new File("")).getAbsolutePath() + "/src/test/resources/simpleMavenProject/src/main/java", mainJavaFolder.getDirectoryName());
		assertEquals(state, mainJavaFolder.getState());
		assertEquals(0, mainJavaFolder.getFiles().size());
		
		// Check main/resources folder
		assertTrue(mainResourceFolder.isEmpty());
		assertEquals((new File("")).getAbsolutePath() + "/src/test/resources/simpleMavenProject/src/main/resources", mainResourceFolder.getDirectoryName());
		assertEquals(state, mainResourceFolder.getState());
		assertEquals(0, mainResourceFolder.getFiles().size());
		
		//Check test folder
		assertFalse(diffDir_3.isEmpty());
		assertEquals((new File("")).getAbsolutePath() + "/src/test/resources/simpleMavenProject/src/test", diffDir_3.getDirectoryName());
		assertEquals(state, diffDir_3.getState());	
		
		FileDiffDirectory testJavaFolder = diffDir_3.getDirectories().get(0);
		FileDiffDirectory testResourceFolder = diffDir_3.getDirectories().get(1);
		
		// Check main/java folder
		assertFalse(testJavaFolder.isEmpty());
		assertEquals((new File("")).getAbsolutePath() + "/src/test/resources/simpleMavenProject/src/test/java", testJavaFolder.getDirectoryName());
		assertEquals(state, testJavaFolder.getState());
		assertEquals(0, testJavaFolder.getFiles().size());
	
		// Check main/resources folder
		assertTrue(testResourceFolder.isEmpty());
		assertEquals((new File("")).getAbsolutePath() + "/src/test/resources/simpleMavenProject/src/test/resources", testResourceFolder.getDirectoryName());
		assertEquals(state, testResourceFolder.getState());
		assertEquals(0, testResourceFolder.getFiles().size());
	}
	
	/**
	 * Test whether a directory is succesfully noted as deleted
	 */
	@Test
	public void testDeletedDirectory() {
		try {
			FileDiffReport diffReport = new FileDiffReport("src/test/resources/simpleMavenProject", null);
			FileDiffDirectory diffDir = diffReport.getChangedFiles();
			checkStatus(diffDir, DiffState.DELETED);
		} catch (IOException e) {
			fail();		
		}
	}
	
	/**
	 * Test whether a directory is succesfully noted as deleted
	 */
	@Test
	public void testNewDirectory() {
		try {
			FileDiffReport diffReport = new FileDiffReport(null, "src/test/resources/simpleMavenProject");
			FileDiffDirectory diffDir = diffReport.getChangedFiles();
			checkStatus(diffDir, DiffState.NEW);
		} catch (IOException e) {
			fail();		
		}
	}
	
	/**
	 * Test real changes in directories
	 */
	@Test
	public void testChangedDirectory() {
		try {
			FileDiffReport diffReport = new FileDiffReport("src/test/resources/simpleMavenProject", "src/test/resources/simpleMavenProject2");
			FileDiffDirectory diffDir = diffReport.getChangedFiles();
			
			assertEquals("src/test/resources/simpleMavenProject", diffReport.getOriginalDirectory());
			assertEquals("src/test/resources/simpleMavenProject2", diffReport.getNewDirectory());
			
			assertEquals(DiffState.CHANGED, diffDir.getState());
			
			assertEquals(DiffState.CHANGED, diffDir.getDirectories().get(0).getDirectories().get(0).getDirectories().get(0).getDirectories().get(0).getState());
			assertEquals(DiffState.SAME, diffDir.getDirectories().get(0).getDirectories().get(0).getDirectories().get(1).getState());
			
			assertEquals(DiffState.DELETED, diffDir.getDirectories().get(0).getDirectories().get(0).getDirectories().get(0).getDirectories().get(0).getFiles().get(0).getState());
			assertEquals((new File("")).getAbsolutePath() + "/src/test/resources/simpleMavenProject/src/main/java/simpleMavenProject/Simple.java", diffDir.getDirectories().get(0).getDirectories().get(0).getDirectories().get(0).getDirectories().get(0).getFiles().get(0).getFileName());
			
			assertEquals(DiffState.NEW, diffDir.getDirectories().get(0).getDirectories().get(0).getDirectories().get(0).getDirectories().get(0).getFiles().get(1).getState());
			assertEquals((new File("")).getAbsolutePath() + "/src/test/resources/simpleMavenProject2/src/main/java/simpleMavenProject/Simple2.java", diffDir.getDirectories().get(0).getDirectories().get(0).getDirectories().get(0).getDirectories().get(0).getFiles().get(1).getFileName());
			
			assertEquals(DiffState.CHANGED, diffDir.getDirectories().get(0).getDirectories().get(1).getDirectories().get(0).getState());
			assertEquals(DiffState.CHANGED, diffDir.getDirectories().get(0).getDirectories().get(1).getDirectories().get(1).getState());
			

			assertEquals(DiffState.DELETED, diffDir.getDirectories().get(0).getDirectories().get(1).getDirectories().get(0).getDirectories().get(0).getState());
			assertEquals(DiffState.NEW, diffDir.getDirectories().get(0).getDirectories().get(1).getDirectories().get(1).getDirectories().get(0).getState());
			assertEquals(DiffState.NEW, diffDir.getDirectories().get(0).getDirectories().get(1).getDirectories().get(1).getDirectories().get(0).getFiles().get(0).getState());
		
		} catch (IOException e) {
			fail();		
		}
	}
	
	/**
	 * Test invalid directory combinations
	 */
	@Test
	public void testInvalidDirectories() {
		boolean exceptionThrown = false;
		
		try {
			new FileDiffReport("src/t", null);
		}
		catch (Exception e) {
			exceptionThrown = true;
		}
		
		assertTrue(exceptionThrown);
		
		exceptionThrown = false;
		
		try {
			new FileDiffReport(null, "src/t");
		}
		catch (Exception e) {
			exceptionThrown = true;
		}
		
		assertTrue(exceptionThrown);
		
		exceptionThrown = false;
		
		try {
			new FileDiffReport("src/t", "src/t");
		}
		catch (Exception e) {
			exceptionThrown = true;
		}
		
		assertTrue(exceptionThrown);
		
		exceptionThrown = false;
		
		try {
			new FileDiffReport("src", "src/t");
		}
		catch (Exception e) {
			exceptionThrown = true;
		}
		
		assertTrue(exceptionThrown);
	}
}
