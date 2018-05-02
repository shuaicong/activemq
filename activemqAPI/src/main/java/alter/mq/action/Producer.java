package alter.mq.action;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class Producer {
	private static final String BROKER_URL = "tcp://localhost:61616";
	//链接工厂  
    private ActiveMQConnectionFactory connectionFactory = null;  
    //链接对象
	private Connection connection = null;  
	//会话  
	private Session session = null;  
    //消息生产者  
	private MessageProducer messageProducer = null;
	
	public Producer() {
		try {
			this.connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnectionFactory.DEFAULT_USER, ActiveMQConnectionFactory.DEFAULT_PASSWORD, BROKER_URL);
            this.connection = this.connectionFactory.createConnection();
            this.connection.start();
            
            this.session = connection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
            
            //第四步:通过Session创建Destination对象，指的是一个客户端用来指定生产消息目标和消费消息来源的对象，在PTP模式中，Destination被称作Queue，即队列；在PUB/SUB模式，Destination被称作Topic,即主题，在程序中可使用多个Queue和Topic。
            Destination destination = session.createQueue("first");
            //第五步，需要通过session对象创建消息的发送和接收对象（生产者和消费者）MessageProducer/MessageConsumer。
            this.messageProducer = session.createProducer(destination);
            //第六步，在生产端，可以使用MessageProducer的setDeliveryMode方法为其设置持久化特性和非持久化特性（DeliveryMode）。
            //producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        } catch (Exception e) {
            System.out.println("Caught exception!"+e);
        }
	}
	
	public Session getSession() {
		return this.session;
	}
	
	public void send1() {
		try {
			Destination destination = this.session.createQueue("first");
			for (int i = 0; i < 100; i++) {
				MapMessage msg = this.session.createMapMessage();
				int id = i;
				msg.setInt("name", id);
				msg.setString("name", "张" + i);
				msg.setString("age", "" + i);
				String receiver = id%2 == 0 ? "A" : "B";
				msg.setStringProperty("receiver", receiver);
				this.messageProducer.send(destination, msg, DeliveryMode.NON_PERSISTENT, 2, 1000*60*10L);
				System.out.println("message send id: " + id);
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
		
	}
	
	
	public static void main(String[] args) {
		Producer p = new Producer();
		p.send1();
	}
	
}
