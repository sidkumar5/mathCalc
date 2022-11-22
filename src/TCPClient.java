//testing this
import java.io.*;
import java.net.*;

class TCPClient {

    public static void main(String argv[]) throws Exception
    {
        String sentence;
        String name;
        String modifiedSentence;

        BufferedReader inFromUser =
                new BufferedReader(new InputStreamReader(System.in));

        Socket clientSocket = new Socket("127.0.0.1", 6789);

        DataOutputStream outToServer =
                new DataOutputStream(clientSocket.getOutputStream());

        BufferedReader inFromServer =
                new BufferedReader(new
                        InputStreamReader(clientSocket.getInputStream()));

        System.out.println("Please enter your name: ");
        name = inFromUser.readLine();

        outToServer.writeBytes(name + '\n');



        System.out.println("Thank you! Connection was successful.");
        while(true) {
            System.out.println("Enter an operation: ");
            sentence = inFromUser.readLine();
            System.out.println(sentence);

            outToServer.writeBytes(sentence + '\n');

            modifiedSentence = inFromServer.readLine();

            System.out.println("FROM SERVER: " + modifiedSentence);

        }

        clientSocket.close();

    }
}

        