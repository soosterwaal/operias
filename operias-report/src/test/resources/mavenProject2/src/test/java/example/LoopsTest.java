package example;

import static org.junit.Assert.*;

import org.junit.Test;

public class LoopsTest {

	@Test
	public void testLoop() {
		Loops loops = new Loops();
		assertEquals(-1, loops.LoopLoop(9));
		

		assertEquals(91, loops.LoopLoop(12));
	}
}
