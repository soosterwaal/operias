package operias.report;

import java.util.LinkedList;
import difflib.ChangeDelta;
import difflib.DeleteDelta;
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
	private LinkedList<OperiasChange> changes;
	
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
	 * Construct a new operias file diff for the given class,  for which the original class is unkown and thus source diff is also unkown
	 * @param cClass
	 */
	public OperiasFile(CoberturaClass revisedClass) {
		
		this.fileName = revisedClass.getFileName();
		this.className = revisedClass.getName();
		this.packageName = revisedClass.getPackageName();
		this.changes = new LinkedList<OperiasChange>();
		this.originalClass = null;
		this.revisedClass = revisedClass;
		this.sourceDiff = null;
		
		for(CoberturaLine line : revisedClass.getLines()) {
			if(line.isCovered()) {
				changes.add(new CoverageIncreaseChange(line.getNumber()));
			} else {
				changes.add(new CoverageDecreaseChange(line.getNumber()));
			}
		}
	}
	
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
		
		CompareLines(1, 1);
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
		
		// Source diff starts at lines 0
		Delta change = sourceDiff.tryGetChange(originalClassLine - 1, revisedClassLine - 1);
		
		if (change == null) {
			// No source diff, check the coverage difference between the lines
			CoberturaLine originalLine = originalClass.tryGetLine(originalClassLine);
			CoberturaLine revisedLine = revisedClass.tryGetLine(revisedClassLine);
			
			if (originalLine == null ^ revisedLine == null) {
				// Something went wrong!
				System.exit(OperiasStatus.ERROR_OPERIAS_INVALID_LINE_COMPARISON.ordinal());
			}
			
			if (originalLine != null) {
				// Lines found, compare!
				if (originalLine.isCondition() ^ revisedLine.isCondition()) {
					// Again something went wrong i suppose... no change in the line, so is either should both be conditions or not
					System.exit(OperiasStatus.ERROR_OPERIAS_INVALID_LINE_COMPARISON.ordinal());				
				}
				
				if (!originalLine.isCovered() && revisedLine.isCovered()) {
					// Increase delta
					changes.add(new CoverageIncreaseChange(originalClassLine, revisedClassLine));
				} else if (originalLine.isCovered() && !revisedLine.isCovered()) {
					// Decrease delta
					changes.add(new CoverageDecreaseChange(originalClassLine, revisedClassLine));
				}
			}
			CompareLines(originalClassLine + 1, revisedClassLine + 1);	
		} else {
			SourceChange sourceChange = null;
			if (change instanceof InsertDelta) {
				sourceChange = new InsertSourceChange(originalClassLine, revisedClassLine, (InsertDelta) change);
			} else if (change instanceof ChangeDelta) {
				sourceChange = new ChangeSourceChange(originalClassLine, revisedClassLine, (ChangeDelta) change);
			} else {
				sourceChange = new DeleteSourceChange(originalClassLine, revisedClassLine, (DeleteDelta) change);
			}
			
			if (!(change instanceof DeleteDelta)) {
				for(int i = revisedClassLine; i < revisedClassLine + change.getRevised().getLines().size(); i++) {
					CoberturaLine line = revisedClass.tryGetLine(i);
					if (line == null) {
						sourceChange.addRevisedCoverageLine(null);
					} else {
						sourceChange.addRevisedCoverageLine(line.isCovered());
					}
				}
			}
			
			if (!(change instanceof InsertDelta)) {
				for(int i = originalClassLine; i < originalClassLine + change.getOriginal().getLines().size(); i++) {
					CoberturaLine line = originalClass.tryGetLine(i);
					if (line == null) {
						sourceChange.addOriginalCoverageLine(null);
					} else {
						sourceChange.addOriginalCoverageLine(line.isCovered());
					}
				}
			}
			changes.add(sourceChange);
			
			CompareLines(originalClassLine + change.getOriginal().getLines().size(), revisedClassLine + change.getRevised().getLines().size());
		}
		
	}

	/**
	 * @return the changes
	 */
	public LinkedList<OperiasChange> getChanges() {
		return changes;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @return the packageName
	 */
	public String getPackageName() {
		return packageName;
	}

	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}
}