import java.beans.Expression;
import java.io.*;
import java.net.*;
import java.util.Vector;

class TCPServer {

    public static void main(String argv[]) throws Exception
    {
        Vector<Client> clientsVector = new Vector<>();

        ServerSocket welcomeSocket = new ServerSocket(6789);

        while(true) {
            Socket connectionSocket = welcomeSocket.accept();
            Client c = new Client(connectionSocket);
            clientsVector.add(c);

        }
    }
}

class Client {
    String clientName;
    String connectionTime;
    BufferedReader inFromClient;
    DataOutputStream  outToClient;
    int solution;


    public Client(Socket connectionSocket) throws Exception {


        inFromClient =
                new BufferedReader(new
                        InputStreamReader(connectionSocket.getInputStream()));

        outToClient =
                new DataOutputStream(connectionSocket.getOutputStream());

        clientName = inFromClient.readLine();

        while(true) {
            String[] clientSentence = inFromClient.readLine().split(" ");


            if (clientSentence.length != 3) {
                // Throw exection if more than 3 terms
                System.out.println("Terms Error.");
            }

            if (clientSentence[1].length() != 1) {
                // Throw exection if operand is not a single character
                System.out.println("Operator Error.");
            }

            // Parse input and perform given operation
            try {
                System.out.println("Beginning operation...");

                int operand1 = Integer.parseInt(clientSentence[0]);
                char operator = clientSentence[1].charAt(0);
                int operand2 = Integer.parseInt(clientSentence[2]);

                if (operator == '+') {
                    solution = operand1 + operand2;
                }
                else if (operator == '-') {
                    solution = operand1 - operand2;
                }
                else if (operator == '*') {
                    solution = operand1 * operand2;
                }
                else if (operator == '/') {
                    solution = operand1 / operand2;
                }
                else {
                    // Throw exection if unsupported operator
                    System.out.println("Operator Error.");
                }

                outToClient.writeBytes(Integer.toString(solution) + '\n');
                System.out.println("Operation completed.");
                System.out.println(clientName);


            }
            catch (NumberFormatException e) {
                // Throw exception if terms are not formatted correctly
                System.out.println("Operand Error.");
            }


        }
        }







    }





 

           