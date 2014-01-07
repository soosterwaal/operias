package operias.html;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.IOUtils;

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
	private List<String> displayedPackages;
	
	public HTMLOverview(OperiasReport report, List<String> packageNames) throws IOException {
		this.report = report;
		this.packageNames = packageNames;
		this.displayedPackages = new LinkedList<String>();
		
		Collections.sort(this.packageNames);
		
		File indexHTMLFile = new File("site/index.html");
		indexHTMLFile.createNewFile();
		
		PrintStream outputStreamHTMLFile = new PrintStream(indexHTMLFile);
		InputStream headerStream = getClass().getResourceAsStream("/html/header.html");
		IOUtils.copy(headerStream, outputStreamHTMLFile);

		outputStreamHTMLFile.println("<div id='mainContent'>");
		
		// ARROW DOWN : &#8595;
		// ARROW UP : &#8593;
		outputStreamHTMLFile.println("<h2>Packages</h2><table class='classOverview'>");
		outputStreamHTMLFile.println("<thead><tr><th>Name</th><th>Line coverage</th><th>Branch coverage</th><th>Source Status</th><th>Coverage Status</th><tr></thead><tbody>");

		
		generatePackageOverviewHTML(0, report.getChangedClasses(),outputStreamHTMLFile);
		

		outputStreamHTMLFile.println("</tbody></table>");
		
		
		// Show list of changed test classes
		if (report.getChangedTests().size() > 0) {
			outputStreamHTMLFile.println("<h2>Test Classes</h2><table class='classOverview'>");
			outputStreamHTMLFile.println("<thead><tr><th>Name</th><th>Status</th><tr></thead><tbody>");
	
			for(DiffFile changedTest : report.getChangedTests()) {
				String fileName = "";
				
				if (changedTest.getSourceState() == SourceDiffState.NEW){
					fileName = changedTest.getRevisedFileName().replace(new File(report.getSourceDiffReport().getRevisedDirectory()).getAbsolutePath() + "/src/test/java/", "");
				} else {
					fileName = changedTest.getOriginalFileName().replace(new File(report.getSourceDiffReport().getOriginalDirectory()).getAbsolutePath() + "/src/test/java/", "");
				}
				
				outputStreamHTMLFile.println("<tr >");
				outputStreamHTMLFile.println("<td><a href='"+fileName.replace('/', '.')+".html'>"+fileName+"</a></td>");
				outputStreamHTMLFile.println("<td>"+changedTest.getSourceState()+"</td>");
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

	private void generatePackageOverviewHTML(int packageID, List<OperiasFile> changedClasses, PrintStream outputStreamHTMLFile) {

		if (packageID >= packageNames.size()) {
			// DONE
			return;
		}
		
		if (displayedPackages.indexOf(packageNames.get(packageID)) >= 0) {
			// Package already shown somehwere as subpackage, so skip
			generatePackageOverviewHTML(packageID + 1, changedClasses, outputStreamHTMLFile);
			return;
		}
		
		generateHTML(packageID, changedClasses, outputStreamHTMLFile);
		
		
		// Show next package
		generatePackageOverviewHTML(packageID + 1, changedClasses, outputStreamHTMLFile);

	}
	
	private void generateHTML(int packageID, List<OperiasFile> changedClasses, PrintStream outputStreamHTMLFile) {
		
		String thisPackageName = this.packageNames.get(packageID);
		CoberturaReport originalReport = report.getOriginalCoverageReport();
		CoberturaReport revisedReport = report.getRevisedCoverageReport();
		
		double revisedLineCoverage = revisedReport.getPackage(this.packageNames.get(packageID)) != null ? revisedReport.getPackage(this.packageNames.get(packageID)).getLineRate(): 0.0;
		double revisedBranchCoverage = revisedReport.getPackage(this.packageNames.get(packageID)) != null ? revisedReport.getPackage(this.packageNames.get(packageID)).getBranchRate() : 0.0;
		
		double originalLineCoverage = originalReport.getPackage(this.packageNames.get(packageID)) != null ? originalReport.getPackage(this.packageNames.get(packageID)).getLineRate() : revisedLineCoverage;
		double originalBranchCoverage = originalReport.getPackage(this.packageNames.get(packageID)) != null ? originalReport.getPackage(this.packageNames.get(packageID)).getBranchRate() : revisedBranchCoverage;
				
		outputStreamHTMLFile.println("<tr class='packageRow' id='Package"+packageID+"'>");
		outputStreamHTMLFile.println("<td>"+this.packageNames.get(packageID)+"</td>");
		outputStreamHTMLFile.println("<td>" + getCoverageBarHTML(originalLineCoverage, revisedLineCoverage) + "</td>");
		outputStreamHTMLFile.println("<td>" + getCoverageBarHTML(originalBranchCoverage, revisedBranchCoverage) + "</td>");
		outputStreamHTMLFile.println("<td></td>");
		outputStreamHTMLFile.println("<td></td>");
		outputStreamHTMLFile.println("</tr>");
		
		displayedPackages.add(thisPackageName);
		
		// Get all DIRECT subpackages
		for(int j = 0; j < packageNames.size(); j++) {
			if (packageNames.get(j).replace(thisPackageName, "").split(".").length == 2) {
				//Found a DIRECT subpackage
				generateHTML(j, changedClasses, outputStreamHTMLFile);
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
								
				String coverageChange = "";
				
				if (changedClass.getOriginalClass() != null && changedClass.getRevisedClass() != null) {
					coverageChange = "SAME";
					
					if (originalBranchCoverage != revisedBranchCoverage || revisedLineCoverage != originalLineCoverage) {
						coverageChange = "CHANGED";
					} 
				}
				
				outputStreamHTMLFile.println("<tr class='classRow ClassInPackage"+packageID+"'>");
				outputStreamHTMLFile.println("<td><a href='"+changedClass.getClassName()+".html'>"+changedClass.getClassName()+"</a></td>");
				outputStreamHTMLFile.println("<td>" + getCoverageBarHTML(originalLineCoverage, revisedLineCoverage) + "</td>");
				outputStreamHTMLFile.println("<td>" + getCoverageBarHTML(originalBranchCoverage, revisedBranchCoverage) + "</td>");
				outputStreamHTMLFile.println("<td>"+changedClass.getSourceDiff().getSourceState()+"</td>");
				outputStreamHTMLFile.println("<td>"+coverageChange+"</td>");
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
			double originalWidth = Math.round(originalCoverage * 100);
			double increasedWidth = Math.round(diff * 100);
			
			barHTML += "<div class='originalCoverage' style='width:" + originalWidth +"%'> </div>";
			barHTML += "<div class='increasedCoverage' style='width:"+increasedWidth+"%''> </div>";
			barHTML += "<div class='originalNotCoverage' style='width:"+(100 - originalWidth - increasedWidth)+"%'> </div>";
		} else {
			double originalWidth = 100 - Math.round(originalCoverage * 100);
			double decreasedWidth = Math.round(Math.abs(diff) * 100);
			barHTML += "<div class='originalCoverage' style='width:"+(100 - originalWidth - decreasedWidth)+"%'> </div>";
			barHTML += "<div class='decreasedCoverage'  style='width:"+decreasedWidth+"%'> </div>";
			barHTML += "<div class='originalNotCoverage' style='width:"+originalWidth+"%'> </div>";
		}
		barHTML += "</div>";
		
		return barHTML;
	}

	
}
