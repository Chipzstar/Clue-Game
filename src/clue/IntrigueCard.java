/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clue;

/**
 * @author xxlig
 */
class IntrigueCard {

    public enum IntrigueCardType {
        EXTRATURN("Extra Turn"), THROWAGAIN("Throw again"), TELEPORT("Teleport"), AVOIDSUGGESTION("Avoid Suggestion");
        private String representation;

        private IntrigueCardType(String s) {
            this.representation = s;
        }

        @Override
        public String toString() {
            return representation;
        }
    }

    private IntrigueCardType type;

    public IntrigueCard(IntrigueCardType type) {
        this.type = type;
    }

    public IntrigueCardType getType() {
        return type;
    }

}
