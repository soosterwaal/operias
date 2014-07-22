package operias.report;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import operias.Main;
import operias.coverage.CoberturaClass;
import operias.coverage.CoberturaPackage;
import operias.coverage.CoverageReport;
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
	private CoverageReport originalReport;
	
	/**
	 * New report of cobertura
	 */
	private CoverageReport revisedReport;
	
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
	 * A list of possible file locations
	 */
	private List<String> sourceLocations;
	/**
	 * Construct a new operias report
	 * @param reportRepo
	 * @param reportSource
	 * @param reportFileDiff
	 */
	public OperiasReport(CoverageReport originalReport, CoverageReport revisedReport, DiffReport sourceDiffReport) {
		this.originalReport = originalReport;
		this.revisedReport = revisedReport;
		this.sourceDiffReport = sourceDiffReport;
		this.changedClasses = new LinkedList<OperiasFile>();
		this.changedTests = new LinkedList<DiffFile>();
		
		// Combine all sources from both the original and revised reports
		sourceLocations = new ArrayList<String>(originalReport.getSources());
		sourceLocations.addAll(revisedReport.getSources());
		
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
					
					List<DiffFile> fileDiffs = sourceDiffReport.getFiles(sourceLocations, oClass.getFileName());
					
					for(DiffFile fileDiff : fileDiffs) {
						// Class was deleted!
						if (fileDiff.getSourceState() == SourceDiffState.DELETED) {
							
							OperiasFile newOFile = new OperiasFile(oClass, fileDiff);
							if (newOFile.getChanges().size() > 0) {
								changedClasses.add(newOFile);
							}
						} else  {
							CoberturaClass rClass = rPackage.getClass(oClass.getName());
							if (rClass != null) {
								// We got the class, so we can compare the classes for any differences
								OperiasFile newOFile = new OperiasFile(oClass, rClass, fileDiff);
								if (newOFile.getChanges().size() > 0) {
									changedClasses.add(newOFile);
								}
							} else {
								// @TODO: think about how to fix this? Using the current structure, a file can only be marked
								// delete if the diff report says its deleted.
								Main.printLine("[Warning] Found a innerclass which was deleted: " + oClass.getName());
							}
						}
					}
				}
			} else {
				// All classes must be marked as deleted
				for(CoberturaClass oClass : oPackage.getClasses()) {
					DiffFile fileDiff = sourceDiffReport.getFile(sourceLocations, oClass.getFileName(), SourceDiffState.DELETED);
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
					DiffFile fileDiff = sourceDiffReport.getFile(sourceLocations, rClass.getFileName(), SourceDiffState.NEW);
					OperiasFile newOFile = new OperiasFile(rClass, fileDiff);
					if (newOFile.getChanges().size() > 0) {
						changedClasses.add(newOFile);
					}
				}
			} else {
				// Package was found, check which classes are new
				for(CoberturaClass rClass : rPackage.getClasses()) {
					CoberturaClass oClass = oPackage.getClass(rClass.getName());
					

					DiffFile fileDiff = sourceDiffReport.getFile(sourceLocations, rClass.getFileName(), SourceDiffState.NEW);
					
					
					if (fileDiff != null && oClass == null) {
						// Class was new
						OperiasFile newOFile = new OperiasFile(rClass, fileDiff);
						if (newOFile.getChanges().size() > 0) {
							changedClasses.add(newOFile);
						}
					} else if (oClass == null) {
						Main.printLine("[Warning] Found a innerclass which was added: " + rClass.getName());
					
					}
				}
			}
		}
		
		Main.printLine("[Info] Done collecting and combining the changed classes");
		
		Main.printLine("[Info] Collect changed test files");
		
		// Finnaly, retrieve all changes test classes
		for(String sourceLocation : sourceLocations) {
			String baseLocation = sourceLocation.replaceAll("src/main/java", "src/test/java");
			DiffDirectory testDirectory = sourceDiffReport.getDirectory(baseLocation);
			collectChangedTests(testDirectory);
		}
		
		// And loop through all files to collect the changed ones
		Main.printLine("[Info] Done collecting changed test files");
	
	}
	
	/**
	 * Collect all the changed files in a given directory
	 * @param directory
	 */
	private void collectChangedTests(DiffDirectory directory) {
		for(DiffFile file : directory.getFiles()) {
			// Check if the file was changed, and not yet adedd to the list
			if (file.getSourceState() != SourceDiffState.SAME && !this.changedTests.contains(file)) {
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
	public CoverageReport getOriginalCoverageReport() {
		return originalReport;
	}

	/**
	 * @return the revisedReport
	 */
	public CoverageReport getRevisedCoverageReport() {
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
