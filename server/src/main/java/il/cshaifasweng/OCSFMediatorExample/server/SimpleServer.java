package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


public class SimpleServer extends AbstractServer {
    private static ArrayList<SubscribedClient> SubscribersList = new ArrayList<>();

    public SimpleServer(int port) {
        super(port);

    }

    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        Message message = (Message) msg;
        String request = message.getMessage();
        try {
            //we got an empty message, so we will send back an error message with the error details.
            if (request.isBlank()) {
                message.setMessage("Error! we got an empty message");
                client.sendToClient(message);
            }
            //we got a request to change submitters IDs with the updated IDs at the end of the string, so we save
            // the IDs at data field in Message entity and send back to all subscribed clients a request to update
            //their IDs text fields. An example of use of observer design pattern.
            //message format: "change submitters IDs: 123456789, 987654321"
            else if (request.startsWith("change submitters IDs:")) {
                message.setData(request.substring(23));
                message.setMessage("update submitters IDs");
                sendToAllClients(message);
            }
            //we got a request to add a new client as a subscriber.
            else if (request.equals("add client")) {
                SubscribedClient connection = new SubscribedClient(client);
                SubscribersList.add(connection);
                message.setMessage("client added successfully");
                client.sendToClient(message);
            }
            //we got a message from client requesting to echo Hello, so we will send back to client Hello world!
            else if (request.startsWith("echo Hello")) {
                message.setMessage("Hello World!");
                client.sendToClient(message);
            } else if (request.startsWith("send Submitters IDs")) {
                //add code here to send submitters IDs to client
                message.setMessage("318699519, 318699519");
                client.sendToClient(message);
            } else if (request.startsWith("send Submitters")) {
                //add code here to send submitters names to client
                message.setMessage("Ameer, Ameer");
                client.sendToClient(message);
            } else if (request.equals("what’s the time?")) {
                //add code here to send the time to client
                message.setMessage("HH:mm:ss");
                client.sendToClient(message);
            } else if (request.startsWith("multiply")) {
                //add code here to multiply 2 numbers received in the message and send result back to client
                int start = request.indexOf(' ') + 1;
                int end = request.indexOf('*');
                int num1 = Integer.parseInt(request.substring(start, end).trim());
                start = end + 1;
                end = request.indexOf(" multiply");
                int num2 = Integer.parseInt(request.substring(start, end).trim());
                // Multiply the two numbers
                int result = num1 * num2;
                message.setMessage("multiply" + "n*m" + "=" + result);
                sendToAllClients(message);
            } else {
                //add code here to send received message to all clients.
                //The string we received in the message is the message we will send back to all clients subscribed.
                //Example:
                // message received: "Good morning"
                // message sent: "Good morning"
                //see code for changing submitters IDs for help
                sendToAllClients(request);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public void sendToAllClients(Message message) {
        try {
            for (SubscribedClient SubscribedClient : SubscribersList) {
                SubscribedClient.getClient().sendToClient(message);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

}
