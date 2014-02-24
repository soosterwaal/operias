package operias.git;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import org.apache.commons.io.FileUtils;

public class Git {

	String baseDirectory;
	
	File tempDirectory;
	
	/**
	 * Construct a new GIT object, based on the given git directory
	 * @param directory
	 */
	public Git(String directory) {
		baseDirectory = directory;
		tempDirectory = new File(Calendar.getInstance().getTime().getTime() + "");
		
		// Copy git directory contents
		try {
			FileUtils.copyDirectory(new File(baseDirectory), tempDirectory);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * Check out to a given commit
	 * 
	 * EG 9d3821f6411ad85a683b8d38e4d42411229f2eec
	 * @return True if succeeds, false otherwise
	 */
	public boolean checkoutCommit(String commit) {
	
		ProcessBuilder builder = new ProcessBuilder("git","--git-dir", tempDirectory.getAbsolutePath() + "/.git/", "--work-tree", tempDirectory.getAbsolutePath() , "checkout", "-f", "-b",  "operias-temp-branch", commit);

		Process process = null;
		try {
			process = builder.start();
			process.waitFor();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int exitValue = process.exitValue();
		
		process.destroy();
		
		return exitValue == 0;
	}
	
	public String getDirectory() {
		return tempDirectory.getAbsolutePath();
	}
	
}


