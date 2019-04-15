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
public class AI extends Player{
     public AI(String name, Set<MurderCard> mCards, Tile t, Game g, DetectiveCard d){
        this.name = name;
        this.mCards = mCards;
        this.position = t;
        this.immuneToSuggestion = false;
    }

    public void rollDiceAndMove(){
        int roll = this.g.d.roll();
        ArrayList<Tile> moveTo = this.g.b.humanDiceRoll(roll, this.position);
        for(int i=0; i < moveTo.size(); i++){
            System.out.println(i +": "+moveTo.get(i).toString());
        }
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
                if(immuneToSuggestion){
                    immuneToSuggestion = false;
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
