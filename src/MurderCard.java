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

    //public String getName(){return name;}

    @Override
    public String toString(){
        return this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MurderCard mCard = (MurderCard) o;
        return this.name == mCard.name;
    }
}

/**
 * Weapon Card subclass
 */
class WeaponMCard extends MurderCard{  
    public WeaponMCard(String name) {
        super(name);
    }   
}

/**
 * Character Card subclass
 */
class CharacterMCard extends MurderCard{   
    public CharacterMCard(String name) {
        super(name);
    }    
}

/**
 * Room Card subclass
 */
class RoomMCard extends MurderCard{   
    public RoomMCard(String name) {
        super(name);
    }   
}