/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clue;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class Game implements GameInterface {
	Board b = null;
	Dice d = null;
	protected ArrayList<Player> playerList;
	protected LinkedList<MurderCard> solution;
	protected LinkedList<IntrigueCard> intrigueDeck;
	int currentPlayer;


}