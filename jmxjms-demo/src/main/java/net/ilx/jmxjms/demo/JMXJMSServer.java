package net.ilx.jmxjms.demo;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.activemq.broker.BrokerService;

public class JMXJMSServer {

    /**
     * @param args
     */
    public static void main(String[] args) {

        JMXJMSServer server = new JMXJMSServer();

        try {
            server.start();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            System.exit(0);
        }

    }

    public void start() throws Exception {

        System.out.println("Setting up activemq on port 61616");

        BrokerService broker = new BrokerService();

        // configure the broker
        broker.addConnector("tcp://localhost:61616/container1");
        broker.start();
        broker.waitUntilStarted();

        System.out.println("Active mq started");

        try {

            System.out.println("Fetching platform mbean server");
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

            String name = "Fathom1.container1:type=Test";

            ObjectName oName = ObjectName.getInstance(name);

            System.out.println("Registering " + oName + " with " + mbs);
            mbs.registerMBean(new Test(name), oName);

            Object res = mbs.invoke(oName, "toString", null, null);
            System.out.println("Result of toString on -- " + oName + " -- " + res);

            System.out.println("Setting up lingo slave");

            // The url to the JMS service
            System.out.println("setting up jmx / jms lingo bridge on port 61616");
            JMXServiceURL serverURL = new JMXServiceURL("service:jmx:jms:///tcp://localhost:61616");
            Map serverEnv = new HashMap();
            serverEnv.put("jmx.remote.protocol.provider.pkgs", "org.logicblaze.lingo.jmx.remote.provider");
            serverEnv.put("destinationServerName", "connection1");
            serverEnv.put("destinationGroupName", "Fathom1");

            JMXConnectorServer connectorServer = JMXConnectorServerFactory.newJMXConnectorServer(serverURL, serverEnv, mbs);
            connectorServer.start();
            System.out.println("Lingo setup to bind activemq to platform jmx server");

            loopUntilQuit();

        } catch (Throwable t) {
            t.printStackTrace(System.err);
        } finally {
            broker.stop();

            broker.waitUntilStopped();

        }

    }

    private void loopUntilQuit() throws IOException {
        Reader reader = new InputStreamReader(System.in);
        LineNumberReader lnr = new LineNumberReader(reader);
        do {

            System.out.println("Type 'quit' to quit");

        } while (!"quit".equals(lnr.readLine()));
    }

}