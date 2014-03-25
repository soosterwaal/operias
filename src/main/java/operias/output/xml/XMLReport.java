package operias.output.xml;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
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
		(new File(Configuration.getDestinationDirectory(), "operias.xml")).createNewFile();
		StreamResult result = new StreamResult(new File(Configuration.getDestinationDirectory(), "operias.xml"));
 
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
		int totalRelevantLineCountRemoved = 0, totalRelevantLineCountAdded = 0, totalRelevantLineCountChangedOriginal = 0, totalRelevantLineCountChangedRevised = 0;
		int totalRelevantLineCountCoveredAndRemoved = 0, totalRelevantLineCountCoveredAndAdded = 0, totalRelevantLineCountCoveredAndChangedOriginal = 0, totalRelevantLineCountCoveredAndChangedRevised = 0;
		
		for(OperiasFile changedClass : changedClasses) {
			
			List<OperiasChange> changes = changedClass.getChanges();
			
			// Collect all numbers
			for(OperiasChange change : changes) {
				// Either there is a change, else no source changes were found, which means that only the coverage changed
				if (change.getSourceDiffDelta() == null || change.getSourceDiffDelta().getType() == TYPE.CHANGE) {
					totalRelevantLineCountChangedOriginal += change.countOriginalRelevantLines();
					totalRelevantLineCountChangedRevised += change.countRevisedRelevantLines();
					totalRelevantLineCountCoveredAndChangedOriginal += change.countOriginalLinesCovered();
					totalRelevantLineCountCoveredAndChangedRevised += change.countRevisedLinesCovered();
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

		// Elements for changed lines
		if (totalRelevantLineCountChangedOriginal > 0 || totalRelevantLineCountChangedRevised > 0){
			Element relevantLinesChanged = doc.createElement("totalRelevantLineChanged");
			Element relevantLinesChangedOriginalCount = doc.createElement("originalLineCount");
			Element relevantLinesAddedOriginalPercentage = doc.createElement("orignalLineRate");

			Element relevantLinesChangedRevisedCount = doc.createElement("revisedLineCount");
			Element relevantLinesAddedRevisedPercentage = doc.createElement("revisedLineRate");
			
			relevantLinesChangedOriginalCount.appendChild(doc.createTextNode("" + totalRelevantLineCountCoveredAndChangedOriginal));
			relevantLinesChangedRevisedCount.appendChild(doc.createTextNode("" + totalRelevantLineCountCoveredAndChangedRevised));
			
			relevantLinesAddedOriginalPercentage.appendChild(doc.createTextNode("" + Math.round((double)totalRelevantLineCountCoveredAndChangedOriginal / (double)totalRelevantLineCountChangedOriginal * 100.0f) / (double)100));
			relevantLinesAddedRevisedPercentage.appendChild(doc.createTextNode("" + Math.round((double)totalRelevantLineCountCoveredAndChangedRevised / (double)totalRelevantLineCountChangedRevised * 100.0f) / (double)100));

			relevantLinesChanged.appendChild(relevantLinesChangedOriginalCount);
			relevantLinesChanged.appendChild(relevantLinesAddedOriginalPercentage);
			relevantLinesChanged.appendChild(relevantLinesChangedRevisedCount);
			relevantLinesChanged.appendChild(relevantLinesAddedRevisedPercentage);
			
			coverageChanges.appendChild(relevantLinesChanged);
		
		}
		
		classChanges.appendChild(coverageChanges);	
		
		// Collect a summary of source changes
		int totalClassSourceLinesAdded = 0, totalClassSourceLinesRemoved = 0, totalClassSourceLinesChangedOriginal = 0, totalClassSourceLinesChangedRevised = 0;
		int totalClassSourceLinesOriginal = 0;
		
		
		for(OperiasFile classFile : report.getChangedClasses()) {
			totalClassSourceLinesOriginal += classFile.getSourceDiff().getOriginalLineCount();
			for(Delta sourceDiff : classFile.getSourceDiff().getChanges()) {
				if (sourceDiff.getType() == TYPE.CHANGE) {
					totalClassSourceLinesChangedOriginal += sourceDiff.getOriginal().size();
					totalClassSourceLinesChangedRevised += sourceDiff.getRevised().size();
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
		Element classSourceLinesChanged = doc.createElement("changedLineCount");
		
		classSourceLinesAdded.appendChild(doc.createTextNode(totalClassSourceLinesAdded + " (" + Math.round((double)totalClassSourceLinesAdded / (double)totalClassSourceLinesOriginal * (double)10000) / (double)100+"%)"));
		classSourceLinesRemoved.appendChild(doc.createTextNode(totalClassSourceLinesRemoved + " (" + Math.round((double)totalClassSourceLinesRemoved / (double)totalClassSourceLinesOriginal * (double)10000) / (double)100+"%)"));
		classSourceLinesChanged.appendChild(doc.createTextNode(totalClassSourceLinesChangedOriginal + " to " + totalClassSourceLinesChangedRevised + " (" + Math.round((double)totalClassSourceLinesChangedOriginal / (double)totalClassSourceLinesOriginal * (double)10000) / (double)100+"%)"));

		classSourceChanges.appendChild(classSourceLinesAdded);
		classSourceChanges.appendChild(classSourceLinesRemoved);
		classSourceChanges.appendChild(classSourceLinesChanged);
		
		
		
		
		classChanges.appendChild(classSourceChanges);
		
		summaryRoot.appendChild(classChanges);
		
		// Collect a summary of source changes
	    totalClassSourceLinesAdded = 0;
	    totalClassSourceLinesRemoved = 0; 
	    totalClassSourceLinesChangedOriginal = 0; 
	    totalClassSourceLinesChangedRevised = 0;
		totalClassSourceLinesOriginal = 0;
		
		
		for(DiffFile testFile : report.getChangedTests()) {
			totalClassSourceLinesOriginal += testFile.getOriginalLineCount();
			for(Delta sourceDiff : testFile.getChanges()) {
				if (sourceDiff.getType() == TYPE.CHANGE) {
					totalClassSourceLinesChangedOriginal += sourceDiff.getOriginal().size();
					totalClassSourceLinesChangedRevised += sourceDiff.getRevised().size();
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
		Element testSourceLinesChanged = doc.createElement("changedLineCount");
		
		testSourceLinesAdded.appendChild(doc.createTextNode(totalClassSourceLinesAdded + " (" + Math.round((double)totalClassSourceLinesAdded / (double)totalClassSourceLinesOriginal * (double)10000) / (double)100+"%)"));
		testSourceLinesRemoved.appendChild(doc.createTextNode(totalClassSourceLinesRemoved + " (" + Math.round((double)totalClassSourceLinesRemoved / (double)totalClassSourceLinesOriginal * (double)10000) / (double)100+"%)"));
		testSourceLinesChanged.appendChild(doc.createTextNode(totalClassSourceLinesChangedOriginal + " to " + totalClassSourceLinesChangedRevised + " (" + Math.round((double)totalClassSourceLinesChangedOriginal / (double)totalClassSourceLinesOriginal * (double)10000) / (double)100+"%)"));

		testSourceChanges.appendChild(testSourceLinesAdded);
		testSourceChanges.appendChild(testSourceLinesRemoved);
		testSourceChanges.appendChild(testSourceLinesChanged);
		
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
			classFile.setAttribute("state", changedClass.getSourceDiff().getSourceState().toString());
			

			Element coverageChanges = doc.createElement("coverageChanges");
			
			int relevantLineCountRemoved = 0, relevantLineCountAdded = 0, relevantLineCountChangedOriginal = 0, relevantLineCountChangedRevised = 0;
			int relevantLineCountCoveredAndRemoved = 0, relevantLineCountCoveredAndAdded = 0, relevantLineCountCoveredAndChangedOriginal = 0, relevantLineCountCoveredAndChangedRevised = 0;
	
			List<OperiasChange> changes = changedClass.getChanges();
			
			// Collect all numbers
			for(OperiasChange change : changes) {
				// Either there is a change, else no source changes were found, which means that only the coverage changed
				if (change.getSourceDiffDelta() == null || change.getSourceDiffDelta().getType() == TYPE.CHANGE) {
					relevantLineCountChangedOriginal += change.countOriginalRelevantLines();
					relevantLineCountChangedRevised += change.countRevisedRelevantLines();
					relevantLineCountCoveredAndChangedOriginal += change.countOriginalLinesCovered();
					relevantLineCountCoveredAndChangedRevised += change.countRevisedLinesCovered();
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
				classFile.setAttribute("conditionCoverageOriginal", changedClass.getOriginalClass().getBranchRate() + "");
			}
			
			if (changedClass.getSourceDiff().getSourceState() != SourceDiffState.DELETED) {
				classFile.setAttribute("lineCoverageRevised", changedClass.getRevisedClass().getLineRate() + "");
				classFile.setAttribute("conditionCoverageRevised", changedClass.getRevisedClass().getBranchRate() + "");
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

			// Elements for changed lines
			if (relevantLineCountChangedOriginal > 0 || relevantLineCountChangedRevised > 0){
				Element relevantLinesChanged = doc.createElement("relevantLinesChanged");
				Element relevantLinesChangedOriginalCount = doc.createElement("originalLineCount");
				Element relevantLinesAddedOriginalPercentage = doc.createElement("orignalLineRate");

				Element relevantLinesChangedRevisedCount = doc.createElement("revisedLineCount");
				Element relevantLinesAddedRevisedPercentage = doc.createElement("revisedLineRate");
				
				relevantLinesChangedOriginalCount.appendChild(doc.createTextNode("" + relevantLineCountCoveredAndChangedOriginal));
				relevantLinesChangedRevisedCount.appendChild(doc.createTextNode("" + relevantLineCountCoveredAndChangedRevised));
				
				relevantLinesAddedOriginalPercentage.appendChild(doc.createTextNode("" + Math.round((double)relevantLineCountCoveredAndChangedOriginal / (double)relevantLineCountChangedOriginal * 100.0f) / (double)100));
				relevantLinesAddedRevisedPercentage.appendChild(doc.createTextNode("" + Math.round((double)relevantLineCountCoveredAndChangedRevised / (double)relevantLineCountChangedRevised * 100.0f) / (double)100));

				relevantLinesChanged.appendChild(relevantLinesChangedOriginalCount);
				relevantLinesChanged.appendChild(relevantLinesAddedOriginalPercentage);
				relevantLinesChanged.appendChild(relevantLinesChangedRevisedCount);
				relevantLinesChanged.appendChild(relevantLinesAddedRevisedPercentage);
				
				coverageChanges.appendChild(relevantLinesChanged);
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
			testFile.setAttribute("state" , changedTest.getSourceState().toString());
			
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
		
		int removedLinesCount = 0, addedLinesCount = 0, changedLinesOriginalCount = 0, changedLinesRevisedCount = 0;
		
		for(Delta sourceDiff : sourceDiffFile.getChanges()) {
			if (sourceDiff.getType() == TYPE.CHANGE) {
				changedLinesOriginalCount += sourceDiff.getOriginal().size();
				changedLinesRevisedCount += sourceDiff.getRevised().size();
			} else if (sourceDiff.getType() == TYPE.DELETE) {
				removedLinesCount += sourceDiff.getOriginal().size();
			} else if (sourceDiff.getType() == TYPE.INSERT) {
				addedLinesCount += sourceDiff.getRevised().size();
			}
		}
		
		int totalLinesChangedCount = sourceDiffFile.getRevisedLineCount() - sourceDiffFile.getOriginalLineCount();
		double totalPercentage = Math.round((double)totalLinesChangedCount / (double) sourceDiffFile.getOriginalLineCount() * (double)10000) / (double)100;

		root.setAttribute("sizeChange", totalLinesChangedCount + " (" + totalPercentage + "%)" );
		
		if (changedLinesOriginalCount > 0) {
			Element changedLines = doc.createElement("changedLineCount");
			double percentage = Math.round((double)changedLinesOriginalCount / (double) sourceDiffFile.getOriginalLineCount() * (double)10000) / (double)100;
			changedLines.appendChild(doc.createTextNode(changedLinesOriginalCount + " into " + changedLinesRevisedCount + "(" + percentage + "%)"));
			
			root.appendChild(changedLines);
		}

		if (removedLinesCount > 0) {
			Element removedLines = doc.createElement("removedLineCount");
			double percentage = Math.round((double)removedLinesCount / (double) sourceDiffFile.getOriginalLineCount() * (double)10000) / (double)100;
			removedLines.appendChild(doc.createTextNode(removedLinesCount + " (" + percentage + "%)"));
			root.appendChild(removedLines);
		}
		
		if (addedLinesCount > 0) {
			Element addedLines = doc.createElement("addedLineCount");
			double percentage = Math.round((double)addedLinesCount / (double) sourceDiffFile.getOriginalLineCount() * (double)10000) / (double)100;
			addedLines.appendChild(doc.createTextNode(addedLinesCount + " (" + percentage + "%)"));
			root.appendChild(addedLines);
		}
	}
}
