package operias;

import static org.junit.Assert.*;

import java.io.File;
import java.security.InvalidParameterException;

import org.junit.Test;
public class ConfigurationTest {

	/**
	 * Check setting the source directory, first try an invalid directory,
	 * then a valid directory.
	 */
	@Test
	public void testSetSourceDirectoryInvalidAndValid() {
		String validSourceDirectory = "src/test/resources/validSourceDirectory";
		String invalidSourceDirectory = "src/test/resource/invalidSourceDirectory";
		
		assertNull(Configuration.getSourceDirectory());
		
		boolean throwsException = false;
		try {
			Configuration.setSourceDirectory(invalidSourceDirectory);
		} catch (InvalidParameterException e) {
			throwsException = true;
		}
		
		assertTrue("Configuration should have thrown an exception", throwsException);

		assertNull(Configuration.getSourceDirectory());
		
		throwsException = false;
		try {
			Configuration.setSourceDirectory(null);
		} catch (InvalidParameterException e) {
			throwsException = true;
		}
		
		assertTrue("Configuration should have thrown an exception", throwsException);
		
		assertNull(Configuration.getSourceDirectory());
		
		Configuration.setSourceDirectory(validSourceDirectory);
		
		assertEquals("Source directory should have been set to " + validSourceDirectory, validSourceDirectory, Configuration.getSourceDirectory());
	}
	
	/**
	 * Check setting the source directory, first try an invalid directory,
	 * then a valid directory.
	 */
	@Test
	public void testSetRepositoryDirectoryInvalidAndValid() {
		String validRepositoryDirectory = (new File("")).getAbsolutePath();
		String invalidRepositoryDirectory = "src/test/resources/validSourceDirectory";
		String invalidDirectory = "src/test/resources/invalidSourceDirectory";
		
		assertNull(Configuration.getRepositoryDirectory());
		
		boolean throwsException = false;
		try {
			Configuration.setRepositoryDirectory(invalidDirectory);
		} catch (Exception e) {
			throwsException = true;
		}
		
		assertTrue("Configuration should have thrown an exception", throwsException);	
		
		assertNull(Configuration.getRepositoryDirectory());
		
		throwsException = false;
		try {
			Configuration.setRepositoryDirectory(invalidRepositoryDirectory);
		} catch (Exception e) {
			throwsException = true;
		}
		
		assertTrue("Configuration should have thrown an exception", throwsException);

		assertNull(Configuration.getRepositoryDirectory());
		
		throwsException = false;
		try {
			Configuration.setRepositoryDirectory(null);
		} catch (Exception e) {
			throwsException = true;
		}
		
		assertTrue("Configuration should have thrown an exception", throwsException);
		
		assertNull(Configuration.getRepositoryDirectory());
		
		Configuration.setRepositoryDirectory(validRepositoryDirectory);
		
		assertEquals("Repository directory should have been set to " + validRepositoryDirectory, validRepositoryDirectory, Configuration.getRepositoryDirectory());
	}

	/**
	 * Test setting the branch name
	 */
	@Test
	public void testSetBranch() {
		String branchName = "testBranch";
		
		assertNull(Configuration.getBranchName());
		
		Configuration.setBranchName(branchName);
		
		assertEquals(branchName, Configuration.getBranchName());
		
	}
}
