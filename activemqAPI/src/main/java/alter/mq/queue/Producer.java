package alter.mq.queue;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class Producer {
	private static final String BROKER_URL = "tcp://localhost:61616";

    private static final Boolean NON_TRANSACTED = false;
    private static final int NUM_MESSAGES_TO_SEND = 100;
    private static final long DELAY = 100;
	
	public static void main(String[] args) {
		String url = BROKER_URL;
        if (args.length > 0) {
            url = args[0].trim();
        }
        
        //第一步：建立ConnectionFactory工厂对象，需要填入用户名，密码，以及要连接的地址，均使用默认即可
		//ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnectionFactory.DEFAULT_USER, ActiveMQConnectionFactory.DEFAULT_PASSWORD, url);
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("alter", "alter", url);
		Connection connection = null;
        try {
        	//第二步：通过ConnectionFactory工厂对象我们创建一个Connection连接，并且调用connection的start()方法开启连接,Connection默认是关闭的
            connection = connectionFactory.createConnection();
            connection.start();
            
            //第三步：通过Connection对象创建Session会话（上下文环境对象），用于接收消息，参加1为是否启用事物，参数2为签收模式，一般我们设置为自动签收
            Session session = connection.createSession(NON_TRANSACTED, Session.AUTO_ACKNOWLEDGE);
            
            //第四步:通过Session创建Destination对象，指的是一个客户端用来指定生产消息目标和消费消息来源的对象，在PTP模式中，Destination被称作Queue，即队列；在PUB/SUB模式，Destination被称作Topic,即主题，在程序中可使用多个Queue和Topic。
            Destination destination = session.createQueue("test-queue");
            //第五步，需要通过session对象创建消息的发送和接收对象（生产者和消费者）MessageProducer/MessageConsumer。
            MessageProducer producer = session.createProducer(destination);

            //第六步，在生产端，可以使用MessageProducer的setDeliveryMode方法为其设置持久化特性和非持久化特性（DeliveryMode）。
            //producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            
            //第七步，使用JMS规范的TextMessage形式创建数据（通过session对象），并用MessageProducer的
            for (int i = 0; i < NUM_MESSAGES_TO_SEND; i++) {
            	
            	
            	
                TextMessage message = session.createTextMessage("Message #" + i);
                System.out.println("Sending message #" + i);
                
                //第一个参数目的地
                //第二个参数消息
                //第三个参数是否持久化
                //第四个参数优先级
                //第五个参数消息在MQ上的存放有效期
                
                producer.send(message);
                //producer.send(destination, message);
                Thread.sleep(DELAY);
            }

            producer.close();
            session.close();

        } catch (Exception e) {
            System.out.println("Caught exception!");
        }
        finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    System.out.println("Could not close an open connection...");
                }
            }
        }
	}
	
}
