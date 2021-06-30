package it.polimi.ingsw.model.board.general;

public enum ActionTokens {
    BLUE("Lorenzo discarded 2 blue cards", "images/ActionTokens/blueToken.png"),
    GREEN("Lorenzo discarded 2 green cards", "images/ActionTokens/greenToken.png"),
    PURPLE("Lorenzo discarded 2 purple cards", "images/ActionTokens/purpleToken.png"),
    YELLOW("Lorenzo discarded 2 yellow cards", "images/ActionTokens/yellowToken.png"),
    DOUBLEFAITH("Lorenzo advanced by 2 in his faith track ", "images/ActionTokens/doubleFaithToken.png"),
    SHUFFLEFAITH("Lorenzo advanced by 1 in his faith track and shuffled", "images/ActionTokens/shuffleFaithToken.png");

    private final String action;
    private final String tokenImage;

    ActionTokens(String action, String tokenImage) {
        this.action = action;
        this.tokenImage = tokenImage;
    }

    @Override
    public String toString() {
        return action;
    }

    public String getTokenImage() {
        return tokenImage;
    }
}
