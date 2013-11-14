package operias.html;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import operias.report.OperiasFile;

public class HTMLPackageView {

	/**
	 * List containing the changed files
	 */
	private List<OperiasFile> changedFiles;
	
	
	/**
	 * Construct a html page for the given package
	 * @param changedFiles
	 * @param packageName
	 * @throws IOException 
	 */
	public HTMLPackageView(List<OperiasFile> changedFiles, String packageName) throws IOException {
		// Add the file to the table
		this.changedFiles = changedFiles;
		
		File classHTMLFile = new File(packageName == "" ? "site/index.html" : "site/package." + packageName + ".html");
		classHTMLFile.createNewFile();
		
		PrintStream outputStreamHTMLFile = new PrintStream(classHTMLFile);
		InputStream headerStream = getClass().getResourceAsStream("/html/header.html");
		IOUtils.copy(headerStream, outputStreamHTMLFile);

		// Generate a simple breadcrumb
		generateBreadCrumb(outputStreamHTMLFile, packageName);
		
		outputStreamHTMLFile.println("<div id='mainContent'>");
		
		ArrayList<String> packageNames = new ArrayList<String>();
		
		for(OperiasFile oFile : changedFiles) {
			if ((oFile.getPackageName().startsWith(packageName) && !oFile.getPackageName().equals(packageName)) || packageName == "") {
				if (packageNames.indexOf(oFile.getPackageName()) < 0) {
					packageNames.add(oFile.getPackageName());
				}
			}
		}
		
		if (packageNames.size() > 0) {
			outputStreamHTMLFile.println("<h2>Packages</h2><table class='classOverview'>");
			outputStreamHTMLFile.println("<thead><tr><th>Package name</th><tr></thead><tbody>");
			
			for(String pName : packageNames) {
				outputStreamHTMLFile.println("<tr><td><a href='package."+pName+".html'>"+ pName + "</a></td></tr>");
			}

			outputStreamHTMLFile.println("</tbody></table>");
		}
		
		// ARROW DOWN : &#8595;
		// ARROW UP : &#8593;
		outputStreamHTMLFile.println("<h2>Classes</h2><table class='classOverview'>");
		outputStreamHTMLFile.println("<thead><tr><th>File</th><th>Line coverage</th><th>Branch coverage</th><th>File status</th><tr></thead><tbody>");
		for(OperiasFile oFile : changedFiles) {
			if (oFile.getPackageName().equals(packageName) || packageName == "") {
				if (oFile.getRevisedClass() == null) {
					outputStreamHTMLFile.println("<tr><td><a href='"+oFile.getOriginalClass().getName()+".html'>"+ oFile.getOriginalClass().getFileName() + "</a></td>"
											+ "<td>" + Math.round(oFile.getOriginalClass().getLineRate() * 100) + "%</td>" 
											+ "<td>" + Math.round(oFile.getOriginalClass().getBranchRate() * 100) + "%</td>" 
											+ "<td>Deleted</td></tr>"); 
				} else if (oFile.getOriginalClass() == null) {
					outputStreamHTMLFile.println("<tr><td><a href='"+oFile.getRevisedClass().getName()+".html'>" + oFile.getRevisedClass().getFileName() + "</a></td>"
							+ "<td>" + Math.round(oFile.getRevisedClass().getLineRate() * 100) + "%</td>" 
							+ "<td>" + Math.round(oFile.getRevisedClass().getBranchRate() * 100) + "%</td>" 
							+ "<td>New</td></tr>");  
				} else {
					String lineRateArrow = "";
					String lineRateClass = "";
					if (oFile.getRevisedClass().getLineRate() > oFile.getOriginalClass().getLineRate()) {
						lineRateArrow = "&#8593;";
						lineRateClass = "classIncreasedCoverage";
					} else if (oFile.getRevisedClass().getLineRate() < oFile.getOriginalClass().getLineRate()) {
						lineRateArrow = "&#8595;";
						lineRateClass = "classDecreasedCoverage";
					}
					
					String branchRateArrow = "";
					String branchRateClass = "";
					if (oFile.getRevisedClass().getBranchRate() > oFile.getOriginalClass().getBranchRate()) {
						branchRateArrow = "&#8593;";
						branchRateClass = "classIncreasedCoverage";
					} else if (oFile.getRevisedClass().getBranchRate() < oFile.getOriginalClass().getBranchRate()) {
						branchRateArrow = "&#8595;";
						branchRateClass = "classDecreasedCoverage";
					}
					outputStreamHTMLFile.println("<tr><td><a href='"+oFile.getRevisedClass().getName()+".html'>" + oFile.getRevisedClass().getFileName() + "</a></td>"
							+ "<td class='"+lineRateClass+"'>" + lineRateArrow + "&nbsp;" + Math.round(oFile.getRevisedClass().getLineRate() * 100) + "%</td>" 
							+ "<td class='"+branchRateClass+"'>" + branchRateArrow + "&nbsp;" + Math.round(oFile.getRevisedClass().getBranchRate() * 100) + "%</td>" 
							+ "<td>Changed</td></tr>");  
				}
			}
		}
		outputStreamHTMLFile.println("</tbody></table></div>");
		
		InputStream footerStream = getClass().getResourceAsStream("/html/footer.html");
		IOUtils.copy(footerStream, outputStreamHTMLFile);
		
		outputStreamHTMLFile.close();
		footerStream.close();
		headerStream.close();
	}

	/**
	 * Create the breadcrumb for the package view
	 * @param outputStreamHTMLFile
	 * @param packageName
	 */
	private void generateBreadCrumb(PrintStream outputStreamHTMLFile, String packageName) {
		outputStreamHTMLFile.println("<div id='breadcrumb'>");
		if (packageName == "") {
			outputStreamHTMLFile.print("<h2>overview");
		} else {
			outputStreamHTMLFile.print("<h2><a href='index.html'>overview</a> / ");
		}
		String[] packagesAndClasses = packageName.split("\\.");
		
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
