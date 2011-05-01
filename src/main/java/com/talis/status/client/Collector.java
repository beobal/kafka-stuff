package com.talis.status.client;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import kafka.message.ByteBufferMessageSet;
import kafka.message.Message;
import kafka.producer.SimpleProducer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Collector {
	
	private static final transient Logger LOG = LoggerFactory.getLogger(Collector.class);
	
	String host = "localhost";
	int port = 9092;
	int bufferSize = 64*1024;
	int connectionTimeoutMs = 30*1000;
	int reconnectInterval = 1000;
	SimpleProducer producer = new SimpleProducer(host, port, bufferSize, connectionTimeoutMs, reconnectInterval);
	String topic = "test";
	int numPartitions = 10;
	long messageCnt = 0;
	ScheduledExecutorService executor;
	List<Message> messageBuffer;
	String senderId;
	
	public static final String MESSAGE_TEMPLATE = new String("{ \"sender_id\" : \"%s\", \"timestamp\" : %d, \"token\": \"%s\", \"status\" : \"%s\" }");
	
	public Collector(){
		try {
			senderId = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			senderId = UUID.randomUUID().toString();
		}
		executor = Executors.newSingleThreadScheduledExecutor();
		executor.scheduleAtFixedRate(new Runnable(){
			@Override
			public void run() {
				flushMessages();
			}	
		}, 1000l, 1000l, TimeUnit.MILLISECONDS);
		messageBuffer = new ArrayList<Message>();
	}
	
	public void sendMessage(String token, String message){
		// need a cntr per token? to order messages with the same timestamp
		messageBuffer.add(new Message(String.format(MESSAGE_TEMPLATE, senderId, System.currentTimeMillis(), token, message).getBytes()));
	}
	
	public void flushMessages(){
		if (messageBuffer.isEmpty()){
			return;
		}
		LOG.info("Sending messages");
		List<Message> copy = new ArrayList<Message>(messageBuffer);
		ByteBufferMessageSet messages = new ByteBufferMessageSet(copy);
		int numMessages = messages.size();
		producer.send(topic, (int)(messageCnt++ % numPartitions), messages);
		LOG.info(numMessages + " messages sent");
		messageBuffer.removeAll(copy);
	}
}
