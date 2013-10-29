package example;

import static org.junit.Assert.*;

import org.junit.Test;

public class CalculationsTest {

	@Test
	public void testSomeCalcution() {
		Calculations c = new Calculations();
		
		assertEquals(25, c.calculateSomethings(5, 1));
	}
	
	@Test
	public void testSomemoreCalculations() {
		Calculations c = new Calculations();
		
		assertEquals(1, c.someOtherCalculations(5, 1));
		assertEquals(2, c.someOtherCalculations(2, 11));
	}
}
