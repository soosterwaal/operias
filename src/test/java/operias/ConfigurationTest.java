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
		assertNull(Configuration.getRevisedDirectory());
		
		boolean throwsException = false;
		try {
			Configuration.setRevisedDirectory(invalidDirectory);
		} catch (InvalidParameterException e) {
			throwsException = true;
		}
		
		assertTrue("Configuration should have thrown an exception", throwsException);

		
		
		// Test the error for valid directory but missing maven project
		assertNull(Configuration.getRevisedDirectory());
		
		throwsException = false;
		try {
			Configuration.setRevisedDirectory(validDirectory);
		} catch (InvalidParameterException e) {
			throwsException = true;
		}
		
		assertTrue("Configuration should have thrown an exception", throwsException);

		assertNull(Configuration.getRevisedDirectory());
		
		
		throwsException = false;
		try {
			Configuration.setRevisedDirectory(null);
		} catch (InvalidParameterException e) {
			throwsException = true;
		}
		
		assertTrue("Configuration should have thrown an exception", throwsException);
		
		assertNull(Configuration.getRevisedDirectory());
		
		Configuration.setRevisedDirectory(validMavenDirectory);
		
		assertEquals("Source directory should have been set to " + validMavenDirectory, validMavenDirectory, Configuration.getRevisedDirectory());
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
		assertNull(Configuration.getOriginalDirectory());
		
		boolean throwsException = false;
		try {
			Configuration.setOriginalDirectory(invalidDirectory);
		} catch (InvalidParameterException e) {
			throwsException = true;
		}
		
		assertTrue("Configuration should have thrown an exception", throwsException);

		
		
		// Test the error for valid directory but missing maven project
		assertNull(Configuration.getOriginalDirectory());
		
		throwsException = false;
		try {
			Configuration.setOriginalDirectory(validDirectory);
		} catch (InvalidParameterException e) {
			throwsException = true;
		}
		
		assertTrue("Configuration should have thrown an exception", throwsException);

		assertNull(Configuration.getOriginalDirectory());
		
		
		throwsException = false;
		try {
			Configuration.setOriginalDirectory(null);
		} catch (InvalidParameterException e) {
			throwsException = true;
		}
		
		assertTrue("Configuration should have thrown an exception", throwsException);
		
		assertNull(Configuration.getOriginalDirectory());
		
		Configuration.setOriginalDirectory(validMavenDirectory);
		
		assertEquals("Source directory should have been set to " + validMavenDirectory, validMavenDirectory, Configuration.getOriginalDirectory());
	}

}
