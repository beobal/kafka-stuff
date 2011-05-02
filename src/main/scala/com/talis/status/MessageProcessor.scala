package com.talis.status

import joptsimple._
import kafka.consumer.{KafkaMessageStream, ConsumerConnector, Consumer, ConsumerConfig}
import kafka.utils.Utils
import java.util.concurrent.CountDownLatch
import grizzled.slf4j.Logging

class MessageProcessor extends Logging{
	
	def main(args: Array[String]): Unit = {
    
    val parser = new OptionParser
    val topicOpt = parser.accepts("topic", "REQUIRED: The topic to consume from.")
                           .withRequiredArg
                           .describedAs("topic")
                           .ofType(classOf[String])
    val consumerPropsOpt = parser.accepts("props", "REQUIRED: Properties file with the consumer properties.")
                           .withRequiredArg
                           .describedAs("properties")
                           .ofType(classOf[String])
    val partitionsOpt = parser.accepts("partitions", "Number of partitions to consume from.")
                           .withRequiredArg
                           .describedAs("count")
                           .ofType(classOf[java.lang.Integer])
                           .defaultsTo(1)
    
    val options = parser.parse(args : _*)
    
    for(arg <- List(topicOpt, consumerPropsOpt)) {
      if(!options.has(arg)) {
        System.err.println("Missing required argument \"" + arg + "\"") 
        parser.printHelpOn(System.err)
        System.exit(1)
      }
    }
    
    val partitions = options.valueOf(partitionsOpt).intValue
    val propsFile = options.valueOf(consumerPropsOpt)
    val topic = options.valueOf(topicOpt)
    println(topic)
    println("Starting consumer..")

    val consumerConfig = new ConsumerConfig(Utils.loadProps(propsFile))
    val consumerConnector: ConsumerConnector = Consumer.create(consumerConfig)
    val topicMessageStreams = consumerConnector.createMessageStreams(Predef.Map(topic -> partitions))
    var threadList = List[ZKConsumerThread]()
    for ((topic, streamList) <- topicMessageStreams)
      for (stream <- streamList)
        threadList ::= new ZKConsumerThread(stream)

    for (thread <- threadList)
      thread.start

    // attach shutdown handler to catch control-c
    Runtime.getRuntime().addShutdownHook(new Thread() {
      override def run() = {
        consumerConnector.shutdown
        threadList.foreach(_.shutdown)
        println("consumer threads shutted down")        
      }
    })
  }
}

class ZKConsumerThread(stream: KafkaMessageStream) extends Thread with Logging{
  val shutdownLatch = new CountDownLatch(1)

  override def run() {
    println("Starting consumer thread...")
    for (message <- stream) {
        info("consumed: " + Utils.toString(message.payload, "UTF-8"))
        // TODO lift SimpleDB code from PDI UI and insert data into simpledb
    }
    shutdownLatch.countDown
    println("thread shutdown !" )
  }

  def shutdown() {
    shutdownLatch.await
  }          
}
