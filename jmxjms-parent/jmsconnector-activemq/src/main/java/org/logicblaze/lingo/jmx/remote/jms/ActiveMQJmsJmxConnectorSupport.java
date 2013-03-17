package org.logicblaze.lingo.jmx.remote.jms;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.apache.activemq.util.IntrospectionSupport;
import org.apache.activemq.util.URISupport;

public class ActiveMQJmsJmxConnectorSupport extends JmsJmxConnectorSupport {

	static void populateProperties(Object value, URI url) throws IOException {
		String query = url.getQuery();
		if (query != null) {
			try {
				Map map = URISupport.parseQuery(query);
				if (map != null && !map.isEmpty()) {
					IntrospectionSupport.setProperties(value, map);
				}
			} catch (URISyntaxException e) {
				e.printStackTrace();
				throw new IOException(e.toString());
			}

		}
	}
}
