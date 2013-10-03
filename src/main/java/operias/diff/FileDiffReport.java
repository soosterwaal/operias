package operias.diff;

import java.io.IOException;

public class FileDiffReport {

	/**
	 * Original source directory
	 */
	private String originalDirectory;
	
	/**
	 * New source directory
	 */
	private String newDirectory;
	
	/**
	 * Changes in the directory
	 */
	private FileDiffDirectory changedFiles;
	
	/**
	 * Construct a new file diff report based on the given directories
	 * @param originalDirectory	Original source directory
	 * @param newDirectory		New source directory
	 * @throws IOException 
	 */
	public FileDiffReport(String originalDirectory, String newDirectory) throws IOException {
		this.originalDirectory = originalDirectory;
		this.newDirectory = newDirectory;
		
		changedFiles = FileDiffDirectory.compareDirectory(originalDirectory, newDirectory);
	}

	/**
	 * @return the originalDirectory
	 */
	public String getOriginalDirectory() {
		return originalDirectory;
	}

	/**
	 * @return the newDirectory
	 */
	public String getNewDirectory() {
		return newDirectory;
	}

	
	/**
	 * @return the changedFiles
	 */
	public FileDiffDirectory getChangedFiles() {
		return changedFiles;
	}
}
