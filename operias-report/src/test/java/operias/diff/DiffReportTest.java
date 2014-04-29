package operias.diff;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class DiffReportTest {

	/**
	 * Test the same directory
	 * @throws IOException 
	 */
	@Test
	public void testSameDirectory() {
		try {
			DiffReport diffReport = new DiffReport("src/test/resources/simpleMavenProject", "src/test/resources/simpleMavenProject");

			assertEquals("src/test/resources/simpleMavenProject", diffReport.getOriginalDirectory());
			assertEquals("src/test/resources/simpleMavenProject", diffReport.getRevisedDirectory());
			
			DiffDirectory diffDir = diffReport.getChangedFiles();

			assertEquals(SourceDiffState.SAME, diffDir.getState());
			assertFalse(diffDir.isEmpty());
		
			List<DiffDirectory> dirs = diffDir.getDirectories();
			
			DiffDirectory diffDir_1 = dirs.get(0);
			
			assertFalse(diffDir_1.isEmpty());
			assertEquals((new File("")).getAbsolutePath() + "/src/test/resources/simpleMavenProject/src", diffDir_1.getOriginalDirectoryName());
			assertEquals(SourceDiffState.SAME, diffDir_1.getState());
			
			// Check the src folder, should contain a main and test folder
			DiffDirectory diffDir_2 = diffDir_1.getDirectories().get(0);
			DiffDirectory diffDir_3 = diffDir_1.getDirectories().get(1);
			
			//Check main folder
			assertFalse(diffDir_2.isEmpty());
			assertEquals((new File("")).getAbsolutePath() + "/src/test/resources/simpleMavenProject/src/main", diffDir_2.getOriginalDirectoryName());
			assertEquals(SourceDiffState.SAME, diffDir_2.getState());

			DiffDirectory mainJavaFolder = diffDir_2.getDirectories().get(0);
			DiffDirectory mainResourceFolder = diffDir_2.getDirectories().get(1);
			
			// Check main/java folder
			assertFalse(mainJavaFolder.isEmpty());
			assertEquals((new File("")).getAbsolutePath() + "/src/test/resources/simpleMavenProject/src/main/java", mainJavaFolder.getOriginalDirectoryName());
			assertEquals(SourceDiffState.SAME, mainJavaFolder.getState());
			assertEquals(0, mainJavaFolder.getFiles().size());
			
			// Check main/resources folder
			assertTrue(mainResourceFolder.isEmpty());
			assertEquals((new File("")).getAbsolutePath() + "/src/test/resources/simpleMavenProject/src/main/resources", mainResourceFolder.getOriginalDirectoryName());
			assertEquals(SourceDiffState.SAME, mainResourceFolder.getState());
			assertEquals(0, mainResourceFolder.getFiles().size());
			
			//Check test folder
			assertFalse(diffDir_3.isEmpty());
			assertEquals((new File("")).getAbsolutePath() + "/src/test/resources/simpleMavenProject/src/test", diffDir_3.getOriginalDirectoryName());
			assertEquals(SourceDiffState.SAME, diffDir_3.getState());	
			
			DiffDirectory testJavaFolder = diffDir_3.getDirectories().get(0);
			DiffDirectory testResourceFolder = diffDir_3.getDirectories().get(1);
			
			// Check main/java folder
			assertFalse(testJavaFolder.isEmpty());
			assertEquals((new File("")).getAbsolutePath() + "/src/test/resources/simpleMavenProject/src/test/java", testJavaFolder.getOriginalDirectoryName());
			assertEquals(SourceDiffState.SAME, testJavaFolder.getState());
			assertEquals(0, testJavaFolder.getFiles().size());
		
			// Check main/resources folder
			assertTrue(testResourceFolder.isEmpty());
			assertEquals((new File("")).getAbsolutePath() + "/src/test/resources/simpleMavenProject/src/test/resources", testResourceFolder.getOriginalDirectoryName());
			assertEquals(SourceDiffState.SAME, testResourceFolder.getState());
			assertEquals(0, testResourceFolder.getFiles().size());
			
		} catch (IOException e) {
			fail();		
		}
	}
	
	
	
	/**
	 * Test whether a directory is succesfully noted as deleted
	 */
	@Test
	public void testDeletedDirectory() {
		try {
			DiffReport diffReport = new DiffReport("src/test/resources/simpleMavenProject", null);
			DiffDirectory diffDir = diffReport.getChangedFiles();
			
			assertEquals(SourceDiffState.DELETED, diffDir.getState());
			assertFalse(diffDir.isEmpty());
		
			List<DiffDirectory> dirs = diffDir.getDirectories();
			List<DiffFile> files = diffDir.getFiles();
			
			DiffDirectory diffDir_1 = dirs.get(0);
			DiffFile diffFile = files.get(0);
			
			assertFalse(diffDir_1.isEmpty());
			assertEquals((new File("")).getAbsolutePath() + "/src/test/resources/simpleMavenProject/src", diffDir_1.getOriginalDirectoryName());
			assertEquals(SourceDiffState.DELETED, diffDir_1.getState());
			
			
			assertEquals((new File("")).getAbsolutePath() + "/src/test/resources/simpleMavenProject/pom.xml", diffFile.getOriginalFileName());
			
			
			assertEquals(SourceDiffState.DELETED, diffFile.getSourceState());
			
			// Check the src folder, should contain a main and test folder
			DiffDirectory diffDir_2 = diffDir_1.getDirectories().get(0);
			DiffDirectory diffDir_3 = diffDir_1.getDirectories().get(1);
			
			//Check main folder
			assertFalse(diffDir_2.isEmpty());
			assertEquals((new File("")).getAbsolutePath() + "/src/test/resources/simpleMavenProject/src/main", diffDir_2.getOriginalDirectoryName());
			assertEquals(SourceDiffState.DELETED, diffDir_2.getState());

			DiffDirectory mainJavaFolder = diffDir_2.getDirectories().get(0);
			DiffDirectory mainResourceFolder = diffDir_2.getDirectories().get(1);
			
			// Check main/java folder
			assertFalse(mainJavaFolder.isEmpty());
			assertEquals((new File("")).getAbsolutePath() + "/src/test/resources/simpleMavenProject/src/main/java", mainJavaFolder.getOriginalDirectoryName());
			assertEquals(SourceDiffState.DELETED, mainJavaFolder.getState());
			assertEquals(0, mainJavaFolder.getFiles().size());
			
			// Check main/resources folder
			assertTrue(mainResourceFolder.isEmpty());
			assertEquals((new File("")).getAbsolutePath() + "/src/test/resources/simpleMavenProject/src/main/resources", mainResourceFolder.getOriginalDirectoryName());
			assertEquals(SourceDiffState.DELETED, mainResourceFolder.getState());
			assertEquals(0, mainResourceFolder.getFiles().size());
			
			//Check test folder
			assertFalse(diffDir_3.isEmpty());
			assertEquals((new File("")).getAbsolutePath() + "/src/test/resources/simpleMavenProject/src/test", diffDir_3.getOriginalDirectoryName());
			assertEquals(SourceDiffState.DELETED, diffDir_3.getState());	
			
			DiffDirectory testJavaFolder = diffDir_3.getDirectories().get(0);
			DiffDirectory testResourceFolder = diffDir_3.getDirectories().get(1);
			
			// Check main/java folder
			assertFalse(testJavaFolder.isEmpty());
			assertEquals((new File("")).getAbsolutePath() + "/src/test/resources/simpleMavenProject/src/test/java", testJavaFolder.getOriginalDirectoryName());
			assertEquals(SourceDiffState.DELETED, testJavaFolder.getState());
			assertEquals(0, testJavaFolder.getFiles().size());
		
			// Check main/resources folder
			assertTrue(testResourceFolder.isEmpty());
			assertEquals((new File("")).getAbsolutePath() + "/src/test/resources/simpleMavenProject/src/test/resources", testResourceFolder.getOriginalDirectoryName());
			assertEquals(SourceDiffState.DELETED, testResourceFolder.getState());
			assertEquals(0, testResourceFolder.getFiles().size());
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
			DiffReport diffReport = new DiffReport(null, "src/test/resources/simpleMavenProject");
			DiffDirectory diffDir = diffReport.getChangedFiles();	
			
			assertEquals(SourceDiffState.NEW, diffDir.getState());
			assertFalse(diffDir.isEmpty());
			
			List<DiffDirectory> dirs = diffDir.getDirectories();
			List<DiffFile> files = diffDir.getFiles();
			
			DiffDirectory diffDir_1 = dirs.get(0);
			DiffFile diffFile = files.get(0);
			
			assertFalse(diffDir_1.isEmpty());
			assertEquals((new File("")).getAbsolutePath() + "/src/test/resources/simpleMavenProject/src", diffDir_1.getRevisedDirectoryName());
			assertEquals(SourceDiffState.NEW, diffDir_1.getState());
			
			assertEquals((new File("")).getAbsolutePath() + "/src/test/resources/simpleMavenProject/pom.xml", diffFile.getRevisedFileName());
			
			
			assertEquals(SourceDiffState.NEW, diffFile.getSourceState());
			
			// Check the src folder, should contain a main and test folder
			DiffDirectory diffDir_2 = diffDir_1.getDirectories().get(0);
			DiffDirectory diffDir_3 = diffDir_1.getDirectories().get(1);
			
			//Check main folder
			assertFalse(diffDir_2.isEmpty());
			assertEquals((new File("")).getAbsolutePath() + "/src/test/resources/simpleMavenProject/src/main", diffDir_2.getRevisedDirectoryName());
			assertEquals(SourceDiffState.NEW, diffDir_2.getState());

			DiffDirectory mainJavaFolder = diffDir_2.getDirectories().get(0);
			DiffDirectory mainResourceFolder = diffDir_2.getDirectories().get(1);
			
			// Check main/java folder
			assertFalse(mainJavaFolder.isEmpty());
			assertEquals((new File("")).getAbsolutePath() + "/src/test/resources/simpleMavenProject/src/main/java", mainJavaFolder.getRevisedDirectoryName());
			assertEquals(SourceDiffState.NEW, mainJavaFolder.getState());
			assertEquals(0, mainJavaFolder.getFiles().size());
			
			// Check main/resources folder
			assertTrue(mainResourceFolder.isEmpty());
			assertEquals((new File("")).getAbsolutePath() + "/src/test/resources/simpleMavenProject/src/main/resources", mainResourceFolder.getRevisedDirectoryName());
			assertEquals(SourceDiffState.NEW, mainResourceFolder.getState());
			assertEquals(0, mainResourceFolder.getFiles().size());
			
			//Check test folder
			assertFalse(diffDir_3.isEmpty());
			assertEquals((new File("")).getAbsolutePath() + "/src/test/resources/simpleMavenProject/src/test", diffDir_3.getRevisedDirectoryName());
			assertEquals(SourceDiffState.NEW, diffDir_3.getState());	
			
			DiffDirectory testJavaFolder = diffDir_3.getDirectories().get(0);
			DiffDirectory testResourceFolder = diffDir_3.getDirectories().get(1);
			
			// Check main/java folder
			assertFalse(testJavaFolder.isEmpty());
			assertEquals((new File("")).getAbsolutePath() + "/src/test/resources/simpleMavenProject/src/test/java", testJavaFolder.getRevisedDirectoryName());
			assertEquals(SourceDiffState.NEW, testJavaFolder.getState());
			assertEquals(0, testJavaFolder.getFiles().size());
		
			// Check main/resources folder
			assertTrue(testResourceFolder.isEmpty());
			assertEquals((new File("")).getAbsolutePath() + "/src/test/resources/simpleMavenProject/src/test/resources", testResourceFolder.getRevisedDirectoryName());
			assertEquals(SourceDiffState.NEW, testResourceFolder.getState());
			assertEquals(0, testResourceFolder.getFiles().size());
		} catch (IOException e) {
			fail();		
		}
	}
	
	
	@Test
	public void testGetFile()  {
		ArrayList<String> sourceLocations = new ArrayList<String>();
		
		sourceLocations.add(new File("src/test/resources/simpleMavenProject/src/main/java").getAbsolutePath());
		sourceLocations.add(new File("src/test/resources/simpleMavenProject2/src/main/java").getAbsolutePath());
		
		DiffReport diffReport;
		try {
			diffReport = new DiffReport("src/test/resources/simpleMavenProject", "src/test/resources/simpleMavenProject2");
			DiffFile file = diffReport.getFile(sourceLocations, "simpleMavenProject/Simple.java");
			
			assertNotNull(file);
			assertEquals((new File("src/test/resources/simpleMavenProject", "src/main/java/simpleMavenProject/Simple.java")).getAbsolutePath(), file.getOriginalFileName());

			file = diffReport.getFile(sourceLocations, "simpleMavenProject/Simple2.java");
	
			assertNotNull(file);
			assertEquals((new File("src/test/resources/simpleMavenProject2", "src/main/java/simpleMavenProject/Simple2.java")).getAbsolutePath(), file.getRevisedFileName());
			
			file = diffReport.getFile(sourceLocations, "simpleMavenProject/DOESNOTEXISTS.java");
			
			assertNull(file);
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
			DiffReport diffReport = new DiffReport("src/test/resources/simpleMavenProject", "src/test/resources/simpleMavenProject2");
			DiffDirectory diffDir = diffReport.getChangedFiles();
			
			assertEquals("src/test/resources/simpleMavenProject", diffReport.getOriginalDirectory());
			assertEquals("src/test/resources/simpleMavenProject2", diffReport.getRevisedDirectory());
			
			assertEquals(SourceDiffState.CHANGED, diffDir.getState());
			
			assertEquals(SourceDiffState.CHANGED, diffDir.getDirectories().get(0).getDirectories().get(0).getDirectories().get(0).getDirectories().get(0).getState());
			assertEquals(SourceDiffState.SAME, diffDir.getDirectories().get(0).getDirectories().get(0).getDirectories().get(1).getState());
			
			assertEquals(SourceDiffState.DELETED, diffDir.getDirectories().get(0).getDirectories().get(0).getDirectories().get(0).getDirectories().get(0).getFiles().get(0).getSourceState());
			assertEquals((new File("")).getAbsolutePath() + "/src/test/resources/simpleMavenProject/src/main/java/simpleMavenProject/Simple.java", diffDir.getDirectories().get(0).getDirectories().get(0).getDirectories().get(0).getDirectories().get(0).getFiles().get(0).getOriginalFileName());
			
			assertEquals(SourceDiffState.NEW, diffDir.getDirectories().get(0).getDirectories().get(0).getDirectories().get(0).getDirectories().get(0).getFiles().get(1).getSourceState());
			assertEquals((new File("")).getAbsolutePath() + "/src/test/resources/simpleMavenProject2/src/main/java/simpleMavenProject/Simple2.java", diffDir.getDirectories().get(0).getDirectories().get(0).getDirectories().get(0).getDirectories().get(0).getFiles().get(1).getRevisedFileName());
			
			assertEquals(SourceDiffState.CHANGED, diffDir.getDirectories().get(0).getDirectories().get(1).getDirectories().get(0).getState());
			assertEquals(SourceDiffState.CHANGED, diffDir.getDirectories().get(0).getDirectories().get(1).getDirectories().get(1).getState());
			

			assertEquals(SourceDiffState.DELETED, diffDir.getDirectories().get(0).getDirectories().get(1).getDirectories().get(0).getDirectories().get(0).getState());
			assertEquals(SourceDiffState.NEW, diffDir.getDirectories().get(0).getDirectories().get(1).getDirectories().get(1).getDirectories().get(0).getState());
			assertEquals(SourceDiffState.NEW, diffDir.getDirectories().get(0).getDirectories().get(1).getDirectories().get(1).getDirectories().get(0).getFiles().get(0).getSourceState());
		
			
			
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
			new DiffReport("src/t", null);
		}
		catch (Exception e) {
			exceptionThrown = true;
		}
		
		assertTrue(exceptionThrown);
		
		exceptionThrown = false;
		
		try {
			new DiffReport(null, "src/t");
		}
		catch (Exception e) {
			exceptionThrown = true;
		}
		
		assertTrue(exceptionThrown);
		
		exceptionThrown = false;
		
		try {
			new DiffReport("src/t", "src/t");
		}
		catch (Exception e) {
			exceptionThrown = true;
		}
		
		assertTrue(exceptionThrown);
		
		exceptionThrown = false;
		
		try {
			new DiffReport("src", "src/t");
		}
		catch (Exception e) {
			exceptionThrown = true;
		}
		
		assertTrue(exceptionThrown);
	}
}
