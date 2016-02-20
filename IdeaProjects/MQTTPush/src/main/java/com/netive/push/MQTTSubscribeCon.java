package com.netive.push;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by netive on 2016. 2. 20..
 */
public class MQTTSubscribeCon {
    private static final int MYTHREADS = 240;
    private static String hostUrl      = "localhost:1883";

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(MYTHREADS); //스레드 갯수 중요

        int startNum = 0;
        int endNum   = 240;

        if (args.length > 0 ) {
            hostUrl  = args[0];
            startNum = Integer.parseInt(args[1]);
            endNum   = Integer.parseInt(args[2]);
        }
        for (int i = startNum; i < endNum; i++) {
            Runnable worker = new MyRunnable(Integer.toString(i));
            executor.execute(worker);
        }

        executor.shutdown();

        // Wait until all threads are finish
        while (!executor.isTerminated()) {

        }
        System.out.println("\nFinished all threads");
    }

    public static class MyRunnable implements Runnable {
        private final String topicNum;
        boolean quietMode 	= false;

        MyRunnable(String topicNum) {
            this.topicNum = topicNum;
        }

        @Override
        public void run() {
            MemoryPersistence persistence = new MemoryPersistence();
            try {
                MqttConnectOptions conOpt = new MqttConnectOptions();
                conOpt.setCleanSession(true);

                //conOpt.setConnectionTimeout(-1);
                conOpt.setKeepAliveInterval(60);

                MqttClient client = new MqttClient("tcp://" + hostUrl, "Client" + topicNum, persistence);
                client.connect(conOpt);

                client.setCallback(new ConnectionCallback(topicNum));

                log("Connected to with client ID " + client.getClientId());
                log("Subscribing to topic \"" + "test/" + topicNum);

                client.subscribe("test/" + topicNum, 0);

                // Continue waiting for messages until the Enter is pressed
                log("Press <Enter> to exit");
                try {
                    System.in.read();
                } catch (IOException e) {
                    //If we can't read we'll just exit
                }

                // Disconnect the client from the server
                client.disconnect();
                log("Disconnected");
            } catch (MqttException e) {
                e.printStackTrace();
                log("Unable to set up client: "+e.toString());
                System.exit(1);
            }
        }

        public void messageArrived(String topic, MqttMessage message) throws MqttException {
            // Called when a message arrives from the server that matches any
            // subscription made by the client
            String time = new Timestamp(System.currentTimeMillis()).toString();
            System.out.println("Time:\t" +time +
                    "  Topic:\t" + topic +
                    "  Message:\t" + new String(message.getPayload()) +
                    "  QoS:\t" + message.getQos());
        }

        private void log(String message) {
            if (!quietMode) {
                System.out.println(message);
            }
        }
    }

    private static class ConnectionCallback implements MqttCallback {
        private final String topicNum;

        public ConnectionCallback(String topicNum) {
            this.topicNum = topicNum;
        }

        @Override
        public void connectionLost(Throwable cause) {
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            String time = new Timestamp(System.currentTimeMillis()).toString();
            System.out.println("Time:\t" +time +
                    "  Topic:\t" + topic +
                    "  Message:\t" + new String(message.getPayload()) +
                    "  QoS:\t" + message.getQos());
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
        }
    }
}
