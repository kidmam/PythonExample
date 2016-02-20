package com.netive.push;

import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by netive on 2016. 2. 17..
 */
public class pushNewMigrateAgent extends Thread {
    private boolean stopped = false;

    public void run() {
        while ( !stopped ) {
            migrate();
        }
        //MQTTTest.shutdown();
    }

    public void setStop() {
        stopped = true;
    }
    private void migrate() {
        sendPush();
    }

    private void sendPush() {
        ExecutorService gcmExecutor = Executors.newFixedThreadPool(2);

        MemoryPersistence persistence = new MemoryPersistence();
        MqttAsyncClient client = null;
        //for ( int i = 1; i < 3; i++ ) {
            try {
                //if ( i == 1 ) {
                    client = new MqttAsyncClient("tcp://localhost:1883", MqttAsyncClient.generateClientId(), persistence);
                //} else {
                    //client = new MqttAsyncClient("tcp://localhost:1884", MqttAsyncClient.generateClientId(), persistence);
                //}
            } catch (MqttException e) {
                e.printStackTrace();
            }
            Runnable worker = new pushSenderMqttThread(Integer.toString(1), client);
            gcmExecutor.execute(worker);
        //}

        gcmExecutor.shutdown();

        while (!gcmExecutor.isTerminated()) {

        }
        System.out.println("\nFinished all publish threads");
        setStop();
    }
}

