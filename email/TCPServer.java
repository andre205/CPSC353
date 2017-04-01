/**
*	TCP Server Program
*	Listens on a TCP port
*	Receives a line of input from a TCP client
*	Returns an upper case version of the line to the client
*
*	@author: Michael Fahy
@	version: 2.0
*/
import java.io.*;
import java.net.*;

class TCPServer {

  public static void main(String argv[]) throws Exception
    {
		String clientSentence;
		String capitalizedSentence;

		ServerSocket welcomeSocket = null;

		try
		{
			welcomeSocket = new ServerSocket(6789);
		}

		catch(Exception e)
		{
			System.out.println("Failed to open socket connection");
			System.exit(0);
		}


      while(true)
      {

           Socket connectionSocket = welcomeSocket.accept();

           BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
    	   PrintWriter outToClient = new PrintWriter(connectionSocket.getOutputStream(),true);

           clientSentence = inFromClient.readLine();
           capitalizedSentence = clientSentence.toUpperCase();
           outToClient.println(capitalizedSentence);

           clientSentence = inFromClient.readLine();
           capitalizedSentence = clientSentence.toUpperCase();
           outToClient.println(capitalizedSentence);
        }
    }
}
