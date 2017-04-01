/**
 * NimClientHandler.java
 *
 * This class handles communication between the client
 * and the server.  It runs in a separate thread but has a
 * link to a common list of sockets to handle broadcast, as well
 * as the current state of the game.
 *
 * The handler receievs a string in the form of int,int and interprets
 * it as a row number,number of sticks to substract. If possible, it
 * udpates the game accordingly and send the update to the other client.
 *
 * @author Tyler Andrews, Sofya Bochkareva
 */

import java.net.Socket;
import java.io.DataOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;

public class NimClientHandler implements Runnable
{
	private Socket connectionSock = null;
	private ArrayList<Socket> socketList;
	private int[] gameArr;
	private int invalidMove = 0;

	NimClientHandler(Socket sock, ArrayList<Socket> socketList, int[] gameArr)
	{
		this.connectionSock = sock;
		this.socketList = socketList;	// Keep reference to master list
		this.gameArr = gameArr;
	}

	public void run()
	{
        // Get data from a client and send it to everyone else
		try
		{
			System.out.println("Connection made with socket " + connectionSock);
			BufferedReader clientInput = new BufferedReader(new InputStreamReader(connectionSock.getInputStream()));

			while (true)
			{
				// Get data sent from a client
				String clientText = clientInput.readLine();
				if (clientText != null)
				{
					invalidMove = 0;
					System.out.println("Received: " + clientText);
					// Turn around and output this data
					// to all other clients except the one
					// that sent us this information

					//take in client string and split into 2 strings
					String[] moveStrings = clientText.split(",");
					//convert those strings to ints and update the game state
					int moveRow = Integer.parseInt(moveStrings[0]);
					int moveSub = Integer.parseInt(moveStrings[1]);

					//check game state for validity of move
					//only take away sticks if possible
					//incorrect moves are ignored
					if(gameArr[moveRow-1] >0)
					{
						if(gameArr[moveRow-1] - moveSub >= 0)
							gameArr[moveRow-1] -= moveSub;
						else
							invalidMove = 1;
					}
					else
						invalidMove = 1;

					for (Socket s : socketList)
					{
						if (s != connectionSock)
						{
							DataOutputStream clientOutput = new DataOutputStream(s.getOutputStream());
							//send game state to client that did NOT make change
							clientOutput.writeBytes(gameArr[0] + "," + gameArr[1] + "," + gameArr[2] + "," + gameArr[3] +  "\n");

							//send victory message to player who DIDNT take the last stick
							clientOutput.writeBytes(checkArr(0, gameArr));

							//If the game isn't over, tell the other player to make a move.
							if(checkArr(0, gameArr) == "")
								clientOutput.writeBytes("Your turn!\nEnter a row number followed by a number to subract (on separate lines) \n");
						}

						//Send lose message to the player who DID take the last stick
						else
						{
							DataOutputStream clientOutput = new DataOutputStream(s.getOutputStream());
							clientOutput.writeBytes(checkArr(1, gameArr));

							//Notify player if their move was invalid
							if (invalidMove == 1)
								clientOutput.writeBytes("Invalid move ignored. Try again! \n");
						}
					}
				}
				else
				{
				  // Connection was lost
				  System.out.println("Closing connection for socket " + connectionSock);
				   // Remove from arraylist
				   socketList.remove(connectionSock);
				   connectionSock.close();
				   break;
				}
			}
		}
		catch (Exception e)
		{
			System.out.println("Error: " + e.toString());
			// Remove from arraylist
			socketList.remove(connectionSock);
		}
	}

	public static String checkArr(int state, int[] gameArr)
	{
		boolean keepRunning = false;
		for(int i = 0; i<4; ++i)
		{
			//if there is still at least one matchstick left, keep the loop running.
			if(gameArr[i] >= 1)
			{
				keepRunning = true;
			}
		}
		//int state = 0 is a check at the start of a turn.
		//This checks to see if a player has won.
		if(!keepRunning && state == 0)
		{
			return "You won! Press Ctrl+Z to exit. \n";
		}
		//If a player has taken the final piece, they have lost.
		if(!keepRunning && state == 1)
		{
			return "You lost! Press Ctrl+Z to exit. \n";
		}

		else return "";
	}
}
