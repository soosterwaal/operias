package operias;

import static org.junit.Assert.*;

import java.security.InvalidParameterException;

import org.junit.Test;
import org.junit.Before;

public class ConfigurationTest {

	@Before
	public void setUp() {
		Configuration.resetConfiguration();
	}
	
	/**
	 * Check setting the source directory, first try an invalid directory,
	 * then a valid directory.
	 */
	@Test
	public void testSetSourceDirectoryInvalidAndValid() {
		String validDirectory = "src/test/resources/validSourceDirectory";
		String invalidDirectory = "src/test/resources/invalidSourceDirectory";
		String validMavenDirectory = "src/test/resources/validMavenDirectory";
		
		
		// Test the error for invalid directory
		assertNull(Configuration.getSourceDirectory());
		
		boolean throwsException = false;
		try {
			Configuration.setSourceDirectory(invalidDirectory);
		} catch (InvalidParameterException e) {
			throwsException = true;
		}
		
		assertTrue("Configuration should have thrown an exception", throwsException);

		
		
		// Test the error for valid directory but missing maven project
		assertNull(Configuration.getSourceDirectory());
		
		throwsException = false;
		try {
			Configuration.setSourceDirectory(validDirectory);
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
		
		Configuration.setSourceDirectory(validMavenDirectory);
		
		assertEquals("Source directory should have been set to " + validMavenDirectory, validMavenDirectory, Configuration.getSourceDirectory());
	}
	
	/**
	 * Check setting the source directory, first try an invalid directory,
	 * then a valid directory.
	 */
	@Test
	public void testSetRepositoryDirectoryInvalidAndValid() {
		String validDirectory = "src/test/resources/validSourceDirectory";
		String invalidDirectory = "src/test/resources/invalidSourceDirectory";
		String validMavenDirectory = "src/test/resources/validMavenDirectory";
		
		
		// Test the error for invalid directory
		assertNull(Configuration.getRepositoryDirectory());
		
		boolean throwsException = false;
		try {
			Configuration.setRepositoryDirectory(invalidDirectory);
		} catch (InvalidParameterException e) {
			throwsException = true;
		}
		
		assertTrue("Configuration should have thrown an exception", throwsException);

		
		
		// Test the error for valid directory but missing maven project
		assertNull(Configuration.getRepositoryDirectory());
		
		throwsException = false;
		try {
			Configuration.setRepositoryDirectory(validDirectory);
		} catch (InvalidParameterException e) {
			throwsException = true;
		}
		
		assertTrue("Configuration should have thrown an exception", throwsException);

		assertNull(Configuration.getRepositoryDirectory());
		
		
		throwsException = false;
		try {
			Configuration.setRepositoryDirectory(null);
		} catch (InvalidParameterException e) {
			throwsException = true;
		}
		
		assertTrue("Configuration should have thrown an exception", throwsException);
		
		assertNull(Configuration.getRepositoryDirectory());
		
		Configuration.setRepositoryDirectory(validMavenDirectory);
		
		assertEquals("Source directory should have been set to " + validMavenDirectory, validMavenDirectory, Configuration.getRepositoryDirectory());
	}

}
