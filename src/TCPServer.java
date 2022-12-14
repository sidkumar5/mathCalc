import java.io.*;
import java.net.*;
import java.sql.Timestamp;
import java.time.Instant;

class TCPServer {

    public static void main(String argv[]) throws Exception
    {
        try (ServerSocket welcomeSocket = new ServerSocket(6789)) {
            // Connect to incomming clients
            while(true) {
                Socket connectionSocket = welcomeSocket.accept();

                Thread t = new Client(connectionSocket);
                t.start();
            }
        }
    }
}

class Client extends Thread {
    SocketAddress clientIP;
    BufferedReader inFromClient;
    DataOutputStream outToClient;
    long connectionStart;

    public Client(Socket connectionSocket) throws Exception {
        inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
        outToClient = new DataOutputStream(connectionSocket.getOutputStream());
        clientIP = connectionSocket.getRemoteSocketAddress();
    }

    @Override
    public void run()  {
        // Recieve and solve expressions
        try {
            BufferedWriter serverLogWriter = new BufferedWriter(new FileWriter("log.txt", true));
            serverLogWriter.write(Timestamp.from(Instant.now()) + "\t(" + clientIP + ") has connected.\n");

            connectionStart = System.currentTimeMillis();

            while(true) {
                String cSentence = inFromClient.readLine();
                serverLogWriter.write(Timestamp.from(Instant.now()) + "\t(" + clientIP + ") has delivered message \"" + cSentence + "\".\n");
                
                if (cSentence.equals("exit")) {
                    // Disconnect from client
                    serverLogWriter.write(Timestamp.from(Instant.now()) + "\t(" + clientIP + ") has requested to disconnect.\n");
                    break;
                }
                else if (cSentence.equals("help")) {
                    // Display help message
                    outToClient.writeBytes("To use the calculator, please enter a simple expression with 2 operands and an operator separated by white space. (i.e. 2 + 3).\n");
                    serverLogWriter.write(Timestamp.from(Instant.now()) + "\t(" + clientIP + ") has requested help.\n");
                }
                else {
                    // Solve expression
                    String[] clientSentence = cSentence.split(" ");

                    if (clientSentence.length != 3) {
                        outToClient.writeBytes("Expression is not 3 terms or not properly formatted (i.e. 2 + 3).\n");
                        serverLogWriter.write(Timestamp.from(Instant.now()) + "\t(" + clientIP + ") was sent an expression formatting error.\n");
                    }
                    else if (clientSentence[1].length() != 1) {
                        outToClient.writeBytes("Operator is not a single character.\n");
                        serverLogWriter.write(Timestamp.from(Instant.now()) + "\t(" + clientIP + ") was sent an operator length error.\n");
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
                                serverLogWriter.write(Timestamp.from(Instant.now()) + "\t(" + clientIP + ") was sent an unsupported operator error.\n");
                            }
                        }
                        catch (NumberFormatException e) {
                            outToClient.writeBytes("Operands are not valid integers.\n");
                            serverLogWriter.write(Timestamp.from(Instant.now()) + "\t(" + clientIP + ") was sent an invalid operand error.\n");
                        }
                    }
                }
            }
            float connectionLength = (System.currentTimeMillis() - connectionStart) / 1000.0f;
            serverLogWriter.write(Timestamp.from(Instant.now()) + "\t(" + clientIP + ") has been disconnected after " + connectionLength + " seconds.\n");
            serverLogWriter.close();
        }
        catch (IOException e) {
            System.out.print("Error writing log file: " + e.getMessage());
        }
    }
}
