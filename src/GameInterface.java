/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.ArrayList;
import java.util.List;


public interface GameInterface {
    Board b = null;
    Dice d = null;
    ArrayList<Player> playerList = null;
    List<MurderCard> solution = null;
    List<IntrigueCard> intrigueDeck = null;
    int currentPlayer = 0;
    /**
    *Game(config files)
    *
    * ------------initialise-------------
    *checks config file exist
    *reads config files
    *get data from files
    *initialise objects and lists
    *creates board as specified in config
    * -----------------------------------
    * -----------Game Start--------------
    * ask for params from user
    *(number of player, number of ai ect)
    * fill player list with corresponding objects
    * Game Start
    **/



}
