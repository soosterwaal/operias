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
	private CoberturaReport newReport;
	
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
	public OperiasReport(CoberturaReport originalReport, CoberturaReport newReport, DiffReport sourceDiffReport) {
		this.originalReport = originalReport;
		this.newReport = newReport;
		this.sourceDiffReport = sourceDiffReport;
		this.changedClasses = new LinkedList<OperiasFile>();
		
		ParseReport();
	}
	
	/**
	 * Parse the reports
	 */
	private void ParseReport() {
		
		// First we loop through the old packages and classes, and compare this to the new classes
		for(CoberturaPackage cPackage : originalReport.getPackages()) {
			CoberturaPackage nPackage = newReport.getPackage(cPackage.getName());
			
			// If package == null, the package was deleted, so the class was deleted, or the package name was new (will be marked in next phase) so we can ignore it
			if (nPackage != null) {
				for(CoberturaClass cClass : cPackage.getClasses()) {
					
					DiffFile fileDiff = sourceDiffReport.getFile("src/main/java/" + cClass.getFileName());
					
					// Only if the source was changed or the same
					if (fileDiff.getSourceState() == SourceDiffState.SAME || fileDiff.getSourceState() == SourceDiffState.CHANGED) {
							
						CoberturaClass nClass = nPackage.getClass(cClass.getName());
						
						// if nClass == null, the class was deleted, but the diffstate says its stayed the same, so we have an error that cannot occur!
						if (nClass == null) {
							System.exit(OperiasStatus.ERROR_COBERTURA_CLASS_REPORT_NOT_FOUND.ordinal());
						} else {
							// We got the class, so we can compare the classes for any differences
							changedClasses.add(new OperiasFile(cClass, nClass, fileDiff));
						}
					} 
				}
			}
		}
		
		// Next we loop through the newCobertura classes and check the newly added files
		for(CoberturaPackage cPackage : newReport.getPackages()) {
			CoberturaPackage nPackage = originalReport.getPackage(cPackage.getName());
			
			if (nPackage == null) {
				// Package was new so, all classes should be "new"
			}
		}
	}

}
