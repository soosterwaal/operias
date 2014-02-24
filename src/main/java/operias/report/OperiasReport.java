package operias.report;

import java.util.LinkedList;
import java.util.List;

import operias.OperiasStatus;
import operias.cobertura.CoberturaClass;
import operias.cobertura.CoberturaPackage;
import operias.cobertura.CoberturaReport;
import operias.diff.DiffDirectory;
import operias.diff.DiffFile;
import operias.diff.DiffReport;
import operias.diff.SourceDiffState;

/**
 * Operias Report class.
 * This class contains the combined information from the source diff report 
 * the two cobertura reports, which were created in the first phase.
 * 
 * This report can be used to generate the HTML report for a pretty visualisation
 * of the information
 * 
 * @author soosterwaal
 *
 */
public class OperiasReport {

	/**
	 * Original report of cobertura
	 */
	private CoberturaReport originalReport;
	
	/**
	 * New report of cobertura
	 */
	private CoberturaReport revisedReport;
	
	/**
	 * Source diff report between the directories
	 */
	private DiffReport sourceDiffReport;
	
	/**
	 * List of the changed classes
	 */
	private List<OperiasFile> changedClasses;
	
	/**
	 * List of changes test suites
	 */
	private List<DiffFile> changedTests;
	
	/**
	 * Construct a new operias report
	 * @param reportRepo
	 * @param reportSource
	 * @param reportFileDiff
	 */
	public OperiasReport(CoberturaReport originalReport, CoberturaReport revisedReport, DiffReport sourceDiffReport) {
		this.originalReport = originalReport;
		this.revisedReport = revisedReport;
		this.sourceDiffReport = sourceDiffReport;
		this.changedClasses = new LinkedList<OperiasFile>();
		this.changedTests = new LinkedList<DiffFile>();
		
		ParseReport();
	}
	
	/**
	 * Parse the reports
	 */
	private void ParseReport() {
		
		// First we loop through the old packages and classes, and compare this to the new classes
		for(CoberturaPackage oPackage : originalReport.getPackages()) {
			CoberturaPackage rPackage = revisedReport.getPackage(oPackage.getName());
			
			// If package == null, the package was deleted, so the class was deleted, or the package name was new (will be marked in next phase) so we can ignore it
			if (rPackage != null) {
				for(CoberturaClass oClass : oPackage.getClasses()) {
					
					DiffFile fileDiff = sourceDiffReport.getFile("src/main/java/" + oClass.getFileName());
					CoberturaClass rClass = rPackage.getClass(oClass.getName());
					
					if (rClass != null) {
						// We got the class, so we can compare the classes for any differences
						OperiasFile newOFile = new OperiasFile(oClass, rClass, fileDiff);
						if (newOFile.getChanges().size() > 0) {
							changedClasses.add(newOFile);
						}
					} else {
						// Class was deleted!
						if (fileDiff.getSourceState() == SourceDiffState.DELETED) {
							
							OperiasFile newOFile = new OperiasFile(oClass, fileDiff);
							if (newOFile.getChanges().size() > 0) {
								changedClasses.add(newOFile);
							}
						} else {
							// @TODO: think about how to fix this? Using the current structure, a file can only be marked
							// delete if the diff report says its deleted.
							System.out.println("Found a innerclass which was deleted: " + oClass.getName());
						}
					}
					
				}
			} else {
				// All classes must be marked as deleted
				for(CoberturaClass oClass : oPackage.getClasses()) {
					DiffFile fileDiff = sourceDiffReport.getFile("src/main/java/" + oClass.getFileName());
					OperiasFile newOFile = new OperiasFile(oClass, fileDiff);
					if (newOFile.getChanges().size() > 0) {
						changedClasses.add(newOFile);
					}
				}
			}
		}
		
		// Next we loop through the newCobertura classes and check the newly added files
		for(CoberturaPackage rPackage : revisedReport.getPackages()) {
			CoberturaPackage oPackage = originalReport.getPackage(rPackage.getName());
			
			if (oPackage == null) {
				// Package was new so, all classes should be "new"
				for(CoberturaClass rClass : rPackage.getClasses()) {
					DiffFile fileDiff = sourceDiffReport.getFile("src/main/java/" + rClass.getFileName());
					OperiasFile newOFile = new OperiasFile(rClass, fileDiff);
					if (newOFile.getChanges().size() > 0) {
						changedClasses.add(newOFile);
					}
				}
			} else {
				// Package was found, check which classes are new
				for(CoberturaClass rClass : rPackage.getClasses()) {
					CoberturaClass oClass = oPackage.getClass(rClass.getName());
					
					if (oClass == null) {
						// Class was new
						DiffFile fileDiff = sourceDiffReport.getFile("src/main/java/" + rClass.getFileName());
						if (fileDiff.getSourceState() == SourceDiffState.NEW) {
							OperiasFile newOFile = new OperiasFile(rClass, fileDiff);
							if (newOFile.getChanges().size() > 0) {
								changedClasses.add(newOFile);
							}
						} else {
							System.out.println("Found a innerclass which was added: " + rClass.getName());
						}
					}
				}
			}
		}
		
		
		// Finnaly, retrieve all changes test classes
		DiffDirectory testDirectory = sourceDiffReport.getDirectory("src/test/java");
		
		// And loop through all files to collect the changed ones
		collectChangedTests(testDirectory);
	
	}
	
	/**
	 * Collect all the changed files in a given directory
	 * @param directory
	 */
	private void collectChangedTests(DiffDirectory directory) {
		for(DiffFile file : directory.getFiles()) {
			if (file.getSourceState() != SourceDiffState.SAME) {
				this.changedTests.add(file);
			}
		}
		
		for(DiffDirectory subDirectory : directory.getDirectories()) {
			collectChangedTests(subDirectory);
		}
	}

	/**
	 * @return the changedClasses
	 */
	public List<OperiasFile> getChangedClasses() {
		return changedClasses;
	}
	
	/**
	 * @return the originalReport
	 */
	public CoberturaReport getOriginalCoverageReport() {
		return originalReport;
	}

	/**
	 * @return the revisedReport
	 */
	public CoberturaReport getRevisedCoverageReport() {
		return revisedReport;
	}
	
	/**
	 * 
	 * @return the changed tests
	 */
	public List<DiffFile> getChangedTests() {
		return changedTests;
	}

	/**
	 * @return the sourceDiffReport
	 */
	public DiffReport getSourceDiffReport() {
		return sourceDiffReport;
	}

}
