/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Set;



/**
 *
 * @author xxlig
 */
class Player {
    protected String name;
    protected Set <MurderCard> mCards;
    protected Tile position;
    protected Game g;
    protected boolean immuneToSuggestion;
    protected DetectiveCard dCard;
    protected Random randomGenerator = new Random();

    protected Tile getPosition(){ return this.position; }

    public void setPosition(Tile t){
        this.position = t;
    }

    protected void drawIntrigue(){
        IntrigueCard c = this.g.intrigueDeck.poll();
        System.out.println(c.toString());
        this.g.intrigueDeck.offer(c);
        switch(c.getType()) {
            case AVOIDSUGGESTION:
                this.immuneToSuggestion = true;
                break;
            case EXTRATURN:
                Collections.rotate(g.playerList, -1);
                break;
            case THROWAGAIN:
                rollDiceAndMove();
                break;
            case TELEPORT:
                int x , y;
                if (this instanceof Human) {
                    do {
                        this.g.b.board.toString();
                        System.out.println("please enter x");
                        x = ((Human) this).getInput(this.g.b.board.size() - 1);
                        System.out.println("please enter y");
                        y = ((Human) this).getInput(this.g.b.board.get(0).size() - 1);
                        if (g.b.landableTile(this.g.b.board.get(y).get(x))){
                            this.setPosition(this.g.b.board.get(y).get(x));
                            break;
                        } else {
                            System.err.println("You are not permitted to teleport to this tile!");
                        }
                    } while (true);
                } else {
                    do {
                        x = randomGenerator.nextInt(this.g.b.board.size() - 1);
                        y = randomGenerator.nextInt(this.g.b.board.get(0).size() - 1);
                    } while (!(g.b.landableTile(this.g.b.board.get(x).get(y))));
                    this.setPosition(this.g.b.board.get(x).get(y));
                }
        }
    }

    /**
     * Reveals all the cards after for the current player's suggestion
     */
    public void revealCards(ArrayList<MurderCard> revealed, ArrayList<MurderCard> suggestion){}

    /**
     * Controls the whole turn of player
     */
    public void doTurn(){}

    public MurderCard answerSuggestion(ArrayList<MurderCard> suggestion){return null;}

    /**
     * Main function for controlling the AI dice roll and movement
     */
    public void rollDiceAndMove(){}

    /**
     * Puts a mark on all cards on the detective card that match a card in the player's holding hand
     * @param cards
     */
    public void updateDetectiveCard(ArrayList<MurderCard> cards) {}
    
    /**
     * Controls functionality for making a suggestion
     * @return
     */
    public ArrayList<MurderCard> makeSuggestion(){ return null;}

    /**
     * Controls functionality for making an accusation
     * @param accusation
     * @return
     */
    public void makeAccusation(ArrayList<MurderCard> accusation){}

    /**
     * Returns a String representation of the current player's status in the game. i.e.
     *  - Player name
     *  - Detective card
     *  - Current tile position
     * @return
     */
    public String toString(){ return null; }

}