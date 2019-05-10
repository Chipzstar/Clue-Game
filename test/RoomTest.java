import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.Assert.*;

public class RoomTest {

	@Before
	public void setUp() throws Exception {

	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Tests that shortcuts are being assigned properly
	 */
	@Test
	public void setShortcut() {
		Random rand = new Random();
		int x1 = rand.nextInt(20);
		int y1 = rand.nextInt(20);
		int x2 = rand.nextInt(10);
		int y2 = rand.nextInt(10);
		Room k = new Room("Kitchen");
		Room l = new Room("Lounge");
		Tile tile1 = new Tile(x1,y1);
		Tile tile2 = new Tile(x2,y2);
		l.setRoomIndex(tile1);
		k.setRoomIndex(tile2);
		k.setShortcut(l);
		l.setShortcut(k);
		assertTrue(k == l.getShortcut());
		assertTrue(l == k.getShortcut());
	}

	@Test
	public void setRoomIndex() {
	}

	/**
	 * Algorithm should only add a door tile to a room if that tile
	 * does not already exist in the current list of doors
	 * i.e. no duplicate tiles can be stored in 'doors'
	 */
	@Test
	public void addDoor() {
		//Room k = new Room("Kitchen");
		//Room l = new Room("Lounge");
		Room c = new Room("Conservatory");
		Tile t1 = new Tile(1,1);
		Tile t2 = new Tile(2,2);
		Tile t3 = new Tile(3,3);
		Tile t4 = new Tile(0,0);
		Tile t5 = new Tile(0,0);
		Tile t6 = new Tile(0,0);
		c.addDoor(t1);
		c.addDoor(t2);
		c.addDoor(t2);
		c.addDoor(t3);
		c.addDoor(t4);
		c.addDoor(t5);
		c.addDoor(t6);
		assertTrue(c.getDoors().size() == 4);
	}
}