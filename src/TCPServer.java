import java.io.*;
import java.net.*;
import java.sql.Timestamp;
import java.time.Instant;
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

        // Create log file
        try {
            BufferedWriter f_writer = new BufferedWriter(new FileWriter("log.txt"));

            f_writer.write(Timestamp.from(Instant.now()) + "\tClient " + clientName + " has connected.\n");
            
            // Recieve and solve expressions
            while(true) {
                String cSentence = inFromClient.readLine();
                f_writer.write(Timestamp.from(Instant.now()) + "\tClient " + clientName + " has delivered message \"" + cSentence + "\".\n");
                
                if (cSentence.equals("exit")) {
                    // Disconnect from client
                    f_writer.write(Timestamp.from(Instant.now()) + "\tClient " + clientName + " has requested to disconnect.\n");
                    break;
                }
                else if (cSentence.equals("help")) {
                    // Display help message
                    outToClient.writeBytes("To use the calculator, please enter a simple expression with 2 operands and an operator separated by white space. (i.e. 2 + 3).\n");
                    f_writer.write(Timestamp.from(Instant.now()) + "\tClient " + clientName + " has requested help.\n");
                }
                else {
                    // Solve expression
                    String[] clientSentence = cSentence.split(" ");
    
                    if (clientSentence.length != 3) {
                        outToClient.writeBytes("Expression is not 3 terms or not properly formatted (i.e. 2 + 3).\n");
                        f_writer.write(Timestamp.from(Instant.now()) + "\tClient " + clientName + " was sent an expression formatting error.\n");
                    }
                    else if (clientSentence[1].length() != 1) {
                        outToClient.writeBytes("Operator is not a single character.\n");
                        f_writer.write(Timestamp.from(Instant.now()) + "\tClient " + clientName + " was sent an operator length error.\n");
                    }
                    else {
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
                                outToClient.writeBytes("Operator is not supported (must be + - * /).\n");
                                f_writer.write(Timestamp.from(Instant.now()) + "\tClient " + clientName + " was sent an unsupported operator error.\n");
                            }
                        }
                        catch (NumberFormatException e) {
                            outToClient.writeBytes("Operands are not valid integers.\n");
                            f_writer.write(Timestamp.from(Instant.now()) + "\tClient " + clientName + " was sent an invalid operand error.\n");
                        }
                    }
                }
            }
            f_writer.write(Timestamp.from(Instant.now()) + "\tClient " + clientName + " has been disconnected.\n");

            f_writer.close();
        }
        catch (IOException e) {
            System.out.print("Error writing log file: " + e.getMessage());
        }
    }
}
