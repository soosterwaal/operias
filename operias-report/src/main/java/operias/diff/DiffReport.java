package operias.diff;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
		
		String searchDirName = (new File(directoryName)).getAbsolutePath();
		
		return changedFiles.getDirectory(searchDirName);
	}

	/**
	 * Get the file diff instance of the given file, returning the first instance found with the given state
	 * @param filename
	 * @return the file diff instance, or null if not found
	 */
	public DiffFile getFile(List<String> sourceLocations, String filename, SourceDiffState state) {

		// Loop through all possible locations
		for(String baseLocation : sourceLocations) {
			String searchFileName = (new File(baseLocation + "/", filename)).getAbsolutePath();
			DiffFile searchedFile = changedFiles.getFile(searchFileName);
			
			if (searchedFile != null && searchedFile.getSourceState() == state) {
				return searchedFile;
			}
		}
		
		return null;
	}
	/**
	 * Get the file diff instance of the given file, returning the first instance found
	 * @param filename
	 * @return the file diff instance, or null if not found
	 */
	public List<DiffFile> getFiles(List<String> sourceLocations, String filename) {

		List<DiffFile> foundFiles = new ArrayList<DiffFile>();
		
		// Loop through all possible locations
		for(String baseLocation : sourceLocations) {
			String searchFileName = (new File(baseLocation + "/", filename)).getAbsolutePath();
			DiffFile searchedFile = changedFiles.getFile(searchFileName);
			
			if (searchedFile != null) {
				if (!foundFiles.contains(searchedFile)) {
					foundFiles.add(searchedFile);
				}
			}
		}
		
		return foundFiles;
	}
}
