package kafka.com.talis.status

import java.util.Properties
import grizzled.slf4j.Logging
import kafka.producer._
import kafka.producer.async._
import com.talis.status.client.JsonEncoder;

class ScalaCollector extends Logging{
		val topic = "test"
		
		val props = new Properties()
		props.put("host", "localhost")
		props.put("port", "9092")
		props.put("queue.size", "200")
		props.put("batch.size", "100")
		props.put("serializer.class", "com.talis.status.client.JsonEncoder")

		props.put("buffer.size", String.valueOf(60*1024))
		props.put("connect.timeout.ms", String.valueOf(100000))
		props.put("reconnect.interval", String.valueOf(10000))

		val syncConfig = new SyncProducerConfig(props)
		val basicProducer =  new SyncProducer(syncConfig)
		
		val asyncConfig = new AsyncProducerConfig(props)
		val asyncProducer = new AsyncProducer[Array[String]](asyncConfig, basicProducer, new JsonEncoder())

		// start the async producer
		asyncProducer.start
				
		// attach shutdown handler to catch control-c
	    Runtime.getRuntime().addShutdownHook(new Thread() {
	      override def run() = {
	    	asyncProducer.close
	        info("AsyncProducer shut down")        
	      }
	    })

	def sendMessage(token: String, message: String){
		info("Sending message " + message);
		asyncProducer.send(topic, Array(token, message));
	}

}