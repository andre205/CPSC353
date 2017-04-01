/**
*	TCP Client Program
*	Connects to a TCP Server
*	Receives a line of input from the keyboard and sends it to the server
*	Receives a response from the server and displays it.
*
*	@author: Michael Fahy
@	version: 2.1
*/

import java.io.*;
import java.net.*;
class TCPClient {

    public static void main(String argv[]) throws Exception
    {
        String sentence;
        String modifiedSentence;

        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

        Socket clientSocket = null;

		try
		{
			clientSocket = new Socket("localhost", 6789);
		}

		catch(Exception e)
		{
			System.out.println("Failed to open socket connection");
			System.exit(0);
		}

        PrintWriter outToServer = new PrintWriter(clientSocket.getOutputStream(),true);
		    BufferedReader inFromServer =  new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        System.out.print("First Sentence: ");
        sentence = inFromUser.readLine();
        outToServer.println(sentence);
        modifiedSentence = inFromServer.readLine();
        System.out.println("FROM SERVER: " + modifiedSentence);

        System.out.print("Second Sentence: ");
        sentence = inFromUser.readLine();
        outToServer.println(sentence);
        modifiedSentence = inFromServer.readLine();
        System.out.println("FROM SERVER: " + modifiedSentence);

        clientSocket.close();

    }
}
