package com.talis.status.client;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kafka.message.Message;
import kafka.serializer.Encoder;

public class JsonEncoder implements Encoder<String[]> {
	
	private static final transient Logger LOG = LoggerFactory.getLogger(JsonEncoder.class);
	
	public static final String MESSAGE_TEMPLATE = new String("{ \"sender_id\" : \"%s\", \"timestamp\" : %d, \"token\": \"%s\", \"status\" : \"%s\" }");
	
	private final String senderId;

	public JsonEncoder(){
		String identifier = UUID.randomUUID().toString();
		try {
			identifier = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			LOG.warn(String.format("Error getting local host name, using UUID as senderId [%s]", identifier));
		}
		senderId = identifier;
	}
	
	
	@Override
	public Message toMessage(String[] args){
		LOG.info("Encoding message : " + args[1]);
		return new Message(
				String.format(MESSAGE_TEMPLATE, senderId, 
						  System.currentTimeMillis(), 
						  args[0], args[1]).getBytes());
	}
}
