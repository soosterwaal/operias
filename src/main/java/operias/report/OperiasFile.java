package operias.report;

import java.util.LinkedList;
import java.util.List;

import difflib.Delta;
import operias.OperiasStatus;
import operias.cobertura.CoberturaClass;
import operias.cobertura.CoberturaLine;
import operias.diff.DiffFile;

public class OperiasFile {

	/**
	 * File name
	 */
	private String fileName;
	
	/**
	 * Package name
	 */
	private String packageName;
	
	/**
	 * Class name
	 */
	private String className;
	
	/**
	 * Changes of the file
	 */
	private List<OperiasChange> changes;
	
	/**
	 * Original class coverage information
	 */
	private CoberturaClass originalClass;
	
	/**
	 * New class coverage information
	 */
	private CoberturaClass newClass;
	
	/**
	 * Source diff information
	 */
	private DiffFile sourceDiff;
	
	
	/**
	 * Construct a new operias file diff for the changes
	 * @param originalClass
	 * @param newClass
	 * @param sourceDiff
	 */
	public OperiasFile(CoberturaClass originalClass, CoberturaClass newClass, DiffFile sourceDiff) {
		this.fileName = sourceDiff.getFileName();
		this.className = originalClass.getName();
		this.changes = new LinkedList<OperiasChange>();
		this.originalClass = originalClass;
		this.newClass = newClass;
		this.sourceDiff = sourceDiff;
		
		if (!originalClass.getName().equals(newClass.getName())) {
			// Invalid class comparison, may not happen!
			System.exit(OperiasStatus.ERROR_OPERIAS_DIFF_INVALID_CLASS_COMPARISON.ordinal());
		}
	}
	
	/**
	 * Compare lines with each other
	 * @param originalClassLine Line in the original class coverage information
	 * @param newClassLine Line in the new class coverage information
	 */
	public void CompareLines(int originalClassLine, int newClassLine) {
		Delta change = sourceDiff.tryGetChange(originalClassLine);
		
		if (change == null) {
			// No source diff, check the coverage difference between the lines
			CoberturaLine originalLine = originalClass.tryGetLine(originalClassLine);
			CoberturaLine newLine = newClass.tryGetLine(newClassLine);
			
			if (originalLine == null ^ newLine == null) {
				// Something went wrong!
				System.exit(OperiasStatus.ERROR_OPERIAS_INVALID_LINE_COMPARISON.ordinal());
			}
			
			if (originalLine != null) {
				// Lines found, compare!
				if (originalLine.isCondition() ^ newLine.isCondition()) {
					// Again something went wrong i suppose... no change in the line, so is either should both be conditions or not
					System.exit(OperiasStatus.ERROR_OPERIAS_INVALID_LINE_COMPARISON.ordinal());				
				}
				
				if (originalLine.isCondition()) {
					if ((originalLine.getHits() == 0 && newLine.getHits() > 0) ||
							(!originalLine.isConditionCompletelyCovered() && newLine.isConditionCompletelyCovered())) {
						// Increase delta
					} else if ((originalLine.getHits() > 0 && newLine.getHits() == 0) ||
							(originalLine.isConditionCompletelyCovered() && !newLine.isConditionCompletelyCovered())) {
						// Decrease delta
					}
				} else {
					if (originalLine.getHits() == 0 && newLine.getHits() > 0) {
						// Increase delta
					} else if (originalLine.getHits() > 0 && newLine.getHits() == 0) {
						// Decrease delta
						
					}
				}
			}
			
		} else {
			
		}
	}
}