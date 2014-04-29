package example;

public class Loops {

	public int LoopLoop(int x) {
		for(int i = 0; i < 100; i++) {
			if (x > 10 && i > 90) {
				return i;
			} else {
				continue;
			}
		}
		return -1;
	}
}
