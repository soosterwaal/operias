package example;

public class Calculations {

	int x = 5;
	
	public int calculateSomethings(int y, int z) {
		if (y > 4) {
			return x * y * z;
		} else {
			return x * z;
		}
	}
	
	public int someOtherCalculations(int y, int z) {
		if (z < 10) {
			return x / y / z;
		} else {
			return x / y;
		}
	}
}
