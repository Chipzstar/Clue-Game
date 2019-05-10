/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 *
 * @author xxlig
 */
class DetectiveCard {
    public enum Mark {
        MARK("X"),BLANK("_");
        private String representation;

        Mark(String s) {
            this.representation = s;
        }

        @Override
        public String toString() {
            return representation;
        }
    }

    HashMap<MurderCard,Mark> weapons;
    HashMap<MurderCard,Mark> rooms;
    HashMap<MurderCard,Mark> characters;

    /**
     * Constructor
     * @param cards
     */
    public DetectiveCard(ArrayList<MurderCard> cards){
        weapons = new HashMap<>();
        rooms = new HashMap<>();
        characters = new HashMap();
        for(MurderCard c : cards) {
            if(c instanceof WeaponMCard) this.weapons.put(c,Mark.BLANK);
            if(c instanceof RoomMCard) this.rooms.put(c,Mark.BLANK);
            if(c instanceof CharacterMCard) this.characters.put(c,Mark.BLANK);  
        }
    }

    /**
     * Places mark on the Detective card
     * @param s
     * @return
     */
    public boolean mark(String s){
        for(MurderCard w: weapons.keySet()){
            if(s == w.toString()){
                weapons.put(w, Mark.MARK);
                return true;
            }
        }
        for(MurderCard r: rooms.keySet()){
            if(s == r.toString()){
                rooms.put(r, Mark.MARK);
                return true;
            }
        }
        for(MurderCard c: characters.keySet()){
            if(s == c.toString()){
                characters.put(c, Mark.MARK);
                return true;
            }
        }
        return false;
    }

    /**
     * Removes a mark on the Detective card
     * @param s
     * @return
     */
    public boolean unmark(String s){
        for(MurderCard w: weapons.keySet()){
            if(s == w.toString()){
                weapons.put(w, Mark.BLANK);
                return true;
            }
        }
        for(MurderCard r: rooms.keySet()){
            if(s == r.toString()){
                rooms.put(r, Mark.BLANK);
                return true;
            }
        }
        for(MurderCard c: characters.keySet()){
            if(s == c.toString()){
                characters.put(c, Mark.BLANK);
                return true;
            }
        }
        return false;
    }

    /**
     * Gets all the Weapon cards
     * @return
     */
    public ArrayList<MurderCard> getWeaponMCards(){
        return new ArrayList<MurderCard>(weapons.keySet());
    }

    /**
     * Gets all the Room cards
     * @return
     */
    public ArrayList<MurderCard> getRoomMCards(){
        return new ArrayList<MurderCard>(rooms.keySet());
    }

    /**
     * Gets all the Character cards
     * @return
     */
    public ArrayList<MurderCard> getCharacterMCards(){
        return new ArrayList<MurderCard>(characters.keySet());
    }

    /**
     * Gets the Room card that has the given name
     * @param s name of the room
     * @return
     */
    public MurderCard getRoomMCard (String s){
        for(MurderCard r: rooms.keySet()){
            if(r.name == s){
                return r;
            }
        }
        return null;
    }

    /**
     * Provides a string representation of the Detective card. Can be printed out on the console.
     * @return
     */
    public String toString(){
        String s = "\t\tDetective Card\n=============================\n\t\tCHARACTERS\n";
        for(MurderCard m: characters.keySet()){
            s+= m.name+": "+ characters.get(m).toString()+"\n";
        }
        s+="\t\tWEAPONS\n";
        for(MurderCard m: weapons.keySet()){
            s+= m.name+": "+ weapons.get(m).toString()+"\n";
        }
        s+="\t\tROOMS\n";
        for(MurderCard m: rooms.keySet()){
            s+= m.name+": "+ rooms.get(m).toString()+"\n";
        }
        return s;
    }
}
