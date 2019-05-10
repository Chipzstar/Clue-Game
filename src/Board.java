/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.File;
import java.io.FileNotFoundException;

import java.util.*;

/**
 * @author xxlig
 */
class Board {
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_PURPLE = "\u001B[35m";


	public ArrayList<ArrayList<Tile>> board;
	private ArrayList<Room> rooms;
	private ArrayList<String> roomNames;
	private int height;
	private int width;
	public ArrayList<Tile> startTiles;

	public Board() throws FileNotFoundException {
		this.board = new ArrayList<>();
		this.rooms = new ArrayList<>();
		this.roomNames = new ArrayList<>();
		this.startTiles = new ArrayList<>();
		try {
			Scanner sc = new Scanner(new File("board.csv"));
			loadBoard(sc);
		} catch (FileNotFoundException e) {
			System.out.println("File Not Found. Loading Default."); //will implement
		}
	}

	public final void loadBoard(Scanner scanner) {
		int x = 0, y = 0;
		String[] vals;
		Room room;

		while (scanner.hasNextLine()) {
			ArrayList<Tile> tiles = new ArrayList<>();
			vals = scanner.nextLine().split(",");
			y = 0;
			for (String val : vals) {
				switch (val) {
					case "n":
						tiles.add(new NullTile(x, y));
						break;
					case "t":
						tiles.add(new Tile(x, y));
						break;
					case "X":
						tiles.add(new Tile(x, y));
						this.startTiles.add(tiles.get(tiles.size() - 1));
						break;
					default:
						//creates new room
						if ((val.contains("@"))) {
							//gets room names before and after the '@'
							String[] sRooms = val.split("@");
							room = getRoom(sRooms[0]);
							tiles.add(new RoomTile(x, y, room, false)); //fix 2
							room.setShortcut(getRoom(sRooms[1]));
						}
						//door tiles end with a digit
						else if (Character.isDigit(val.charAt(val.length() - 1))) {
							room = getRoom(val);
							tiles.add(new RoomTile(x, y, room, true));
							room.addDoor(tiles.get(tiles.size() - 1));
						}
						//creates shortcut and links to the room
						else {
							room = getRoom(val);
							tiles.add(new RoomTile(x, y, room, false));
						}
						//if it's first tile in room, room index is null
						//sets that tile to room index
						if (room.getRoomIndex() == null) {
							room.setRoomIndex(tiles.get(tiles.size() - 1));
						}
						break;
				}
				y++;
			}
			this.board.add(tiles);
			x++;
		}
		this.height = x;
		this.width = y;
		setSpecials();
		scanner.close();
	}

	public Room getRoom(String name) {
		name = name.replaceAll("[0-9X]+", "");
		if (!roomNames.contains(name)) {
			rooms.add(new Room(name));
			roomNames.add(name);
		}
		return rooms.get(roomNames.indexOf(name));
	}

	public void setSpecials() {
		Random rand = new Random();
		Tile tile;
		int y;
		int x;
		for (int i = 0; i < 6; i++) {
			do {
				x = rand.nextInt(this.height - 1);
				y = rand.nextInt(this.width - 1);
				tile = board.get(x).get(y);
			} while (tile instanceof NullTile || tile instanceof RoomTile);
			board.get(x).remove(y);
			board.get(x).add(y, new SpecialTile(x, y)); //fix 1
		}
	}

	public ArrayList<Tile> getAdjTiles(Tile t) {
		ArrayList<Tile> adjTile = new ArrayList<>();
		int y = t.getCoords().x;
		int x = t.getCoords().y;
		/*if (t instanceof RoomTile) {
			if (!((RoomTile) t).getIsDoor()) {
				adjTile.addAll(((RoomTile) t).getRoom().getDoors());
			}
		} else {*/
			if (x > 0 && board.get(x - 1).size() > y) {
				if (landableTile(board.get(x - 1).get(y))) {
					adjTile.add(board.get(x - 1).get(y));
				}
			}
			if (x < board.size() - 1 && board.get(x + 1).size() > y) {
				if (landableTile(board.get(x + 1).get(y))) {
					adjTile.add(board.get(x + 1).get(y));
				}
			}
			if (y > 0) {
				if (landableTile(board.get(x).get(y - 1))) {
					adjTile.add(board.get(x).get(y - 1));
				}
			}
			if (y < board.get(0).size() - 1) {
				if (landableTile(board.get(x).get(y + 1))) {
					adjTile.add(board.get(x).get(y + 1));
				}
			}
		return adjTile;
	}

	public boolean landableTile(Tile t) {
		boolean landable = false;
		if (t instanceof RoomTile) {
			if (((RoomTile) t).getIsDoor()) {
				landable = true;
			}
		} else if (t instanceof Tile || t instanceof SpecialTile) {
			landable = true;
		}
		if (landable && t instanceof NullTile) {
//            System.out.println("==============ERROR========\n"+t.toString());
			landable = false;
		}
		return landable;
	}

	/**
	 * @param s
	 * @return
	 */
	public HashMap<Integer, ArrayList<Tile>> getBFS(Tile s) {
		HashMap<Integer, ArrayList<Tile>> hashMap = new HashMap<>();
		ArrayList<Tile> boardTiles = new ArrayList<>();
		ArrayList<Tile> visited = new ArrayList<>();
		Integer i = 0;
		boardTiles.add(s);
		visited.add(s);
		hashMap.put(i, boardTiles);
		while (!hashMap.get(i).isEmpty()) {
			boardTiles = new ArrayList<>();
			for (Tile checkingTile : hashMap.get(i)) {
				for (Tile adjacentTile : getAdjTiles(checkingTile)) {
					if (!visited.contains(adjacentTile)) {
						visited.add(adjacentTile);
						boardTiles.add(adjacentTile);
					}
				}
			}
			i++;
			hashMap.put(i, boardTiles);
		}
		return hashMap;
	}

	public ArrayList<Tile> diceRoll(int value, Tile position) {
		HashMap<Integer, ArrayList<Tile>> hashMap;
		ArrayList<Tile> tilesInReach = new ArrayList<>();
		if(position instanceof RoomTile){
			ArrayList<Tile> doors = ((RoomTile) position).getRoom().getDoors();
			for(Tile t : doors) {
				hashMap = getBFS(t);
				tilesInReach.addAll(getReachableTiles(hashMap, value));
			}
		} else {
			hashMap = getBFS(position);
			tilesInReach = getReachableTiles(hashMap, value);
		}
		/*REMOVE DUPLICATES*/
		// Create a new LinkedHashSet
		Set<Tile> set = new LinkedHashSet<>();
		// Add the elements to set
		set.addAll(tilesInReach);
		//System.out.println("Reachable tiles (Set):" +Arrays.toString(set.toArray()));
		// Clear the list
		tilesInReach.clear();
		// add the elements of set  with no duplicates to the list
		tilesInReach.addAll(set);
		//System.out.println("Reachable tiles (ArrayList):" +Arrays.toString(tilesInReach.toArray()));
		// return the list
		return tilesInReach;
	}

	private ArrayList<Tile> getReachableTiles(HashMap<Integer, ArrayList<Tile>> hashMap, int value){
		ArrayList<Tile> tilesInReach = new ArrayList<>();
		for (int i = 1; i <= value; i++) {
			if (i < hashMap.size()) {
				for (Tile x : hashMap.get(i)) {
					if (x instanceof RoomTile) {
						tilesInReach.add(x);
					}
				}
			}
		}
		if (value < hashMap.size()) {
			tilesInReach.addAll(hashMap.get(value));
		}
		return tilesInReach;
	}

	private ArrayList<RoomTile> getBFSRooms( HashMap<Integer, ArrayList<Tile>> hashMap, int value){
		ArrayList<RoomTile> roomsInReach = new ArrayList<>();
		for (Integer i = 1; i <= value; i++) {
			if (hashMap.get(i).size() > 0) {
				for (Tile t : hashMap.get(i)) {
					if (t instanceof RoomTile) {    //check if a reachable tile is a room
						if (((RoomTile) t).getIsDoor()) {
							roomsInReach.add((RoomTile) t);
						}
					}
				}
			}
		}
		return roomsInReach;
	}

	public ArrayList<RoomTile> getReachableRooms(int value, Tile position) {
		HashMap<Integer, ArrayList<Tile>> hashMap;
		//ArrayList<RoomTile> temp;
		ArrayList<RoomTile> roomsInReach = new ArrayList<>();
		if(position instanceof RoomTile){
			ArrayList<Tile> doors = ((RoomTile) position).getRoom().getDoors();
			for(Tile t : doors) {
				hashMap = getBFS(t);
				//System.out.println(hashMap.toString());
				roomsInReach.addAll(getBFSRooms(hashMap, value));
			}
		} else {
			hashMap = getBFS(position);
			roomsInReach = getBFSRooms(hashMap, value);
		}
		//if ai is already in a room, that same room is ignored in the BFS.
		if (position instanceof RoomTile) {
			for (Iterator<RoomTile> itr = roomsInReach.iterator(); itr.hasNext(); ) {
				RoomTile r = itr.next();
				if (r.getRoom().equals(((RoomTile) position).getRoom())) {
					itr.remove(); // right call
				}
			}
		}
		return roomsInReach;
	}

	public ArrayList<String> getRooms() {
		return this.roomNames;
	}

	@Override
	public String toString() {
		String s = "";
		for (ArrayList line : board) {
			for (Object tile : line) {
				if (tile instanceof NullTile) s += "NULL   ";
				else if (tile instanceof RoomTile) {
					s += "r_" + ((RoomTile) tile).getRoom().getName().substring(0, 3);
					if (((RoomTile) tile).getIsDoor()) {
						s += "D ";
					} else {
						s += "  ";
					}
				} else if (tile instanceof SpecialTile) s += "SPECIAL   ";
				else if (tile instanceof Tile) s += "Tile   ";

			}
			s += "\n";
		}
		return s;
	}

	public String toStringWithPlayers(ArrayList<Player> PL) {
		String s = "";
		HashMap<Tile, Player> tileLocation = new HashMap<>();
		for (Player p : PL) {
			tileLocation.put(p.getPosition(), p);
		}

		for (ArrayList line : board) {
			for (Object tile : line) {
				if (tileLocation.containsKey(tile)) {
					s += ANSI_RED + "#" + tileLocation.get(tile).name.substring(1, 6) + "#" + ANSI_RESET;
				} else {
					if (tile instanceof NullTile) s += "NULL   ";
					else if (tile instanceof RoomTile) {
						s += ANSI_PURPLE + "r_" + ((RoomTile) tile).getRoom().getName().substring(0, 3) + ANSI_RESET;
						if (((RoomTile) tile).getIsDoor()) {
							s += ANSI_BLUE + "D " + ANSI_RESET;
						} else if (((RoomTile) tile).getRoom().getRoomIndex() == tile) {
							s += ANSI_BLUE +"I#" + ANSI_RESET;
						} else {
							s += "  ";
						}
					} else if (tile instanceof SpecialTile) s += ANSI_GREEN + "SPEC   " + ANSI_RESET;
					else if (tile instanceof Tile) s += "Tile   ";
				}
			}
			s += "\n";
		}
		return s;
	}
}
