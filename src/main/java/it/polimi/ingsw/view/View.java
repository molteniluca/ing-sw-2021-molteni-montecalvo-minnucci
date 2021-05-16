package it.polimi.ingsw.view;

public interface View {

    public void welcomeInfo();

    public void askCreateOrJoin() throws Exception;

    public void askServerInfo();

    public void askNickname();
}
