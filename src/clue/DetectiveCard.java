/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clue;


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

        private Mark(String s) {
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


    public DetectiveCard(ArrayList<WeaponMCard> weapon, ArrayList<RoomMCard> room,
                         ArrayList<CharacterMCard> character){
        weapons = new HashMap<>();
        rooms = new HashMap<>();
        characters = new HashMap();
        for(WeaponMCard w :weapon){
            this.weapons.put(w,Mark.BLANK);
        }
        for(RoomMCard r :room){
            this.rooms.put(r,Mark.BLANK);
        }
        for(CharacterMCard c: character){
            this.characters.put(c,Mark.BLANK);
        }
    }

    public boolean mark(String s){
        for(MurderCard w: weapons.keySet()){
            if(s == w.getName()){
                weapons.put(w, Mark.MARK);
                return true;
            }
        }
        for(MurderCard r: rooms.keySet()){
            if(s == r.getName()){
                rooms.put(r, Mark.MARK);
                return true;
            }
        }
        for(MurderCard c: characters.keySet()){
            if(s == c.getName()){
                characters.put(c, Mark.MARK);
                return true;
            }
        }
        return false;
    }

    public boolean unmark(String s){
        for(MurderCard w: weapons.keySet()){
            if(s == w.getName()){
                weapons.put(w, Mark.BLANK);
                return true;
            }
        }
        for(MurderCard r: rooms.keySet()){
            if(s == r.getName()){
                rooms.put(r, Mark.BLANK);
                return true;
            }
        }
        for(MurderCard c: characters.keySet()){
            if(s == c.getName()){
                characters.put(c, Mark.BLANK);
                return true;
            }
        }
        return false;
    }

    public ArrayList<MurderCard> getWeaponMCards(){
        return new ArrayList<MurderCard>(weapons.keySet());
    }

    public ArrayList<MurderCard> getRoomMCards(){
        return new ArrayList<MurderCard>(rooms.keySet());
    }

    public ArrayList<MurderCard> getCharacterMCards(){
        return new ArrayList<MurderCard>(characters.keySet());
    }

    public MurderCard getRoomMCard (String s){
        for(MurderCard r: rooms.keySet()){
            if(r.name == s){
                return r;
            }
        }
        return null;
    }

    public void cardToString(){
        System.out.println(Arrays.asList(weapons));
        System.out.println(Arrays.asList(rooms));
        System.out.println(Arrays.asList(characters));
    }
}
