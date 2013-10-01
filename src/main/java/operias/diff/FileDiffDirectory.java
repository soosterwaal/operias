package operias.diff;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileDiffDirectory {

	/**
	 * Directory name
	 */
	private String directoryName;
	
	/**
	 * List of changed files within the directory
	 */
	private List<FileDiffFile> files;
	
	/**
	 * List of changed files within the directory
	 */
	private List<FileDiffDirectory> directories;
	
	/**
	 * Diff state of the directory
	 */
	private DiffState state;
	
	/**
	 * Construct a new direcotyr for in the diff report
	 * @param directoryName
	 */
	public FileDiffDirectory(String directoryName, DiffState state) {
		this.directoryName = directoryName;
		this.state = state;
		this.files = new ArrayList<FileDiffFile>();
		this.directories = new ArrayList<FileDiffDirectory>();
	}
	
	/**
	 * @return the directoryName
	 */
	public String getDirectoryName() {
		return directoryName;
	}

	/**
	 * @return the files
	 */
	public List<FileDiffFile> getFiles() {
		return files;
	}
	
	/**
	 * @return the state
	 */
	public DiffState getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(DiffState state) {
		this.state = state;
	}

	/**
	 * Add a new file to the files within this directory;
	 * @param file
	 */
	public void addFile(FileDiffFile file) {
		files.add(file);
	}
	
	/**
	 * Add a new directory to this directory
	 * @param directory
	 */
	public void addDirectory(FileDiffDirectory directory) {
		this.directories.add(directory);
	}
	
	/**
	 * 
	 * @param originalDirectory
	 * @param newDirectory
	 * @throws IOException 
	 */
	public static FileDiffDirectory compareDirectory(String originalDirectory, String newDirectory) throws IOException {
		
		// Short cuts 
		if (originalDirectory == null) {
			return fillDirectoryDiff(newDirectory, DiffState.NEW);
		} else if (newDirectory == null) {
			return fillDirectoryDiff(originalDirectory, DiffState.DELETED);
		} 
		
		// Both directories should exists, if not, throw exceptions
		FileDiffDirectory diffDirectory = new FileDiffDirectory(originalDirectory, DiffState.SAME);
		
		File originalDirectoryFile = new File(originalDirectory);
		File newDirectoryFile = new File(newDirectory);
		
		if (!originalDirectoryFile.isDirectory()) {
			throw new InvalidParameterException("'" +originalDirectory + "' is not a valid directory");
		}
		
		if (!newDirectoryFile.isDirectory()) {
			throw new InvalidParameterException("'" +newDirectoryFile + "' is not a valid directory");
		}
		
		String[] newFiles = newDirectoryFile.list();
		Arrays.sort(newFiles);
		String[] originalFiles = originalDirectoryFile.list();
		Arrays.sort(originalFiles);
		
		List<String> filesWithinNewDirectory = Arrays.asList(newFiles);
		List<String> filesWithinOriginalDirectory = Arrays.asList(originalFiles);
		
		//First cmpare the original dirs with the new one to see changes and deleted file and directories
		for(String fileName : filesWithinOriginalDirectory) {
			File originalFile = new File(originalDirectoryFile, fileName);
			
			if (originalFile.isHidden()) {
				continue;
			}
			
			if (originalFile.isDirectory()) {
				if (filesWithinNewDirectory.indexOf(fileName) >= 0) {
					// Directory exists
					File newDir = new File(newDirectoryFile, fileName);
					diffDirectory.addDirectory(FileDiffDirectory.compareDirectory(originalFile.getAbsolutePath(), newDir.getAbsolutePath()));
				} else {
					// Directory was deleted
					diffDirectory.addDirectory(FileDiffDirectory.compareDirectory(originalFile.getAbsolutePath(), null));
				}
			} else {
				if (filesWithinNewDirectory.indexOf(fileName) >= 0) {
					// File exists
					File newFile = new File(newDirectoryFile, fileName);
					diffDirectory.addFile(FileDiffFile.compareFile(originalFile.getAbsolutePath(), newFile.getAbsolutePath()));
				} else {
					// File was deleted
					diffDirectory.addFile(FileDiffFile.compareFile(originalFile.getAbsolutePath(), null));
				}
			}
		}
		
		//Secondly check the new directory for any new files or directories
		for(String fileName : filesWithinNewDirectory) {
			File newFile = new File(newDirectory, fileName);

			if (newFile.isHidden()) {
				continue;
			}
			if (filesWithinOriginalDirectory.indexOf(fileName) < 0) {
				if (newFile.isDirectory()) {
					// directory is new
					diffDirectory.addDirectory(FileDiffDirectory.compareDirectory(null, newFile.getAbsolutePath()));		
				} else {
					// file is new
					diffDirectory.addFile(FileDiffFile.compareFile(null, newFile.getAbsolutePath()));					
				}
			}
		}
		
		//Check if we need to changed this directory status to changed
		diffDirectory.setNewStatusIfNeeded();
		
		
		return diffDirectory;
	}
	
	
	/**
	 * Set the status to CHANGED if any directory or file within this directory is changed
	 */
	private void setNewStatusIfNeeded() {
		for(FileDiffDirectory dir : directories ) {
			if (dir.getState() != DiffState.SAME) {
				setState(DiffState.CHANGED);
				return;
			}
		}
		
		for(FileDiffFile file : files ) {
			if (file.getState() != DiffState.SAME) {
				setState(DiffState.CHANGED);
				return;
			}
		}
	}

	/**
	 * File a deleted directory with state deleted, only states DELETED or NEW are accepted
	 * @param fileDiffDirectory
	 * @return
	 * @throws IOException 
	 */
	private static FileDiffDirectory fillDirectoryDiff(String directory, DiffState state) throws IOException {
	
		File dir = new File(directory);
		
		File[] files = dir.listFiles();
		Arrays.sort(files);
		
		List<File> filesWithinDirectory = Arrays.asList(files);
		
		FileDiffDirectory diffDirectory = new FileDiffDirectory(directory, state);
		
		for(File file : filesWithinDirectory) {
			
			if (file.isHidden()) {
				continue;
			}
			
			if (file.isFile()) {
				if (state.equals(DiffState.DELETED)) {
					diffDirectory.addFile(FileDiffFile.compareFile(file.getAbsolutePath(), null));
				} else {
					diffDirectory.addFile(FileDiffFile.compareFile(null, file.getAbsolutePath()));
				}
			} else {
				if (state.equals(DiffState.DELETED)) {
					diffDirectory.addDirectory(FileDiffDirectory.compareDirectory(file.getAbsolutePath(), null));
				} else {
					diffDirectory.addDirectory(FileDiffDirectory.compareDirectory(null, file.getAbsolutePath()));
				}
			}
		}
		
		return diffDirectory;
	}
	
	/**
	 * Checks if the directory has no changes
	 * @return True if no changes were found in this directory, false otherwise
	 */
	public boolean isEmpty() {
		
		boolean isEmpty = files.size() == 0;
		
		for(FileDiffDirectory dir : directories) {
			isEmpty &= dir.isEmpty();
		}
		
		return isEmpty;
	}

	/**
	 * @return the directories
	 */
	public List<FileDiffDirectory> getDirectories() {
		return directories;
	}
}
