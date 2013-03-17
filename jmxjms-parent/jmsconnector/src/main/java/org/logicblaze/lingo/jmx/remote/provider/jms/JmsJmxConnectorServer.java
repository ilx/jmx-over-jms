package org.logicblaze.lingo.jmx.remote.provider.jms;

import javax.management.MBeanServer;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXServiceURL;

public abstract class JmsJmxConnectorServer extends JMXConnectorServer {

	public JmsJmxConnectorServer() {
		super();
	}

	public JmsJmxConnectorServer(MBeanServer mbeanServer) {
		super(mbeanServer);
	}

	public boolean supports(JMXServiceURL url) {
		boolean retval = true;
		String protocol = url.getProtocol();
        if (!"jms".equals(protocol)) {
        	retval = false;
        }
        return retval;
	}


}
