package org.app;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.Scanner;

public class Client {
    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
    private static String mainsubject = "MESSAGEQUEUE";
    public static void main(String[] args) throws JMSException {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        Connection connection = connectionFactory.createConnection();
        connection.start();
        System.out.println("Connected.");
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        String connectionID = "queue-" + connection.getClientID();
        Destination sendDestination = session.createQueue(mainsubject);
        MessageProducer producer = session.createProducer(sendDestination);
        Destination receiveDestination = session.createQueue(connectionID);
        MessageConsumer consumer = session.createConsumer(receiveDestination);
        String msg;
        Scanner scanner = new Scanner(System.in);
        while(true) {
            System.out.print("Enter an url: ");
//            msg = "http://activemq.apache.org/download.html";
            msg = scanner.nextLine();
            msg = connectionID + " " + msg;
            TextMessage sendMessage = session.createTextMessage(msg);
            producer.send(sendMessage);
            System.out.println("Sent: '" + msg + "'");
            // wait answer
            System.out.println("Result:");
            while(true) {
                Message receivedMessage = consumer.receive();
                if (receivedMessage instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) receivedMessage;
                    String receivedTextMessage = textMessage.getText();
                    if(receivedTextMessage.equalsIgnoreCase("end")){
                        break;
                    }
                    else System.out.println(textMessage.getText());
                }
            }
            System.out.print("Continue? (Y/N) ");
            msg = scanner.nextLine();
            if (msg.equals("N") || msg.equals("n")) {
                break;
            }
        }
        connection.close();
    }
}
