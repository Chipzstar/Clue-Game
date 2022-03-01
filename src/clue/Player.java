/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clue;

import java.util.ArrayList;
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
    protected ArrayList<MurderCard> hand;
    protected boolean immuneToSuggestion;
    protected DetectiveCard dCard;
    protected Random randomGenerator = new Random();

    protected Tile getPosition(){ return this.position; }

    protected void setPosition(Tile t){
        this.position = t;
    }

    protected void drawIntrigue(){
        IntrigueCard c = this.g.intrigueDeck.poll();
        this.g.intrigueDeck.offer(c);
        switch(c.getType()) {
            case AVOIDSUGGESTION:
                this.immuneToSuggestion = true;
                break;
            case EXTRATURN:
                g.currentPlayer--;
                break;
            case THROWAGAIN:
                if (this instanceof Human) {
                    ((Human) this).rollDiceAndMove();
                } else if (this instanceof AI) {
                    ((AI) this).rollDiceAndMove();
                } else {
                    System.err.println("ERROR! Unrecognized Player!!");
                }
            case TELEPORT:
                int x = 0, y = 0;
                boolean landable = this.g.b.landableTile(this.g.b.board.get(x).get(y));
                if (this instanceof Human) {
                    do {
                        //this.g.b.board.toString();
                        System.out.println("please enter x");
                        x = ((Human) this).getInput(this.g.b.board.size() - 1);
                        System.out.println("please enter y");
                        y = ((Human) this).getInput(this.g.b.board.get(0).size() - 1);
                        if (landable) {
                            setPosition(this.g.b.board.get(x).get(y));
                        } else {
                            System.err.println("You are not permitted to teleport to this tile!");
                        }
                    } while (!landable);
                } else {
                    do {
                        x = randomGenerator.nextInt(this.g.b.board.size() - 1);
                        y = randomGenerator.nextInt(this.g.b.board.get(0).size() - 1);
                    } while (!landable);
                    setPosition(this.g.b.board.get(x).get(y));
                }
        }
    }

    public boolean checkAccusation(ArrayList<MurderCard> accusation) {
        if(accusation.containsAll(this.g.solution)){
            System.out.println("WINNER!");
            return true;
        }
        return false;
    }
    //turnStart -> roll, stay or use shortcut
    //roll -> move
    //if in room -> make a suggestion?
    //can make accusation


}

