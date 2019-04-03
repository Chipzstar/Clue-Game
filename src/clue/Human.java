/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;


/**
 *
 * @author xxlig
 */
public class Human extends Player{
 
    public Human(String name, Set<MurderCard> mCards, Tile t, Game g){
        this.name = name;
        this.mCards = mCards;
        this.position = t;
        this.immunueToSuggestion = false;
    }  
    
    public MurderCard answerSuggestion(ArrayList<MurderCard> suggestion){
        ArrayList <MurderCard> matches = new ArrayList<>();
        for(MurderCard m:suggestion){
            if(mCards.contains(m)){
                matches.add(m);
            }
        }
        if(matches.isEmpty()){
            return null;
        }
            else
            {
                if(immunueToSuggestion){
                    immunueToSuggestion = false;
                    return null;
                } 
                else
                {
                    //return random match, can be choosen, but will be implemented later
                    Collections.shuffle(matches);
                    return(matches.get(0));
            }
        }  
    } 
    
}
