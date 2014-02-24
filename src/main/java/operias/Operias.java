package operias;

import java.io.IOException;

import operias.cobertura.*;
import operias.diff.DiffReport;
import operias.html.HTMLReport;
import operias.report.OperiasReport;

/**
 * Base class of the tool, where the information is combined and the reports and html sites are generated
 * @author soosterwaal
 *
 */
public class Operias {

	OperiasReport report;
	
	CoberturaReport reportRevised;
	
	CoberturaReport reportOriginal;
	
	DiffReport reportFileDiff;
	/**
	 * Construct a report based on the difference in source files and coverage between the two folders in the configuration
	 * @return Operias instance
	 */
	public Operias constructReport() {

		// Construct the cobertura reports
		Thread reportRevisedThread = new Thread("RevisedCoverageThread") { public void run() { reportRevised = constructCoberturaReport(Configuration.getRevisedDirectory());}};
		Thread reportOriginalThread = new Thread("OriginalCoverageThread") { public void run() { reportOriginal = constructCoberturaReport(Configuration.getOriginalDirectory());}};
		Thread reportFileDiffThread = new Thread("DiffReportThread") { public void run() {
			try {
				reportFileDiff = new DiffReport(Configuration.getOriginalDirectory(), Configuration.getRevisedDirectory());
			} catch (IOException e) {
				System.exit(OperiasStatus.ERROR_FILE_DIFF_REPORT_GENERATION.ordinal());
			}
		}};
		
		
		reportRevisedThread.start();
		reportOriginalThread.start();
		reportFileDiffThread.start();
		
		try {
			reportRevisedThread.join();
			reportOriginalThread.join();
			reportFileDiffThread.join();
		} catch (InterruptedException e1) {
			System.exit(OperiasStatus.ERROR_THREAD_JOINING.ordinal());
		}
		
		report = new OperiasReport(reportOriginal, reportRevised, reportFileDiff);
		
		return this;
	}
	
	/**
	 * Construct a cobertura coverage report for the given directory
	 * @param baseDirectory Directory containing the source code which needs to be checked for coverage
	 * @param destinationDirectory Destination folder for the result
	 * @param dataFile Data file used
	 * @return A cobertura report containing coverage metrics
	 */
	private CoberturaReport constructCoberturaReport(String baseDirectory) {
		
		Cobertura cobertura = new Cobertura(baseDirectory);
		
		CoberturaReport report = cobertura.executeCobertura();
		
		if (report == null) {
			System.exit(OperiasStatus.ERROR_COBERTURA_TASK_EXECUTION.ordinal());	
		}
		
		return report;
	}
	
	
	/**
	 * Write a site based on the report
	 * @return Operias instance
	 */
	public Operias writeSite() {
		try {
			(new HTMLReport(report)).generateSite();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return this;
	}
}
