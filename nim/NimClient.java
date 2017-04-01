/**
 * NimClient.java
 *
 * This program implements a simple multithreaded chat client.  It connects to the
 * server (assumed to be localhost on port 7654) and starts two threads:
 * one for listening for data sent from the server, and another that waits
 * for the user to type something in that will be sent to the server.
 * This message is then interpreted by the server.
 *
 * The NimClient uses a ClientListener whose code is in a separate file.
 * The ClientListener runs in a separate thread, recieves messages form the server,
 * and displays them on the screen.
 *
 * Data received is sent to the output screen, so it is possible that as
 * a user is typing in information a message from the server will be
 * inserted.
 *
 * @author Tyler Andrews, Sofya Bochkareva
 */

import java.net.Socket;
import java.io.DataOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Scanner;

public class NimClient
{
	public static void main(String[] args)
	{
		try
		{
			// Initialize connection
			String hostname = "localhost";
			int port = 7654;
			System.out.println("Connecting to server on port " + port);
			Socket connectionSock = new Socket(hostname, port);

			System.out.println("Connection made.");

			// Start a thread to listen and display data sent by the server
			NimClientListener listener = new NimClientListener(connectionSock);
			Thread theThread = new Thread(listener);
			theThread.start();

			run(connectionSock);
			// Runs the game itself
			//NimClientInput game = new NimClientInput(connectionSock);
			//Thread inputThread = new Thread(run());
			//inputThread.start();

		}
		catch (IOException e)
		{
			System.out.println(e.getMessage());
		}
	}

	public static void run(Socket connectionSock)
	{
		try
		{
			DataOutputStream serverOutput = new DataOutputStream(connectionSock.getOutputStream());

			printInstructions();

			// Initialize variables.
			int row = 0;
			int sub = 0;
			int[] gameArr = {1,3,5,7};

			Scanner keyboard = new Scanner(System.in);

			while (true)
			{
				//get row input
				do {
					while (!keyboard.hasNextInt())
					{
						System.out.println("Wrong input! Enter 1-4.");
						keyboard.next();
					}
					row = keyboard.nextInt();
				} while (row < 1 || row > 4);

			    //given row input, get subtraction value.
				do {
					while (!keyboard.hasNextInt())
					{
						System.out.println("Wrong input! Enter a valid number.");
						keyboard.next();
					}
					sub = keyboard.nextInt();
				} while (sub < 0 || sub > gameArr[row-1]);

				//Send change of game state to server
				serverOutput.writeBytes(row + "," + sub + "\n");

				System.out.println("Move sent! Please wait for the other player to make a move. \n");
			}
		}

		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	public static void printInstructions()
	{
		System.out.println("-----------------------------------------");
		System.out.println("Welcome to the game of NIM!");
		System.out.println("To play, first type a row number, 1-4.");
		System.out.println("Then type a number of matches to subtract from that row.");
		System.out.println("There are four rows containting 1, 3, 5, and 7 matches respectively.");
		System.out.println("-----------------------------------------");
	}
}
