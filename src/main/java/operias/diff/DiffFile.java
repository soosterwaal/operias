package operias.diff;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import operias.report.OperiasReport;
import difflib.Delta;
import difflib.Delta.TYPE;
import difflib.DiffUtils;
import difflib.Patch;

/**
 * Contains the source diff changes for a file
 * @author soosterwaal
 *
 */
public class DiffFile {

	/**
	 * Original file name
	 */
	private String originalFileName;

	/**
	 * Original file name
	 */
	private String revisedFileName;
	
	/**
	 * State of the diff file
	 */
	private SourceDiffState sourceState;
	
		
	/**
	 * Changed in the file
	 */
	private List<Delta> changes;
	
	/**
	 * The amount of lines in the file
	 */
	private int originalLineCount;
	
	/**
	 * The amount of lines in the file
	 */
	private int revisedLineCount;

	/**
	 * Construct a new file for the file diff report
	 * @param fileName
	 */
	public DiffFile(String originalFileName, String revisedFileName, int originalLineCount, int revisedLineCount) {
		
		this.originalFileName = originalFileName == null ? "" : originalFileName;
		this.revisedFileName = revisedFileName == null ? "" : revisedFileName;
		
		if (originalLineCount == 0) {
			this.sourceState = SourceDiffState.NEW;
			this.originalFileName = "";
		} else if (revisedLineCount == 0) {
			this.sourceState = SourceDiffState.DELETED;
			this.revisedFileName = "";
		} else {
			this.sourceState = SourceDiffState.CHANGED;
		}
		this.changes = new LinkedList<Delta>();
		this.originalLineCount = originalLineCount;
		this.revisedLineCount = revisedLineCount;
	}
	
	/**
	 * Create a list of strings from a file
	 * @param filename File name
	 * @return List of string in the file
	 * @throws IOException 
	 */
	private static List<String> fileToLines(String filename) {
        List<String> lines = new LinkedList<String>();
        String line = "";
        
        try {
	        BufferedReader in = new BufferedReader(new FileReader(filename));
	        while ((line = in.readLine()) != null) {
	                lines.add(line);
	        }
	        
	        in.close();
        } catch(Exception e) {
        }
        
        return lines;
	}
	
	/**
	 * Compare two files, the original one against the new one
	 * @param originalFile 
	 * @param newFile
	 * @throws IOException 
	 */
	public static DiffFile compareFile(String originalFileName, String revisedFileName) throws IOException {
		
		List<String> originalFileList = fileToLines(originalFileName);
		List<String> newFileList = fileToLines(revisedFileName);
		
		DiffFile diffFile = new DiffFile(originalFileName, revisedFileName, originalFileList.size(), newFileList.size());
		
		Patch difference = DiffUtils.diff(originalFileList, newFileList);
		
		diffFile.setChanges(difference.getDeltas());
		
		return diffFile;
	}

	/**
	 * @return the fileName
	 */
	public String getOriginalFileName() {
		return originalFileName;
	}
	
	/**
	 * @return the fileName
	 */
	public String getRevisedFileName() {
		return revisedFileName;
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

	/**
	 * @return the originalLineCount
	 */
	public int getOriginalLineCount() {
		return originalLineCount;
	}

	/**
	 * @return the revisedLineCount
	 */
	public int getRevisedLineCount() {
		return revisedLineCount;
	}
	
	/**
	 * Get the file name including the path within the project
	 * @return
	 */
	public String getFileName(OperiasReport report) {
		
		String fileName = null;
		if (this.getSourceState() == SourceDiffState.NEW){
			fileName = this.getRevisedFileName().replace(new File(report.getSourceDiffReport().getRevisedDirectory()).getAbsolutePath(), "");
		} else {
			fileName = this.getOriginalFileName().replace(new File(report.getSourceDiffReport().getOriginalDirectory()).getAbsolutePath(), "");
		}
		
		return fileName;
	}
	
	/**
	 * COunt the amount of lines added
	 * @return Amount of lines added
	 */
	public int getAddedLinesCount() {
		int count = 0;
		for(Delta change : changes) {
			if (change.getType() != TYPE.DELETE) {
				count += change.getRevised().size();
			}
		}
		return count;
	}
	
	/**
	 * Count the amoutn of lines removed
	 * @return Amount of lines removed
	 */
	public int getRemovedLineCount() {
		int count = 0;
		for(Delta change : changes) {
			if (change.getType() != TYPE.DELETE) {
				count += change.getOriginal().size();
			}
		}
		return count;
	}
	
}
