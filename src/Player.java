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

    public void revealCards(ArrayList<MurderCard> revealed){}
    public void doTurn(){}
    public MurderCard answerSuggestion(ArrayList<MurderCard> suggestion){return null;}
    public void rollDiceAndMove(){}
    //turnStart -> roll, stay or use shortcut
    //roll -> move
    //if in room -> make a suggestion?
    //can make accusation


}