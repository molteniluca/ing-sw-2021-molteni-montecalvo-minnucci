package it.polimi.ingsw.view;

//used to implement an observable interface

public interface View {

    void initializeView();

    void welcomeInfo();

    void askCreateOrJoin();

    void askServerInfo();

    void askNickname();

}
