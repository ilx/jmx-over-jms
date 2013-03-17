package org.logicblaze.lingo.jmx.remote.provider.jms;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXServiceURL;

public interface JmsJmxConnector extends JMXConnector {

	boolean supports(JMXServiceURL url);

}
