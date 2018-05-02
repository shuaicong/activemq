package alter.mq.action;

import javax.jms.MapMessage;

public class MessageTask implements Runnable {
	
	private MapMessage message;
	
	public MessageTask(MapMessage message) {
		this.message = message;
	}

	public void run() {

		try {
			Thread.sleep(500);
			System.out.println("当前线程："+ Thread.currentThread().getName()+"处理任务："+this.message.getStringProperty("receiver"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
