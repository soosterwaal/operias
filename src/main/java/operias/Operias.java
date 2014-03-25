package operias;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import operias.cobertura.*;
import operias.diff.DiffReport;
import operias.output.html.HTMLReport;
import operias.output.xml.XMLReport;
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

		System.out.println("[Info] Setting up threads");
		// Construct the cobertura reports
		Thread reportRevisedThread = new Thread("RevisedCoverage") { public void run() { reportRevised = constructCoberturaReport(Configuration.getRevisedDirectory());}};
		Thread reportOriginalThread = new Thread("OriginalCoverage") { public void run() { reportOriginal = constructCoberturaReport(Configuration.getOriginalDirectory());}};
		Thread reportFileDiffThread = new Thread("DiffReport") { public void run() {
			try {
				reportFileDiff = new DiffReport(Configuration.getOriginalDirectory(), Configuration.getRevisedDirectory());
			} catch (IOException e) {
				System.out.println("[Info] [" + Thread.currentThread().getName() + "] Error while comparing directory \"" +Configuration.getRevisedDirectory() + "\" to \"" + Configuration.getOriginalDirectory()+ "\"");
			
				System.exit(OperiasStatus.ERROR_FILE_DIFF_REPORT_GENERATION.ordinal());
			}
		}};
		

		System.out.println("[Info] Starting threads");
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
		System.out.println("[Info] Start to combine reports");
		
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
	public Operias writeHTMLReport() {
		
		System.out.println("[Info] Start writing data to html report");
		try {
			(new HTMLReport(report)).generateReport();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this;
	}
	
	/**
	 * Write a xml report based on the operias report
	 * @return This operias instance
	 */
	public Operias writeXMLReport() {
		
		System.out.println("[Info] Start writing data to xml report");
		
		try {
			(new XMLReport(report)).generateReport();
		} catch (ParserConfigurationException | TransformerException | IOException e) {
			e.printStackTrace();
		}
		
		return this;
	}
}
