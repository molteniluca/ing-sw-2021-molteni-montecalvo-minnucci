package it.polimi.ingsw;

import it.polimi.ingsw.controller.Server;

import java.io.*;
import java.net.Socket;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        Server server= new Server(9000);
    }
}
