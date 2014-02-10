package operias.html;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

import org.apache.commons.io.IOUtils;

import difflib.ChangeDelta;
import difflib.DeleteDelta;
import difflib.Delta;
import difflib.InsertDelta;
import operias.diff.DiffFile;
import operias.diff.SourceDiffState;

public class HTMLTestView extends HTMLCodeView {
	
	
	/**
	 * Create a new hTML file view page
	 * @param file
	 * @throws IOException
	 */
	public HTMLTestView(String fileName, DiffFile file) throws IOException {
		
		File classHTMLFile = new File("site/" + fileName + ".html");
		classHTMLFile.createNewFile();
		
		PrintStream outputStreamHTMLFile = new PrintStream(classHTMLFile);
		InputStream headerStream = getClass().getResourceAsStream("/html/header.html");
		IOUtils.copy(headerStream, outputStreamHTMLFile);
		
		
		outputStreamHTMLFile.println("<div id='mainContent'><div id='tableContent'>");


		InputStream legendStream = getClass().getResourceAsStream("/html/codeviewlegend.html");
		IOUtils.copy(legendStream, outputStreamHTMLFile);
		
		outputStreamHTMLFile.println("<div id='breadcrumb'>");
		outputStreamHTMLFile.print("<h2>Code");
		outputStreamHTMLFile.println("</h2>");
		outputStreamHTMLFile.println("</div>");
		
		// Generate source diff view
		generateSourceDiffView(outputStreamHTMLFile, file, true);
		
		
		outputStreamHTMLFile.println("</div></div>");
	
		InputStream footerStream = getClass().getResourceAsStream("/html/footer.html");
		IOUtils.copy(footerStream, outputStreamHTMLFile);
		
		outputStreamHTMLFile.close();
		footerStream.close();
		headerStream.close();
	}
	




}
