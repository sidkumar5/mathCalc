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
        inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
        outToClient = new DataOutputStream(connectionSocket.getOutputStream());
        clientName = inFromClient.readLine();

        while(true) {
            /*
             * TO-DO:
             * Input of 'help' returns usage information
             * Input of 'exit', 'quit', or 'close' closes connection with client
             */

            String cSentence = inFromClient.readLine();

            if(cSentence.equals("exit")) {
                System.out.println("Client " + clientName + " has disconnected");
                break;
            } else if(cSentence.equals("help")) {
                outToClient.writeBytes("To use the calculator, please enter a simple expression with 2 operands and an operator separated by white space. (i.e. 2 + 3).\n");
            }  else {

                String[] clientSentence = cSentence.split(" ");

                if (clientSentence.length != 3) {
                    outToClient.writeBytes("Expression is not 3 terms or not properly formatted (i.e. 2 + 3).\n");
                } else if (clientSentence[1].length() != 1) {
                    outToClient.writeBytes("Operand is not a single character.\n");
                } else {
                    // Parse input and perform given operation
                    try {
                        int operand1 = Integer.parseInt(clientSentence[0]);
                        char operator = clientSentence[1].charAt(0);
                        int operand2 = Integer.parseInt(clientSentence[2]);

                        if (operator == '+') {
                            outToClient.writeBytes(Integer.toString(operand1 + operand2) + "\n");
                        } else if (operator == '-') {
                            outToClient.writeBytes(Integer.toString(operand1 - operand2) + "\n");
                        } else if (operator == '*') {
                            outToClient.writeBytes(Integer.toString(operand1 * operand2) + "\n");
                        } else if (operator == '/') {
                            outToClient.writeBytes(Integer.toString(operand1 / operand2) + "\n");
                        } else {
                            outToClient.writeBytes("Operand is not supported (must be + - * /).\n");
                        }
                    } catch (NumberFormatException e) {
                        outToClient.writeBytes("Operands are not valid integers.\n");
                    }
                }
            }
            }
        }
    }
