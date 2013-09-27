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
	
	/**
	 * Link to the coverage file produced by cobertura
	 */
	private File coverageXML;
	
	public Cobertura(String directory) {
		this.directory = directory;
		this.outputDirectory = "";
		this.coverageXML = null;
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
	 * @return True if the execution succeeded, false otherwise
	 */
	public boolean executeCobertura() {
		File pomXML = new File(directory, "pom.xml");
		ProcessBuilder builder = new ProcessBuilder("mvn","clean", "cobertura:cobertura", "-Dcobertura.report.format=xml", "-f", pomXML.getAbsolutePath());
		
		Process process = null;
		boolean executionSucceeded = false;
		try {
			process = builder.start();
			process.waitFor();
			int exitValue = process.exitValue();
			process.destroy();
			
			executionSucceeded = exitValue == 0;
		} catch (Exception e) {
			System.exit(OperiasStatus.ERROR_COBERTURA_TASK_CREATION.ordinal());
		}
		
		if (executionSucceeded) {
			retrieveXMLFile();
		}
		
		return executionSucceeded;
			
	}
	
	/**
	 * Retrieve the coverage.xml file
	 */
	private void retrieveXMLFile() {
		coverageXML = new File(outputDirectory, "target/site/cobertura/coverage.xml");
		
		if (!coverageXML.exists()) {
			System.exit(OperiasStatus.COVERAGE_XML_NOT_FOUND.ordinal());
		}
	}
	
	/**
	 * Construct a report from the coverage.xml file
	 * @return
	 */
	public CoberturaReport constructReport() {
		if(coverageXML == null) {
			return null;
		}
		
		return new CoberturaReport(coverageXML);
	}
	
	/**
	 * Clean the maven project
	 */
	public boolean cleanUp() {
		File pomXML = new File(directory, "pom.xml");
		ProcessBuilder builder = new ProcessBuilder("mvn","clean", "-f", pomXML.getAbsolutePath());
		
		Process process = null;
		boolean executionSucceeded = false;
		try {
			process = builder.start();
			process.waitFor();
			int exitValue = process.exitValue();
			process.destroy();
			
			executionSucceeded = exitValue == 0;
		} catch (Exception e) {
			System.exit(OperiasStatus.ERROR_COBERTURA_CLEAN_TASK_CREATION.ordinal());
		}
		
		coverageXML = null;
		
		return executionSucceeded;
	}
}
