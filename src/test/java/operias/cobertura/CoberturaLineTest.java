package operias.cobertura;

import static org.junit.Assert.*;

import org.junit.Test;

public class CoberturaLineTest {

	/**
	 * Test the equals function of cobertura line
	 */
	@Test
	public void testEquals() {
		CoberturaLine line1 = new CoberturaLine(1, 2, false, false);
		
		assertFalse(line1.equals(null));
		assertFalse(line1.equals(10));
		assertTrue(line1.equals(line1));
		
		CoberturaLine line2 = new CoberturaLine(2, 2, false, false);
		assertFalse(line1.equals(line2));
		
		line2 = new CoberturaLine(1, 1, false, false);
		assertFalse(line1.equals(line2));
		
		line2 = new CoberturaLine(1, 2, true, false);
		assertFalse(line1.equals(line2));
		
		line2 = new CoberturaLine(1, 2, false, false);
		assertTrue(line1.equals(line2));
	}
}
