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

    /**
     *
     * @param name
     * @param mCards
     * @param t
     * @param d
     */
    public Human(String name, Set<MurderCard> mCards, Tile t, DetectiveCard d){
        this.name = name;
        this.mCards = mCards;
        this.position = t;
        this.dCard = d;
        this.immuneToSuggestion = false;
        input = new Scanner(System.in);
    }

    /**
     *
     * @param maxVal
     * @return
     */
    public int getInput (int maxVal){
        int val= -1;
        while(val<0||val>maxVal){
            val = input.nextInt()-1;
        }
        return val;
    }

    /**
     *
     */
    public void rollDiceAndMove(){
        int roll = this.g.d.roll();
        ArrayList<Tile> accessibleTiles = this.g.b.getReachableTiles(roll, this.position);
        for(int i = 0; i<accessibleTiles.size();i++){
            System.out.println(i +": "+accessibleTiles.get(i).toString());
        }
        setPosition(accessibleTiles.get(getInput(accessibleTiles.size()-1)));
        if(this.position instanceof SpecialTile){
            drawIntrigue();
        }
        if(this.position instanceof RoomTile){
            setPosition(((RoomTile) this.position).getRoom().getRoomIndex());
        }
    }

    /**
     *
     */
    public void useShortcut(){
        this.position =((RoomTile) this.position).getRoom().getShortcut().getRoomIndex();
    }

    /**
     *
     * @return
     */
    public ArrayList<MurderCard> makeSuggestion(){
        ArrayList<MurderCard> suggestion = new ArrayList<>();

        //WEAPONS
        ArrayList<MurderCard> temp = dCard.getWeaponMCards();
        System.out.println("Select Weapon");
        displayDetectiveCardItems(temp);
        System.out.print("Selection: ");
        suggestion.add(temp.get(getInput(temp.size()-1)));

        //ROOM - gets roomMCard of current room player is in
        suggestion.add(dCard.getRoomMCard(((RoomTile) this.position).getRoom().getName()));

        //CHARACTERS
        temp = dCard.getCharacterMCards();
        System.out.println("Select Character");
        displayDetectiveCardItems(temp);
        System.out.print("Selection: ");
        suggestion.add(temp.get(getInput(temp.size()-1)));
        
        return suggestion;
    }

    /**
     *
     * @return
     */
    public ArrayList<MurderCard> makeAccusation(){
        ArrayList<MurderCard> accusation = new ArrayList<>();
        ArrayList<MurderCard> temp;

        //WEAPONS
        temp = dCard.getWeaponMCards();
        System.out.println("Select a Weapon");
        displayDetectiveCardItems(temp);
        System.out.print("Selection: ");
        accusation.add(temp.get(getInput(temp.size()-1)));

        //ROOMS
        temp = dCard.getRoomMCards();
        System.out.println("Select a Room");
        displayDetectiveCardItems(temp);
        System.out.print("Selection: ");
        accusation.add(temp.get(getInput(temp.size()-1)));

        //CHARACTERS
        temp = dCard.getCharacterMCards();
        System.out.println("Select a Character");
        displayDetectiveCardItems(temp);
        System.out.print("Selection: ");
        accusation.add(temp.get(getInput(temp.size()-1)));

        return accusation;
    }

    /**
     *
     * @param items
     */
    private void displayDetectiveCardItems(ArrayList<MurderCard> items){
        for(int i = 0; i<items.size(); i++){
            System.out.println(++i +": " +items.get(i).getName());
        }
    }

    /**
     * Revealing cards - if any matches, select card to reveal
     * @param suggestion
     * @return
     */

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
            if(immuneToSuggestion){
                immuneToSuggestion = false;
                return null;
            } else {
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
