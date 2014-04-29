package operias.output.html;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;


import operias.diff.DiffFile;
import operias.diff.SourceDiffState;
import difflib.ChangeDelta;
import difflib.DeleteDelta;
import difflib.Delta;
import difflib.InsertDelta;

public abstract class HTMLCodeView {

	/**
	 * Generate suorce diff view for deleted files
	 * @param outputStreamHTMLFile
	 * @param sourceDiff
	 * @param showOnLoad
	 * @throws IOException
	 */
	private void generateSourceDiffViewDeletedFile(PrintStream outputStreamHTMLFile, DiffFile sourceDiff, boolean showOnLoad) throws IOException {
	
		BufferedReader sourceFileReader = new BufferedReader(new FileReader(sourceDiff.getOriginalFileName()));;
		
		int originalLineNumber = 1;
		int revisedLineNumber = 1;
	    JavaToHtml jth = new JavaToHtml();
		
		String line;
		
		outputStreamHTMLFile.println("<table id='sourceDiffTable' " + (showOnLoad ? "style='display:block;'" : "") + " class='code'>");
		
		// Read all the lines from the source file
		while ((line = sourceFileReader.readLine()) != null) {
			
				// Just show the line, nothing special here
				outputStreamHTMLFile.println("<tr>");
				outputStreamHTMLFile.println(" 	<td class='deletedRow'>" + (originalLineNumber) + "</td>");
				outputStreamHTMLFile.println(" 	<td class='deletedRow'>" + (revisedLineNumber) + "</td>");
				outputStreamHTMLFile.println(" 	<td class='deletedRow'><pre>" + jth.process(line) + "</pre></td>");
				outputStreamHTMLFile.println("</tr>");
				originalLineNumber++;
				revisedLineNumber++;
			
		}

		outputStreamHTMLFile.println("</table>");
		sourceFileReader.close();
	}
	
	/**
	 * Generate the source diff view
	 * @param outputStreamHTMLFile
	 * @param sourceDiff
	 * @throws IOException 
	 */
	protected void generateSourceDiffView(PrintStream outputStreamHTMLFile, DiffFile sourceDiff, boolean showOnLoad) throws IOException {
		// Generate the source file reader for the combined view and source diff view
		if (sourceDiff.getSourceState() == SourceDiffState.DELETED){
			// Special case,
			generateSourceDiffViewDeletedFile(outputStreamHTMLFile, sourceDiff, showOnLoad);
			
			return;
		} 

		BufferedReader sourceFileReader = new BufferedReader(new FileReader(sourceDiff.getRevisedFileName()));
		
		int originalLineNumber = 0;
		int revisedLineNumber = 0;
		int changeIndex = 0;
		
	    JavaToHtml jth = new JavaToHtml();
		
		List<Delta> sourceChanges = sourceDiff.getChanges();
		Delta currentChange = null;
		if (sourceChanges.size() > 0) {
			currentChange = sourceChanges.get(changeIndex);
		}
		
		String line;
		
		outputStreamHTMLFile.println("<table id='sourceDiffTable' " + (showOnLoad ? "style='display:block;'" : "") + " class='code'>");
		
		// Read all the lines from the source file
		while ((line = sourceFileReader.readLine()) != null) {
			if (currentChange != null && currentChange.getOriginal().getPosition() == originalLineNumber && 
					currentChange.getRevised().getPosition() == revisedLineNumber) {
				
				if (currentChange instanceof DeleteDelta || currentChange instanceof ChangeDelta) {
					for(int i =0; i < currentChange.getOriginal().getLines().size(); i++) {
						outputStreamHTMLFile.println("<tr>");
						outputStreamHTMLFile.println(" 	<td class='deletedRow'>" + (originalLineNumber + i + 1) + "</td>");
						outputStreamHTMLFile.println(" 	<td class='deletedRow'></td>");
						outputStreamHTMLFile.println(" 	<td class='deletedRow'><pre>" + currentChange.getOriginal().getLines().get(i) + "</pre></td>");
						outputStreamHTMLFile.println("</tr>");
					}	
					
					originalLineNumber += currentChange.getOriginal().getLines().size();
					
					// Show the last received line again
					if (currentChange instanceof DeleteDelta) {
						outputStreamHTMLFile.println("<tr>");
						outputStreamHTMLFile.println(" 	<td>" + (originalLineNumber + 1) + "</td>");
						outputStreamHTMLFile.println(" 	<td>" + (revisedLineNumber + 1) + "</td>");
						outputStreamHTMLFile.println(" 	<td><pre>" + jth.process(line) + "</pre></td>");
						outputStreamHTMLFile.println("</tr>");
						originalLineNumber++;
						revisedLineNumber++;
					}
				}
				
				if (currentChange instanceof InsertDelta || currentChange instanceof ChangeDelta) {
					int insertSize = currentChange.getRevised().getLines().size();
					for(int i = 0; i < insertSize; i++) {
						outputStreamHTMLFile.println("<tr>");
						outputStreamHTMLFile.println(" 	<td class='left "+(i == (insertSize - 1) ? "bottom" : "")+" "+(i == 0 ? "top" : "")+"'></td>");
						outputStreamHTMLFile.println(" 	<td class=' "+(i == (insertSize - 1) ? "bottom" : "")+" "+(i == 0 ? "top" : "")+"'>" + (revisedLineNumber + i + 1) + "</td>");
						outputStreamHTMLFile.println(" 	<td class='right "+(i == (insertSize - 1) ? "bottom" : "")+" "+(i == 0 ? "top" : "")+"'><pre>" + jth.process(line) + "</pre></td>");
						outputStreamHTMLFile.println("</tr>");
						
						// Prevent reading the line AFTER the changed
						if (i < (insertSize - 1)) {
							line = sourceFileReader.readLine();
						}
					}	
					
					revisedLineNumber += currentChange.getRevised().getLines().size();
				}
				

				// Get the a new change if possible
				changeIndex++;
				if (sourceChanges.size() > changeIndex) {
					currentChange = sourceChanges.get(changeIndex);
				}
				
			} else {
				// Just show the line, nothing special here
				outputStreamHTMLFile.println("<tr>");
				outputStreamHTMLFile.println(" 	<td>" + (originalLineNumber + 1) + "</td>");
				outputStreamHTMLFile.println(" 	<td>" + (revisedLineNumber + 1) + "</td>");
				outputStreamHTMLFile.println(" 	<td><pre>" + jth.process(line) + "</pre></td>");
				outputStreamHTMLFile.println("</tr>");
				originalLineNumber++;
				revisedLineNumber++;
			}
		}

		outputStreamHTMLFile.println("</table>");
		sourceFileReader.close();
	}
}
