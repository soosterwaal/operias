package operias.diff;

import java.io.File;
import java.io.IOException;

public class DiffReport {

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
	private DiffDirectory changedFiles;
	
	/**
	 * Construct a new file diff report based on the given directories
	 * @param originalDirectory	Original source directory
	 * @param newDirectory		New source directory
	 * @throws IOException 
	 */
	public DiffReport(String originalDirectory, String newDirectory) throws IOException {
		this.originalDirectory = originalDirectory;
		this.newDirectory = newDirectory;
		
		changedFiles = DiffDirectory.compareDirectory(originalDirectory, newDirectory);
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
	public DiffDirectory getChangedFiles() {
		return changedFiles;
	}
	
	/**
	 * Get the file diff instance of the given file
	 * @param filename
	 * @return the file diff instance, or null if not found
	 */
	public DiffFile getFile(String filename) {

		// first search for the original directory + filename, if the file was found,
		// it means it was changed, same or deleted
		String searchFileName = (new File(originalDirectory, filename)).getAbsolutePath();
		
		DiffFile searchedFile = changedFiles.getFile(searchFileName);
		
		if (searchedFile == null) {
			searchFileName = (new File(newDirectory, filename)).getAbsolutePath();
			
			searchedFile = changedFiles.getFile(searchFileName);
		}
		
		// if not found, search with the new direcotry + filename, if the file was found,
		// it means it was new, else it doesn't exists in any of the directories
		return searchedFile;
	}
}
