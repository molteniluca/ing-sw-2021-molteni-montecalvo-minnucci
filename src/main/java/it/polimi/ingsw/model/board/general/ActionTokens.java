package it.polimi.ingsw.model.board.general;

public enum ActionTokens {
    BLUE("Lorenzo discarded 2 blue cards"),
    GREEN("Lorenzo discarded 2 green cards"),
    PURPLE("Lorenzo discarded 2 purple cards"),
    YELLOW("Lorenzo discarded 2 yellow cards"),
    DOUBLEFAITH("Lorenzo advanced by 2 in his fath track "),
    SHUFFLEFAITH("Lorenzo advanced by 1 in his fath track and shuffled");

    private final String action;

    ActionTokens(String action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return action;
    }
}
