package net.ilx.jmxjms.demo;

import java.util.HashMap;
import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class JMXJMSClient {

    public static void main(String[] avgsv) {

        JMXJMSClient client = new JMXJMSClient();

        try {
            client.start();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }

    public void start() throws Exception {

        // Now connect the client-side
        // The URL to the JMS service
        JMXServiceURL clientURL = new JMXServiceURL("service:jmx:jms:///tcp://localhost:61616");
        Map clientEnv = new HashMap();
        clientEnv.put("jmx.remote.protocol.provider.pkgs", "org.logicblaze.lingo.jmx.remote.provider");

        clientEnv.put("destinationServerName", "slave1");
        clientEnv.put("destinationGroupName", "Fathom1");

        JMXConnector clientConnector = null;

        try {
            clientConnector = JMXConnectorFactory.connect(clientURL, clientEnv);

            MBeanServerConnection connection = clientConnector.getMBeanServerConnection();

            try {
                String name = "Fathom1.container1:type=Test";

                ObjectName oName = ObjectName.getInstance(name);
                Object res = connection.invoke(oName, "toString", null, null);
                System.out.println("invoke of toString on " + oName + " --" + connection + " -- " + res);

            } catch (Exception e) {
                e.printStackTrace();
            }

//            try {
//                String name2 = "Fathom1.slave11:type=Test";
//
//                ObjectName oName2 = ObjectName.getInstance(name2);
//
//                Object res = connection.invoke(oName2, "toString", null, null);
//
//                System.out.println("invoke of toString on " + oName2 + " --" + connection + " -- " + res);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        } finally {

            if (clientConnector != null) {
                clientConnector.close();
            }
        }
    }
}
