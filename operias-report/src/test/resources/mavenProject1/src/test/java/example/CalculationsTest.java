package example;

import static org.junit.Assert.*;

import org.junit.Test;

public class CalculationsTest {

	@Test
	public void testSomeCalcution() {
		Calculations c = new Calculations();
		
		assertEquals(25, c.calculateSomethings(5, 1));
	}
}
