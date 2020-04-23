package org.app;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;
import java.util.HashMap;

public class Server {
    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
    private static String mainsubject = "MESSAGEQUEUE";
    public static void main(String[] args) throws JMSException {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        Connection connection = connectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination receiveDestination = session.createQueue(mainsubject);
        MessageConsumer consumer = session.createConsumer(receiveDestination);
        while(true){
            System.out.println("Waiting");
            //get data
            Message recieveMessage = consumer.receive();
            if(recieveMessage instanceof TextMessage){
                TextMessage textMessage = (TextMessage) recieveMessage;
                String connectionID = textMessage.getText().split(" ", 2)[0];
                String data = textMessage.getText().split(" ", 2)[1];

                Destination sendDestination = session.createQueue(connectionID);
                MessageProducer producer = session.createProducer(sendDestination);

                Crawler crawler = new Crawler();
                HashMap<String, String> output = crawler.crawlPageLinks(data);
                if(output.size()==0){
                    TextMessage sendMessage = session.createTextMessage("Nothing can be crawled from URL.");
                    producer.send(sendMessage);
                }
                else {
                    for (String link : output.keySet()) {
                        TextMessage sendMessage = session.createTextMessage(link + ": " + output.get(link));
                        producer.send(sendMessage);
                    }
                }
                producer.send(session.createTextMessage("end"));
//                for(int i = 0; i < output.size(); i++){
//                    if(output.get(i).equals("END")){
//                        TextMessage sendMessage = session.createTextMessage(output[i]);
//                        producer.send(sendMessage);
//                        break;
//                    }
//                    else{
//                        TextMessage sendMessage = session.createTextMessage(output[i]);
//                        producer.send(sendMessage);
//                    }
//                }
                producer.close();
                System.out.println("Sent to: '" + connectionID + "'");
            }
        }
    }
}