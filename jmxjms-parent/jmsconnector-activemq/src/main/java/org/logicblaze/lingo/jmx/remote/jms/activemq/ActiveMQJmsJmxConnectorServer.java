/**
 *
 * Copyright RAJD Consultancy Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **/

package org.logicblaze.lingo.jmx.remote.jms.activemq;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Map;

import javax.jms.ConnectionFactory;
import javax.management.MBeanServer;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXServiceURL;
import javax.management.remote.MBeanServerForwarder;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.logicblaze.lingo.jms.JmsProducerConfig;
import org.logicblaze.lingo.jms.JmsServiceExporter;
import org.logicblaze.lingo.jms.impl.MultiplexingRequestor;
import org.logicblaze.lingo.jmx.remote.jms.JmsJmxConnectorSupport;
import org.logicblaze.lingo.jmx.remote.jms.MBeanJmsServerConnection;
import org.logicblaze.lingo.jmx.remote.provider.jms.JmsJmxConnectorServer;

/**
 * <p>The client end of a JMX API connector.  An object of this type can
 * be used to establish a connection to a connector server.</p>
 *
 * <p>A newly-created object of this type is unconnected.  Its {@link#connect connect}
 * method must be called before it can be used.
 * However, objects created by {@link
 * JMXConnectorFactory#connect(JMXServiceURL, Map)
 * JMXConnectorFactory.connect} are already connected.</p>
 *
 * @since 1.5
 * @since.unbundled 1.0
 */
public class ActiveMQJmsJmxConnectorServer extends JmsJmxConnectorServer {

    private static final Log log = LogFactory.getLog(ActiveMQJmsJmxConnectorServer.class);
    private JMXServiceURL url;
    private Map env;
    private String destinationName;
    private String destinationGroupName = JmsJmxConnectorSupport.MBEAN_GROUP_NAME;
    private String destinationServerName = JmsJmxConnectorSupport.MBEAN_SERVER_NAME;
    private volatile boolean stopped = true;
    private JmsServiceExporter service;
    private MultiplexingRequestor requestor;
    private MBeanJmsServerConnectionImpl jmsServerConnection;
    private URI jmsURL;


    public ActiveMQJmsJmxConnectorServer() {
        // initialize...
        super();
    }

    /**
     * Create the JmsJmxConnectorServer
     * @param url
     * @param environment
     * @param server
     * @throws IOException
     */
    public ActiveMQJmsJmxConnectorServer(JMXServiceURL url,Map environment,MBeanServer server) throws IOException{
        super(server);
        this.url = url;
        this.env = environment;
        this.jmsURL = JmsJmxConnectorSupport.getProviderURL(url);
        //set any props in the url
        ActiveMQJmsJmxConnectorSupport.populateProperties(this, jmsURL);
    }

    /**
     * start the connector
     * @throws IOException
     */
    public void start() throws IOException{
        try{
            service = new JmsServiceExporter();
            ConnectionFactory fac = new ActiveMQConnectionFactory(jmsURL);
            if (destinationName == null){
                destinationName = JmsJmxConnectorSupport.DEFAULT_DESTINATION_PREFIX + destinationGroupName + "." + destinationServerName;
            }
            service.setDestination(new ActiveMQTopic(destinationName));
            service.setConnectionFactory(fac);
            service.setServiceInterface(MBeanJmsServerConnection.class);
            this.requestor = (MultiplexingRequestor) MultiplexingRequestor.newInstance(fac,new JmsProducerConfig(),null);
            service.setResponseRequestor(requestor);
            this.jmsServerConnection = new MBeanJmsServerConnectionImpl(getMBeanServer(),requestor.getConnection());
            service.setService(jmsServerConnection);
            service.afterPropertiesSet();
            stopped = false;
        }catch(Exception e){
            log.error("Failed to start ",e);
            throw new IOException(e.toString());
        }

    }

    /**
     * stop the connector
     * @throws IOException
     */
    public void stop() throws IOException{
       try{
           if(!stopped){
               stopped = true;
               service.destroy();
           }
    }catch(Exception e){
        log.error("Failed to stop ",e);
        throw new IOException(e.toString());
    }

    }

    public boolean isActive(){
        return !stopped;
    }

    public JMXServiceURL getAddress(){
        return url;
    }

    public Map getAttributes(){
       return Collections.unmodifiableMap(env);
    }

}