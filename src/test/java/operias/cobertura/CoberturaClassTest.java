package operias.cobertura;

import static org.junit.Assert.*;

import org.junit.Test;

public class CoberturaClassTest {

	@Test
	public void testGetLine() {
		CoberturaClass cClass = new CoberturaClass("simple", "simpleTest.java" , null, 1,1);
	
		CoberturaLine line1 = new CoberturaLine(1, 1, false, false);
		CoberturaLine line3 = new CoberturaLine(3, 1, false, false);
		
		cClass.addLine(line1);
		cClass.addLine(line3);
		
		assertEquals(line1, cClass.tryGetLine(1));
		assertEquals(line3, cClass.tryGetLine(3));
		assertNull(cClass.tryGetLine(2));
		assertNull(cClass.tryGetLine(4));
	}
}
