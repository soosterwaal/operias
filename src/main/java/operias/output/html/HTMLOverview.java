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
import operias.cobertura.CoberturaReport;
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
	 * @throws IOException
	 */
	public HTMLOverview(OperiasReport report, List<String> packageNames) throws IOException {
		this.report = report;
		this.packageNames = packageNames;
		
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
		outputStreamHTMLFile.println("<thead><tr><th>Name</th><th>Line coverage</th><th>Condition coverage</th><th>Source Status</th><tr></thead><tbody>");

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
				
				new HTMLTestView(fileName.replace('/', '.'), changedTest);
				
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
		CoberturaReport originalReport = report.getOriginalCoverageReport();
		CoberturaReport revisedReport = report.getRevisedCoverageReport();
		
		double revisedLineCoverage = revisedReport.getPackage(this.packageNames.get(packageID)) != null ? revisedReport.getPackage(this.packageNames.get(packageID)).getLineRate(): 0.0;
		double revisedBranchCoverage = revisedReport.getPackage(this.packageNames.get(packageID)) != null ? revisedReport.getPackage(this.packageNames.get(packageID)).getBranchRate() : 0.0;
		
		double originalLineCoverage = originalReport.getPackage(this.packageNames.get(packageID)) != null ? originalReport.getPackage(this.packageNames.get(packageID)).getLineRate() : revisedLineCoverage;
		double originalBranchCoverage = originalReport.getPackage(this.packageNames.get(packageID)) != null ? originalReport.getPackage(this.packageNames.get(packageID)).getBranchRate() : revisedBranchCoverage;
			
		double lineCoverageChange = Math.round((revisedLineCoverage - originalLineCoverage) * (double)10000) / (double)100;
		double branchCoverageChange = Math.round((revisedBranchCoverage - originalBranchCoverage) * (double)10000) / (double)100;
		
		int relevantLinesSizeChange = 0;	
		double relevantLinesSizeChangePercentage = 0;
		
		int originalRelevantsLines = originalReport.getPackage(this.packageNames.get(packageID)) != null ? originalReport.getPackage(this.packageNames.get(packageID)).getRelevantLinesCount() : 0;
		int revisedRelevantsLines = revisedReport.getPackage(this.packageNames.get(packageID)) != null ? revisedReport.getPackage(this.packageNames.get(packageID)).getRelevantLinesCount(): 0;
		
		relevantLinesSizeChange = revisedRelevantsLines - originalRelevantsLines;
		if (originalRelevantsLines == 0) {
			relevantLinesSizeChangePercentage = 100;
		} else {
			relevantLinesSizeChangePercentage = Math.round((double)relevantLinesSizeChange / (double)originalRelevantsLines * (double)10000) / (double)100;	
		}
		outputStreamHTMLFile.println("<tr class='packageRow level"+packageLevel+"' id='Package"+packageID+"'>");
		outputStreamHTMLFile.println("<td>"+this.packageNames.get(packageID)+"</td>");
		outputStreamHTMLFile.println("<td>" + getCoverageBarHTML(originalLineCoverage, revisedLineCoverage));
		outputStreamHTMLFile.println("<span class='"+((lineCoverageChange > 0) ? "inceasedText" : (lineCoverageChange < 0) ? "decreasedText" : "")+"'>"+((lineCoverageChange > 0) ? "+" : "") + lineCoverageChange+"%</span>"+ "</td>");
		outputStreamHTMLFile.println("<td>" + getCoverageBarHTML(originalBranchCoverage, revisedBranchCoverage) );
		outputStreamHTMLFile.println("<span class='"+((branchCoverageChange > 0) ? "inceasedText" : (branchCoverageChange < 0) ? "decreasedText" : "")+"'>"+((branchCoverageChange > 0) ? "+" : "")+branchCoverageChange+"%</span>"+ "</td>");
		outputStreamHTMLFile.println("<td>"+(relevantLinesSizeChange > 0 ? "+" : "") + relevantLinesSizeChange+" ("+relevantLinesSizeChangePercentage+"%)</td>");
		outputStreamHTMLFile.println("</tr>");
		
		displayedPackages.add(packageID);
		
		// Get all DIRECT subpackages
		for(int j = 0; j < packageNames.size(); j++) {
			if (packageNames.get(j).replace(thisPackageName, "").startsWith(".") && !(displayedPackages.indexOf(j) >= 0)) {
				//Found a DIRECT subpackage
				generateHTML(j, changedClasses, outputStreamHTMLFile, packageLevel + 1);
			}
		}
		
		// Show all classes in the package
		for (int j = 0; j < changedClasses.size(); j++) {
			if (changedClasses.get(j).getPackageName().equals(this.packageNames.get(packageID))) {
				// Class belongs to package
				OperiasFile changedClass = changedClasses.get(j);
				
				revisedLineCoverage = (changedClass.getSourceDiff().getSourceState() != SourceDiffState.DELETED) ? changedClass.getRevisedClass().getLineRate() : 0.0;
				revisedBranchCoverage = (changedClass.getSourceDiff().getSourceState() != SourceDiffState.DELETED) ? changedClass.getRevisedClass().getBranchRate() : 0.0;

				originalLineCoverage = (changedClass.getSourceDiff().getSourceState() != SourceDiffState.NEW) ? changedClass.getOriginalClass().getLineRate() : revisedLineCoverage;
				originalBranchCoverage = (changedClass.getSourceDiff().getSourceState() != SourceDiffState.NEW) ? changedClass.getOriginalClass().getBranchRate() : revisedBranchCoverage;
				
				if (changedClass.getSourceDiff().getSourceState() == SourceDiffState.DELETED) {
					relevantLinesSizeChange = changedClass.getOriginalClass().getLines().size();
					relevantLinesSizeChangePercentage = -100;
				} else if (changedClass.getSourceDiff().getSourceState() == SourceDiffState.NEW) {
					relevantLinesSizeChange = changedClass.getRevisedClass().getLines().size();
					relevantLinesSizeChangePercentage = 100;
				} else {
					relevantLinesSizeChange = changedClass.getRevisedClass().getLines().size() - changedClass.getOriginalClass().getLines().size();
					relevantLinesSizeChangePercentage = Math.round((double)relevantLinesSizeChange / (double)changedClass.getOriginalClass().getLines().size() * (double)10000) / (double)100;
				}
				
				lineCoverageChange = Math.round((revisedLineCoverage - originalLineCoverage) * (double)10000) / (double)100;
				branchCoverageChange = Math.round((revisedBranchCoverage - originalBranchCoverage) * (double)10000) / (double)100;
					
				String[] splittedClassName = changedClass.getClassName().split("\\.");
				String className = splittedClassName[splittedClassName.length - 1];
				outputStreamHTMLFile.println("<tr class='classRowLevel"+packageLevel+" ClassInPackage"+packageID+" '>");
				outputStreamHTMLFile.println("<td><a href='"+changedClass.getClassName()+".html'>"+className+"</a></td>");
				outputStreamHTMLFile.println("<td>" + getCoverageBarHTML(originalLineCoverage, revisedLineCoverage));
				outputStreamHTMLFile.println("<span class='"+((lineCoverageChange > 0) ? "inceasedText" : (lineCoverageChange < 0) ? "decreasedText" : "")+"'>"+((lineCoverageChange > 0) ? "+" : "") + lineCoverageChange+"%</span>"+ "</td>");
				outputStreamHTMLFile.println("<td>" + getCoverageBarHTML(originalBranchCoverage, revisedBranchCoverage) );
				outputStreamHTMLFile.println("<span class='"+((branchCoverageChange > 0) ? "inceasedText" : (branchCoverageChange < 0) ? "decreasedText" : "")+"'>"+((branchCoverageChange > 0) ? "+" : "")+branchCoverageChange+"%</span>"+ "</td>");
				outputStreamHTMLFile.println("<td>"+(relevantLinesSizeChange > 0 ? "+" : "") + relevantLinesSizeChange+" ("+relevantLinesSizeChangePercentage+"%)</td>");
				outputStreamHTMLFile.println("</tr>");
			
			}
		}
	}
	
	/**
	 * Get the coverage bar html based on the original and revised coverage number
	 * @param originalCoverage
	 * @param revisedCoverage
	 * @return
	 */
	private String getCoverageBarHTML(double originalCoverage, double revisedCoverage)  {
		String title = "Coverage stayed the same at " + Math.round(revisedCoverage * 100) +"%";
		
		if (originalCoverage < revisedCoverage) {
			title = "Coverage increased from " + Math.round(originalCoverage * 100) +"% to " + Math.round(revisedCoverage * 100) +"%";
		} else if (originalCoverage > revisedCoverage) {
			title = "Coverage decreased from " + Math.round(originalCoverage * 100) +"% to " + Math.round(revisedCoverage * 100) +"%";		
		}
		
		double diff = revisedCoverage - originalCoverage;
		
		String barHTML = "<div class='coverageChangeBar' title='"+title+"'>";
		
		if (diff > 0) {
			double originalWidth = Math.floor(originalCoverage * 100);
			double increasedWidth = Math.ceil(diff * 100);
			
			barHTML += "<div class='originalCoverage' style='width:" + originalWidth +"%'> </div>";
			barHTML += "<div class='increasedCoverage' style='width:"+increasedWidth+"%''> </div>";
			barHTML += "<div class='originalNotCoverage' style='width:"+(100 - originalWidth - increasedWidth)+"%'> </div>";
		} else {
			double originalWidth = 100 - Math.floor(originalCoverage * 100);
			double decreasedWidth = Math.ceil(Math.abs(diff) * 100);
			barHTML += "<div class='originalCoverage' style='width:"+(100 - originalWidth - decreasedWidth)+"%'> </div>";
			barHTML += "<div class='decreasedCoverage'  style='width:"+decreasedWidth+"%'> </div>";
			barHTML += "<div class='originalNotCoverage' style='width:"+originalWidth+"%'> </div>";
		}
		barHTML += "</div>";
		
		return barHTML;
	}

	
}
