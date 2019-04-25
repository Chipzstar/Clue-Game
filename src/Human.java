/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.ArrayList;
import java.util.HashSet;
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
    public Human(String name, HashSet<MurderCard> mCards, Tile t,Game g, DetectiveCard d){
        this.name = name;
        this.mCards = mCards;
        this.position = t;
        this.dCard = d;
        this.g = g;
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
            System.out.print("->");
            val = (input.nextInt())-1;
        }
        return val;
    }

    public static void clearScreen() {  
    System.out.print("\033[H\033[2J");  
    System.out.flush();  
   }
    
    @Override
    public void doTurn(){
        //make move
        g.showBoard();
        System.out.println(toString());
        makeMove();
        clearScreen();
        System.out.println("screen cleared");
        System.out.println(toString());
        
        //do suggestion
        if(this.position instanceof SpecialTile){
            this.drawIntrigue();
        }
        else if(this.position instanceof RoomTile){
            System.out.println("Do you want to make a suggestion?");
            System.out.println("1: yes");
            System.out.println("2: no");           
            switch(getInput(2)){
                case 0:
                    makeSuggestion();
                    break;
                case 1:
                    System.out.println("no suggestion");
            }          
        }
        

        //do accusation

        
    }
    @Override
    public void revealCards(ArrayList<MurderCard> revealed){
        for(MurderCard m: revealed){
            this.dCard.mark(m.getName());
        }
        
    }
    private void makeMove(){
        int choice = -1;
        System.out.println("Making a move:");
        System.out.println("1: Roll Dice and move");
        System.out.println("2: Stay");
        if(this.position instanceof RoomTile){
            if(((RoomTile)this.position).getRoom().getShortcut()!= null ){
                System.out.println("3: use shortcut to "+ ((RoomTile)this.position).getRoom().getShortcut().getName());
                choice = getInput(3);
            }
        }else{
            choice = getInput(2);
        }
        switch(choice){
            case 0:
                rollDiceAndMove();
                break;
            case 1:
                System.out.println("staying");
            case 2:
                useShortcut();
        }
        
    }
    public void rollDiceAndMove(){
        System.out.println(this.g.d.getClass());
        int roll = this.g.d.roll();
        ArrayList<Tile> accessibleTiles = this.g.b.humanDiceRoll(roll, this.position);
        for(int i = 0; i<accessibleTiles.size();i++){
            System.out.println((i+1) +": "+accessibleTiles.get(i).toString());
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
    public void makeSuggestion(){
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
        this.g.doSuggestion(suggestion);
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
            System.out.println((i+1) +": " +items.get(i).getName());
        }
    }

    /**
     * Revealing cards - if any matches, select card to reveal
     * @param suggestion
     * @return
     */

    @Override
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
    @Override
    public String toString(){
        String s = "Human player: " +name +"\n" +"Location: ";
        if(position instanceof RoomTile){
            s+= ((RoomTile)position).getRoom().getName();
        }
        else{
            s+= position.getClass().getSimpleName() + "  x:"+ position.getCoords().x+"  y: "+position.getCoords().y;
        }
        return s;
    }
}