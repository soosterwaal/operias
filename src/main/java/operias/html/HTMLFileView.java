package operias.html;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

import org.apache.commons.io.IOUtils;

import operias.report.OperiasFile;
import operias.report.change.ChangeSourceChange;
import operias.report.change.CoverageDecreaseChange;
import operias.report.change.CoverageIncreaseChange;
import operias.report.change.DeleteSourceChange;
import operias.report.change.InsertSourceChange;
import operias.report.change.OperiasChange;

public class HTMLFileView {
	
	private List<OperiasFile> changedFiles;
	
	/**
	 * Create a new hTML file view page
	 * @param file
	 * @throws IOException
	 */
	public HTMLFileView(OperiasFile file, List<OperiasFile> changedFiles) throws IOException {
		this.changedFiles = changedFiles;
		BufferedReader sourceFileReader = new BufferedReader(new FileReader(file.getFileName()));
		
		File classHTMLFile = new File("site/" + file.getClassName() + ".html");
		classHTMLFile.createNewFile();
		
		PrintStream outputStreamHTMLFile = new PrintStream(classHTMLFile);
		InputStream headerStream = getClass().getResourceAsStream("/html/header.html");
		IOUtils.copy(headerStream, outputStreamHTMLFile);
		
		// Generate a simple breadcrumb
		generateBreadCrumb(outputStreamHTMLFile, file);
		
		// Generate some info about the file
		generateInfoBox(outputStreamHTMLFile, file);
		
		// Generate the actual code table
		generateCode(outputStreamHTMLFile, file, sourceFileReader);
		
	
		InputStream footerStream = getClass().getResourceAsStream("/html/footer.html");
		IOUtils.copy(footerStream, outputStreamHTMLFile);
		
		outputStreamHTMLFile.close();
		footerStream.close();
		headerStream.close();
		sourceFileReader.close();
	}
	
	/**
	 * Generate the actual code table
	 * @param outputStreamHTMLFile
	 * @param file
	 * @param sourceFileReader
	 * @throws IOException 
	 */
	private void generateCode(PrintStream outputStreamHTMLFile, OperiasFile file, BufferedReader sourceFileReader) throws IOException {

		if (file.getChanges().size() == 0) {
			System.out.println("EMPTY FILE!");
			return;
		}
		
	    JavaToHtml jth = new JavaToHtml();
	    
		int changeIndex = 0;
		OperiasChange currentChange = file.getChanges().get(changeIndex);
		
		int originalLineNumber = 1;
		int revisedLineNumber = 1;
		String line;
		
		outputStreamHTMLFile.println("<div id='mainContent'><div id='tableContent'><table class='code'>");
		
		// Read all the lines from the source file
		while ((line = sourceFileReader.readLine()) != null) {

			// Check if we have a change on this number
			if (currentChange.getOriginalLineNumber() == originalLineNumber && currentChange.getRevisedLineNumber() == revisedLineNumber){
				// Change!
			
				if (currentChange instanceof CoverageIncreaseChange) {
					outputStreamHTMLFile.println("<tr>");
					outputStreamHTMLFile.println(" 	<td class='coverageIncrease'>" + originalLineNumber + "</td>");
					outputStreamHTMLFile.println(" 	<td class='coverageIncrease'>" + revisedLineNumber + "</td>");
					outputStreamHTMLFile.println(" 	<td class='coverageIncrease'><pre>" + jth.process(line) + "</pre></td>");
					outputStreamHTMLFile.println("</tr>");					
				} else if (currentChange instanceof CoverageDecreaseChange) {
					outputStreamHTMLFile.println("<tr>");
					outputStreamHTMLFile.println(" 	<td class='coverageDecrease'>" + originalLineNumber + "</td>");
					outputStreamHTMLFile.println(" 	<td class='coverageDecrease'>" + revisedLineNumber + "</td>");
					outputStreamHTMLFile.println(" 	<td class='coverageDecrease'><pre>" + jth.process(line) + "</pre></td>");
					outputStreamHTMLFile.println("</tr>");	
				} 
				
				if (currentChange instanceof DeleteSourceChange || currentChange instanceof ChangeSourceChange) {				
					// Print rest of the lines
					for(int i = 0; i < currentChange.getOriginalCoverage().size(); i++) {
						String coverageClass = "wasNotCovered";
						if (currentChange.getOriginalCoverage().get(i) == null) {
							coverageClass = "deletedRow";
						} else if (currentChange.getOriginalCoverage().get(i)) {
							coverageClass = "wasCovered";
						}
						
						outputStreamHTMLFile.println("<tr>");
						outputStreamHTMLFile.println(" 	<td class='"+coverageClass+"'>" + (originalLineNumber + i) + "</td>");
						outputStreamHTMLFile.println(" 	<td class='"+coverageClass+"'></td>");
						outputStreamHTMLFile.println(" 	<td class='"+coverageClass+"'><pre>" + currentChange.getSourceDiffDelta().getOriginal().getLines().get(i) + "</pre></td>");
						outputStreamHTMLFile.println("</tr>");
					}
					
					if (currentChange instanceof DeleteSourceChange) {
						outputStreamHTMLFile.println("<tr>");
						outputStreamHTMLFile.println(" 	<td>" + originalLineNumber + "</td>");
						outputStreamHTMLFile.println(" 	<td>" + revisedLineNumber + "</td>");
						outputStreamHTMLFile.println(" 	<td><pre>" + jth.process(line) + "</pre></td>");
						outputStreamHTMLFile.println("</tr>");
						originalLineNumber++;
						revisedLineNumber++;
					}
					
				} 
				if (currentChange instanceof InsertSourceChange || currentChange instanceof ChangeSourceChange) {
					int insertSize = currentChange.getRevisedCoverage().size();
					String coverageClass = "notCovered";
					if (currentChange.getRevisedCoverage().get(0) == null) {
						coverageClass = "insertedRow";
					} else if (currentChange.getRevisedCoverage().get(0)) {
						coverageClass = "isCovered";
					}
					
					// Print first line
					outputStreamHTMLFile.println("<tr>");
					outputStreamHTMLFile.println(" 	<td class='"+coverageClass+" left top "+(1 == insertSize ? "bottom" : "")+"'></td>");
					outputStreamHTMLFile.println(" 	<td class='"+coverageClass+" top "+(1 == insertSize ? "bottom" : "")+"'>" + revisedLineNumber + "</td>");
					outputStreamHTMLFile.println(" 	<td class='"+coverageClass+" right top "+(1 == insertSize ? "bottom" : "")+"'><pre>"  + jth.process(line) + "</pre></td>");
					outputStreamHTMLFile.println("</tr>");
					// Print rest of the lines
					for(int i = 1; i < insertSize; i++) {
						coverageClass = "notCovered";
						if (currentChange.getRevisedCoverage().get(i) == null) {
							coverageClass = "insertedRow";
						} else if (currentChange.getRevisedCoverage().get(i)) {
							coverageClass = "isCovered";
						}
						line = sourceFileReader.readLine();
						outputStreamHTMLFile.println("<tr>");
						outputStreamHTMLFile.println(" 	<td class='"+coverageClass+" left "+(i == (insertSize - 1) ? "bottom" : "")+"'></td>");
						outputStreamHTMLFile.println(" 	<td class='"+coverageClass+" "+(i == (insertSize - 1) ? "bottom" : "")+"'>" + (revisedLineNumber + i) + "</td>");
						outputStreamHTMLFile.println(" 	<td class='"+coverageClass+" right "+(i == (insertSize - 1) ? "bottom" : "")+"'><pre>" + jth.process(line) + "</pre></td>");
						outputStreamHTMLFile.println("</tr>");
					}
					
				} 
				
				// Set the new line numbers
				originalLineNumber += currentChange.getOriginalCoverage().size();
				revisedLineNumber += currentChange.getRevisedCoverage().size();
				
				// Get the a new change if possible
				changeIndex++;
				if (file.getChanges().size() > changeIndex) {
					currentChange = file.getChanges().get(changeIndex);
				}
			} else {
				// No change on this line, just show the line!
				
				outputStreamHTMLFile.println("<tr>");
				outputStreamHTMLFile.println(" 	<td>" + originalLineNumber + "</td>");
				outputStreamHTMLFile.println(" 	<td>" + revisedLineNumber + "</td>");
				outputStreamHTMLFile.println(" 	<td><pre>" + jth.process(line) + "</pre></td>");
				outputStreamHTMLFile.println("</tr>");
				originalLineNumber++;
				revisedLineNumber++;
			}
	    }
		
		outputStreamHTMLFile.println("</table></div></div>");
		// TODO, change for any chances after we are done, there can be inserts afterwards
	}
	
	/**
	 * Generate a breadcrumb
	 * @param outputStreamHTMLFile
	 * @param file
	 */
	private void generateBreadCrumb(PrintStream outputStreamHTMLFile, OperiasFile file) {
		outputStreamHTMLFile.println("<div id='breadcrumb'>");
		outputStreamHTMLFile.print("<h2><a href='index.html'>overview</a> / ");
		String[] packagesAndClasses = file.getClassName().split("\\.");
		
		String completePackageName = "";
		
		// All package links
		for(int i = 0; i < packagesAndClasses.length - 1; i++) {
			completePackageName += "." + packagesAndClasses[i];
			if (packageExists(completePackageName)) {
				outputStreamHTMLFile.print("<a href='package"+completePackageName+".html'>"+packagesAndClasses[i]+"</a> / ");
			} else {
				outputStreamHTMLFile.print(""+packagesAndClasses[i]+" / ");
			}
		}
		outputStreamHTMLFile.print(packagesAndClasses[packagesAndClasses.length - 1]);
		outputStreamHTMLFile.println("</h2>");
		outputStreamHTMLFile.println("</div>");
	}
	
	/**
	 * Generates the info box
	 * @param outputStreamHTMLFile
	 * @param file
	 */
	private void generateInfoBox(PrintStream outputStreamHTMLFile, OperiasFile file) {
		
		if (file.getOriginalClass() == null) {
			outputStreamHTMLFile.println("<div id='informationContent'>");
			outputStreamHTMLFile.println("  <strong>File</strong> "+file.getRevisedClass().getFileName()+" <br />");
			
			outputStreamHTMLFile.println("  <strong>Line coverage</strong> is "+
				Math.round(file.getRevisedClass().getLineRate() * 100)+"% <br />");
			
			outputStreamHTMLFile.println("  <strong>Branch coverage</strong> is "+
					Math.round(file.getRevisedClass().getBranchRate() * 100)+"% <br />");
			outputStreamHTMLFile.println("</div>");
			
		} else if (file.getRevisedClass() == null) {
			outputStreamHTMLFile.println("<div id='informationContent'>");
			outputStreamHTMLFile.println("  <strong>File</strong> "+file.getOriginalClass().getFileName()+" <br />");
			
			outputStreamHTMLFile.println("  <strong>Line coverage</strong> was "+
				Math.round(file.getOriginalClass().getLineRate() * 100)+"% <br />");
			
			outputStreamHTMLFile.println("  <strong>Branch coverage</strong> was "+
					Math.round(file.getOriginalClass().getBranchRate() * 100)+"% <br />");
			outputStreamHTMLFile.println("</div>");
			
		} else {
			outputStreamHTMLFile.println("<div id='informationContent'>");
			outputStreamHTMLFile.println("  <strong>File</strong> "+file.getRevisedClass().getFileName()+" <br />");
			
			if (file.getOriginalClass().getLineRate() > file.getRevisedClass().getLineRate()) {
				outputStreamHTMLFile.println("  <strong>Line coverage</strong> decreased from "+
						Math.round(file.getOriginalClass().getLineRate() * 100)+"% to "+
						Math.round(file.getRevisedClass().getLineRate() * 100)+"% <br />");
			} else if (file.getOriginalClass().getLineRate() < file.getRevisedClass().getLineRate()) {
				outputStreamHTMLFile.println("  <strong>Line coverage</strong> increased from "+
						Math.round(file.getOriginalClass().getLineRate() * 100)+"% to "+
						Math.round(file.getRevisedClass().getLineRate() * 100)+"% <br />");
			} else {
				outputStreamHTMLFile.println("  <strong>Line coverage</strong> stayed the same at "+
						Math.round(file.getOriginalClass().getLineRate() * 100)+"%<br />");
			}
			
			if (file.getOriginalClass().getBranchRate() > file.getRevisedClass().getBranchRate()) {
				outputStreamHTMLFile.println("  <strong>Branch coverage</strong> decreased from "+
						Math.round(file.getOriginalClass().getBranchRate() * 100)+"% to "+
						Math.round(file.getRevisedClass().getBranchRate() * 100)+"% <br />");
			} else if (file.getOriginalClass().getBranchRate() < file.getRevisedClass().getBranchRate()) {
				outputStreamHTMLFile.println("  <strong>Branch coverage</strong> increased from "+
						Math.round(file.getOriginalClass().getBranchRate() * 100)+"% to "+
						Math.round(file.getRevisedClass().getBranchRate() * 100)+"% <br />");
			} else {
				outputStreamHTMLFile.println("  <strong>Branch coverage</strong> stayed the same at "+
						Math.round(file.getOriginalClass().getBranchRate() * 100)+"%<br />");
			}
			outputStreamHTMLFile.println("<br/>");

			outputStreamHTMLFile.print("<a href='javascript:void(0);' id='showAllChanges'>Show all changes</a>&nbsp;&nbsp;|&nbsp;&nbsp;");
			outputStreamHTMLFile.print("<a href='javascript:void(0);' id='showOriginal'>Show only the original file</a>&nbsp;&nbsp;|&nbsp;&nbsp;");
			outputStreamHTMLFile.print("<a href='javascript:void(0);' id='showNew'>Show only the revised file</a>");
			
			outputStreamHTMLFile.println("</div>");
		}
	}

	/**
	 * Checks whether there is a file in the given package
	 * @param packageName
	 * @return
	 */
	private boolean packageExists(String packageName) {
		for(OperiasFile oFile : changedFiles){
			if (oFile.getPackageName().equals(packageName)) {
				return true;
			}
		}
		return false;
	}
}
