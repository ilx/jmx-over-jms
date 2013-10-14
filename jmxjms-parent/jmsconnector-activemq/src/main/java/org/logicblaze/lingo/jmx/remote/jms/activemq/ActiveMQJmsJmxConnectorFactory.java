package org.logicblaze.lingo.jmx.remote.jms.activemq;

import java.io.IOException;
import java.util.Map;

import javax.management.remote.JMXServiceURL;

import org.logicblaze.lingo.jmx.remote.provider.jms.JmsJmxConnector;
import org.logicblaze.lingo.jmx.remote.provider.jms.JmsJmxConnectorFactory;

public class ActiveMQJmsJmxConnectorFactory implements JmsJmxConnectorFactory {

    public boolean supports(JMXServiceURL url) {
        boolean retval = true;
        String protocol = url.getProtocol();
        if (!"jms".equals(protocol)) {
            retval = false;
        }
        return retval;
    }

    public JmsJmxConnector newJmsJmxConnector(JMXServiceURL url, Map<String, ?> environment) {
        ActiveMQJmsJmxConnector connector = null;
        try {
            connector = new ActiveMQJmsJmxConnector(environment, url);
        } catch (IOException e) {
            throw new IllegalStateException("unable to create ActiveMQJmsJmxConnector", e);
        }
        return connector;
    }


}
