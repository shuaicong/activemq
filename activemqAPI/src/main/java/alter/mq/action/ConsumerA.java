package alter.mq.action;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.builder.ThreadPoolBuilder;

public class ConsumerA {
	private static final String BROKER_URL = "tcp://localhost:61616";
	
	public final String SELECTOR = "receiver = 'A'";
	//链接工厂  
    private ActiveMQConnectionFactory connectionFactory = null;  
    //链接对象
	private Connection connection = null;  
	//会话  
	private Session session = null;  
    //消息消费者  
	private MessageConsumer messageConsumer = null;
	//目标地址
	private Destination destination = null;
	
	public ConsumerA() {
		try {
			this.connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnectionFactory.DEFAULT_USER, ActiveMQConnectionFactory.DEFAULT_PASSWORD, BROKER_URL);
            this.connection = this.connectionFactory.createConnection();
            this.connection.start();
            
            this.session = connection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
            
            //第四步:通过Session创建Destination对象，指的是一个客户端用来指定生产消息目标和消费消息来源的对象，在PTP模式中，Destination被称作Queue，即队列；在PUB/SUB模式，Destination被称作Topic,即主题，在程序中可使用多个Queue和Topic。
            this.destination = session.createQueue("first");
            //第五步，需要通过session对象创建消息的发送和接收对象（生产者和消费者）MessageProducer/MessageConsumer。
            this.messageConsumer = session.createConsumer(this.destination, SELECTOR);
            System.out.println("start ConsumerA ....");
        } catch (Exception e) {
            System.out.println("Caught exception!"+e);
        }
	}
	
	public void receiver() {
		try {
			this.messageConsumer.setMessageListener(new Listener());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	class Listener implements MessageListener{
		BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(10000);
		ExecutorService executor = new ThreadPoolExecutor(
				Runtime.getRuntime().availableProcessors(), 
				20, 
				120L, 
				TimeUnit.SECONDS, 
				queue);
		
		public void onMessage(Message message) {
			try {
				if(message instanceof MapMessage) {
					MapMessage ret = (MapMessage)message;
					
//					Thread.sleep(500);
//					System.out.println("处理任务："+ret.getString("id"));
					executor.execute(new MessageTask(ret));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		ConsumerA c = new ConsumerA();
		c.receiver();
	}

	
	
}
