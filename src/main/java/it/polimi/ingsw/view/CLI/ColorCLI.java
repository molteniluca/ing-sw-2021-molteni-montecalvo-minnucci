package it.polimi.ingsw.view.CLI;

import java.util.Objects;

/**
 * Enumeration that is used to handle the colors of the prints in the command line interface
 */
public enum ColorCLI {
    ANSI_RED("\u001B[31m", "Red"),
    ANSI_GREEN("\u001B[32m", "Green"),
    ANSI_YELLOW("\u001B[33m", "Yellow"),
    ANSI_BLUE("\u001B[34m", "Blue"),
    ANSI_PURPLE("\u001B[35m", "Purple"),
    ANSI_WHITE("\u001B[97m", "White"),
    ANSI_BLACK("\u001B[30m", "Black"),
    ANSI_GRAY("\u001B[90m", "Gray"),
    ANSI_BOLD("\033[1m", "Bold");


    static final String RESET = "\u001B[0m"; ///declared as a constant so it is not returned when using the values() method
    static final String CLEAR = "\033[H\033[2J";

    private final String escape;
    private final String friendlyName;

     ColorCLI(String escape, String friendlyName){
        this.escape = escape;
        this.friendlyName = friendlyName;
    }

    public String getEscape()
    {
        return escape;
    }

    public String getFriendlyName() {
        return Objects.requireNonNullElse(friendlyName, "non supported value");
    }

    @Override
    public String toString()
    {
        return escape;
    }
}
