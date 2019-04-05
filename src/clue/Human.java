/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clue;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;


/**
 *
 * @author xxlig
 */
public class Human extends Player{
 
    private Scanner input;
    
    public Human(String name, Set<MurderCard> mCards, Tile t, Game g){
        this.name = name;
        this.mCards = mCards;
        this.position = t;
        this.immunueToSuggestion = false;
        Scanner input = new Scanner(System.in);
    }  
    
    private int getInput (int maxVal){
        int val= -1;
        while(val<0||val>maxVal){
            val = input.nextInt();
            
        }
        return val;
    }
       
    //Reaveling cards- if any matches, select card to reveal
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
        else{
            if(immunueToSuggestion){                   
                immunueToSuggestion = false;
                    return null;
                } 
                else
                {
                    //return random match, can be choosen, but will be implemented later
                    System.out.println("Cards matched with suggestion are:");
                    for(int i = 0; i<matches.size(); i++){
                        System.out.println(i +"  "+matches.get(i).name + " "+ matches.get(i).getClass().getName());
                    }
                    System.out.println("What card do you want to reveal");
                    getInput(matches.size()-1);
                    return(matches.get(0));
            }
        }  
    } 
    
}
