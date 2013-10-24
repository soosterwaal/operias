package operias.report;

import java.util.LinkedList;
import java.util.List;

import difflib.ChangeDelta;
import difflib.Delta;
import difflib.InsertDelta;
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
	private CoberturaClass revisedClass;
	
	/**
	 * Source diff information
	 */
	private DiffFile sourceDiff;
	
	
	/**
	 * Construct a new operias file diff for the changes
	 * @param originalClass
	 * @param revisedClass
	 * @param sourceDiff
	 */
	public OperiasFile(CoberturaClass originalClass, CoberturaClass revisedClass, DiffFile sourceDiff) {
		this.fileName = sourceDiff.getFileName();
		this.className = originalClass.getName();
		this.packageName = originalClass.getPackageName();
		this.changes = new LinkedList<OperiasChange>();
		this.originalClass = originalClass;
		this.revisedClass = revisedClass;
		this.sourceDiff = sourceDiff;
		
		if (!originalClass.getName().equals(revisedClass.getName())) {
			// Invalid class comparison, may not happen!
			System.exit(OperiasStatus.ERROR_OPERIAS_DIFF_INVALID_CLASS_COMPARISON.ordinal());
		}
		
		CompareLines(0, 0);
	}
	
	/**
	 * Compare lines with each other
	 * @param originalClassLine Line in the original class coverage information
	 * @param revisedClassLine Line in the new class coverage information
	 */
	public void CompareLines(int originalClassLine, int revisedClassLine) {
		if (originalClassLine > originalClass.getMaxLineNumber() && 
				revisedClassLine > revisedClass.getMaxLineNumber()) {
			return;
		}
		
		Delta change = sourceDiff.tryGetChange(originalClassLine);
		
		if (change == null) {
			// No source diff, check the coverage difference between the lines
			CoberturaLine originalLine = originalClass.tryGetLine(originalClassLine);
			CoberturaLine newLine = revisedClass.tryGetLine(revisedClassLine);
			
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
				
				if ((originalLine.getHits() == 0 && newLine.getHits() > 0) ||
						(originalLine.isCondition() && (!originalLine.isConditionCompletelyCovered() && newLine.isConditionCompletelyCovered()))) {
					// Increase delta
					changes.add(new CoverageIncreaseChange(originalClassLine, revisedClassLine));
				} else if ((originalLine.getHits() > 0 && newLine.getHits() == 0) ||
						(originalLine.isCondition() && (originalLine.isConditionCompletelyCovered() && !newLine.isConditionCompletelyCovered()))) {
					// Decrease delta
					changes.add(new CoverageDecreaseChange(originalClassLine, revisedClassLine));
				}
			}
			CompareLines(originalClassLine + 1, revisedClassLine + 1);	
		} else {
			if (change instanceof InsertDelta) {
				// New code was inserted
				InsertSourceChange insertChanges = new InsertSourceChange(originalClassLine, revisedClassLine, (InsertDelta) change);
				
				changes.add(insertChanges);
				
				CompareLines(originalClassLine + 1, revisedClassLine + change.getRevised().getLines().size());
				
			} else if (change instanceof ChangeDelta) {
				
			}
		}
	}

	/**
	 * @return the changes
	 */
	public List<OperiasChange> getChanges() {
		return changes;
	}
}