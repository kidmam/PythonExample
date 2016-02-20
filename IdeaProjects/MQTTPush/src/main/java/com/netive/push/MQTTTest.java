package com.netive.push;

/**
 * Created by netive on 2016. 2. 17..
 */
public class MQTTTest {
    public static void main(String[] args)
    {
        new pushNewMigrateAgent().start();
        //shutdown();
    }

    public static void shutdown() {
        System.out.println("Push shutdown.");
        System.exit(0);
    }
}
