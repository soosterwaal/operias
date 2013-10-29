package operias.report;

import java.util.LinkedList;
import java.util.List;

import operias.OperiasStatus;
import operias.cobertura.CoberturaClass;
import operias.cobertura.CoberturaPackage;
import operias.cobertura.CoberturaReport;
import operias.diff.DiffFile;
import operias.diff.DiffReport;
import operias.diff.SourceDiffState;

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
					
					// if nClass == null, the class was deleted, but the diffstate says its stayed the same, so we have an error that cannot occur!
					if (rClass == null) {
						System.exit(OperiasStatus.ERROR_COBERTURA_CLASS_REPORT_NOT_FOUND.ordinal());
					} else {
						// We got the class, so we can compare the classes for any differences
						changedClasses.add(new OperiasFile(oClass, rClass, fileDiff));
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
					changedClasses.add(new OperiasFile(rClass));
				}
			} else {
				// Package was found, check which classes are new
				for(CoberturaClass rClass : rPackage.getClasses()) {
					CoberturaClass oClass = oPackage.getClass(rClass.getName());
					
					if (oClass == null) {
						// Class was new
						changedClasses.add(new OperiasFile(rClass));
					}
				}
			}
		}
	}

	/**
	 * @return the changedClasses
	 */
	public List<OperiasFile> getChangedClasses() {
		return changedClasses;
	}

}
