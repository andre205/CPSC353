/**
 * NimServer.java
 *
 * This program implements a simple multithreaded nim server
 *
 * The NimServer uses a ClientHandler whose code is in a separate file.
 * When a client connects, the NimServer starts a ClientHandler in a separate thread
 * to receive game states from the client.
 *
 * Each client shares the gameArr gamestate and updates it during the game.
 *
 * @author Tyler Andrews, Sofya Bochkareva
 */

import java.net.ServerSocket;
import java.net.Socket;
import java.io.DataOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class NimServer
{
	// Maintain list of all client sockets for broadcast
	private ArrayList<Socket> socketList;
	private int[] gameArr = {1,3,5,7};

	public NimServer()
	{
		socketList = new ArrayList<Socket>();
	}

	private void getConnection()
	{
		// Wait for a connection from the client
		try
		{
			System.out.println("Waiting for client connections on port 7654.");
			ServerSocket serverSock = new ServerSocket(7654);
			int connectionCount = 0;

			Socket Client1 = null;
			Socket Client2 = null;

			while (true)
			{
				//Limit connection to 2 clients
				if (connectionCount < 2)
				{
					Socket connectionSock = serverSock.accept();
					// Add this socket to the list
					socketList.add(connectionSock);
					connectionCount++;

					//First player has connected
					if (connectionCount == 1)
					{
						//Wait a second so the instructions display first
						try
						{
							TimeUnit.SECONDS.sleep(1);
						}
						catch (InterruptedException e){}

						//Client1 = connectionSock.getOutputStream();
						//Client1.writeBytes("You are first!\nPlease wait for another player to connect.\n");
						Client1 = connectionSock;
						DataOutputStream Client1Output = new DataOutputStream(connectionSock.getOutputStream());
						Client1Output.writeBytes("You are first!\nPlease wait for another player to connect.\n\n");
					}

					//Second player has connected
					else if (connectionCount == 2)
					{
						//Wait a second so the instructions display first
						try
						{
							TimeUnit.SECONDS.sleep(1);
						}
						catch (InterruptedException e){}

						//Notify player 2 that they are second
						Client2 = connectionSock;
						DataOutputStream Client2Output = new DataOutputStream(connectionSock.getOutputStream());
						Client2Output.writeBytes("You are second!\nPlease wait for a move from Player 1.\n\n");

						//Notify player 1 that they can make the first move
						DataOutputStream Client1Output = new DataOutputStream(Client1.getOutputStream());
						Client1Output.writeBytes("Player 2 has connected!\n\nYour turn!\n1,3,5,7\nEnter a row number followed by a number to subract (on separate lines)\n");
					}

					// Send to ClientHandler the socket and arraylist of all sockets
					NimClientHandler handler = new NimClientHandler(connectionSock, this.socketList, this.gameArr);
					Thread theThread = new Thread(handler);
					theThread.start();
				}

				//SERVER IS FULL -- ignore user's moves if they choose not to disconnect
				//They will not be added to the game's socket list.
				else
				{
					Socket connectionSock = serverSock.accept();
					DataOutputStream extraClientOutput = new DataOutputStream(connectionSock.getOutputStream());

					//Wait one second to let them know to disconnect
					try
					{
						TimeUnit.SECONDS.sleep(1);
					}
					catch (InterruptedException e){}

					extraClientOutput.writeBytes("-------------\nERROR\nNim server is full! Sorry!\nPlease disconnect.\n-------------\n");
				}
			}
		}

		catch (IOException e)
		{
			System.out.println(e.getMessage());
		}
	}

	public static void main(String[] args)
	{
		NimServer server = new NimServer();
		server.getConnection();
	}
}
