package operias.servlet;

import java.io.File;
import java.util.Calendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.gson.JsonObject;

public class Operias {
	
	/**
	 * Pull data
	 */
	JsonObject gitData;
	
	/**
	 * Xml result, null if  opeiras failed executing
	 */
	File xmlResult = null;
	
	
	/**
	 * Construct a new operias execution instance
	 * @param pullData
	 */
	public Operias(JsonObject gitData) {
		this.gitData = gitData;
	}
	
	/**
	 * Execute Operias, if the pull request is a valid pull request, this method returns true
	 * This does not mean that Operias executed succesfully
	 * @return True if pull request can be evaluated
	 */
	public boolean execute() {
		if (gitData.get("action").getAsString().equals("opened") || 
				gitData.get("action").getAsString().equals("synchronize")) {
				
				JsonObject pullRequest = gitData.getAsJsonObject("pull_request");
				
				JsonObject head = pullRequest.getAsJsonObject("head");
				JsonObject base = pullRequest.getAsJsonObject("base");
				
				String pullID = pullRequest.get("id").getAsString();
				
				String headCloneURL = head.getAsJsonObject("repo").get("clone_url").getAsString();
				String headSHA = head.get("sha").getAsString();
				String headRef = head.get("ref").getAsString();
				
				String baseCloneURL = base.getAsJsonObject("repo").get("clone_url").getAsString();
				String baseSHA = base.get("sha").getAsString();
				String baseRef = base.get("ref").getAsString();
				
				
				String[] operiasArgs = new String[17];

				String destinationDirectory = new File(Configuration.getResultDirectory(), "/result" + pullID).getAbsolutePath();
				operiasArgs[0] = "--original-repository-url";
				operiasArgs[1] = baseCloneURL;
				operiasArgs[2] = "--original-commit-id";
				operiasArgs[3] = baseSHA;
				operiasArgs[4] = "--original-branch-name";
				operiasArgs[5] = baseRef;
				operiasArgs[6] = "--revised-repository-url";
				operiasArgs[7] = headCloneURL;
				operiasArgs[8] = "--revised-commit-id";
				operiasArgs[9] = headSHA;
				operiasArgs[10] = "--revised-branch-name";
				operiasArgs[11] = headRef;
				operiasArgs[12] = "--temp-directory";
				operiasArgs[13] = new File(Configuration.getTemporaryDirectory(), "/temp" + pullID  + Calendar.getInstance().getTimeInMillis()).getAbsolutePath();
				operiasArgs[14] = "--destination-directory";
				operiasArgs[15] = destinationDirectory;

				try {
					operias.Main.main(operiasArgs);
					
					// Get the xml result file
					xmlResult = new File(destinationDirectory, "operias.xml");
					if (!xmlResult.exists()) {
						xmlResult = null;
					}
				} catch (Exception e) {
					xmlResult = null;
				}

				return true;
			} else {
				return false;
			}
	}
	
	/**
	 * Execute the pull request and construct the messag
	 * @return
	 */
	public String constructMessage() 
	{
		String message;
		if (xmlResult == null) {
			message = "The execution of Operias failed";
		} else {
			message = "";
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder;
			
			try {
				dBuilder = dbFactory.newDocumentBuilder();
			
				Document doc = dBuilder.parse(xmlResult);
				
				Element operias = doc.getDocumentElement();
				
				Element summary = (Element)operias.getElementsByTagName("summary").item(0);
				
				Element classChanges = (Element)summary.getElementsByTagName("classChanges").item(0);
				
				Element classCoverageChanges = (Element)classChanges.getElementsByTagName("coverageChanges").item(0);
				Element classSourceChanges = (Element)classChanges.getElementsByTagName("sourceChanges").item(0);
				
				double originalLineRate = Double.parseDouble(classCoverageChanges.getAttribute("originalLineRate"));
				double originalConditionRate = Double.parseDouble(classCoverageChanges.getAttribute("originalConditionRate"));
				double revisedLineRate = Double.parseDouble(classCoverageChanges.getAttribute("revisedLineRate"));
				double revisedConditionRate = Double.parseDouble(classCoverageChanges.getAttribute("revisedConditionRate"));

				String classSourceAddedLineCount = classSourceChanges.getElementsByTagName("addedLineCount").item(0).getChildNodes().item(0).getNodeValue();
				String classSourceRemovedLineCount = classSourceChanges.getElementsByTagName("removedLineCount").item(0).getChildNodes().item(0).getNodeValue();
				
				Element classRelevantLinesAdded = null, classRelevantLinesRemoved = null;
				
				if (classCoverageChanges.getElementsByTagName("totalRelevantLinesAdded").getLength() > 0) {
					classRelevantLinesAdded = (Element)classCoverageChanges.getElementsByTagName("totalRelevantLinesAdded").item(0);
				}
				if (classCoverageChanges.getElementsByTagName("totalRelevantLinesRemoved").getLength() > 0) {
					classRelevantLinesRemoved = (Element)classCoverageChanges.getElementsByTagName("totalRelevantLinesRemoved").item(0);
				}
				
				String summaryMessage = "This pull request will have the following effects on the line and condition coverage of the project:\n";
				

				originalLineRate = Math.round(originalLineRate * (double)10000) / (double)100;	
				revisedLineRate = Math.round(revisedLineRate * (double)10000) / (double)100;
				if (originalLineRate == revisedLineRate) {		
					summaryMessage += "- The line coverage stayed the same at " + String.valueOf(originalLineRate) + "% \n";
				} else if (revisedLineRate > originalLineRate) {		
					summaryMessage += "- The line coverage increased from " + String.valueOf(originalLineRate) + "% to " + String.valueOf(revisedLineRate) + "%\n";
				} else {
					summaryMessage += "- The line coverage decreased from " + String.valueOf(originalLineRate) + "% to " + String.valueOf(revisedLineRate) + "%\n";
				}
				

				originalConditionRate = Math.round(originalConditionRate * (double)10000) / (double)100;	
				revisedConditionRate = Math.round(revisedConditionRate * (double)10000) / (double)100;
				if (originalConditionRate == revisedConditionRate) {		
					summaryMessage += "- The condition coverage stayed the same at " + String.valueOf(originalConditionRate) + "%";
			 	} else if (revisedConditionRate > originalConditionRate) {	
					summaryMessage += "- The condition coverage increased from " + String.valueOf(originalConditionRate) + "% to " + String.valueOf(revisedConditionRate) + "%";
			 	} else {		
					summaryMessage += "- The condition coverage decreased from " + String.valueOf(originalConditionRate) + "% to " + String.valueOf(revisedConditionRate) + "%";			
				}
				
				String classMessage = "";
				
				
				String addedLinesMessage = "", removedLinesMessage = "";
				
				if (!classSourceAddedLineCount.equals("0 (0.0%)")) {			
					if (classRelevantLinesAdded != null) {
						String lineCount = classRelevantLinesAdded.getElementsByTagName("lineCount").item(0).getChildNodes().item(0).getNodeValue();
						double lineRate = Math.round(Double.parseDouble(classRelevantLinesAdded.getElementsByTagName("lineRate").item(0).getChildNodes().item(0).getNodeValue()) * (double)10000) / (double)100;
						addedLinesMessage = "- " + classSourceAddedLineCount + " ("+lineCount+" relevant) lines were added, which are line covered for "+String.valueOf(lineRate)+ "% \n";
					} else {
						addedLinesMessage = "- " + classSourceAddedLineCount + " (0 relevant) lines were added \n";	
					}
				}
				
				if (!classSourceRemovedLineCount.equals("0 (0.0%)")) {
					if (classRelevantLinesRemoved != null) {
						String lineCount = classRelevantLinesRemoved.getElementsByTagName("lineCount").item(0).getChildNodes().item(0).getNodeValue();
						double lineRate = Math.round(Double.parseDouble(classRelevantLinesRemoved.getElementsByTagName("lineRate").item(0).getChildNodes().item(0).getNodeValue()) * (double)10000) / (double)100;
						removedLinesMessage = "- " + classSourceRemovedLineCount + " ("+lineCount+" relevant) lines were removed, which were line covered for "+String.valueOf(lineRate)+ "% \n";
					} else {
						removedLinesMessage = "- " + classSourceRemovedLineCount + " (0 relevant) lines were removed \n";
					}
				}
									
				if (removedLinesMessage != "" || addedLinesMessage != "") {
					classMessage = "\n\nThe following changes were made to the source code of the project: \n";
					classMessage += addedLinesMessage + removedLinesMessage;
				}
				
				
				Element testChanges = (Element)summary.getElementsByTagName("testChanges").item(0);
				Element sourceChanges = (Element)testChanges.getElementsByTagName("sourceChanges").item(0);
				
				String testAddedLineCount = sourceChanges.getElementsByTagName("addedLineCount").item(0).getChildNodes().item(0).getNodeValue();
				String testRemovedLineCount = sourceChanges.getElementsByTagName("removedLineCount").item(0).getChildNodes().item(0).getNodeValue();
				
				String testMessage = "";
				
				String testAddedLinesMessage = "", testRemovedLinesMessage ="";
				
				if (!testAddedLineCount.equals("0 (0.0%)")) {
					testAddedLinesMessage = "- " + testAddedLineCount + " lines were added \n";
				}
								
				if (!testRemovedLineCount.equals("0 (0.0%)")) {
					testRemovedLinesMessage = "- " + testRemovedLineCount + " lines were removed \n";
				}
								
				if (testAddedLinesMessage != "" || testRemovedLinesMessage != "") {
					testMessage = "\nThe following changes were made to the test suite of the project: \n";
					testMessage += testAddedLinesMessage + testRemovedLinesMessage;
				}
					
					String linkMessage = "";
				//linkMessage = "\n[Click here](http://87.253.142.214:8081"+path+"/) for a more detailed report for this pull request."
					
				message = summaryMessage+ classMessage + testMessage + linkMessage;
				
				
			} catch (Exception e) {
				message = "The execution of Operias failed, invalid xml was produced";
			}
		}
		
		
		
		return message;
	}
	
	/**
	 * Get the comments url
	 * @return
	 */
	public String GetCommentsURL() {
		JsonObject pullRequest = gitData.getAsJsonObject("pull_request");
		return pullRequest.get("comments_url").getAsString();
	}
	
	/**
	 * Get the pull request id
	 * @return
	 */
	public String GetPullRequestID() {
		JsonObject pullRequest = gitData.getAsJsonObject("pull_request");
		return pullRequest.get("id").getAsString();
	}

	/**
	 * @return the xmlResult
	 */
	public File getXmlResult() {
		return xmlResult;
	}

	/**
	 * @param xmlResult the xmlResult to set
	 */
	public void setXmlResult(File xmlResult) {
		this.xmlResult = xmlResult;
	}
}
