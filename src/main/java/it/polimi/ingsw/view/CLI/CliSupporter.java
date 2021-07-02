package it.polimi.ingsw.view.CLI;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.board.personal.PersonalBoard;
import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.cards.DevelopmentCardRequirement;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.cards.specialAbility.*;
import it.polimi.ingsw.model.resources.ResourceTypes;
import it.polimi.ingsw.model.resources.Resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

import static it.polimi.ingsw.model.resources.ResourceTypes.*;
import static it.polimi.ingsw.view.CLI.ColorCLI.*;
import static it.polimi.ingsw.view.CLI.ColoredResources.FAITH;

/**
 * Class thant contains support methods for the CLI, used to make the otiginal class thinner
 */
public class CliSupporter {

    private final BufferedReader input;

    public CliSupporter(BufferedReader input) {
        this.input = input;
    }


    /**
     * It shows the legend of the CLI associating every
     * resource to a colored circle
     */
    void showLegend() {
        System.out.println(RESET+"Legend\tFaith:" + FAITH + " Gold:" + ColoredResources.GOLD +" Shield:" + ColoredResources.SHIELD + " Servant:" + ColoredResources.SERVANT + " Stone:" + ColoredResources.STONE+"\n");
    }


    /**
     * Method that returns resourceTypes associated to number: 1 = GOLD, 2 = SERVANT, 3 = SHIELD, 4 = STONE
     * @param number number from input that you want to convert in resourceTypes
     * @return resourceTypes associated to the number passed
     */
    ResourceTypes numberToResourceType(int number) {
        ResourceTypes resourceType = null;
        switch (number) {
            case 1:
                resourceType = GOLD;
                break;
            case 2:
                resourceType = SERVANT;
                break;
            case 3:
                resourceType = SHIELD;
                break;
            case 4:
                resourceType = STONE;
                break;
        }
        return resourceType;
    }


    /**
     * Method that prints the end of swap section
     */
    void resourceMovedCorrectly() {
        String currentString = null;
        do {
            System.out.println("\nResources moved correctly, press enter to continue...");
            try {
                currentString = input.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        while (!"".equals(currentString));
    }


    /**
     * It clears the screen printing a clear character
     */
    void refresh() {
        System.out.print(ColorCLI.CLEAR);
        System.out.flush();
    }


    /**
     * Method that prints the main title of the game in ASCIIArt
     */
    void printMainTitle() {
        System.out.println(ANSI_YELLOW +
                "888b     d888                   888                                            .d888      8888888b.                            d8b                                                      \n" +
                "8888b   d8888                   888                                           d88P\"       888   Y88b                           Y8P                                                      \n" +
                "88888b.d88888                   888                                           888         888    888                                                                                    \n" +
                "888Y88888P888  8888b.  .d8888b  888888 .d88b.  888d888 .d8888b        .d88b.  888888      888   d88P .d88b.  88888b.   8888b.  888 .d8888b  .d8888b   8888b.  88888b.   .d8888b .d88b.  \n" +
                "888 Y888P 888     \"88b 88K      888   d8P  Y8b 888P\"   88K           d88\"\"88b 888         8888888P\" d8P  Y8b 888 \"88b     \"88b 888 88K      88K          \"88b 888 \"88b d88P\"   d8P  Y8b \n" +
                "888  Y8P  888 .d888888 \"Y8888b. 888   88888888 888     \"Y8888b.      888  888 888         888 T88b  88888888 888  888 .d888888 888 \"Y8888b. \"Y8888b. .d888888 888  888 888     88888888 \n" +
                "888   \"   888 888  888      X88 Y88b. Y8b.     888          X88      Y88..88P 888         888  T88b Y8b.     888  888 888  888 888      X88      X88 888  888 888  888 Y88b.   Y8b.     \n" +
                "888       888 \"Y888888  88888P'  \"Y888 \"Y8888  888      88888P'       \"Y88P\"  888         888   T88b \"Y8888  888  888 \"Y888888 888  88888P'  88888P' \"Y888888 888  888  \"Y8888P \"Y8888  \n" +
                "                                                                                                                                                                                        \n" + RESET);
    }


    /**
     * Method the print the title od a section
     * @param title the string that has to be printed
     */
    void printTitle(String title) {
        System.out.println(RESET+ANSI_BOLD+title+RESET);
    }


    /**
     * Method that prints the name of a player in an ASCIIArt box, using a bold font
     * It also prints if the player is the first one, depending on the inkWell
     * @param player tha player that has to be shown
     */
    void printName(Player player) {
        for(int i = 0; i<player.getName().length()+12; i++)
            System.out.print("=");
        System.out.println("\n"+ANSI_BOLD+player.getName()+RESET+"'s board");
        if(player.isHasInkwell())
            System.out.println(ANSI_PURPLE+"First player"+RESET);
        for(int i = 0; i<player.getName().length()+12; i++)
            System.out.print("=");
    }


    /**
     * Method that prints the resources given
     * @param resources the resources that has to be printed
     * @param mod the way of printing that resources:
     * 'real' prints only shield, stone, gold and servant
     * 'all' prints also faith and blank
     */
     int printResources(Resources resources, String mod) {
        int printedResources=0;

        if(mod.equals("real")) {
            for (ResourceTypes res : EnumSet.of(GOLD, STONE, SHIELD, SERVANT)) //Only real resources are counted
            {
                int amount = resources.getResourceNumber(res);
                if (amount > 0) {
                    System.out.print(amount + "" + selectResourceColor(res) + ", ");
                    printedResources++;
                }
            }
        }

        if(mod.equals("all")){
            for (ResourceTypes res :ResourceTypes.values()) //Only real resources are counted
            {
                int amount = resources.getResourceNumber(res);
                if (amount > 0) {
                    System.out.print(amount + "" + selectResourceColor(res) + ", ");
                    printedResources++;
                }
            }
        }
        System.out.print("\b\b"); //FIXME erase the last comma and the last space but does not work in terminal
        return printedResources;
    }


    /**
     * Method that associates a ResourceType to a ColoredResources
     * @param resource the resource that has to be printed
     * @return the color of the resource
     */
     ColoredResources selectResourceColor(ResourceTypes resource) {
        switch (resource) {
            case GOLD:
                return ColoredResources.GOLD;

            case BLANK:
                return ColoredResources.BLANK;

            case FAITH:
                return ColoredResources.FAITH;

            case STONE:
                return ColoredResources.STONE;

            case SHIELD:
                return ColoredResources.SHIELD;

            case SERVANT:
                return ColoredResources.SERVANT;

        }

        return null;
    }


    /**
     * It prints out an ArrayList of cards one by one in the same line
     * @param cards the array of cards that has to be printed
     * @param personalBoard the personal board of the player used to control if the player has
     */
    void printDevelopmentCards(List<DevelopmentCard> cards, PersonalBoard personalBoard) {
        int tabs; //the number of tabs used to format the table
        //PersonalBoard personalBoard = game.getPlayerTurn(playerNumber).getPlayer().getPersonalBoard();

        //Type of the card
        System.out.print("\n");
        for (DevelopmentCard card : cards)
        {
            if(card!=null) {
                switch (card.getType()) {
                    case 'b':
                        System.out.print("Type: " + ANSI_BLUE + "Blue" + RESET + "\t\t\t\t\t\t\t");
                        break;
                    case 'g':
                        System.out.print("Type: " + ANSI_GREEN + "Green" + RESET + "\t\t\t\t\t\t\t"); //33
                        break;
                    case 'p':
                        System.out.print("Type: " + ANSI_PURPLE + "Purple" + RESET + "\t\t\t\t\t\t");
                        break;
                    case 'y':
                        System.out.print("Type: " + ANSI_YELLOW + "Yellow" + RESET + "\t\t\t\t\t\t");
                        break;
                }
            }
            else
                System.out.print("\t\t\t\t\t\t\t\t\t ");
        }

        //Level of the card
        System.out.print("\n");
        for (DevelopmentCard card : cards)
        {
            if(card!=null) {
                System.out.print("Level: " + card.getLevel() + "\t\t\t\t\t\t\t");
            }
            else
                System.out.print("\t\t\t\t\t\t\t\t\t");
        }

        //Cost of the card
        System.out.print("\n");
        for(DevelopmentCard card : cards)
        {
            if(card!=null) {
                Resources cost = personalBoard.handleDiscount(card.getCost());
                System.out.print("Cost: ");
                tabs = printResources(cost, "real");
                switch (tabs) {
                    case 1:
                        System.out.print("\t\t\t\t\t\t\t");
                        break;
                    case 2:
                        System.out.print("\t\t\t\t\t");
                        break;
                    case 3:
                        System.out.print("\t\t\t\t");
                        break;
                }
            }
            else
                System.out.print("\tNO CARDS   \t\t\t\t\t\t");
        }

        //Production cost
        System.out.print("\n");
        for(DevelopmentCard card : cards)
        {
            if(card!=null) {
                Resources productionCost = card.getProductionCost();
                System.out.print("Production Cost: ");
                tabs = printResources(productionCost, "real");

                switch (tabs) {
                    case 1:
                        System.out.print("\t\t\t\t");
                        break;
                    case 2:
                        System.out.print("\t\t\t");
                        break;
                    case 3:
                        System.out.print("\t\t");
                        break;
                }
            }
            else
                System.out.print("   \t\t\t\t\t\t\t\t\t");
        }

        //Production power
        System.out.print("\n");
        for(DevelopmentCard card : cards)
        {
            if(card!=null) {
                Resources productionPower = card.getProductionPower();
                System.out.print("Production Power: ");
                tabs = printResources(productionPower, "all");

                switch (tabs) {
                    case 1:
                        System.out.print("\t\t\t\t");
                        break;
                    case 2:
                        System.out.print("\t\t");
                        break;
                    case 3:
                        System.out.print("\t");
                        break;
                }
            }
            else
                System.out.print("   \t\t\t\t\t\t\t\t\t");
        }

    }


    /**
     * It prints out an ArrayList of LeaderCards in different lines
     * @param cards the array of cards that has to be printed
     */
    void printLeaderCards(ArrayList<LeaderCard> cards) {
        Resources resourceRequirements;
        DevelopmentCardRequirement levelRequirements;
        List<DevelopmentCardRequirement> colorRequirements;
        SpecialAbility specialAbility;

        for(LeaderCard card : cards)
        {
            resourceRequirements = card.getResourceRequirements();
            levelRequirements = card.getDevelopmentCardRequirementWithLevel();
            colorRequirements = card.getDevelopmentCardRequirementOnlyColor();
            specialAbility = card.getSpecialAbility();

            System.out.print(ANSI_BOLD+"Requirements: "+RESET);

            if(resourceRequirements!=null) {
                printResources(resourceRequirements, "real");
                System.out.print("\t  ");
            }

            if(levelRequirements!=null)
            {
                switch (levelRequirements.getType())
                {
                    case 'b':
                        System.out.print(ANSI_BLUE + "🁢"+ RESET);
                        break;
                    case 'g':
                        System.out.print(ANSI_GREEN + "🁢" + RESET);
                        break;
                    case 'p':
                        System.out.print(ANSI_PURPLE + "🁢" + RESET);
                        break;
                    case 'y':
                        System.out.print(ANSI_YELLOW+ "🁢" +RESET);
                        break;
                }
                System.out.print("level " + levelRequirements.getLevel()+"");
            }


            if(colorRequirements!=null)
            {
                ArrayList<Character> tmpType = new ArrayList<>();
                ArrayList<Character> tmp2Type;

                for(DevelopmentCardRequirement cardRequirement: colorRequirements)
                    tmpType.add(cardRequirement.getType());

                tmp2Type = new ArrayList<>(tmpType);

                Set<Character> set = new HashSet<>(tmpType); //used to remove every duplicated object
                tmpType.clear();
                tmpType.addAll(set);

                for(Character car1 : tmpType)
                {
                    switch (car1)
                    {
                        case 'b':
                            System.out.print(ANSI_BLUE + "🁢" + RESET);
                            break;
                        case 'g':
                            System.out.print(ANSI_GREEN + "🁢" + RESET);
                            break;
                        case 'p':
                            System.out.print(ANSI_PURPLE + "🁢" + RESET);
                            break;
                        case 'y':
                            System.out.print(ANSI_YELLOW+ "🁢" +RESET);
                            break;
                    }
                    System.out.print(Collections.frequency(tmp2Type, car1) + "  ");

                }
            }

            System.out.print(ANSI_BOLD+"  Special ability: "+RESET);

            if(specialAbility.getClass() == Discount.class)
                System.out.print("Discount: your development cards will cost 1 " +((Discount) specialAbility).getResourceDiscount().toString() + " less");

            if(specialAbility.getClass() == ExtraDeposit.class)
                System.out.print("Extra deposit: you can have 2 extra "+ ((ExtraDeposit) specialAbility).getResourceType().toString()+ " deposits");

            if(specialAbility.getClass() == ExtraProduction.class)
                System.out.print("Extra production: you can use 1 "+((ExtraProduction) specialAbility).getProductionCost()+" to produce 1 resource and 1 faith point");

            if(specialAbility.getClass() == ExtraResource.class)
                System.out.print("Extra resource: all the BLANK resources you get from market will become "+((ExtraResource) specialAbility).getResource());

            System.out.print("\n");
        }
    }


    /**
     * Method that returns a correct integer input for a particular request
     * @param request The string that has to be print, spaces and columns are required in the string
     * @param min minimum value of the range
     * @param max maximum value of the range
     * @return a correct integer in the range
     */
    int integerInput(String request, int min, int max) {
        int value = -1;
        do{
            try{
                System.out.print(request);
                value = Integer.parseInt(input.readLine());
                if(value<min || value> max)
                    throw new NumberFormatException();
            }catch (IOException | NumberFormatException e )
            {
                wrongInput();
            }
        }while (value<min || value >max);

        return value;
    }


    /**
     * It notifies the user about a wrong input
     */
    void wrongInput() {
        System.out.println(ANSI_RED+"Wrong input, retry"+RESET);
    }
}
