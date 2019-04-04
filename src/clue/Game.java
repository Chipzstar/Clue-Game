/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clue;

import java.util.ArrayList;
import java.util.List;


public class Game implements GameInterface {
    private Board b = null;
    private Dice d = null;
    ArrayList<Player> playerList;
    List<MurderCard> solution;
    List<IntrigueCard> intrigueDeck;
}
