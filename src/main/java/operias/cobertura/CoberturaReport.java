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
		
		try {
			dBuilder = dbFactory.newDocumentBuilder();
		
			Document doc = dBuilder.parse(coverageXML);
			
			Element coverage = doc.getDocumentElement();
			lineRate = Double.parseDouble(coverage.getAttribute("line-rate"));
			branchRate = Double.parseDouble(coverage.getAttribute("branch-rate"));
			
			NodeList packages = doc.getElementsByTagName("package");
			
			for (int temp = 0; temp < packages.getLength(); temp++) {
				 
				Element ePackage = (Element) packages.item(temp);
				
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
			
			CoberturaClass cClass = new CoberturaClass(className,fileName, classLineRate,classBranchRate);
			
			addLinesToClass(cClass, eClass);
			addMethodsToClass(cClass, eClass);
			
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
			
			CoberturaLine cLine = new CoberturaLine(number, hits,condition);
			
			if (condition) {
				addConditionsToLines(cLine, eLine);
			}
			
			cClass.addLine(cLine);
		}
	}
	
	/**
	 * Add methods to the cobertura class
	 * @param cClass	Cobertura class
	 * @param eClass	XML class element 
	 */
	private void addMethodsToClass(CoberturaClass cClass, Element eClass) {
		NodeList lines = eClass.getElementsByTagName("method");
		
		for(int i = 0; i < lines.getLength(); i++){
			Element eMethod = (Element) lines.item(i);

			String name = eMethod.getAttribute("name");
			double lineRate = Double.parseDouble(eMethod.getAttribute("line-rate"));
			double branchRate = Double.parseDouble(eMethod.getAttribute("branch-rate"));
			
			CoberturaMethod cMethod = new CoberturaMethod(name, lineRate ,branchRate);
			
			addLinesToMethod(cMethod, eMethod);
			
			
			cClass.addMethod(cMethod);
		}
	}
	
	/**
	 * Add lines to the cobertura method
	 * @param cMethod	Cobertura method
	 * @param eMethod	XML method element
	 */
	private void addLinesToMethod(CoberturaMethod cMethod, Element eMethod) {
		NodeList lines = eMethod.getElementsByTagName("line");
		
		for(int i = 0; i < lines.getLength(); i++){
			Element eLine = (Element) lines.item(i);
			
			int number 			= Integer.parseInt(eLine.getAttribute("number"));
			int hits			= Integer.parseInt(eLine.getAttribute("hits"));
			boolean condition 	= Boolean.parseBoolean(eLine.getAttribute("branch"));
			
			CoberturaLine cLine = new CoberturaLine(number, hits,condition);
			
			if (condition) {
				addConditionsToLines(cLine, eLine);
			}
			
			cMethod.addLine(cLine);
		}
	}
	
	/**
	 * Add conditions to the coberture line
	 * @param cLine	CoberturaLine
	 * @param eLine	XML line element
	 */
	private void addConditionsToLines(CoberturaLine cLine, Element eLine) {
		NodeList lines = eLine.getElementsByTagName("condition");
		
		for(int i = 0; i < lines.getLength(); i++){
			Element eMethod = (Element) lines.item(i);

			int number = Integer.parseInt(eMethod.getAttribute("number"));
			String coverage = eMethod.getAttribute("coverage");
			
			CoberturaCondition cCondition = new CoberturaCondition(number, coverage);
			
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
	public double getBranchRate() {
		return branchRate;
	}

	/**
	 * @return the packages
	 */
	public List<CoberturaPackage> getPackages() {
		return packages;
	}
}
