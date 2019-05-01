/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



import java.awt.Desktop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;

import java.util.HashSet;

import java.util.List;
import java.util.LinkedList;

import java.util.Random;

import java.util.Properties;
import java.util.Scanner;
import java.util.Collections;


public class Game implements GameInterface {
        
    public static void clearScreen() {  
        try
        {
            new ProcessBuilder("cmd","/c","cls").inheritIO().start().waitFor();
        }catch(Exception e){
            System.out.println(e);
        }
    }
    //default config settings
    public enum Settings {
        CONFIG("config.properties"), BOARD("board.csv");
        
        private static final String DEFAULT_CHARACTERS = " Miss Scarlet, Col Mustard, Prof Plum, " 
                                                       + "Mrs White, Rev Green, Mrs Peacock";
        private static final String DEFAULT_WEAPONS = "Dagger, Candle Stick, Revolver, " 
                                                    + "Rope, Spanner, Lead Piping";
        private final String representation;
        
        private Settings(String rep) {
            this.representation = rep;
        }
          
        @Override
        public String toString() {return this.representation;}
    }
    
    private static final int MIN_PLAYERS = 2;
    private static final int MAX_PLAYERS = 6;
    
    private Scanner input;
    private Properties config;
    
    public Board b;
    public Dice d;
    
    public ArrayList<Player> playerList;
    public LinkedList<MurderCard> solution;
    public LinkedList<IntrigueCard> intrigueDeck;
    public int currentPlayer;
    
    public static Random rand = new Random();
    
    //loads config file
    public Game() throws FileNotFoundException, IOException {
        this.input = new Scanner(System.in);
        this.config = new Properties();
        this.d = new Dice(6, 2);
        this.playerList = new ArrayList<>();
        this.solution = new LinkedList<>();
        this.intrigueDeck = new LinkedList<>();
        this.currentPlayer = 0;
        try {
            FileInputStream in = new FileInputStream(new File("config"));
            this.config.load(in);
            in.close();
            menu();
        }
        //creates a new config file if one cannot be found
        catch(FileNotFoundException e) {
            loadDefaults(Settings.CONFIG);
        }
        
    }
    
    //can remove for javafx
    private void menu() throws IOException {
        while(true) {
            clearScreen();
            String menu =  "1. New Game\n"
                         + "2. Load Game\n"
                         + "3. Edit Settings\n"    
                         + "4. Reset Settings\n"
                         + "5. Quit";
            System.out.println(menu);
            switch(input.nextInt()) {
                case 1: initialise(); new Game().menu(); break;
                
                case 2: //load and save games?
                //opens config and board csv files in user systems selected editors (e.g. notepad and excel)
                //see readme
                case 3:
                    for(Settings setting: Settings.values()) {
                        try {
                            Desktop.getDesktop().edit(new File(setting.toString()));
                        }
                        catch(IOException e) {
                            System.out.println("Invalid File Or File Could Not Be Found!\n"
                                              +"Loading Default.");
                            loadDefaults(setting);
                        }
                    } break;
                case 4: loadDefaults(Settings.CONFIG);  break;
                case 5: System.exit(0);
                default: System.out.println("Invalid Choice. Select Again."); break;
            }
            

            
        }
    }
    
    private void loadDefaults(Settings setting) throws FileNotFoundException, IOException {
        switch(setting) {
            case CONFIG:
                FileOutputStream out = new FileOutputStream("config");
                this.config.setProperty("Characters", Settings.DEFAULT_CHARACTERS);
                this.config.setProperty("Weapons", Settings.DEFAULT_WEAPONS);
                config.store(out, "##");
                out.close();
            case BOARD://I need to come back to this
        }
    }
    
    public void initialise() throws FileNotFoundException {
        //Loads config, loads defaults if characters and/or weapons are missing
        //getPropeerty() returns String[], so is converted to fixed-size list, 
        //fixed array lists can,t have anything removed so is used to constuct arraylists
        List<String> characters = new ArrayList<>(Arrays.asList(config.getProperty("Characters", Settings.DEFAULT_CHARACTERS).split(",")));
        List<String> weapons = new ArrayList<>(Arrays.asList(config.getProperty("Weapons", Settings.DEFAULT_WEAPONS).split(",")));
        ArrayList<MurderCard> mCards;
        intialiseIntrigue();
        this.b = new Board();
        //System.out.println(b.toString());
        
        int numPlayers;
        int numAI = 0;
        //makes sure player and ai number is between 2 and 6        
        do {
            System.out.print("Number of players: ");
            numPlayers = getInput(0, MAX_PLAYERS, "Ivalid number. Try Again.");
            if(numPlayers < MAX_PLAYERS) { 
                System.out.print("Number of ai: ");
                numAI = getInput(0, MAX_PLAYERS-numPlayers, "Invalid number. Try Again.");
            }
        } while(numPlayers+numAI < MIN_PLAYERS || numPlayers+numAI > MAX_PLAYERS);
        
        mCards = genCards(b.getRooms(), "room");
        mCards.addAll(genCards(weapons, "weapon"));
        mCards.addAll(genCards(characters, "character"));

        int index = characters.size();
        
        for(int i = 0; i < numPlayers; i++) {
            int charNum=0;
            for(String character : characters) {
                charNum++;
                System.out.println((charNum) + "." + character);
            }   
            System.out.print("Select Character for Player " + (i+1) + ": ");
            int j = getInput(1, characters.size(), "Invalid choice. Try Again.");
            playerList.add(new Human(characters.remove(j-1), new HashSet<>(), 
                           b.startTiles.remove(0), this, new DetectiveCard(mCards)));
        }
        for(int i = 0; i < numAI; i++) {
            playerList.add(new AI(characters.remove(rand.nextInt(characters.size())), new HashSet<>(), 
                            b.startTiles.remove(0), this, new DetectiveCard(mCards),AI.Difficulty.EASY));
        }
        deal(mCards);
        d = new Dice(6, 2);
        playGame();
    }
    
    private ArrayList<MurderCard> genCards(List<String> names, String type) {
        ArrayList<MurderCard> cards = new ArrayList<>();
        names.forEach((String name) -> {
            if("room".equals(type)) cards.add(new RoomMCard(name));
            if("weapon".equals(type)) cards.add(new WeaponMCard(name));
            if("character".equals(type)) cards.add(new CharacterMCard(name));
        });
        int r = rand.nextInt(cards.size());
//        System.out.println(r);
        solution.add(cards.get(r));
        return cards;
    }
    
    private void deal(ArrayList<MurderCard> cards) {
        int highest = 0;
        int dealer = 0;
        int index;
        cards.removeAll(solution); // removes solution cards from deck 
        for(int i = 0; i < playerList.size(); i++) {
            if(d.roll() > highest) dealer = i;
        }
        index = dealer;
        for(MurderCard card : cards) {
            playerList.get(index).mCards.add(card);
            index = ++index % playerList.size();
        }
    }
    
    private void intialiseIntrigue(){
        for(int i = 0; i <2;i++){
            intrigueDeck.add(new IntrigueCard(IntrigueCard.IntrigueCardType.AVOIDSUGGESTION));
            intrigueDeck.add(new IntrigueCard(IntrigueCard.IntrigueCardType.EXTRATURN));
            intrigueDeck.add(new IntrigueCard(IntrigueCard.IntrigueCardType.TELEPORT));
            intrigueDeck.add(new IntrigueCard(IntrigueCard.IntrigueCardType.THROWAGAIN));
        }
        Collections.shuffle(intrigueDeck);
    }
    
    private int getInput (int minVal, int maxVal, String message){
        int val;
        val = input.nextInt();
        while(val<minVal||val>maxVal){
            System.out.print(message);
            System.out.print("->");
            val = input.nextInt();
        }
        return val;
    }
    
    private void playGame(){
         for(MurderCard m:solution){
               System.out.println(m.name+" "+ m.getClass());
          }
              int i = 0;
        do{
            playerList.get(0).doTurn();
            System.out.println(playerList.get(0).toString());
            Collections.rotate(playerList, 1);
            i++;
        }while(playerList.size()>1 && i<10);
        System.out.println(playerList.get(0).name + " is the WINNER");
         for(MurderCard m:solution){
               System.out.println(m.name+" "+ m.getClass());
          }
         
    }
    
    public void doSuggestion( ArrayList<MurderCard> suggestion){
        ArrayList<MurderCard> cardsRevealed = new ArrayList<>();
        for(int i = 1; i<playerList.size();i++){
            cardsRevealed.add(playerList.get(i).answerSuggestion(suggestion));
            if(suggestion.get(2).name.equals(playerList.get(i).name)){
                playerList.get(i).setPosition(((RoomTile)playerList.get(0).getPosition()).getRoom().getRoomIndex());
            }
        }
        playerList.get(0).revealCards(cardsRevealed);
        
    }
    
    public void doAccusation(ArrayList<MurderCard> accusation){
        if(solution.containsAll(accusation)){
            System.out.println(playerList.get(0).name + " has guessed correctly");
            for(int i = 1;i< playerList.size();i++){
                playerList.remove(i);
            }
        }
        else{
            System.out.println(playerList.get(0).name + 
                    " has guessed incorrectly and has been removed from the game");
            playerList.remove(0);
            Collections.rotate(playerList, -1);
        }
    }

    public void showBoard(){
        System.out.print(b.toStringWithPlayers(playerList));
    }
    
        public static void main(String[] args) throws IOException {
        new Game();
        }
    }

