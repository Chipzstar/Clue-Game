/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.awt.Desktop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.*;


public class Game implements GameInterface {

	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_PURPLE = "\u001B[35m";

	public static void clearScreen() {
		try {
			new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	//default config settings
	public enum Settings {
		CONFIG("config.properties"), BOARD("board.csv");

		private static final String DEFAULT_CHARACTERS = " Miss Scarlet, Col Mustard, Prof Plum, "
				+ "Mrs White, Rev Green, Mrs Peacock";
		private static final String DEFAULT_WEAPONS = "Dagger, Candle Stick, Revolver, "
				+ "Rope, Spanner, Lead Piping";
		private final String representation;

		Settings(String rep) {
			this.representation = rep;
		}

		@Override
		public String toString() {
			return this.representation;
		}
	}

	private static final int MIN_PLAYERS = 2;
	private static final int MAX_PLAYERS = 6;

	private Scanner input;
	private Properties config;
	private int counter = 0;

	public Board b;
	public Dice d;
	public ArrayList<Player> playerList;
	public LinkedList<MurderCard> solution;
	public LinkedList<IntrigueCard> intrigueDeck;
	public int currentPlayer;

	public static Random rand = new Random();

	//loads config file
	public Game() throws IOException {
		this.input = new Scanner(System.in);
		this.config = new Properties();
		this.d = new Dice(6, 2);
		this.playerList = new ArrayList<>();
		this.solution = new LinkedList<>();
		this.intrigueDeck = new LinkedList<>();
		this.currentPlayer = 0;
		try {
			FileInputStream in = new FileInputStream(new File("config"));
			this.config.load(in);
			in.close();
			menu();
		}
		//creates a new config file if one cannot be found
		catch (FileNotFoundException e) {
			loadDefaults(Settings.CONFIG);
		}

	}

	//can remove for javafx
	private void menu() throws IOException {
		while (true) {
			clearScreen();
			String menu = "1. New Game\n"
					+ "2. Load Game\n"
					+ "3. Edit Settings\n"
					+ "4. Reset Settings\n"
					+ "5. Quit";
			System.out.println(menu);
			switch (input.nextInt()) {
				case 1:
					initialise();
					new Game().menu();
					break;

				case 2: //load and save games?
					//opens config and board csv files in user systems selected editors (e.g. notepad and excel)
					//see readme
				case 3:
					for (Settings setting : Settings.values()) {
						try {
							Desktop.getDesktop().edit(new File(setting.toString()));
						} catch (IOException e) {
							System.out.println("Invalid File Or File Could Not Be Found!\n"
									+ "Loading Default.");
							loadDefaults(setting);
						}
					}
					break;
				case 4:
					loadDefaults(Settings.CONFIG);
					break;
				case 5:
					System.exit(0);
				default:
					System.out.println("ERROR! Invalid Choice. Select Again.");
					break;
			}
		}
	}

	private void loadDefaults(Settings setting) throws FileNotFoundException, IOException {
		switch (setting) {
			case CONFIG:
				FileOutputStream out = new FileOutputStream("config");
				this.config.setProperty("Characters", Settings.DEFAULT_CHARACTERS);
				this.config.setProperty("Weapons", Settings.DEFAULT_WEAPONS);
				config.store(out, "##");
				out.close();
			case BOARD://I need to come back to this
		}
	}

	public void initialise() throws FileNotFoundException {
		//Loads config, loads defaults if characters and/or weapons are missing
		//getProperty() returns String[], so is converted to fixed-size list,
		//fixed array lists can,t have anything removed so is used to construct arraylists
		List<String> characters = new ArrayList<>(Arrays.asList(config.getProperty("Characters", Settings.DEFAULT_CHARACTERS).split(",")));
		List<String> weapons = new ArrayList<>(Arrays.asList(config.getProperty("Weapons", Settings.DEFAULT_WEAPONS).split(",")));
		ArrayList<MurderCard> mCards;
		initialiseIntrigue();
		this.b = new Board();
		this.d = new Dice(6, 2);
		//System.out.println(b.toString());

		int numPlayers, numAI = 0, diff = 0;
		//makes sure player and ai number is between 2 and 6
		do {
			System.out.print("Number of Human players: ");
			numPlayers = getInput(0, MAX_PLAYERS, "ERROR! Invalid number. Try Again.");
			if (numPlayers < MAX_PLAYERS) {
				System.out.print("Number of AI players: ");
				numAI = getInput(0, MAX_PLAYERS, "ERROR! Invalid number. Try Again.");
				if(numAI > 0){
					System.out.println("Select Ai difficulty: ");
					System.out.println("1. Easy\n2. Medium\n3. Hard");
					diff = getInput(1, 3, "ERROR! Invalid number! Please try again");
				}
			}
			if(numPlayers + numAI > 6){
				System.err.println("ERROR! Max number of players allowed is 6");
			} else if(numPlayers + numAI <= 1){
				System.err.println("ERROR! Min number of players allowed is 2");
			}
		} while (numPlayers + numAI < MIN_PLAYERS || numPlayers + numAI > MAX_PLAYERS);

		mCards = genCards(b.getRooms(), "room");
		mCards.addAll(genCards(weapons, "weapon"));
		mCards.addAll(genCards(characters, "character"));

		//int index = characters.size();

		for (int i = 0; i < numPlayers; i++) {
			int charNum = 0;
			for (String character : characters) {
				charNum++;
				System.out.println((charNum) + "." + character);
			}
			System.out.print("Select Character for Player " + (i + 1) + ": ");
			int j = getInput(1, characters.size(), "ERROR! Invalid choice. Try Again.");
			playerList.add(new Human(characters.remove(j - 1), new HashSet<>(),
					b.startTiles.remove(0), this, new DetectiveCard(mCards)));
		}
		for (int i = 0; i < numAI; i++) {
			switch (diff) {
				case 1:
					playerList.add(new AI(characters.remove(rand.nextInt(characters.size())), new HashSet<>(),
							b.startTiles.remove(0), this, new DetectiveCard(mCards), AI.Difficulty.EASY));
					break;
				case 2:
					playerList.add(new AI(characters.remove(rand.nextInt(characters.size())), new HashSet<>(),
							b.startTiles.remove(0), this, new DetectiveCard(mCards), AI.Difficulty.MEDIUM));
					break;
				case 3:
					playerList.add(new AI(characters.remove(rand.nextInt(characters.size())), new HashSet<>(),
							b.startTiles.remove(0), this, new DetectiveCard(mCards), AI.Difficulty.HARD));
					break;
			}
		}
		//showCards(mCards);
		deal(mCards);
		showPlayerCards();
		playGame();
	}

	private ArrayList<MurderCard> genCards(List<String> names, String type) {
		ArrayList<MurderCard> cards = new ArrayList<>();
		names.forEach((String name) -> {
			if ("room".equals(type)) cards.add(new RoomMCard(name));
			if ("weapon".equals(type)) cards.add(new WeaponMCard(name));
			if ("character".equals(type)) cards.add(new CharacterMCard(name));
		});
		int r = rand.nextInt(cards.size());
		solution.add(cards.get(r));
		return cards;
	}

	private void deal(ArrayList<MurderCard> cards) {
		int highest = 0, dealer = 0, index, roll;
		cards.removeAll(solution); // removes solution cards from deck
		for (int i = 0; i < playerList.size(); i++) {
			roll = d.roll();
			//System.out.println(playerList.get(i).name + " rolled a "+ roll);
			if (roll > highest) {
				dealer = i;
				highest = roll;
			}
		}
		index = dealer;
		//System.out.println(ANSI_GREEN + "\n"+playerList.get(dealer).name + " is the dealer!\n" + ANSI_RESET);
		for (MurderCard card : cards) {
			playerList.get(index).mCards.add(card);
			index = ++index % playerList.size();
		}
	}

	private void initialiseIntrigue() {
		for (int i = 0; i < 2; i++) {
			intrigueDeck.add(new IntrigueCard(IntrigueCard.IntrigueCardType.AVOIDSUGGESTION));
			intrigueDeck.add(new IntrigueCard(IntrigueCard.IntrigueCardType.EXTRATURN));
			intrigueDeck.add(new IntrigueCard(IntrigueCard.IntrigueCardType.TELEPORT));
			intrigueDeck.add(new IntrigueCard(IntrigueCard.IntrigueCardType.THROWAGAIN));
		}
		Collections.shuffle(intrigueDeck);
	}

	private int getInput(int minVal, int maxVal, String message) {
		int val;
		val = input.nextInt();
		while (val < minVal || val > maxVal) {
			System.err.print(message);
			System.out.print("->");
			val = input.nextInt();
		}
		return val;
	}

	private void playGame() {
		System.out.println(ANSI_PURPLE + "MURDER SOLUTION CARDS: "+ Arrays.toString(solution.toArray())+"\n" + ANSI_RESET);
		border();
		System.out.println("\t\t\t\t\t\tGAME STARTED");
		border();
		//Update Detective cards
		for (Player p : playerList){
			p.updateDetectiveCard(new ArrayList<>(p.mCards));
		}
		do {
			System.out.println("\nTURN "+ ++counter+"#\n");
			playerList.get(0).doTurn();
			System.out.println(playerList.get(0).toString());
			Collections.rotate(playerList, 1);
		} while (playerList.size() > 1);
		border();
		System.out.println(ANSI_GREEN + "\t\t\t\t\t"+playerList.get(0).name + " is the WINNER" + ANSI_RESET);
		border();
		System.out.println("MURDER SOLUTION CARDS: "+ Arrays.toString(solution.toArray())+"\n");
	}

	public boolean doSuggestion(ArrayList<MurderCard> suggestion) {
		ArrayList<MurderCard> cardsRevealed = new ArrayList<>();
		for (int i = 1; i < playerList.size(); i++) {
			cardsRevealed.add(playerList.get(i).answerSuggestion(suggestion));
			//if a player's character piece matches the character card included in the suggestion
			//the player's piece is moved to that room
			if (suggestion.get(0).name.equals(playerList.get(i).name)) {
				playerList.get(i).setPosition(((RoomTile) playerList.get(0).getPosition()).getRoom().getRoomIndex());
			}
		}
		cardsRevealed.removeIf(Objects::isNull);
		System.out.println("Revealed Cards: " + cardsRevealed.size() + " -> " + Arrays.toString(cardsRevealed.toArray()));
		for (int i = 0; i < playerList.size(); i++) {
			playerList.get(i).revealCards(cardsRevealed, suggestion);
		}
		return cardsRevealed.size() == 0;
	}

	public void doAccusation(ArrayList<MurderCard> accusation) {
		if (solution.containsAll(accusation)) {
			System.out.println(playerList.get(0).name + " has guessed correctly");
			for (int i = 1; i < playerList.size(); i++) {
				playerList.remove(i);
			}
			/*
			* Below for loop removes all other players from the game except the player that guessed correctly
			* */
			for (Iterator<Player> itr = playerList.iterator(); itr.hasNext(); ) {
				Player p = itr.next();
				if (!p.name.equals(playerList.get(0).name)) {
					itr.remove(); // right call
				}
			}
		} else {
			border();
			System.out.println(ANSI_RED + playerList.get(0).name + " guessed incorrectly!\n"+playerList.get(0).name + " removed from the game" + ANSI_RESET);
			border();
			playerList.remove(0);
			Collections.rotate(playerList, -1);
		}
	}

	public void showBoard() {
		System.out.print(b.toStringWithPlayers(playerList));
	}

	/*private void showCards(ArrayList<MurderCard> cards){
		DetectiveCard d = new DetectiveCard(cards);
		System.out.println(ANSI_YELLOW + "\nAll Character cards: "+Arrays.toString(d.getCharacterMCards().toArray())+ ANSI_RESET);
		System.out.println(ANSI_YELLOW + "All Weapon cards: " +Arrays.toString(d.getWeaponMCards().toArray())+ ANSI_RESET);
		System.out.println(ANSI_YELLOW + "All Room cards:" + Arrays.toString(d.getRoomMCards().toArray()) + ANSI_RESET);
	}*/

	private void showPlayerCards(){
		int counter = 0;
		for (Player p : playerList) {
			System.out.println(ANSI_YELLOW + "Player "+ ++counter + " Hand: "+ Arrays.toString(p.mCards.toArray()) + ANSI_RESET);
		}
	}

	private void border(){
		System.out.println(ANSI_BLUE +"*******************************************************************" + ANSI_RESET);
	}

	public static void main(String[] args) throws IOException {
		new Game();
	}
}

