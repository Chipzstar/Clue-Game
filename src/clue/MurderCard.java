/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clue;

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

    @Override
    public boolean equals(Object obj) {
        return (this.name.equals(((MurderCard) obj).getName()));
    }
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

//How to convert RoomTile to RoomMCard
//How to compare Name (string name) of Room in RoomTile with (string name) of RoomMCard??