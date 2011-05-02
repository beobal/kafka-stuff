package com.talis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talis.status.MessageProcessor;
import com.talis.status.client.Collector;


public class Driver {

	private static final transient Logger LOG = LoggerFactory.getLogger(Driver.class);
	
	public static void main(String[] args) throws InterruptedException{
		
		String[] procArgs = {"--topic","test", "--props", "/home/sam/code/dev/delilah/consumer.properties", "--partitions", "4"};
		
		new MessageProcessor().main(procArgs);
  		
//		Thread server = new Thread(new Runnable(){
//			@Override
//			public void run() {
//				LOG.info("SERVER starting");
//				// TODO Auto-generated method stub
//				Consumer c = new Consumer();
//				c.start();
//			}
//			
//		});
//		server.start();
		
		Thread client = new Thread(new Runnable(){
			@Override
			public void run() {
				LOG.info("CLIENT started");
				// TODO Auto-generated method stub
				Collector c = new Collector();
				for (int i = 0; i < 200; i++ ){
					
					c.sendMessage("foo-token", "This is message " + i);
				}
			}
			
		});
		client.start();
		
//		server.join();
	}
	
}
