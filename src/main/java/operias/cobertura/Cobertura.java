package operias.cobertura;

import java.io.File;
import java.io.IOException;

import operias.OperiasStatus;

/**
 * This class is used to execute cobertura for a maven project
 * @author soosterwaal
 *
 */
public class Cobertura {
	
	/**
	 * Directory of the maven project
	 */
	private String directory;
	
	/**
	 * Output directory of the maven project, normally this would be empty
	 */
	private String outputDirectory;
	
	
	public Cobertura(String directory) {
		this.directory = directory;
		this.outputDirectory = directory;
	}
	
	/**
	 * Set a different output directory (mainly used for tests, which have a target directory within the main target directory of operias)
	 * @param outputDirectory Absolute path to output directory
	 */
	public void setOutputDirectory(String outputDirectory) {
		this.outputDirectory = outputDirectory;
	}
	
	/**
	 * Execute cobertura on the given directory,
	 * we need to execute mvn cobertura:cobertura -Dcobertura.report.format=xml -f directory on the target directory.
	 * @return A Cobertura report object
	 */
	public CoberturaReport executeCobertura() {
		boolean succeeded;
		try {
			succeeded = executeCoberturaTask();		
			if (succeeded) {
				CoberturaReport coberturaReport = constructReport();
				cleanUp();
				return coberturaReport;
			}
			
		} catch (IOException | InterruptedException e) {
			System.exit(OperiasStatus.ERROR_COBERTURA_TASK_CREATION.ordinal());
		}
		
		return null;
	}
	
	/**
	 * Execute the cobertura task
	 * @return true if cobertura was succesfully executed, false otherwise
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	private boolean executeCoberturaTask() throws IOException, InterruptedException {
		boolean executionSucceeded = false;

		File pomXML = new File(directory, "pom.xml");
		String  firstString = null, secondString = null;
		
		try {
			firstString = (new File(directory)).getCanonicalPath();
			secondString = (new File("")).getCanonicalPath();
		} catch (Exception e) {
			System.exit(OperiasStatus.ERROR_COBERTURA_TASK_CREATION.ordinal());
		}
		
		if (firstString.equals(secondString)) {
			System.exit(OperiasStatus.ERROR_COBERTURA_TASK_OPERIAS_EXECUTION.ordinal());
		}
		
		ProcessBuilder builder = new ProcessBuilder("mvn","clean", "cobertura:cobertura", "-Dcobertura.report.format=xml", "-f", pomXML.getAbsolutePath());
		System.out.println("Start executing mvn");
		Process process = null;
		process = builder.start();
		process.waitFor();
		int exitValue = process.exitValue();
		process.destroy();
		
		executionSucceeded = exitValue == 0;
		System.out.println("Execution of mvn complete : " + executionSucceeded);
		
		return executionSucceeded;	
	}
	
	
	/**
	 * Construct a report from the coverage.xml file
	 * @return
	 */
	private CoberturaReport constructReport() {
		File coverageXML = new File(outputDirectory, "target/site/cobertura/coverage.xml");
		
		System.out.println(coverageXML.getAbsolutePath());
		if (!coverageXML.exists()) {
			System.exit(OperiasStatus.COVERAGE_XML_NOT_FOUND.ordinal());
		}
		
		return new CoberturaReport(coverageXML);
	}
	
	/**
	 * Clean the maven project
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	private boolean cleanUp() throws InterruptedException, IOException {
		File pomXML = new File(directory, "pom.xml");
		ProcessBuilder builder = new ProcessBuilder("mvn","clean", "-f", pomXML.getAbsolutePath());
		
		Process process = builder.start();
		process.waitFor();
		int exitValue = process.exitValue();
		process.destroy();
			
		return exitValue == 0;
	}
}
