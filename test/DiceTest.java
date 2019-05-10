import org.junit.Test;

import static org.junit.Assert.*;

public class DiceTest {

	/**
	 * Test to show that a player can only roll a number between 2 and 12 when two 6 sided dice are used
	 */
	@Test
	public void roll() {
		int sides = 6;
		int num = 2;
		Dice dice1 = new Dice(sides,  num);
		for (int i = 0; i < 100;i++) {
			assertTrue(dice1.roll() <= 12 && dice1.roll() >= 2);
		}
	}
}