package operias.cobertura;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import operias.OperiasStatus;

public class CoberturaReport {

	/**
	 * Line rate of the project
	 */
	private double lineRate;

	/**
	 * Branch rate of the project
	 */
	private double branchRate;
	
	/**
	 * List of packages in the project
	 */
	private List<CoberturaPackage> packages;
	
	/**
	 * List of sources from where the data was collected
	 */
	private List<String> sources;
	
	/**
	 * Construct a new cobertura report according to the provided coverage xml
	 * @param coverageXML
	 */
	public CoberturaReport(File coverageXML) {
		if (coverageXML == null || !coverageXML.exists() || !coverageXML.isFile() || !coverageXML.canRead()) {
			System.exit(OperiasStatus.ERROR_COBERTURA_INVALID_XML.ordinal());
		}
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		packages = new ArrayList<CoberturaPackage>();
		sources = new ArrayList<String>();
		
		try {
			dBuilder = dbFactory.newDocumentBuilder();
		
			Document doc = dBuilder.parse(coverageXML);
			
			Element coverage = doc.getDocumentElement();
			lineRate = Double.parseDouble(coverage.getAttribute("line-rate"));
			branchRate = Double.parseDouble(coverage.getAttribute("branch-rate"));
			
			// Get all the sources, used by cobertura
			NodeList sources = doc.getElementsByTagName("source");
			for(int i = 0; i < sources.getLength(); i++) {
				Element eSource = (Element)sources.item(i);
				if(!eSource.getTextContent().equals("--source")) {
					this.sources.add(eSource.getTextContent());
				}
			}
			
			
			// Get all the packages in the report
			NodeList packages = doc.getElementsByTagName("package");
			
			for (int i = 0; i < packages.getLength(); i++) {
				 
				Element ePackage = (Element) packages.item(i);
				
				String packageName = ePackage.getAttribute("name");
				double packageLineRate = Double.parseDouble(ePackage.getAttribute("line-rate"));
				double packageBranchRate = Double.parseDouble(ePackage.getAttribute("branch-rate"));
				
				CoberturaPackage cPackage = new CoberturaPackage(packageName, packageLineRate, packageBranchRate);
		 
				addClassesToPackage(cPackage, ePackage);
				
				this.packages.add(cPackage);
			}
			
		} catch (Exception e) {
			System.exit(OperiasStatus.ERROR_COBERTURA_INVALID_XML.ordinal());
		}
		
	}
	
	/**
	 * Add the classes from the element into the coberture instance
	 * @param cPackage Cobertura package instance
	 * @param ePackage XML package element
	 */
	private void addClassesToPackage(CoberturaPackage cPackage, Element ePackage) {
		NodeList classes = ePackage.getElementsByTagName("class");
		
		for(int i = 0; i < classes.getLength(); i++) {
			Element eClass = (Element) classes.item(i);

			String className = eClass.getAttribute("name");
			String fileName = eClass.getAttribute("filename");
			double classLineRate = Double.parseDouble(eClass.getAttribute("line-rate"));
			double classBranchRate = Double.parseDouble(eClass.getAttribute("branch-rate"));
			
			CoberturaClass cClass = new CoberturaClass(className,fileName, cPackage.getName(), classLineRate,classBranchRate);
			
			addLinesToClass(cClass, eClass);
			
			cPackage.addClass(cClass);
		}
	}
	
	/**
	 * add lines to the cobertura class
	 * @param cClass Cobertura class
	 * @param eClass XML class element
	 */
	private void addLinesToClass(CoberturaClass cClass, Element eClass){
		NodeList linesLists = eClass.getElementsByTagName("lines");
		
		Element eLines = (Element)linesLists.item(linesLists.getLength() - 1);
		
		NodeList lines = eLines.getElementsByTagName("line");
		
		for(int i = 0; i < lines.getLength(); i++){
			Element eLine = (Element) lines.item(i);
			
			int number 			= Integer.parseInt(eLine.getAttribute("number"));
			int hits			= Integer.parseInt(eLine.getAttribute("hits"));
			boolean condition 	= Boolean.parseBoolean(eLine.getAttribute("branch"));
			boolean conditionCompletelyCovered = false;
			
			if (condition) {
				conditionCompletelyCovered = eLine.getAttribute("condition-coverage").startsWith("100");
				
			}

			
			CoberturaLine cLine = new CoberturaLine(number, hits, condition, conditionCompletelyCovered);
			
			if (condition) {
				addConditionsToLine(cLine, eLine);
			}
			
			cClass.addLine(cLine);
		}
	}
	
	/**
	 * Add all conditions from this line to the line 
	 * @param cLine Cobertura Line instance
	 * @param eLine XML line element
	 */
	private void addConditionsToLine(CoberturaLine cLine, Element eLine) {
		NodeList linesLists = eLine.getElementsByTagName("conditions");
		
		Element eConditions = (Element)linesLists.item(linesLists.getLength() - 1);
		
		NodeList conditions = eConditions.getElementsByTagName("condition");
		
		for(int i = 0; i < conditions.getLength(); i++){
			Element eCondition = (Element) conditions.item(i);
			
			int number 			= Integer.parseInt(eCondition.getAttribute("number"));
			String type			= eCondition.getAttribute("type");
			String coverage		= eCondition.getAttribute("coverage");
			
			CoberturaCondition cCondition = new CoberturaCondition(number, type, coverage);
			
			cLine.addCondition(cCondition);
		}
			
	}

	/**
	 * @return the lineRate
	 */
	public double getLineRate() {
		return lineRate;
	}

	/**
	 * @return the branchRate
	 */
	public double getConditionRate() {
		return branchRate;
	}

	/**
	 * @return the packages
	 */
	public List<CoberturaPackage> getPackages() {
		return packages;
	}
	
	/**
	 * Get the package with the given name, null otherwise
	 * @param packageName
	 * @return
	 */
	public CoberturaPackage getPackage(String packageName) {
		for(CoberturaPackage cPackage : packages) {
			if (cPackage.getName().equals(packageName)) {
				return cPackage;
			}
		}
		
		return null;
	}
	
	/**
	 * Checks if a package exists
	 * @param packageName
	 * @return
	 */
	public boolean packageExists(String packageName) {
		return getPackage(packageName) != null;
	}

	/**
	 * @return the sources
	 */
	public List<String> getSources() {
		return sources;
	}
}
