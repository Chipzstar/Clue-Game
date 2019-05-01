/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.Random;
/**
 *
 * @author its your boi
 */
public class Dice {
    private Random randomGenerator = new Random();
    private int sides;
    private int noOfDie;
    
    public Dice(int s,int n){
        this.sides = s;
        this.noOfDie = n;
    }
    public int roll() {
        int r = 0;
        for(int i = 0; i < noOfDie; i++){
            r += randomGenerator.nextInt(sides) + 1;  
        }
        return r;   
    }
}
