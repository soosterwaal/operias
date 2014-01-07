package operias.html;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
	
	public HTMLOverview(OperiasReport report, List<String> packageNames) throws IOException {
		this.report = report;
		this.packageNames = packageNames;
		
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

		List<OperiasFile> changedClasses = report.getChangedClasses();
		
		for (int i = 0; i < this.packageNames.size(); i++) {
			CoberturaReport originalReport = report.getOriginalCoverageReport();
			CoberturaReport revisedReport = report.getRevisedCoverageReport();
			
			double revisedLineCoverage = revisedReport.getPackage(this.packageNames.get(i)) != null ? revisedReport.getPackage(this.packageNames.get(i)).getLineRate(): 0.0;
			double revisedBranchCoverage = revisedReport.getPackage(this.packageNames.get(i)) != null ? revisedReport.getPackage(this.packageNames.get(i)).getBranchRate() : 0.0;
			
			double originalLineCoverage = originalReport.getPackage(this.packageNames.get(i)) != null ? originalReport.getPackage(this.packageNames.get(i)).getLineRate() : revisedLineCoverage;
			double originalBranchCoverage = originalReport.getPackage(this.packageNames.get(i)) != null ? originalReport.getPackage(this.packageNames.get(i)).getBranchRate() : revisedBranchCoverage;
					
			outputStreamHTMLFile.println("<tr class='packageRow' id='Package"+i+"'>");
			outputStreamHTMLFile.println("<td>"+this.packageNames.get(i)+"</td>");
			outputStreamHTMLFile.println("<td>" + getCoverageBarHTML(originalLineCoverage, revisedLineCoverage) + "</td>");
			outputStreamHTMLFile.println("<td>" + getCoverageBarHTML(originalBranchCoverage, revisedBranchCoverage) + "</td>");
			outputStreamHTMLFile.println("<td></td>");
			outputStreamHTMLFile.println("<td></td>");
			outputStreamHTMLFile.println("</tr>");
			
			// Show all classes in the package
			for (int j = 0; j < changedClasses.size(); j++) {
				if (changedClasses.get(j).getPackageName().equals(this.packageNames.get(i))) {
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
					
					outputStreamHTMLFile.println("<tr class='classRow ClassInPackage"+i+"'>");
					outputStreamHTMLFile.println("<td><a href='"+changedClass.getClassName()+".html'>"+changedClass.getClassName()+"</a></td>");
					outputStreamHTMLFile.println("<td>" + getCoverageBarHTML(originalLineCoverage, revisedLineCoverage) + "</td>");
					outputStreamHTMLFile.println("<td>" + getCoverageBarHTML(originalBranchCoverage, revisedBranchCoverage) + "</td>");
					outputStreamHTMLFile.println("<td>"+changedClass.getSourceDiff().getSourceState()+"</td>");
					outputStreamHTMLFile.println("<td>"+coverageChange+"</td>");
					outputStreamHTMLFile.println("</tr>");
				
				}
			}
		}

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
