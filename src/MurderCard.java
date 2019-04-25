/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author xxlig
 */
class MurderCard {
    
    protected String name;
        
    public MurderCard(String name){
        this.name = name;
    }

    public String getName(){return name;}
      
}

class WeaponMCard extends MurderCard{  
    public WeaponMCard(String name) {
        super(name);
    }   
}

class CharacterMCard extends MurderCard{   
    public CharacterMCard(String name) {
        super(name);
    }    
}

class RoomMCard extends MurderCard{   
    public RoomMCard(String name) {
        super(name);
    }   
}