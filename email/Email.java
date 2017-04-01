/**
*	TCP Email Assignment - PA04
*	Connects to a TCP Server
*	Receives mail variables from user
*	Connects to chapman mail server on port 25 and sends email message
*
*	@author: Tyler Andrews - modified code of Michael Fahy
*/

import java.io.*;
import java.net.*;
class Email {

    public static void main(String argv[]) throws Exception
    {
        String str_to, str_from, str_subject, str_line = "";
        //MAX 50 lines for message
        String[] message = new String[50];
        String modifiedSentence;
        int line_count = 0;

        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

        Socket clientSocket = null;

		try
		{
			clientSocket = new Socket("smtp.chapman.edu", 25);
		}

		catch(Exception e)
		{
			System.out.println("Failed to open socket connection");
			System.exit(0);
		}

        PrintWriter outToServer = new PrintWriter(clientSocket.getOutputStream(), true);
		BufferedReader inFromServer =  new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        //USER INPUT
        System.out.print("Mail from: ");
        str_from = inFromUser.readLine();

        System.out.print("Mail to: ");
        str_to = inFromUser.readLine();

        System.out.print("Subject: ");
        str_subject = inFromUser.readLine();

        System.out.print("Message body (to end message, type 'END' on its own line) - MAX 50 lines: ");

        while(true)
        {
            str_line = inFromUser.readLine().toString();

            //Check for END indication
            if(str_line.length() >= 3)
                if (str_line.substring(0,3).equals("END"))
                    break;

            //Append line to message array, increase line count (Do not send if END message)
            message[line_count] = str_line;
            line_count++;

            //Check if max line count is reached
            if(line_count == 50)
            {
                System.out.println("Max line count reached - sending message as is.");
                break;
            }
        }

        //Recieve ok message from server
        modifiedSentence = inFromServer.readLine();
        System.out.println(modifiedSentence);

        //Send mail
        outToServer.println("HELO chapman.edu");
        modifiedSentence = inFromServer.readLine();
        System.out.println(modifiedSentence);

        outToServer.println("MAIL FROM: " + str_from);
        modifiedSentence = inFromServer.readLine();
        System.out.println(modifiedSentence);

        outToServer.println("RCPT TO: " + str_to);
        modifiedSentence = inFromServer.readLine();
        System.out.println(modifiedSentence);

        outToServer.println("DATA");
        modifiedSentence = inFromServer.readLine();
        System.out.println(modifiedSentence);

        outToServer.println("From: " + str_from);
        outToServer.println("To: " + str_to);
        outToServer.println("Subject: " + str_subject);

        for(int i = 0; i < line_count; ++i)
        {
            outToServer.println(message[i]);
        }

        outToServer.println(".");

        modifiedSentence = inFromServer.readLine();
        System.out.println(modifiedSentence);

        outToServer.println("QUIT");

        clientSocket.close();
    }
}
