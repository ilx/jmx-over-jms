package org.logicblaze.lingo.jmx.remote.jms;

import java.io.IOException;
import java.util.Map;

import javax.management.MBeanServer;
import javax.management.remote.JMXServiceURL;

import org.logicblaze.lingo.jmx.remote.provider.jms.JmsJmxConnectorServer;
import org.logicblaze.lingo.jmx.remote.provider.jms.JmsJmxConnectorServerFactory;

public class ActiveMQJmsJmxConnectorServerFactory extends JmsJmxConnectorServerFactory {

    @Override
    public JmsJmxConnectorServer newJmxConnectorServer(JMXServiceURL url, Map<String, ?> environment, MBeanServer server) {
        ActiveMQJmsJmxConnectorServer jmsJmxServer = null;
        try {
            jmsJmxServer = new ActiveMQJmsJmxConnectorServer(url, environment, server);
        } catch (IOException e) {
            throw new IllegalStateException("unable to create ActiveMQJmsJmxConnectorServer", e);
        }

        return jmsJmxServer;
    }

    @Override
    public boolean supports(JMXServiceURL url) {
        boolean retval = true;
        String protocol = url.getProtocol();
        if (!"jms".equals(protocol)) {
            retval = false;
        }
        return retval;
    }

}
