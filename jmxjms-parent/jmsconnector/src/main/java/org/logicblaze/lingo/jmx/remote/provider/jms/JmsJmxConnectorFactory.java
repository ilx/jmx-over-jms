package org.logicblaze.lingo.jmx.remote.provider.jms;

import java.util.Map;

import javax.management.remote.JMXServiceURL;

public interface JmsJmxConnectorFactory {

	boolean supports(JMXServiceURL url);

	JmsJmxConnector newJmsJmxConnector(JMXServiceURL url, Map<String, ?> environment);
}
