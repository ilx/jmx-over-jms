package net.ilx.jmxjms.demo;

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

public class JMXJMSSlave {

    public static void main(String[] argsv) {

        JMXJMSSlave slave = new JMXJMSSlave();

        try {
            slave.start();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }

    public void start() throws Exception {

        System.out.println("Fetching platform mbean server");
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

        String name = "Fathom1.slave11:type=Test";

        ObjectName oName = ObjectName.getInstance(name);

        System.out.println("Registering " + oName + " with " + mbs);
        mbs.registerMBean(new Test(name), oName);
        System.out.println("Setting up lingo slave");

        // The url to the JMS service
        System.out.println("setting up jmx / jms lingo bridge on port 61616");
        JMXServiceURL serverURL = new JMXServiceURL("service:jmx:jms:///tcp://localhost:61616");
        Map serverEnv = new HashMap();
        serverEnv.put("jmx.remote.protocol.provider.pkgs", "org.logicblaze.lingo.jmx.remote.provider");
        serverEnv.put("destinationServerName", "slave1");
        serverEnv.put("destinationGroupName", "Fathom1");
        JMXConnectorServer connectorServer = null;

        try {
            connectorServer = JMXConnectorServerFactory.newJMXConnectorServer(serverURL, serverEnv, mbs);
            connectorServer.start();
            System.out.println("Lingo setup to bind activemq to platform jmx server");

            Reader reader = new InputStreamReader(System.in);
            LineNumberReader lnr = new LineNumberReader(reader);
            do {

                System.out.println("Type 'quit' to quit");

            } while (!"quit".equals(lnr.readLine()));
        } catch (Throwable t) {
            t.printStackTrace(System.err);
        } finally {
            if (connectorServer != null) {
                connectorServer.stop();
            }
        }

    }
}