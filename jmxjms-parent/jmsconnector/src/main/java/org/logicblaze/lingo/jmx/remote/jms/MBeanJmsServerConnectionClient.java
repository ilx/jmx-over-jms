package org.logicblaze.lingo.jmx.remote.jms;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.management.ListenerNotFoundException;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @version $Revision: 95 $
 */
public class MBeanJmsServerConnectionClient extends MBeanServerConnectionDelegate implements MessageListener {
	private static final Log log = LogFactory.getLog(MBeanJmsServerConnectionClient.class);
	private MBeanJmsServerConnection serverConnection;
	private Topic replyToDestination;
	private List<ListenerInfo> listeners = new CopyOnWriteArrayList<ListenerInfo>();
	private NotificationBroadcasterSupport localNotifier = new NotificationBroadcasterSupport();

	/**
	 * Construct this thing
	 *
	 * @param connection
	 * @throws JMSException
	 */
	public MBeanJmsServerConnectionClient(MBeanJmsServerConnection connection, Connection jmsConnection) throws JMSException {
		super(connection);
		this.serverConnection = connection;
		Session jmsSession = jmsConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		replyToDestination = jmsSession.createTemporaryTopic();
		MessageConsumer consumer = jmsSession.createConsumer(replyToDestination);
		consumer.setMessageListener(this);
	}

	/**
	 * Add a notification listener
	 * @param name
	 * @param listener
	 * @param filter
	 * @param handback
	 */
	public void addNotificationListener(ObjectName name, NotificationListener listener, NotificationFilter filter, Object handback) {
		String id = UUID.randomUUID().toString();
		ListenerInfo info = new ListenerInfo(id, listener, filter, handback);
		listeners.add(info);
		localNotifier.addNotificationListener(listener, filter, handback);
		serverConnection.addNotificationListener(id, name, replyToDestination);
	}

	/**
	 * Remove a Notification Listener
	 * @param name
	 * @param listener
	 * @throws ListenerNotFoundException
	 */
	public void removeNotificationListener(ObjectName name, NotificationListener listener) throws ListenerNotFoundException {
		for (Iterator<ListenerInfo> i = listeners.iterator(); i.hasNext();) {
			ListenerInfo li = i.next();
			if (li.getListener() == listener) {
				listeners.remove(i);
				serverConnection.removeNotificationListener(li.getId());
				break;
			}
		}
		localNotifier.removeNotificationListener(listener);
	}

	/**
	 * Remove a Notification Listener
	 * @param name
	 * @param listener
	 * @param filter
	 * @param handback
	 * @throws ListenerNotFoundException
	 */
	public void removeNotificationListener(ObjectName name, NotificationListener listener, NotificationFilter filter, Object handback)
			throws ListenerNotFoundException {
		for (Iterator<ListenerInfo> i = listeners.iterator(); i.hasNext();) {
			ListenerInfo li = i.next();
			if (li.getListener() == listener && li.getFilter() == filter && li.getHandback() == handback) {
				listeners.remove(i);
				serverConnection.removeNotificationListener(li.getId());
			}
		}
		localNotifier.removeNotificationListener(listener, filter, handback);
	}

	/**
	 * MessageListener implementation
	 * @param msg
	 */
	public void onMessage(Message msg) {
		ObjectMessage objMsg = (ObjectMessage) msg;
		try {
			Notification notification = (Notification) objMsg.getObject();
			localNotifier.sendNotification(notification);
		} catch (JMSException jmsEx) {
			log.error("Failed to send Notification", jmsEx);
		}
	}
}