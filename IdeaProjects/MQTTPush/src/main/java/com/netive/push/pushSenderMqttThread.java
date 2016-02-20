package com.netive.push;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * Created by netive on 2016. 2. 17..
 */
public class pushSenderMqttThread extends Thread {

    private String topicId;

    private MqttAsyncClient client;
    private String topicStr  = "test/";

    public pushSenderMqttThread(String topicId, MqttAsyncClient client){
        this.topicId = topicId;
        this.client  = client;
        topicStr = topicStr + topicId;
    }

    public void run() {
        processCommand();
    }

    private void processCommand() {
        try {
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

            client.connect(connOpts, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    try {
                        pushTopic();
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                }
            });

            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {

                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {

                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });

            client.disconnect();

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

        private void pushTopic() throws MqttException {
            MqttMessage message = new MqttMessage();
            String messageCon = "A single message " + topicId;
            message.setQos(0);
            message.setPayload(messageCon.getBytes());

            long MqttStartTime = System.currentTimeMillis();

            for ( int i = 0; i < 200; i++) {
                System.out.println( topicId + " i => " + i);
                client.publish(topicStr + Integer.toString(i), message);
            }

            long MqttEndTime = System.currentTimeMillis();
            System.out.println("Mqtt 걸린시간 = " + (MqttEndTime - MqttStartTime) / 1000 + "초");

            MQTTTest.shutdown();
        }

}
