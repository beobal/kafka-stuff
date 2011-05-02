package com.talis.status.client;

import kafka.com.talis.status.ScalaCollector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Collector {
	
	private static final transient Logger LOG = LoggerFactory.getLogger(Collector.class);
	
	private final ScalaCollector sc;
	
	public Collector(){
		sc = new ScalaCollector();
	}
	
	public void sendMessage(String token, String message){
		LOG.info("Sending message " + message);
		sc.sendMessage(token, message);
	}
	
	
	
}
