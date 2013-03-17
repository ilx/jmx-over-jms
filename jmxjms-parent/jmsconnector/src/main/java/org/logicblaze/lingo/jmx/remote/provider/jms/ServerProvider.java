/**
 *
 * Copyright RAJD Consultancy Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 */
package org.logicblaze.lingo.jmx.remote.provider.jms;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.ServiceLoader;

import javax.management.MBeanServer;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerProvider;
import javax.management.remote.JMXServiceURL;

/**
 * @version $Revision: 86 $
 */
public class ServerProvider implements JMXConnectorServerProvider {
    /**
     * Create a JmsJmxConnectorServer
     *
     * @param url
     * @param environment
     * @param server
     * @return the JmsJmxConnectorServer
     * @throws IOException
     */
    public JMXConnectorServer newJMXConnectorServer(JMXServiceURL url, Map<String, ?> environment, MBeanServer server) throws IOException {
        String protocol = url.getProtocol();
        if (!"jms".equals(protocol)) {
            throw new MalformedURLException("Wrong protocol " + protocol + " for provider " + this);
        }

        JmsJmxConnectorServerFactory connectorServerFactory = null;
        ServiceLoader<JmsJmxConnectorServerFactory> connectorServerFactoryLoader = ServiceLoader.load(JmsJmxConnectorServerFactory.class);
        for (JmsJmxConnectorServerFactory jmsJmxConnectorServerFactory : connectorServerFactoryLoader) {
            if (jmsJmxConnectorServerFactory.supports(url)) {
                connectorServerFactory = jmsJmxConnectorServerFactory;
                break;
            }
        }

        JMXConnectorServer connectorServer = connectorServerFactory.newJmxConnectorServer(url, environment, server);

        return connectorServer;
    }


}
