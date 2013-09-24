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
		String validSourceDirectory = "src/test/resources/validSourceDirectory";
		String invalidSourceDirectory = "src/test/resource/invalidSourceDirectory";
		
		assertNull(Configuration.getRepositoryDirectory());
		
		boolean throwsException = false;
		try {
			Configuration.setRepositoryDirectory(invalidSourceDirectory);
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
		
		Configuration.setRepositoryDirectory(validSourceDirectory);
		
		assertEquals("Repository directory should have been set to " + validSourceDirectory, validSourceDirectory, Configuration.getRepositoryDirectory());
	}

}
