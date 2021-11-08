package com.learn.hivemq_mqttclient.server;

import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;



import com.hivemq.client.internal.mqtt.MqttRxClient;
import com.hivemq.client.internal.mqtt.message.publish.MqttPublishBuilder;

import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5PublishBuilder;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5PublishBuilderBase;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5PublishResult;
/**
 * 
 * 
 * <p>
 * 							description:																			</br>	
 * &emsp;						use different value to publish message each time 									</br>	
 * 																													</br>
 *
 *
 * @author laipl
 *
 */
public class TestMain_Test {


	public static void main(String[] args) {

        //String topic        = "MQTT Examples";
        String topic        = "sensors/temperature";
        //String content      = "Message from MqttPublishSample";
        String content      = "你好";
        //String content      = "hi_myfriend";
        int qos             = 2;
        //String broker       = "tcp://iot.eclipse.org:1883";
        String broker       = "tcp://localhost:1883";
        //String broker       = "ssl://localhost:8883";
        String clientId     = "JavaSample";
        //MemoryPersistence persistence = new MemoryPersistence();

        
        /*
        Mqtt5BlockingClient client = Mqtt5Client.builder()
                .identifier(UUID.randomUUID().toString())
                .serverHost("broker.hivemq.com")
                .buildBlocking();
        */
        final InetSocketAddress LOCALHOST_EPHEMERAL1 = new InetSocketAddress("localhost",1883);
        //  builder.serverAddress是去   MqttRxClientBuilder 的父类的     MqttRxClientBuilderBase的 serverAddress
        //  builder.buildAsync   是去  MqttRxClientBuilder 的父类的     MqttRxClientBuilderBase的 buildAsync
        // 其过程中会经过
        // MqttAsyncClient(final @NotNull MqttRxClient delegate) {
        //    this.delegate = delegate;
        // }
        // 所以初步认为 MqttAsyncClient 是包含了 MqttRxClient 
        //Mqtt5AsyncClient client1 = Mqtt5Client.builder().serverAddress(LOCALHOST_EPHEMERAL1).identifier(clientId).buildAsync();
        
        //client1.connect();
        
        /*
        Mqtt5BlockingClient client = Mqtt5Client.builder()
                .serverHost("localhost")
                .buildBlocking();

        client.connect();
        client.publishWith().topic("test/topic").qos(MqttQos.AT_LEAST_ONCE).payload("hello".getBytes()).send();
        */
        
        Mqtt5AsyncClient client1 = Mqtt5Client.builder().serverAddress(LOCALHOST_EPHEMERAL1).identifier(clientId).buildAsync();
        client1.connect();
        client1.publishWith().topic("test/topic").qos(MqttQos.AT_LEAST_ONCE).payload("hello".getBytes()).send();
        
        
    	try {
    		Thread.sleep(15000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        client1.disconnect();

        

    }

}
