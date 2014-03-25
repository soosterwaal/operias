package operias;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.InvalidParameterException;

import operias.test.general.NoExitSecurityManager;

import org.junit.After;
import org.junit.Test;
import org.junit.Before;

public class ConfigurationTest {

	@Before
	public void setUp() {
		Configuration.resetConfiguration();
        System.setSecurityManager(new NoExitSecurityManager());
	}
	
	@After
	public void tearDown() {
        Configuration.resetConfiguration();
        System.setSecurityManager(null);
	}
	
	
	/**
	 * Test that when an original and/or revised directory is already given, the git extension won't override it
	 */
	@Test
	public void testDirectoryDefaultOverrulesGit() {
		Configuration.setTemporaryDirectory(new File("").getAbsolutePath() + "/target/gittests");
		Configuration.setOriginalDirectory("src/test/resources/validMavenDirectory");
		Configuration.setRevisedDirectory("src/test/resources/validMavenDirectory");
		
		Configuration.setRevisedRepositoryURL("https://github.com/soosterwaal/operias.git");
		Configuration.setOriginalRepositoryURL("https://github.com/soosterwaal/operias.git");
		
		Configuration.setOriginalBranchName("master");
		
		Configuration.setOriginalCommitID("6143a8cf29eb7d56a0b65cee562d07c29250b71e");
		Configuration.setRevisedCommitID("342e0d9ce8a62587930a9c8c4cbe208320662b85");
		
		try {
			Configuration.setUpDirectoriesThroughGit();
		} catch (Exception e) {
			fail(e.getMessage());
		}
		assertEquals("src/test/resources/validMavenDirectory", Configuration.getRevisedDirectory());
		assertEquals("src/test/resources/validMavenDirectory", Configuration.getOriginalDirectory());
		
	}
	
	
	/**
	 * Set up git directories through repo url + branch/commit
	 */
	@Test
	public void testSettingUpDirectoriesUsingGit() {
		Configuration.setTemporaryDirectory(new File("").getAbsolutePath() + "/target/gittests");
		Configuration.setRevisedRepositoryURL("https://github.com/soosterwaal/operias.git");
		Configuration.setOriginalRepositoryURL("https://github.com/soosterwaal/operias.git");
		
		Configuration.setOriginalBranchName("master");
		
		Configuration.setOriginalCommitID("6143a8cf29eb7d56a0b65cee562d07c29250b71e");
		Configuration.setRevisedCommitID("342e0d9ce8a62587930a9c8c4cbe208320662b85");
		
		try {
			Configuration.setUpDirectoriesThroughGit();
		} catch (Exception e) {
			fail(e.getMessage());
		}
		
		// Check if the head is set correctly
		File HEADfileOriginal = new File(Configuration.getOriginalDirectory(), "/.git/HEAD");
		
		
		try {
			assertEquals("6143a8cf29eb7d56a0b65cee562d07c29250b71e\n",new String(Files.readAllBytes(HEADfileOriginal.toPath())));
		} catch (IOException e) {
			fail(e.getMessage());
		}

		// Check if the head is set correctly
		File HEADfileRevised = new File(Configuration.getRevisedDirectory(), "/.git/HEAD");
		
		
		try {
			assertEquals("342e0d9ce8a62587930a9c8c4cbe208320662b85\n",new String(Files.readAllBytes(HEADfileRevised.toPath())));
		} catch (IOException e) {
			fail(e.getMessage());
		}

	}

	/**
	 * Test given an argument without a value
	 */
	@Test
	public void testMissingValueWithArgument() {
		boolean exceptionThrow = false;
		try {
			Configuration.parseArguments(new String[] { "-d" });
		} catch(Exception e) {
			exceptionThrow = true;
		}
		
		assertTrue(exceptionThrow);
	}
	
	/**
	 * Test given an argument without a value
	 */
	@Test
	public void testUknownArgument() {
		boolean exceptionThrow = false;
		try {
			Configuration.parseArguments(new String[] { "-dontknowthisargument" });
		} catch(Exception e) {
			exceptionThrow = true;
		}
		
		assertTrue(exceptionThrow);
	}
	/**
	 * Test parsing the command line arguments
	 */
	@Test
	public void testCommandLineShortArguments() {

		assertEquals(new File("site").getAbsolutePath(), Configuration.getDestinationDirectory());			
		assertEquals(null, Configuration.getOriginalBranchName());		
		assertEquals(null, Configuration.getOriginalCommitID());		
		assertEquals(null, Configuration.getOriginalDirectory());		
		assertEquals(null, Configuration.getOriginalRepositoryURL());		
		assertEquals(null, Configuration.getRevisedBranchName());		
		assertEquals(null, Configuration.getRevisedCommitID());		
		assertEquals(null, Configuration.getRevisedDirectory());		
		assertEquals(null, Configuration.getRevisedRepositoryURL());
		assertEquals(new File("temp").getAbsolutePath(), Configuration.getTemporaryDirectory());
		assertEquals(false, Configuration.isOutputEnabled());
		
		Configuration.parseArguments(new String[] { "-d", "dest/dir"});
		assertEquals("dest/dir" , Configuration.getDestinationDirectory());
		
		Configuration.parseArguments(new String[] { "-td", "temp/dir"});
		assertEquals("temp/dir" , Configuration.getTemporaryDirectory());	
		
		Configuration.parseArguments(new String[] { "-obn", "orbrancname" });
		assertEquals("orbrancname" , Configuration.getOriginalBranchName());
		
		Configuration.parseArguments(new String[] { "-oc", "213471234512345" });
		assertEquals("213471234512345" , Configuration.getOriginalCommitID());
		
		Configuration.parseArguments(new String[] { "-od", "src/test/resources/validMavenDirectory" });
		assertEquals("src/test/resources/validMavenDirectory" , Configuration.getOriginalDirectory());
		
		Configuration.parseArguments(new String[] { "-oru", "orig_repo_url" });
		assertEquals("orig_repo_url" , Configuration.getOriginalRepositoryURL());

		Configuration.parseArguments(new String[] { "-rbn", "revbrancname" });
		assertEquals("revbrancname" , Configuration.getRevisedBranchName());
		
		Configuration.parseArguments(new String[] { "-rc", "2134712hkgjjhg34512345" });
		assertEquals("2134712hkgjjhg34512345" , Configuration.getRevisedCommitID());
		
		Configuration.parseArguments(new String[] { "-rd", "src/test/resources/validMavenDirectory" });
		assertEquals("src/test/resources/validMavenDirectory" , Configuration.getRevisedDirectory());
		
		Configuration.parseArguments(new String[] { "-rru", "revi_repo_url" });
		assertEquals("revi_repo_url" , Configuration.getRevisedRepositoryURL());
		

		Configuration.parseArguments(new String[] { "-ru", "combined_repo_url" });
		assertEquals("combined_repo_url" , Configuration.getRevisedRepositoryURL());
		assertEquals("combined_repo_url" , Configuration.getOriginalRepositoryURL());
		
		Configuration.parseArguments(new String[] { "-v" });
		assertEquals(true, Configuration.isOutputEnabled());
		
	}
	
	/**
	 * Test parsing the command line arguments
	 */
	@Test
	public void testCommandLineLongArguments() {

		assertEquals(new File("site").getAbsolutePath(), Configuration.getDestinationDirectory());			
		assertEquals(null, Configuration.getOriginalBranchName());		
		assertEquals(null, Configuration.getOriginalCommitID());		
		assertEquals(null, Configuration.getOriginalDirectory());		
		assertEquals(null, Configuration.getOriginalRepositoryURL());		
		assertEquals(null, Configuration.getRevisedBranchName());		
		assertEquals(null, Configuration.getRevisedCommitID());		
		assertEquals(null, Configuration.getRevisedDirectory());		
		assertEquals(null, Configuration.getRevisedRepositoryURL());
		assertEquals(new File("temp").getAbsolutePath(), Configuration.getTemporaryDirectory());
		assertEquals(false, Configuration.isOutputEnabled());
		
		Configuration.parseArguments(new String[] { "--destination-directory", "dest/dir"});
		assertEquals("dest/dir" , Configuration.getDestinationDirectory());
		
		Configuration.parseArguments(new String[] { "--temp-directory", "temp/dir"});
		assertEquals("temp/dir" , Configuration.getTemporaryDirectory());	
		
		Configuration.parseArguments(new String[] { "--original-branch-name", "orbrancname" });
		assertEquals("orbrancname" , Configuration.getOriginalBranchName());
		
		Configuration.parseArguments(new String[] { "--original-commit-id", "213471234512345" });
		assertEquals("213471234512345" , Configuration.getOriginalCommitID());
		
		Configuration.parseArguments(new String[] { "--original-directory", "src/test/resources/validMavenDirectory" });
		assertEquals("src/test/resources/validMavenDirectory" , Configuration.getOriginalDirectory());
		
		Configuration.parseArguments(new String[] { "--original-repository-url", "orig_repo_url" });
		assertEquals("orig_repo_url" , Configuration.getOriginalRepositoryURL());

		Configuration.parseArguments(new String[] { "--revised-branch-name", "revbrancname" });
		assertEquals("revbrancname" , Configuration.getRevisedBranchName());
		
		Configuration.parseArguments(new String[] { "--revised-commit-id", "2134712hkgjjhg34512345" });
		assertEquals("2134712hkgjjhg34512345" , Configuration.getRevisedCommitID());
		
		Configuration.parseArguments(new String[] { "--revised-directory", "src/test/resources/validMavenDirectory" });
		assertEquals("src/test/resources/validMavenDirectory" , Configuration.getRevisedDirectory());
		
		Configuration.parseArguments(new String[] { "--revised-repository-url", "revi_repo_url" });
		assertEquals("revi_repo_url" , Configuration.getRevisedRepositoryURL());
		

		Configuration.parseArguments(new String[] { "--repository-url", "combined_repo_url" });
		assertEquals("combined_repo_url" , Configuration.getRevisedRepositoryURL());
		assertEquals("combined_repo_url" , Configuration.getOriginalRepositoryURL());
		
		Configuration.parseArguments(new String[] { "--verbose" });
		assertEquals(true, Configuration.isOutputEnabled());
		
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
