package operias.output.html;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import operias.Configuration;
import operias.cobertura.CoberturaClass;
import operias.cobertura.CoberturaPackage;
import operias.diff.DiffFile;
import operias.diff.SourceDiffState;
import operias.report.OperiasFile;
import operias.report.OperiasReport;

public class HTMLOverview {

	/**
	 * List containing the changed files
	 */
	private OperiasReport report;
	
	/**
	 * List containing all the package names
	 */
	private List<String> packageNames;
	
	/**
	 * List of already displayed packages
	 */
	private List<Integer> displayedPackages;
	
	 static class PackageComparator implements Comparator<String>
	 {
	     public int compare(String p1, String p2)
	     {
	    	 int lengthP1 = p1.length() - p1.replace(".", "").length();
	    	 int lengthP2 = p2.length() - p2.replace(".", "").length();
	    	 
	    	 return lengthP1 - lengthP2;
	     }
	 }
	 
	/**
	 * 
	 * @param report
	 * @param packageNames
	 */
	public HTMLOverview(OperiasReport report, List<String> packageNames) {
		this.report = report;
		this.packageNames = packageNames;
	}
	
	/**
	 * Generate HTM:
	 * @throws IOException
	 */
	public void generateHTML() throws IOException {

		Collections.sort(this.packageNames, new PackageComparator());
		
		this.displayedPackages = new LinkedList<Integer>();
		
		Collections.sort(this.packageNames);
		
		File indexHTMLFile = new File(Configuration.getDestinationDirectory() + "/index.html");
		indexHTMLFile.createNewFile();
		
		PrintStream outputStreamHTMLFile = new PrintStream(indexHTMLFile);
		InputStream headerStream = getClass().getResourceAsStream("/html/header.html");
		IOUtils.copy(headerStream, outputStreamHTMLFile);
		
		InputStream legendStream = getClass().getResourceAsStream("/html/overviewlegend.html");
		IOUtils.copy(legendStream, outputStreamHTMLFile);

		outputStreamHTMLFile.println("<div id='mainContent'>");
		
		// ARROW DOWN : &#8595;
		// ARROW UP : &#8593;
		outputStreamHTMLFile.println("<h2>Packages</h2><table class='classOverview'>");
		outputStreamHTMLFile.println("<thead><tr><th>Name</th><th style='width:230px'>Line coverage</th><th style='width:182px;'># Relevant lines</th><th style='width:230px'>Condition coverage</th><th># Conditions</th><tr></thead><tbody>");

		generatePackageOverviewHTML(0, report.getChangedClasses(),outputStreamHTMLFile);
		

		outputStreamHTMLFile.println("</tbody></table>");
		
		
		// Show list of changed test classes
		if (report.getChangedTests().size() > 0) {
			outputStreamHTMLFile.println("<h2>Test Classes</h2><table class='classOverview'>");
			outputStreamHTMLFile.println("<thead><tr><th>Name</th><th>Amount of lines changed</th><tr></thead><tbody>");
	
			for(DiffFile changedTest : report.getChangedTests()) {
				String fileName = changedTest.getFileName(report);
				
				outputStreamHTMLFile.println("<tr >");
				outputStreamHTMLFile.println("<td><a href='"+fileName.replace('/', '.')+".html'>"+fileName+"</a></td>");
				if (changedTest.getSourceState() == SourceDiffState.NEW) {
					outputStreamHTMLFile.println("<td>+"+changedTest.getRevisedLineCount()+" (100%)</td>");
				} else if (changedTest.getSourceState() == SourceDiffState.DELETED) {
					outputStreamHTMLFile.println("<td>-"+changedTest.getOriginalLineCount()+" (100%)</td>");
				} else {
					int changedLineCount = changedTest.getRevisedLineCount() - changedTest.getOriginalLineCount();
					double changedLineCountPercentage = Math.round((double)changedLineCount / (double) changedTest.getOriginalLineCount() * (double)10000) / (double)100;
					outputStreamHTMLFile.println("<td>"+(changedLineCount > 0 ? "+" : "") +changedLineCount+"("+changedLineCountPercentage+"%)</td>");
				}
				outputStreamHTMLFile.println("</tr >");
				
				new HTMLTestView(fileName.replace('/', '.').replaceFirst(".", ""), changedTest);
				
			}
			
			outputStreamHTMLFile.println("</tbody></table>");
		}
		
		outputStreamHTMLFile.println("</div>");
		
		InputStream footerStream = getClass().getResourceAsStream("/html/footer.html");
		IOUtils.copy(footerStream, outputStreamHTMLFile);
		
		outputStreamHTMLFile.close();
		footerStream.close();
		headerStream.close();
	}

	/**
	 * Display all the packages and its inner classes
	 * @param packageID
	 * @param changedClasses
	 * @param outputStreamHTMLFile
	 */
	private void generatePackageOverviewHTML(int packageID, List<OperiasFile> changedClasses, PrintStream outputStreamHTMLFile) {

		if (packageID >= packageNames.size()) {
			// DONE
			return;
		}
		
		if (displayedPackages.indexOf(packageID) >= 0) {
			// Package already shown somehwere as subpackage, so skip
			generatePackageOverviewHTML(packageID + 1, changedClasses, outputStreamHTMLFile);
			return;
		}
		
		// Generate the HTML for this pacakge
		generateHTML(packageID, changedClasses, outputStreamHTMLFile, 0);
		
		
		// Show next package
		generatePackageOverviewHTML(packageID + 1, changedClasses, outputStreamHTMLFile);

	}
	
	/**
	 * Generate HTML for a specific package
	 * @param packageID
	 * @param changedClasses
	 * @param outputStreamHTMLFile
	 * @param packageLevel The Level of the package, 0 if its a top level package
	 */
	private void generateHTML(int packageID, List<OperiasFile> changedClasses, PrintStream outputStreamHTMLFile, int packageLevel) {
		
		String thisPackageName = this.packageNames.get(packageID);
		
		CoberturaPackage originalPackage = report.getOriginalCoverageReport().getPackage(thisPackageName);
		CoberturaPackage revisedPackage = report.getRevisedCoverageReport().getPackage(thisPackageName);
		
				
		SourceDiffState packageState = SourceDiffState.CHANGED;
		if (revisedPackage == null) {
			packageState = SourceDiffState.DELETED;
		} else if (originalPackage == null) {
			packageState = SourceDiffState.NEW;
		}
		
		
		outputStreamHTMLFile.println("<tr class='packageRow level"+packageLevel+" "+(packageState == SourceDiffState.DELETED ? "deletedOverviewRow" : "")+"' id='Package"+packageID+"'>");
		outputStreamHTMLFile.println("<td>"+thisPackageName+"</td>");
		

		switch (packageState) {
			case DELETED:
				outputStreamHTMLFile.println(generateCoverageBarsHTML(originalPackage.getLineRate(), 0.0, packageState));
				outputStreamHTMLFile.println("<td>"+ (int)originalPackage.getLineCount()+" (Deleted)</td>");
				outputStreamHTMLFile.println(generateCoverageBarsHTML(originalPackage.getConditionRate(), 0.0, packageState));
				outputStreamHTMLFile.println("<td>" + originalPackage.getConditionCount()+" (Deleted)</td>");
				break;
			case NEW:
				outputStreamHTMLFile.println(generateCoverageBarsHTML(0.0, revisedPackage.getLineRate(), packageState));
				outputStreamHTMLFile.println("<td>" + (int)revisedPackage.getLineCount()+" (New)</td>");
				outputStreamHTMLFile.println(generateCoverageBarsHTML(0.0, revisedPackage.getConditionRate(), packageState));
				outputStreamHTMLFile.println("<td>" + revisedPackage.getConditionCount()+" (New)</td>");
				break;
			default:
				outputStreamHTMLFile.println(generateCoverageBarsHTML(originalPackage.getLineRate(), revisedPackage.getLineRate(), packageState));
				double packageRelevantLinesSizeChange = revisedPackage.getLineCount() - originalPackage.getLineCount();
				double packageRelevantLinesSizeChangePercentage = Math.round((double)packageRelevantLinesSizeChange / (double)originalPackage.getLineCount() * (double)10000) / (double)100;	

				outputStreamHTMLFile.println("<td>"+(packageRelevantLinesSizeChange > 0 ? "+" : "") + (int)packageRelevantLinesSizeChange+" ("+packageRelevantLinesSizeChangePercentage+"%)</td>");
				outputStreamHTMLFile.println(generateCoverageBarsHTML(originalPackage.getConditionRate(), revisedPackage.getConditionRate(), packageState));
				

				double packageConditionSizeChange = revisedPackage.getConditionCount() - originalPackage.getConditionCount();
				double packageConditionChangePercentage;
				if (originalPackage.getConditionCount() == 0 && packageConditionSizeChange != 0) {
					packageConditionChangePercentage = 100;
				} else if (revisedPackage.getConditionCount() == 0 && packageConditionSizeChange != 0) {
					packageConditionChangePercentage = -100;
				} else {
					packageConditionChangePercentage = Math.round((double)packageConditionSizeChange / (double)originalPackage.getConditionCount() * (double)10000) / (double)100;
				}
				
				outputStreamHTMLFile.println("<td>"+(packageConditionSizeChange > 0 ? "+" : "") + (int)packageConditionSizeChange+" ("+packageConditionChangePercentage+"%)</td>");

				
				break;
		}	
		
		outputStreamHTMLFile.println("</tr>");
		
		displayedPackages.add(packageID);
		
		
		// Show all classes in the package
		for (int j = 0; j < changedClasses.size(); j++) {
			if (changedClasses.get(j).getPackageName().equals(thisPackageName)) {
				// Class belongs to package
				
				outputStreamHTMLFile.println(generateClassRow(changedClasses.get(j), packageLevel, packageID));
			
			}
		}
		
		// Get all DIRECT subpackages
		for(int j = 0; j < packageNames.size(); j++) {
			if (packageNames.get(j).replace(thisPackageName, "").startsWith(".") && !(displayedPackages.indexOf(j) >= 0)) {
				//Found a DIRECT subpackage
				generateHTML(j, changedClasses, outputStreamHTMLFile, packageLevel + 1);
			}
		}
				
	}
	
	/**
	 * Generate HTML for a sepecific class
	 * @param changedClass
	 * @param packageLevel
	 * @param packageID
	 * @return
	 */
	public String generateClassRow(OperiasFile changedClass, int packageLevel, int packageID) {
		String html = "";

		CoberturaClass originalClass = changedClass.getOriginalClass();
		CoberturaClass revisedClass = changedClass.getRevisedClass();
			
		String[] splittedClassName = changedClass.getClassName().split("\\.");
		String className = splittedClassName[splittedClassName.length - 1];
		
		html += "<tr class=' classRowLevel"+packageLevel+" ClassInPackage"+packageID+" "+(changedClass.getSourceDiff().getSourceState() ==SourceDiffState.DELETED ? "deletedOverviewRow"  : "")+"'>";
		html += "<td><a href='"+changedClass.getClassName()+".html'>"+className+"</a></td>";
		switch (changedClass.getSourceDiff().getSourceState()) {
			case DELETED:
				html += generateCoverageBarsHTML(originalClass.getLineRate(), 0.0, SourceDiffState.DELETED);
				html += "<td>"+ (int)originalClass.getLineCount()+" (Deleted)</td>";
				html += generateCoverageBarsHTML(originalClass.getConditionRate(), 0.0, SourceDiffState.DELETED);
				html += "<td>"+ (int)originalClass.getConditionCount()+" (Deleted)</td>";
				break;
			case NEW:
				html += generateCoverageBarsHTML(0.0, revisedClass.getLineRate(),SourceDiffState.NEW);
				html += "<td>" + (int)revisedClass.getLineCount()+" (New)</td>";
				html += generateCoverageBarsHTML(0.0, revisedClass.getConditionRate(), SourceDiffState.NEW);
				html += "<td>" + (int)revisedClass.getConditionCount()+" (New)</td>";
				break;
			default:
				html += generateCoverageBarsHTML(originalClass.getLineRate(), revisedClass.getLineRate(), changedClass.getSourceDiff().getSourceState());
				double classRelevantLinesSizeChange = changedClass.getRevisedClass().getLineCount() - changedClass.getOriginalClass().getLineCount();
				double classRelevantLinesSizeChangePercentage = Math.round((double)classRelevantLinesSizeChange / (double)changedClass.getOriginalClass().getLineCount() * (double)10000) / (double)100;

				html += "<td>"+(classRelevantLinesSizeChange > 0 ? "+" : "") + (int)classRelevantLinesSizeChange+" ("+classRelevantLinesSizeChangePercentage+"%)</td>";
				html += generateCoverageBarsHTML(originalClass.getConditionRate(), revisedClass.getConditionRate(), changedClass.getSourceDiff().getSourceState());
				
				double classConditionSizeChange = changedClass.getRevisedClass().getConditionCount() - changedClass.getOriginalClass().getConditionCount();
				double classConditionChangePercentage;
				if (changedClass.getOriginalClass().getConditionCount() == 0 && classConditionSizeChange != 0) {
					classConditionChangePercentage = 100;
				} else if (changedClass.getRevisedClass().getConditionCount() == 0 && classConditionSizeChange != 0) {
					classConditionChangePercentage = -100;
				} else {
					classConditionChangePercentage = Math.round((double)classConditionSizeChange / (double)changedClass.getOriginalClass().getConditionCount() * (double)10000) / (double)100;
				}
				
				html += "<td>"+(classConditionSizeChange > 0 ? "+" : "") + (int)classConditionSizeChange+" ("+classConditionChangePercentage+"%)</td>";

				break;
		}	
			
		
		
		html += "</tr>";
		
		return html;
	}
	
	/**
	 * Generate the columns for the bars
	 * @param originalCoverage
	 * @param revisedCoverage
	 * @param fileState
	 * @return
	 */
	public String generateCoverageBarsHTML(double originalCoverage, double revisedCoverage, SourceDiffState fileState) {
		double coverageChange = Math.round((revisedCoverage - originalCoverage) * (double)10000) / (double)100;
		
		String html = "<td>" + generateCoverageBarHTML(originalCoverage, revisedCoverage, fileState);
		html += "<span class='"+
					// Make the text green or red according to the coverage percentage make it normal if the file was deleted
					((coverageChange > 0) ? "inceasedText" : (coverageChange < 0 && fileState != SourceDiffState.DELETED) ? "decreasedText" : "")+"'>"+
					
					// Plus sign, when the coverage was changed. But not when the file was new or deleted
					((coverageChange > 0 && (fileState == SourceDiffState.CHANGED || fileState == SourceDiffState.SAME)) ? "+" : "")
					
					// The coverage percentage, take absolute value when deleted to remove the - sign
					+(fileState == SourceDiffState.DELETED ? ((int)Math.abs(coverageChange)) : (int)coverageChange)+"%</span>"+ "</td>";
		return html;
	}
	
	
	/**
	 * Get the coverage bar html based on the original and revised coverage number
	 * @param originalCoverage
	 * @param revisedCoverage
	 * @return
	 */
	private String generateCoverageBarHTML(double originalCoverage, double revisedCoverage, SourceDiffState fileState)  {


		String barHTML = "";
		
		switch (fileState) {
		
			case CHANGED:
			case SAME:
				if (originalCoverage > revisedCoverage) {
					barHTML  += "<div class='coverageChangeBar' title='Coverage decreased from " + Math.round(originalCoverage * 10000) / (double)100 +"% to " + Math.round(revisedCoverage * 10000) / (double)100 +"%'>";

					
					// Force at least 1 decreasedWidth
					double decreasedWidth = Math.max(Math.round(Math.abs(revisedCoverage - originalCoverage) * 100), 1);

					double originalWidth = Math.round(originalCoverage * 100) - decreasedWidth;
					
					barHTML += "<div class='originalCoverage' style='width:"+originalWidth+"%'> </div>";
					barHTML += "<div class='decreasedCoverage'  style='width:"+decreasedWidth+"%'> </div>";
					barHTML += "<div class='originalNotCoverage' style='width:"+(100 - originalWidth - decreasedWidth)+"%'> </div>";
				} else if (originalCoverage < revisedCoverage) {
					barHTML  += "<div class='coverageChangeBar' title='Coverage increased from " + Math.round(originalCoverage * 10000)  / (double)100+"% to " + Math.round(revisedCoverage * 10000)  / (double)100 +"%'>";		

					double increasedWidth = Math.max(Math.round((revisedCoverage - originalCoverage) * 100), 1);
					double originalWidth = Math.round(revisedCoverage * 100) - increasedWidth;
					
					barHTML += "<div class='originalCoverage' style='width:" + originalWidth +"%'> </div>";
					barHTML += "<div class='increasedCoverage'  style='width:"+increasedWidth+"%'> </div>";
					barHTML += "<div class='originalNotCoverage' style='width:"+ (100 - originalWidth - increasedWidth)+"%'> </div>";
				} else {
					barHTML  += "<div class='coverageChangeBar' title='Coverage stayed the same at " + Math.round(originalCoverage * 10000)  / (double)100 +"%'>";		

					double originalWidth = Math.max(Math.round(originalCoverage * 100), 1);
					
					barHTML += "<div class='originalCoverage' style='width:" + originalWidth +"%'> </div>";
					barHTML += "<div class='originalNotCoverage' style='width:"+(100 - originalWidth)+"%'> </div>";
				}
				break;
			case NEW:
				double revisedCoveredWidth = Math.max(Math.round(revisedCoverage * 100), 1);
				
				barHTML  += "<div class='coverageChangeBar' title='Coverage is " + Math.round(revisedCoverage * 10000)  / (double)100+"%'>";
				barHTML += "<div class='increasedCoverage' style='width:" + revisedCoveredWidth +"%'> </div>";
				barHTML += "<div class='decreasedCoverage' style='width:"+(100 - revisedCoveredWidth)+"%'> </div>";
				break;
			case DELETED:
				double originalCoveredWidth = Math.max(Math.round(originalCoverage * 100), 1);
				
				barHTML  += "<div class='coverageChangeBar' title='Coverage was " + Math.round(originalCoverage * 10000) / (double)100 +"%'>";
				barHTML += "<div class='originalCoverage' style='width:" + originalCoveredWidth +"%'> </div>";
				barHTML += "<div class='originalNotCoverage' style='width:"+(100 - originalCoveredWidth)+"%'> </div>";
				break;
		}
	
		
		return barHTML + "</div>";
	}

	
}
