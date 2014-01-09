package operias.html;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

import org.apache.commons.io.IOUtils;

import difflib.ChangeDelta;
import difflib.DeleteDelta;
import difflib.Delta;
import difflib.InsertDelta;
import operias.cobertura.CoberturaClass;
import operias.cobertura.CoberturaLine;
import operias.diff.DiffFile;
import operias.diff.SourceDiffState;
import operias.report.OperiasFile;
import operias.report.change.ChangeSourceChange;
import operias.report.change.CoverageDecreaseChange;
import operias.report.change.CoverageIncreaseChange;
import operias.report.change.DeleteSourceChange;
import operias.report.change.InsertSourceChange;
import operias.report.change.OperiasChange;

public class HTMLClassView extends HTMLCodeView {
	
	
	/**
	 * Create a new hTML file view page
	 * @param file
	 * @throws IOException
	 */
	public HTMLClassView(OperiasFile file, List<OperiasFile> changedFiles) throws IOException {
		
		File classHTMLFile = new File("site/" + file.getClassName() + ".html");
		classHTMLFile.createNewFile();
		
		PrintStream outputStreamHTMLFile = new PrintStream(classHTMLFile);
		InputStream headerStream = getClass().getResourceAsStream("/html/header.html");
		IOUtils.copy(headerStream, outputStreamHTMLFile);
		
		// Generate a simple breadcrumb
		generateBreadCrumb(outputStreamHTMLFile, file);
		
		// Generate some info about the file
		generateInfoBox(outputStreamHTMLFile, file);
		
		outputStreamHTMLFile.println("<div id='mainContent'><div id='tableContent'>");

		// Generate the original and revised views
		if (file.getSourceDiff().getSourceState() != SourceDiffState.NEW){
			generateSimpleCoverageView(outputStreamHTMLFile, file.getOriginalClass(), file.getSourceDiff().getOriginalFileName(), "originalCoverageTable");
		}
		
		if (file.getSourceDiff().getSourceState() != SourceDiffState.DELETED){
			generateSimpleCoverageView(outputStreamHTMLFile, file.getRevisedClass(), file.getSourceDiff().getRevisedFileName(), "revisedCoverageTable");
		}

		// Generate source diff view
		generateSourceDiffView(outputStreamHTMLFile, file.getSourceDiff(), false);
		
		// Generate the combined code table
		generateCombinedCodeView(outputStreamHTMLFile, file);
		
		outputStreamHTMLFile.println("</div></div>");
	
		InputStream footerStream = getClass().getResourceAsStream("/html/footer.html");
		IOUtils.copy(footerStream, outputStreamHTMLFile);
		
		outputStreamHTMLFile.close();
		footerStream.close();
		headerStream.close();
	}
	
	

	/**
	 * Generate and print a table containing the basic coverage information for a file
	 * @param outputStreamHTMLFile
	 * @param coverageInformation
	 * @param sourceFileName
	 * @throws IOException 
	 */
	private void generateSimpleCoverageView(PrintStream outputStreamHTMLFile, CoberturaClass coverageInformation, String sourceFileName, String tableName) throws IOException {
		JavaToHtml jth = new JavaToHtml();
		
		BufferedReader sourceFileReader = new BufferedReader(new FileReader(sourceFileName));
		
		String line;
		
		int lineNumber = 1;
		outputStreamHTMLFile.println("<table id='"+tableName+"' class='code'>");
		while ((line = sourceFileReader.readLine()) != null) {
			String tdClass = "";
			CoberturaLine coverageLine = coverageInformation.tryGetLine(lineNumber);
			if (coverageLine != null && coverageLine.isCovered()) {
				tdClass = "coveredLight";
			} else if (coverageLine != null && !coverageLine.isCovered()) {
				tdClass = "notCoveredLight";
			}
			
			outputStreamHTMLFile.println("<tr>");
			outputStreamHTMLFile.println(" 	<td class='"+tdClass+"'>" + lineNumber + "</td>");
			outputStreamHTMLFile.println(" 	<td class='"+tdClass+"'><pre>" + jth.process(line) + "</pre></td>");
			outputStreamHTMLFile.println("</tr>");					

			lineNumber++;
		}
		
		sourceFileReader.close();
	}
	
	/**
	 * Generate the actual code table
	 * @param outputStreamHTMLFile
	 * @param file
	 * @param sourceFileReader
	 * @throws IOException 
	 */
	private void generateCombinedCodeView(PrintStream outputStreamHTMLFile, OperiasFile file) throws IOException {
		// Generate the source file reader for the combined view and source diff view
		BufferedReader sourceFileReader = null;
		CoberturaClass coverageInformation = null;
		if (file.getSourceDiff().getSourceState() == SourceDiffState.DELETED){
			sourceFileReader = new BufferedReader(new FileReader(file.getSourceDiff().getOriginalFileName()));
			coverageInformation = file.getOriginalClass();
		} else {
			sourceFileReader = new BufferedReader(new FileReader(file.getSourceDiff().getRevisedFileName()));
			coverageInformation = file.getRevisedClass();
		}
		
	    JavaToHtml jth = new JavaToHtml();
	    
		int changeIndex = 0;
		OperiasChange currentChange = file.getChanges().get(changeIndex);
		
		int originalLineNumber = 1;
		int revisedLineNumber = 1;
		String line;
		
		outputStreamHTMLFile.println("<table id='combinedTable' class='code'>");
		
		// Read all the lines from the source file
		while ((line = sourceFileReader.readLine()) != null) {

			// Check if we have a change on this number
			if (currentChange.getOriginalLineNumber() == originalLineNumber && currentChange.getRevisedLineNumber() == revisedLineNumber){
				// Change!
			
				if (currentChange instanceof CoverageIncreaseChange) {
					outputStreamHTMLFile.println("<tr>");
					outputStreamHTMLFile.println(" 	<td class='coveredDark'>" + originalLineNumber + "</td>");
					outputStreamHTMLFile.println(" 	<td class='coveredDark'>" + revisedLineNumber + "</td>");
					outputStreamHTMLFile.println(" 	<td class='coveredDark'><pre>" + jth.process(line) + "</pre></td>");
					outputStreamHTMLFile.println("</tr>");					
				} else if (currentChange instanceof CoverageDecreaseChange) {
					outputStreamHTMLFile.println("<tr>");
					outputStreamHTMLFile.println(" 	<td class='notCoveredDark'>" + originalLineNumber + "</td>");
					outputStreamHTMLFile.println(" 	<td class='notCoveredDark'>" + revisedLineNumber + "</td>");
					outputStreamHTMLFile.println(" 	<td class='notCoveredDark'><pre>" + jth.process(line) + "</pre></td>");
					outputStreamHTMLFile.println("</tr>");	
				} 
				
				if (currentChange instanceof DeleteSourceChange || currentChange instanceof ChangeSourceChange) {				
					// Print rest of the lines
					for(int i = 0; i < currentChange.getOriginalCoverage().size(); i++) {
						String coverageClass = "deletedRow notCoveredLight";
						if (currentChange.getOriginalCoverage().get(i) == null) {
							coverageClass = "deletedRow";
						} else if (currentChange.getOriginalCoverage().get(i)) {
							coverageClass = "deletedRow coveredLight";
						}
						
						outputStreamHTMLFile.println("<tr>");
						outputStreamHTMLFile.println(" 	<td class='"+coverageClass+"'>" + (originalLineNumber + i) + "</td>");
						outputStreamHTMLFile.println(" 	<td class='"+coverageClass+"'></td>");
						outputStreamHTMLFile.println(" 	<td class='"+coverageClass+"'><pre>" + currentChange.getSourceDiffDelta().getOriginal().getLines().get(i) + "</pre></td>");
						outputStreamHTMLFile.println("</tr>");
					}
					
					if (currentChange instanceof DeleteSourceChange) {
						outputStreamHTMLFile.println("<tr>");
						outputStreamHTMLFile.println(" 	<td>" + (originalLineNumber + currentChange.getOriginalCoverage().size()) + "</td>");
						outputStreamHTMLFile.println(" 	<td>" + revisedLineNumber + "</td>");
						outputStreamHTMLFile.println(" 	<td><pre>" + jth.process(line) + "</pre></td>");
						outputStreamHTMLFile.println("</tr>");
						originalLineNumber++;
						revisedLineNumber++;
					}
					
				} 
				if (currentChange instanceof InsertSourceChange || currentChange instanceof ChangeSourceChange) {
					int insertSize = currentChange.getRevisedCoverage().size();
		
					// Print rest of the lines
					for(int i = 0; i < insertSize; i++) {
						String coverageClass = "notCoveredDark";
						if (currentChange.getRevisedCoverage().get(i) == null) {
							coverageClass = "insertedRow";
						} else if (currentChange.getRevisedCoverage().get(i)) {
							coverageClass = "coveredDark";
						}
						outputStreamHTMLFile.println("<tr>");
						outputStreamHTMLFile.println(" 	<td class='"+coverageClass+" left "+(i == (insertSize - 1) ? "bottom" : "")+" "+(i == 0 ? "top" : "")+"'></td>");
						outputStreamHTMLFile.println(" 	<td class='"+coverageClass+" "+(i == (insertSize - 1) ? "bottom" : "")+" "+(i == 0 ? "top" : "")+"'>" + (revisedLineNumber + i) + "</td>");
						outputStreamHTMLFile.println(" 	<td class='"+coverageClass+" right "+(i == (insertSize - 1) ? "bottom" : "")+" "+(i == 0 ? "top" : "")+"'><pre>" + jth.process(line) + "</pre></td>");
						outputStreamHTMLFile.println("</tr>");
						
						
						if (i < (insertSize - 1)) {
							line = sourceFileReader.readLine();	
						}
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
				// No change on this line, show the line with coverage information if possible
				
				String tdClass = "";
				CoberturaLine coverageLine = null;
				if (file.getSourceDiff().getSourceState() == SourceDiffState.DELETED){
					coverageLine = coverageInformation.tryGetLine(originalLineNumber);
				} else {
					coverageLine = coverageInformation.tryGetLine(revisedLineNumber);
				}
				
				if (coverageLine != null && coverageLine.isCovered()) {
					tdClass = "coveredLight";
				} else if (coverageLine != null && !coverageLine.isCovered()) {
					tdClass = "notCoveredLight";
				}
				
				
				outputStreamHTMLFile.println("<tr>");
				outputStreamHTMLFile.println(" 	<td class='"+tdClass+"'>" + originalLineNumber + "</td>");
				outputStreamHTMLFile.println(" 	<td class='"+tdClass+"'>" + revisedLineNumber + "</td>");
				outputStreamHTMLFile.println(" 	<td class='"+tdClass+"'><pre>" + jth.process(line) + "</pre></td>");
				outputStreamHTMLFile.println("</tr>");
				originalLineNumber++;
				revisedLineNumber++;
			}
	    }
		
		outputStreamHTMLFile.println("</table>");
		// TODO, check for any chances after we are done, there can be inserts afterwards
		
		sourceFileReader.close();
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
		
		// All package links
		for(int i = 0; i < packagesAndClasses.length - 1; i++) {
			outputStreamHTMLFile.print(""+packagesAndClasses[i]+" / ");
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
			

			outputStreamHTMLFile.print("<a href='javascript:void(0);' id='showOriginal'>Show the original coverage</a>&nbsp;&nbsp;|&nbsp;&nbsp;");
			outputStreamHTMLFile.print("<a href='javascript:void(0);' id='showNew'>Show the revised coverage</a>&nbsp;&nbsp;|&nbsp;&nbsp;");
			outputStreamHTMLFile.print("<a href='javascript:void(0);' id='showSourceDiff'>Show source differences</a>&nbsp;&nbsp;|&nbsp;&nbsp;");
			outputStreamHTMLFile.print("<a href='javascript:void(0);' id='showAllChanges'>Show the combined changes</a>");
			
			outputStreamHTMLFile.println("</div>");
		}
	}


}
