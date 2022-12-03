//testing this
import java.io.*;
import java.net.*;
class TCPClient {

    public static void main(String argv[]) throws Exception
    {
        // Start client
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Please enter the destination IP: ");
        String serverIP = inFromUser.readLine();
        
        // Establish connection with server
        Socket clientSocket = new Socket(serverIP, 6789);
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        // Wait for socket connection
        while(!clientSocket.isConnected()) {
            // Do nothing
        }
        System.out.println("Connected to " + serverIP);

        // Send and recieve messages
        String userInput = "";
        String response = "";
        while (userInput != "exit") {
            // Read input and send to server
            System.out.print("Enter an operation: ");
            userInput = inFromUser.readLine();
            outToServer.writeBytes(userInput + '\n');

            // Print server response
            response = inFromServer.readLine();
            System.out.println("FROM SERVER: " + response);
        }

        clientSocket.close();
        System.exit(0);
    }
}