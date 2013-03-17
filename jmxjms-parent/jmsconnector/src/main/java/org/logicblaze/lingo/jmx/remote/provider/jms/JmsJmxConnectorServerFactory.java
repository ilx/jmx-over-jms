package org.logicblaze.lingo.jmx.remote.provider.jms;

import java.util.Map;

import javax.management.MBeanServer;
import javax.management.remote.JMXServiceURL;

public abstract class JmsJmxConnectorServerFactory {

    public JmsJmxConnectorServerFactory() {
        // nothing?
    }

    public abstract JmsJmxConnectorServer newJmxConnectorServer(JMXServiceURL url, Map<String, ?> environment, MBeanServer server);

    public abstract boolean supports(JMXServiceURL url);
}
