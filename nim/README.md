PA05 Nim - Tyler Andrews, Sofya Bochkareva

This repo contains programs to implement a multi-threaded Nim game

* NimClient.java handles keyboard input from the user.
* NimClientListener.java recieves responses from the server and displays them.
* NimServer.java listens for client connections and creates a ClientHandler for each new client.
* NimClientHandler.java recieves game state from a client and relays it to the other client(s).

The player that removes the last stick loses!
You must use ctrl+z to exit.

NOTES
Currently, the same user can make 2 moves in a row.
An incorrect/impossible move will be ignored by the server.
Any players after 2 will not be added to the socket list but must close their client manually. 
