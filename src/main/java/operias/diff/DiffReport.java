package operias.diff;

import java.io.File;
import java.io.IOException;

import operias.Main;

/**
 * A source diff report containing 1 DiffDirectory which includes the source diff changes for the complete project
 * @author soosterwaal
 *
 */
public class DiffReport {

	/**
	 * Original source directory
	 */
	private String originalDirectory;
	
	/**
	 * New source directory
	 */
	private String revisedDirectory;
	
	/**
	 * Changes in the directory
	 */
	private DiffDirectory changedFiles;
	
	/**
	 * Construct a new file diff report based on the given directories
	 * @param originalDirectory	Original source directory
	 * @param revisedDirectory		New source directory
	 * @throws IOException 
	 */
	public DiffReport(String originalDirectory, String revisedDirectory) throws IOException {
		this.originalDirectory = originalDirectory;
		this.revisedDirectory = revisedDirectory;
		

		Main.printLine("[Info] [" + Thread.currentThread().getName() + "] Comparing directory \"" +revisedDirectory + "\" to \"" + originalDirectory+ "\"");
		changedFiles = DiffDirectory.compareDirectory(originalDirectory, revisedDirectory);
		Main.printLine("[Info] [" + Thread.currentThread().getName() + "] Done comparing directories");
		
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
	public String getRevisedDirectory() {
		return revisedDirectory;
	}

	
	/**
	 * @return the changedFiles
	 */
	public DiffDirectory getChangedFiles() {
		return changedFiles;
	}
	
	/**
	 * Get a directory, the original and revised directory will be used as prefixes
	 * @param directoryName
	 * @return
	 */
	public DiffDirectory getDirectory(String directoryName) {
		
		String searchDirName = (new File(originalDirectory, directoryName)).getAbsolutePath();
		
		DiffDirectory searchDirectory = changedFiles.getDirectory(searchDirName);
		
		if (searchDirectory == null) {
			searchDirName = (new File(revisedDirectory, directoryName)).getAbsolutePath();
			
			searchDirectory = changedFiles.getDirectory(searchDirName);
		}
		
		return searchDirectory;
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
			searchFileName = (new File(revisedDirectory, filename)).getAbsolutePath();
			
			searchedFile = changedFiles.getFile(searchFileName);
		}
		
		// if not found, search with the new direcotry + filename, if the file was found,
		// it means it was new, else it doesn't exists in any of the directories
		return searchedFile;
	}
}
