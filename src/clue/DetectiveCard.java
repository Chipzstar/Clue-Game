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
        
        HashMap<WeaponMCard,Mark> weapons;
        HashMap<RoomMCard,Mark> rooms;
        HashMap<CharacterMCard,Mark> characters;
        
        
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
        for(WeaponMCard w: weapons.keySet()){
            if(s == w.getName()){
                weapons.put(w, Mark.MARK);
                return true;
            }
        }
        for(RoomMCard r: rooms.keySet()){
            if(s == r.getName()){
                rooms.put(r, Mark.MARK);
                return true;
            }
        }
        for(CharacterMCard c: characters.keySet()){
            if(s == c.getName()){
                characters.put(c, Mark.MARK);
                return true;
            }
        }
        return false;
    }
    
    public boolean unmark(String s){
        for(WeaponMCard w: weapons.keySet()){
            if(s == w.getName()){
                weapons.put(w, Mark.BLANK);
                return true;
            }
        }
        for(RoomMCard r: rooms.keySet()){
            if(s == r.getName()){
                rooms.put(r, Mark.BLANK);
                return true;
            }
        }
        for(CharacterMCard c: characters.keySet()){
            if(s == c.getName()){
                characters.put(c, Mark.BLANK);
                return true;
            }
        }
        return false;
    }
      
    
    public ArrayList<MurderCard> getSuggestion(ArrayList<String> items){
        ArrayList<MurderCard> suggestion = new ArrayList<>();
        for(WeaponMCard w: weapons.keySet()){
            if(items.get(1) == w.getName()){
                suggestion.add(w);
                break;
            }
        }
        for(RoomMCard r: rooms.keySet()){
            if(items.get(2) == r.getName()){
                suggestion.add(r);
                break;
            }
        }
        for(CharacterMCard c: characters.keySet()){
            if(items.get(3) == c.getName()){
                suggestion.add(c);
                break;
            }
        }
        if(suggestion.size()!=3){
            System.out.println("ERROR");
            return null;
        }else{
            return suggestion;
        }
            
    }
    
     public void cardToString(){
         System.out.println(Arrays.asList(weapons));
         System.out.println(Arrays.asList(rooms));
         System.out.println(Arrays.asList(characters));         
       
    }
}
