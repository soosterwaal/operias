package operias.servlet;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.junit.Test;

import com.google.gson.JsonParser;

public class OperiasTest {
	
	

	/**
	 * Test correct working of execution of operias
	 */
	@Test
	public void testPullData() {

		Configuration.setTemporaryDirectory(new File("target/").getAbsolutePath());
		Configuration.setResultDirectory(new File("target/test").getAbsolutePath());
		String pullData = fileToString(new File(new File("").getAbsolutePath(), "src/test/resources/pulldata.json").getAbsolutePath());
		
		JsonParser parser = new JsonParser();
		
		Operias op = new Operias(parser.parse(pullData).getAsJsonObject());
		
		assertTrue(op.execute());
	}
	
	/**
	 * Test the construction of the message 
	 */
	@Test
	public void testMessageConstruction() {

		Configuration.setTemporaryDirectory(new File("target/").getAbsolutePath());
		Configuration.setResultDirectory(new File("target/").getAbsolutePath());
		Operias op = new Operias(null);
		op.setXmlResult(new File(new File("").getAbsolutePath(), "src/test/resources/operias.xml"));
		
		String message = op.constructMessage();
		
		assertEquals("This pull request will have the following effects on the line and condition coverage of the project:\n"+
"- The line coverage increased from 85.86% to 85.92%\n" +
"- The condition coverage decreased from 83.88% to 83.65%\n\n" +

"The following changes were made to the source code of the project: \n" +
"- 263 (5.52%) (38 relevant) lines were added, which are line covered for 95.0% \n"  +
"- 824 (17.31%) (66 relevant) lines were removed, which were line covered for 97.0% \n\n" +

"The following changes were made to the test suite of the project: \n" +
"- 88 (6.73%) lines were added \n" +
"- 434 (33.18%) lines were removed \n", message );
	}
	
	/**
	 * Create a list of strings from a file
	 * @param filename File name
	 * @return List of string in the file
	 * @throws IOException 
	 */
	private String fileToString(String filename) {
        
        String line = "";
        String lines= "";
        try {
	        BufferedReader in = new BufferedReader(new FileReader(filename));
	        while ((line = in.readLine()) != null) {
	                lines += line;
	        }
	        
	        in.close();
        } catch(Exception e) {
        }
        
        return lines;
	}
}
