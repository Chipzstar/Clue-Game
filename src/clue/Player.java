/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clue;


import java.util.Set;



/**
 *
 * @author xxlig
 */
class Player {
    protected String name;
    protected Set <MurderCard> mCards;  
    protected Tile position;
    protected boolean immunueToSuggestion;
    protected Game g;
    
    public Tile getPosition(){return position;} 
    
    

    }

