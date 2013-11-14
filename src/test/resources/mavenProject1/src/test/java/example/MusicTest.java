package example;

import static org.junit.Assert.*;

import org.junit.Test;

public class MusicTest {

	@Test
	public void testMusic() {
		Music m = new Music("song");
		
		assertTrue(m.isPlaying());
		
	}
}
