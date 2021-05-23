package it.polimi.ingsw.view.CLI;

public enum ColoredResources {
    FAITH( ColorCLI.ANSI_RED +" ● "+ColorCLI.RESET),
    BLANK(ColorCLI.ANSI_WHITE + " ● " +ColorCLI.RESET),
    GOLD(ColorCLI.ANSI_YELLOW + " ● " + ColorCLI.RESET),
    SHIELD(ColorCLI.ANSI_BLUE + " ● " + ColorCLI.RESET),
    STONE(ColorCLI.ANSI_GRAY + " ● " + ColorCLI.RESET),
    SERVANT(ColorCLI.ANSI_PURPLE + " ● "+ ColorCLI.RESET);

    private String escape;

    ColoredResources(String escape)
    {
        this.escape = escape;
    }

    @Override
    public String toString()
    {
        return escape;
    }

}
