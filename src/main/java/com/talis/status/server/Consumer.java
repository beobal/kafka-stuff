package com.talis.status.server;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerConnector;
import kafka.consumer.KafkaMessageStream;
import kafka.consumer.ZookeeperConsumerConnector;
import kafka.message.Message;
import kafka.utils.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Consumer {
	private static final transient Logger LOG = LoggerFactory.getLogger(Consumer.class);
	// specify some consumer properties
	Properties props = new Properties();
	ConsumerConfig consumerConfig;
	ConsumerConnector consumerConnector;
	
	public Consumer(){
		props.put("zk.connect", "localhost:2181");
		props.put("zk.connectiontimeout.ms", "1000000");
		props.put("groupid", "test_group");
		consumerConfig = new ConsumerConfig(props);
		consumerConnector = kafka.consumer.Consumer.create(consumerConfig);
	}
	
	@SuppressWarnings("serial")
	public void start(){
		// create 4 partitions of the stream for topic “test”, to allow 4 threads to consume
		Map<String,Integer> map = new HashMap<String, Integer>(){{ put("test", 2);}};
		Map<String, List<KafkaMessageStream>> topicMessageStreams = 
		    consumerConnector.createMessageStreams(map);
		List<KafkaMessageStream> streams = topicMessageStreams.get("test");

		// create list of 4 threads to consume from each of the partitions 
		ExecutorService executor = Executors.newFixedThreadPool(2);		

		// consume the messages in the threads
		for(final KafkaMessageStream stream: streams) {
		  executor.submit(new Runnable() {
		    public void run() {
		      LOG.info("Message consumer thread running");
		      for(Message message: stream) {
		    	LOG.info("Message received: " + getMessage(message));//new String(message.payload().array()));
		      }	
		    }
		  });
		}		
	}

	public static String getMessage(Message message){
	    ByteBuffer buffer = message.payload();
	    byte [] bytes = new byte[buffer.remaining()];
	    buffer.get(bytes);
	    return new String(bytes);
	}
}
