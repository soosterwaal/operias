package operias.output.xml;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import difflib.Delta;
import difflib.Delta.TYPE;
import operias.Configuration;
import operias.diff.DiffFile;
import operias.diff.SourceDiffState;
import operias.report.OperiasFile;
import operias.report.OperiasReport;
import operias.report.change.OperiasChange;

public class XMLReport {

	/**
	 * Operias report on which the xml report is based
	 */
	OperiasReport report;
	
	/**
	 * XML Document
	 */
	Document doc;
	
	
	/**
	 * Construct a new xml report instance using an operias report
	 * @param report
	 */
	public XMLReport(OperiasReport report) {
		this.report = report;
	}
	
	/**
	 * Generate the XML report for Operias
	 * @throws ParserConfigurationException 
	 * @throws TransformerException 
	 * @throws IOException 
	 */
	public void generateReport() throws ParserConfigurationException, TransformerException, IOException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
 
		// root elements
		doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("operias");
		doc.appendChild(rootElement);
		
		Element summaryElement = doc.createElement("summary");
		rootElement.appendChild(summaryElement);
		
		Element changedFilesElement = doc.createElement("changedFiles");
		rootElement.appendChild(changedFilesElement);
		
		Element changedClassesElement = doc.createElement("changedClasses");
		changedFilesElement.appendChild(changedClassesElement);
		
		Element changedTestsElement = doc.createElement("changedTests");
		changedFilesElement.appendChild(changedTestsElement);
		
		generateSummary(summaryElement);
		generateChangedClasses(changedClassesElement);
		generateChangedTests(changedTestsElement);
		
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		File xmlFile = new File(Configuration.getDestinationDirectory(), "operias.xml");
		xmlFile.createNewFile();
		
		PrintStream outputStreamXMLFile = new PrintStream(xmlFile);
		
		StreamResult result = new StreamResult(outputStreamXMLFile);
 
		transformer.transform(source, result);
	}
	
	/**
	 * Generate a summary of the coverage and source differences of the comparison
	 * @param summaryRoot
	 */
	public void generateSummary(Element summaryRoot) {
		
		Element classChanges = doc.createElement("classChanges");
		Element testChanges = doc.createElement("testChanges");
		
		List<OperiasFile> changedClasses = report.getChangedClasses();
		int totalRelevantLineCountRemoved = 0, totalRelevantLineCountAdded = 0;
		int totalRelevantLineCountCoveredAndRemoved = 0, totalRelevantLineCountCoveredAndAdded = 0;
		
		for(OperiasFile changedClass : changedClasses) {
			
			List<OperiasChange> changes = changedClass.getChanges();
			
			// Collect all numbers
			for(OperiasChange change : changes) {
				// Either there is a change, else no source changes were found, which means that only the coverage changed
				if (change.getSourceDiffDelta() == null || change.getSourceDiffDelta().getType() == TYPE.CHANGE) {
					totalRelevantLineCountRemoved += change.countOriginalRelevantLines();
					totalRelevantLineCountAdded += change.countRevisedRelevantLines();
					totalRelevantLineCountCoveredAndRemoved += change.countOriginalLinesCovered();
					totalRelevantLineCountCoveredAndAdded += change.countRevisedLinesCovered();
				} else if (change.getSourceDiffDelta().getType() ==TYPE.INSERT) {
					totalRelevantLineCountAdded += change.countRevisedRelevantLines();
					totalRelevantLineCountCoveredAndAdded += change.countRevisedLinesCovered();
				} else if (change.getSourceDiffDelta().getType() ==TYPE.DELETE) {
					totalRelevantLineCountRemoved += change.countOriginalRelevantLines();
					totalRelevantLineCountCoveredAndRemoved += change.countOriginalLinesCovered();
				}
			}
		}

		Element coverageChanges = doc.createElement("coverageChanges");
		
		coverageChanges.setAttribute("originalLineRate", report.getOriginalCoverageReport().getLineRate() + "");
		coverageChanges.setAttribute("originalConditionRate", report.getOriginalCoverageReport().getConditionRate() + "");
		coverageChanges.setAttribute("revisedLineRate", report.getRevisedCoverageReport().getLineRate() + "");
		coverageChanges.setAttribute("revisedConditionRate", report.getRevisedCoverageReport().getConditionRate() + "");

		// Element for removed lines
		if (totalRelevantLineCountRemoved > 0) {
			Element relevantLinesRemoved = doc.createElement("totalRelevantLinesRemoved");
			Element relevantLinesRemovedCount = doc.createElement("lineCount");
			Element relevantLinesRemovedPercentage = doc.createElement("lineRate");
			
			relevantLinesRemovedCount.appendChild(doc.createTextNode("" + totalRelevantLineCountRemoved));
			relevantLinesRemovedPercentage.appendChild(doc.createTextNode("" + Math.round((double)totalRelevantLineCountCoveredAndRemoved / (double)totalRelevantLineCountRemoved * 100.0f) / (double)100));
			relevantLinesRemoved.appendChild(relevantLinesRemovedCount);
			relevantLinesRemoved.appendChild(relevantLinesRemovedPercentage);
			
			coverageChanges.appendChild(relevantLinesRemoved);
		}
		
		// Elements for added lines

		if (totalRelevantLineCountAdded > 0) {
			Element relevantLinesAdded = doc.createElement("totalRelevantLinesAdded");
			Element relevantLinesAddedCount = doc.createElement("lineCount");
			Element relevantLinesAddedPercentage = doc.createElement("lineRate");
			
			relevantLinesAddedCount.appendChild(doc.createTextNode("" + totalRelevantLineCountAdded));
			relevantLinesAddedPercentage.appendChild(doc.createTextNode("" + Math.round((double)totalRelevantLineCountCoveredAndAdded / (double)totalRelevantLineCountAdded * 100.0f) / (double)100));
			relevantLinesAdded.appendChild(relevantLinesAddedCount);
			relevantLinesAdded.appendChild(relevantLinesAddedPercentage);
			
			coverageChanges.appendChild(relevantLinesAdded);
		}

		
		classChanges.appendChild(coverageChanges);	
		
		// Collect a summary of source changes
		int totalClassSourceLinesAdded = 0, totalClassSourceLinesRemoved = 0;
		int totalClassSourceLinesOriginal = 0;
		
		
		for(OperiasFile classFile : report.getChangedClasses()) {
			totalClassSourceLinesOriginal += classFile.getSourceDiff().getOriginalLineCount();
			for(Delta sourceDiff : classFile.getSourceDiff().getChanges()) {
				if (sourceDiff.getType() == TYPE.CHANGE) {
					totalClassSourceLinesRemoved += sourceDiff.getOriginal().size();
					totalClassSourceLinesAdded += sourceDiff.getRevised().size();
				} else if (sourceDiff.getType() == TYPE.DELETE) {
					totalClassSourceLinesRemoved += sourceDiff.getOriginal().size();
				} else if (sourceDiff.getType() == TYPE.INSERT) {
					totalClassSourceLinesAdded += sourceDiff.getRevised().size();
				}
			}
		}
		
		// All counts collected
		Element classSourceChanges = doc.createElement("sourceChanges");
		Element classSourceLinesAdded = doc.createElement("addedLineCount");
		Element classSourceLinesRemoved = doc.createElement("removedLineCount");
		
		classSourceLinesAdded.appendChild(doc.createTextNode(totalClassSourceLinesAdded + " (" + Math.round((double)totalClassSourceLinesAdded / (double)totalClassSourceLinesOriginal * (double)10000) / (double)100+"%)"));
		classSourceLinesRemoved.appendChild(doc.createTextNode(totalClassSourceLinesRemoved + " (" + Math.round((double)totalClassSourceLinesRemoved / (double)totalClassSourceLinesOriginal * (double)10000) / (double)100+"%)"));
		
		classSourceChanges.appendChild(classSourceLinesAdded);
		classSourceChanges.appendChild(classSourceLinesRemoved);
		
		
		classChanges.appendChild(classSourceChanges);
		
		summaryRoot.appendChild(classChanges);
		
		// Collect a summary of source changes
	    totalClassSourceLinesAdded = 0;
	    totalClassSourceLinesRemoved = 0; 
		totalClassSourceLinesOriginal = 0;
		
		
		for(DiffFile testFile : report.getChangedTests()) {
			totalClassSourceLinesOriginal += testFile.getOriginalLineCount();
			for(Delta sourceDiff : testFile.getChanges()) {
				if (sourceDiff.getType() == TYPE.CHANGE) {
					totalClassSourceLinesRemoved += sourceDiff.getOriginal().size();
					totalClassSourceLinesAdded += sourceDiff.getRevised().size();
				} else if (sourceDiff.getType() == TYPE.DELETE) {
					totalClassSourceLinesRemoved += sourceDiff.getOriginal().size();
				} else if (sourceDiff.getType() == TYPE.INSERT) {
					totalClassSourceLinesAdded += sourceDiff.getRevised().size();
				}
			}
		}
		
		// All counts collected
		Element testSourceChanges = doc.createElement("sourceChanges");
		Element testSourceLinesAdded = doc.createElement("addedLineCount");
		Element testSourceLinesRemoved = doc.createElement("removedLineCount");
		
		testSourceLinesAdded.appendChild(doc.createTextNode(totalClassSourceLinesAdded + " (" + Math.round((double)totalClassSourceLinesAdded / (double)totalClassSourceLinesOriginal * (double)10000) / (double)100+"%)"));
		testSourceLinesRemoved.appendChild(doc.createTextNode(totalClassSourceLinesRemoved + " (" + Math.round((double)totalClassSourceLinesRemoved / (double)totalClassSourceLinesOriginal * (double)10000) / (double)100+"%)"));
		
		testSourceChanges.appendChild(testSourceLinesAdded);
		testSourceChanges.appendChild(testSourceLinesRemoved);
		
		testChanges.appendChild(testSourceChanges);
		summaryRoot.appendChild(testChanges);
	}
	
	
	
	/**
	 * Generate the xml for the changed class files
	 * @param changedClassesRoot
	 */
	public void generateChangedClasses(Element changedClassesRoot) {
		List<OperiasFile> changedClasses = report.getChangedClasses();
		
		for(OperiasFile changedClass : changedClasses) {
			String className = changedClass.getClassName();
			String fileName = changedClass.getSourceDiff().getFileName(report);
			
			Element classFile = doc.createElement("classFile");
			classFile.setAttribute("classname", className);
			classFile.setAttribute("filename", fileName);
			classFile.setAttribute("sourceState", changedClass.getSourceDiff().getSourceState().toString());
			

			Element coverageChanges = doc.createElement("coverageChanges");
			
			int relevantLineCountRemoved = 0, relevantLineCountAdded = 0;
			int relevantLineCountCoveredAndRemoved = 0, relevantLineCountCoveredAndAdded = 0;
	
			List<OperiasChange> changes = changedClass.getChanges();
			
			// Collect all numbers
			for(OperiasChange change : changes) {
				// Either there is a change, else no source changes were found, which means that only the coverage changed
				if (change.getSourceDiffDelta() == null || change.getSourceDiffDelta().getType() == TYPE.CHANGE) {
					relevantLineCountRemoved += change.countOriginalRelevantLines();
					relevantLineCountAdded += change.countRevisedRelevantLines();
					relevantLineCountCoveredAndRemoved += change.countOriginalLinesCovered();
					relevantLineCountCoveredAndAdded += change.countRevisedLinesCovered();
				} else if (change.getSourceDiffDelta().getType() ==TYPE.INSERT) {
					relevantLineCountAdded += change.countRevisedRelevantLines();
					relevantLineCountCoveredAndAdded += change.countRevisedLinesCovered();
				} else if (change.getSourceDiffDelta().getType() ==TYPE.DELETE) {
					relevantLineCountRemoved += change.countOriginalRelevantLines();
					relevantLineCountCoveredAndRemoved += change.countOriginalLinesCovered();
				}
			}

			if (changedClass.getSourceDiff().getSourceState() != SourceDiffState.NEW) {
				classFile.setAttribute("lineCoverageOriginal", changedClass.getOriginalClass().getLineRate() + "");
				classFile.setAttribute("conditionCoverageOriginal", changedClass.getOriginalClass().getConditionRate() + "");
			}
			
			if (changedClass.getSourceDiff().getSourceState() != SourceDiffState.DELETED) {
				classFile.setAttribute("lineCoverageRevised", changedClass.getRevisedClass().getLineRate() + "");
				classFile.setAttribute("conditionCoverageRevised", changedClass.getRevisedClass().getConditionRate() + "");
			}
			
			// Element for removed lines
			if (relevantLineCountRemoved > 0) {
				Element relevantLinesRemoved = doc.createElement("relevantLinesRemoved");
				Element relevantLinesRemovedCount = doc.createElement("lineCount");
				Element relevantLinesRemovedPercentage = doc.createElement("lineRate");
				
				relevantLinesRemovedCount.appendChild(doc.createTextNode("" + relevantLineCountRemoved));
				relevantLinesRemovedPercentage.appendChild(doc.createTextNode("" + Math.round((double)relevantLineCountCoveredAndRemoved / (double)relevantLineCountRemoved * 100.0f) / (double)100));
				relevantLinesRemoved.appendChild(relevantLinesRemovedCount);
				relevantLinesRemoved.appendChild(relevantLinesRemovedPercentage);
				
				coverageChanges.appendChild(relevantLinesRemoved);
			}
			
			// Elements for added lines

			if (relevantLineCountAdded > 0) {
				Element relevantLinesAdded = doc.createElement("relevantLinesAdded");
				Element relevantLinesAddedCount = doc.createElement("lineCount");
				Element relevantLinesAddedPercentage = doc.createElement("lineRate");
				
				relevantLinesAddedCount.appendChild(doc.createTextNode("" + relevantLineCountAdded));
				relevantLinesAddedPercentage.appendChild(doc.createTextNode("" + Math.round((double)relevantLineCountCoveredAndAdded / (double)relevantLineCountCoveredAndAdded * 100.0f) / (double)100));
				relevantLinesAdded.appendChild(relevantLinesAddedCount);
				relevantLinesAdded.appendChild(relevantLinesAddedPercentage);
				
				coverageChanges.appendChild(relevantLinesAdded);
			}

			
			
			classFile.appendChild(coverageChanges);
			
			Element sourceChanges = doc.createElement("sourceChanges");
			generateSourceDifferenceCount(changedClass.getSourceDiff(), sourceChanges);
			classFile.appendChild(sourceChanges);
			
			changedClassesRoot.appendChild(classFile);
		}
	}
	
	
	
	/**
	 * Generate xml for the changed test files
	 * @param changedTestsRoot
	 */
	public void generateChangedTests(Element changedTestsRoot) {
		List<DiffFile> changedTests = report.getChangedTests();
		
		for(DiffFile changedTest : changedTests) {
			String fileName = changedTest.getFileName(report);
			
			Element testFile = doc.createElement("testFile");
		
			testFile.setAttribute("filename" , fileName);
			testFile.setAttribute("sourceState" , changedTest.getSourceState().toString());
			
			generateSourceDifferenceCount(changedTest, testFile);
			
			changedTestsRoot.appendChild(testFile);
		}
	}
	
	/**
	 * Generate source different count elements
	 * @param sourceDiffFile The source differences
	 * @param root Root element where the generated element will be placed
	 */
	private void generateSourceDifferenceCount(DiffFile sourceDiffFile, Element root) {
		
		int removedLinesCount = 0, addedLinesCount = 0;
		
		for(Delta sourceDiff : sourceDiffFile.getChanges()) {
			if (sourceDiff.getType() == TYPE.CHANGE) {
				removedLinesCount += sourceDiff.getOriginal().size();
				addedLinesCount += sourceDiff.getRevised().size();
			} else if (sourceDiff.getType() == TYPE.DELETE) {
				removedLinesCount += sourceDiff.getOriginal().size();
			} else if (sourceDiff.getType() == TYPE.INSERT) {
				addedLinesCount += sourceDiff.getRevised().size();
			}
		}
		
		int totalLinesChangedCount = sourceDiffFile.getRevisedLineCount() - sourceDiffFile.getOriginalLineCount();
		double totalPercentage = 100.0;
		if (sourceDiffFile.getOriginalLineCount() > 0) {
			totalPercentage = Math.round((double)totalLinesChangedCount / (double) sourceDiffFile.getOriginalLineCount() * (double)10000) / (double)100;
		} 

		root.setAttribute("sizeChange", totalLinesChangedCount + " (" + totalPercentage + "%)" );
		

		if (removedLinesCount > 0) {
			Element removedLines = doc.createElement("removedLineCount");
			double percentage = Math.round((double)removedLinesCount / (double) sourceDiffFile.getOriginalLineCount() * (double)10000) / (double)100;
			removedLines.appendChild(doc.createTextNode(removedLinesCount + " (" + percentage + "%)"));
			root.appendChild(removedLines);
		}
		
		if (addedLinesCount > 0) {
			Element addedLines = doc.createElement("addedLineCount");
			double percentage = 100.0;
			if (sourceDiffFile.getOriginalLineCount() > 0) {
				 percentage = Math.round((double)addedLinesCount / (double) sourceDiffFile.getOriginalLineCount() * (double)10000) / (double)100;		
			}
			
			addedLines.appendChild(doc.createTextNode(addedLinesCount + " (" + percentage + "%)"));
			root.appendChild(addedLines);
		}
	}
}
