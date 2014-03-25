package operias;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import operias.test.general.*;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MainTest {
	
    Main main;
	
	/**
	 * Set up the test case
	 */
	@Before
	public void setUp() {		
		main = new Main();
        System.setSecurityManager(new NoExitSecurityManager());
        Configuration.resetConfiguration();
	}
	
	/**
	 * Reset the security manager
	 */
	@After
	public void tearDown() {
        System.setSecurityManager(null);		
	}
	
	@Test
	public void testFunctional() {
		try {
			new File(new File("").getAbsolutePath() + "/target/testsProjects/mavenProject1").mkdirs();
			FileUtils.copyDirectory(
				new File(new File("").getAbsolutePath() +"/src/test/resources/mavenProject1"), 
				new File(new File("").getAbsolutePath() +"/target/testsProjects/mavenProject1"));
			
			new File(new File("").getAbsolutePath() +"/target/testsProjects/mavenProject2").mkdirs();
			FileUtils.copyDirectory(
				new File(new File("").getAbsolutePath() +"/src/test/resources/mavenProject2"), 
				new File(new File("").getAbsolutePath() +"/target/testsProjects/mavenProject2"));
		} catch (IOException e) {
			fail(e.getMessage());
		}
		
		String tempDirectory = new File("").getAbsolutePath() + "/target/temp" + Calendar.getInstance().getTime().getTime();
		String destDirectory = new File("").getAbsolutePath() + "/target/mainTest" + Calendar.getInstance().getTime().getTime();
		assertFalse(new File(tempDirectory).exists());
		String[] arguments = new String[] {
				"--original-directory", new File("").getAbsolutePath() + "/target/testsProjects/mavenProject1",
				"--revised-directory", new File("").getAbsolutePath() + "/target/testsProjects/mavenProject2",
				"--temp-directory", tempDirectory,
				"--destination-directory", destDirectory
		};
		
		Main.main(arguments);
		
		// Check if th destination directory exists and the temp is deleted correctly
		assertFalse(new File(tempDirectory).exists());
		assertTrue(new File(destDirectory).exists());
		
		// Finnaly, check the content

		assertTrue(new File(destDirectory + "/img").exists());
		assertTrue(new File(destDirectory + "/img/arc.png").exists());
		assertTrue(new File(destDirectory + "/css").exists());
		assertTrue(new File(destDirectory + "/css/style.css").exists());
		assertTrue(new File(destDirectory + "/example.Calculations.html").exists());
		assertTrue(new File(destDirectory + "/example.deletablePackage.DeletableClass.html").exists());
		assertTrue(new File(destDirectory + "/example.Loops.html").exists());
		assertTrue(new File(destDirectory + "/example.NewClass.html").exists());
		assertTrue(new File(destDirectory + "/index.html").exists());
		assertTrue(new File(destDirectory + "/moreExamples.Switch.html").exists());
		assertTrue(new File(destDirectory + "/operias.xml").exists());
	}
	
	
	
}
