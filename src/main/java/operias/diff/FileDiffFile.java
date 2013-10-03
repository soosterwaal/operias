package operias.diff;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

public class FileDiffFile {

	/**
	 * File name
	 */
	private String fileName;
	
	/**
	 * State of the diff file
	 */
	private DiffState state;
	
	/**
	 * Changed in the file
	 */
	private List<Delta> changes;

	/**
	 * Construct a new file for the file diff report
	 * @param fileName
	 */
	public FileDiffFile(String fileName, DiffState state) {
		this.fileName = fileName;
		this.state = state;
	}
	
	/**
	 * Create a list of strings from a file
	 * @param filename File name
	 * @return List of string in the file
	 * @throws IOException 
	 */
	private static List<String> fileToLines(String filename) throws IOException {
        List<String> lines = new LinkedList<String>();
        String line = "";
        
        BufferedReader in = new BufferedReader(new FileReader(filename));
        while ((line = in.readLine()) != null) {
                lines.add(line);
        }
        
        in.close();
        
        return lines;
	}
	
	/**
	 * Compare two files, the original one against the new one
	 * @param originalFile 
	 * @param newFile
	 * @throws IOException 
	 */
	public static FileDiffFile compareFile(String originalFile, String newFile) throws IOException {
		FileDiffFile diffFile = new FileDiffFile(newFile, DiffState.CHANGED);
		
		List<String> originalFileList = new ArrayList<String>();
		List<String> newFileList = new ArrayList<String>();
		
		try {
			 originalFileList = fileToLines(originalFile);
		} catch (NullPointerException | FileNotFoundException e) {
			diffFile = new FileDiffFile(newFile, DiffState.NEW); 
		}
		
		try {
			newFileList = fileToLines(newFile);
		} catch (NullPointerException | FileNotFoundException e) {
			diffFile = new FileDiffFile(originalFile, DiffState.DELETED); 
		}
		
		Patch difference = DiffUtils.diff(originalFileList, newFileList);
		
		diffFile.setChanges(difference.getDeltas());
		
		return diffFile;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @return the state
	 */
	public DiffState getState() {
		return state;
	}

	/**
	 * @return the changes
	 */
	public List<Delta> getChanges() {
		return changes;
	}

	/**
	 * @param changes the changes to set
	 */
	public void setChanges(List<Delta> changes) {
		if (changes.isEmpty()) {
			state = DiffState.SAME;
		}
		
		this.changes = changes;
	}
}
