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

/**
 * Contains the source diff changes for a file
 * @author soosterwaal
 *
 */
public class DiffFile {

	/**
	 * File name
	 */
	private String fileName;
	
	/**
	 * State of the diff file
	 */
	private SourceDiffState sourceState;
	
		
	/**
	 * Changed in the file
	 */
	private List<Delta> changes;
	

	/**
	 * Construct a new file for the file diff report
	 * @param fileName
	 */
	public DiffFile(String fileName, SourceDiffState state) {
		this.fileName = fileName;
		this.sourceState = state;
		this.changes = new LinkedList<Delta>();
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
	public static DiffFile compareFile(String originalFile, String newFile) throws IOException {
		DiffFile diffFile = new DiffFile(newFile, SourceDiffState.CHANGED);
		
		List<String> originalFileList = new ArrayList<String>();
		List<String> newFileList = new ArrayList<String>();
		
		try {
			 originalFileList = fileToLines(originalFile);
		} catch (NullPointerException | FileNotFoundException e) {
			diffFile = new DiffFile(newFile, SourceDiffState.NEW); 
		}
		
		try {
			newFileList = fileToLines(newFile);
		} catch (NullPointerException | FileNotFoundException e) {
			diffFile = new DiffFile(originalFile, SourceDiffState.DELETED); 
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
	public SourceDiffState getSourceState() {
		return sourceState;
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
			sourceState = SourceDiffState.SAME;
		}
		
		this.changes = changes;
	}
	
	/**
	 * Get the delta for the original line number
	 * @param originalLineNumber Line number for the original file
	 * @return
	 */
	public Delta tryGetChange(int originalLineNumber, int revisedLineNumber) {
		for(Delta change : changes) {
			if (change.getOriginal().getPosition() == originalLineNumber && change.getRevised().getPosition() == revisedLineNumber) {
				return change;
			} else if (change.getOriginal().getPosition() > originalLineNumber && change.getRevised().getPosition() > revisedLineNumber) {
				break;
			}
		}
		
		return null;
	}

}
