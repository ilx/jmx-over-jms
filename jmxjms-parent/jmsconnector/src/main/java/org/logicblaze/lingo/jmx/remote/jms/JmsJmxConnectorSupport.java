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

package org.logicblaze.lingo.jmx.remote.jms;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.management.remote.JMXServiceURL;

/**
 * @version $Revision: 95 $
 */
public abstract class JmsJmxConnectorSupport {
    /**
     * Default destination prefix
     */
    public static final String DEFAULT_DESTINATION_PREFIX = "jms.jmx.";

    // TODO ILX: remove static fields
    /**
     * The default destination server name
     */
    public static final String MBEAN_SERVER_NAME = "*";
    /**
     * The default destination group name
     */
    public static final String MBEAN_GROUP_NAME = "*";

    public static URI getProviderURL(JMXServiceURL serviceURL) throws IOException {
        String protocol = serviceURL.getProtocol();
        if (!"jms".equals(protocol))
            throw new MalformedURLException("Wrong protocol " + protocol + " expecting jms ");
        try {
            String path = serviceURL.getURLPath();
            while (path.startsWith("/")) {
                path = path.substring(1);
            }
            return new URI(path);
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
            throw new IOException(e.toString());
        }
    }


}