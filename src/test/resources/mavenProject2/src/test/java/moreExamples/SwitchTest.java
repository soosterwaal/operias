package moreExamples;

import static org.junit.Assert.*;

import org.junit.Test;

public class SwitchTest {

	@Test
	public void testSwitch() {
		
		Switch sw = new Switch();
		
		assertEquals(1, sw.DoSwitch(5));
		assertEquals(2, sw.DoSwitch(10));
	}
}
