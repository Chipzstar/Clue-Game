import org.junit.*;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class DetectiveCardTest {

	private DetectiveCard dCard;
	ArrayList<MurderCard> cards;

	@BeforeClass
	public static void setUpClass(){
		System.out.println("Setup");
	}

	@Before
	public void setUp() throws Exception {
		dCard = new DetectiveCard(cards);
	}

	@After
	public void tearDown() throws Exception {
		dCard = null;
	}

	@AfterClass
	public static void tearDownClass() {
		System.out.println("Tear Down");
	}

	@Test
	public void mark() {
		dCard = new DetectiveCard(cards);
	}

	@Test
	public void unmark() {
	}

	@Test
	public void getWeaponMCards() {
	}

	@Test
	public void getRoomMCards() {
	}

	@Test
	public void getCharacterMCards() {
	}

	@Test
	public void getRoomMCard() {
	}

	@Test
	public void toString1() {
	}
}