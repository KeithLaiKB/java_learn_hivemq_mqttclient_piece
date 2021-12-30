package com.learn.hivemq_mqttclient.receiver.testRx;

import java.net.InetSocketAddress;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient.Mqtt5SubscribeAndCallbackBuilder;
import com.hivemq.client.mqtt.mqtt5.message.connect.connack.Mqtt5ConnAck;

public class TestMain_modified_cbdeleted {

	public static void main(String[] args) {

        //String topic        = "MQTT Examples";
        String topic        = "zigbee2mqtt/0x00124b00250c256f";
        //String topic        = "zigbee2mqtt/example/extensionOGChange1";
        //String content      = "Message from MqttPublishSample";
        String content      = "receiver";
        int qos             = 1;
        //String broker       = "tcp://iot.eclipse.org:1883";
        String broker       = "tcp://localhost:1883";
        //String clientId     = "JavaSample";
        String clientId     = "JavaSample_revcevier";
        //MemoryPersistence persistence = new MemoryPersistence();

        final InetSocketAddress LOCALHOST_EPHEMERAL1 = new InetSocketAddress("135.0.237.84",1883);
        //Mqtt5Client sampleClient = new Mqtt5Client(broker, clientId, persistence);
        //Mqtt5AsyncClient client1 = Mqtt5Client.builder().serverAddress(LOCALHOST_EPHEMERAL1).identifier(clientId).buildAsync();
        Mqtt5AsyncClient client1 = Mqtt5Client.builder().serverAddress(LOCALHOST_EPHEMERAL1).buildAsync();
        //Mqtt5Client client1 = Mqtt5Client.builder().identifier(clientId).build();
        /*
        Mqtt5Subscribe subscribeMessage = Mqtt5Subscribe.builder()
                .topicFilter(topic)
                .qos(MqttQos.EXACTLY_ONCE)
                .build();
        client1.subscribe(subscribeMessage)*/
        
        
        //client1.connect();
        //------------------------------- client connect --------------------------------------
        // 一定要注意 connect之后 如果不用thenAccept之类的方法, 就一定要 让他 等一等 , 等connect成功!!!!!!!!!!!!!!!!
        // 不然刚connect 就去publish 会出现第一条无法publish, 然后成功publish第二条的现象
        // 有点像 MqttAsyncClient sampleClient.connect(connOpts, null, null).waitForCompletion(-1); 需要block自己然后直到连接成功才进行下一步
        // 只是我选择 用段时间等待而已
        CompletableFuture<Mqtt5ConnAck> cplfu_connect_rslt = client1.connect();
        //wait
    	while (cplfu_connect_rslt.isDone() == false) {
    		// 这里的 sleep 可以不用, 不影响主逻辑
    		// 只不过 这里加了个 sleep, 可以减少 不停地loop, 因为太多loop会给计算机带来的资源消耗
        	try {
        		Thread.sleep(1000);
    		} catch (InterruptedException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}
		System.out.println("connected");
        
        
        
		//------------------------------- 第 A1 种写法 --------------------------------------
		/*
        //ref : hivemq-mqtt-client/examples/src/main/java/com/hivemq/client/mqtt/examples/Mqtt5Features.java /
		// subscribeWith() 是为了获得  Mqtt5SubscribeAndCallbackBuilder
		client1
        .subscribeWith()
        .topicFilter(topic)
        .qos(MqttQos.AT_LEAST_ONCE)
        .callback(publish -> System.out.println("received message: " + publish + "////"+ new String(publish.getPayloadAsBytes())) ) 	// set callback
        .send();		//subscribe callback and something 
        */
        //
        //
        //
        //   
        //------------------------------- 第 A2 种写法 --------------------------------------
        // 因为MqttAsyncClient.MqttSubscribeAndCallbackBuilder -> Mqtt5SubscribeAndCallbackBuilder.Start.Complete -> Mqtt5SubscribeAndCallbackBuilder.Start
        Mqtt5AsyncClient.Mqtt5SubscribeAndCallbackBuilder.Start subscribeBuilder1 = client1.subscribeWith();
        // 一开始 不知道c1的类, 所以鼠标转移到 topicFilter, 可以看到 是Complete
        // 但是我们不知道这个Complete是哪个包里的 Complete
        //com.hivemq.client.mqtt.mqtt5.message.subscribe.Mqtt5SubscriptionBuilderBase<C extends Complete<C>> c1 = subscribeBuilder1.topicFilter(topic);
        //Mqtt5SubscribeAndCallbackBuilder.Start.Complete c1 = subscribeBuilder1.topicFilter(topic);
        Mqtt5SubscribeAndCallbackBuilder.Start.Complete c1 = subscribeBuilder1.topicFilter(topic);
        c1.qos(MqttQos.AT_LEAST_ONCE);
        c1.callback(publish -> {
        	
        	String jsonRsTmp = new String(publish.getPayloadAsBytes());
        	//
        	//
        	ObjectMapper mapperTmp = new ObjectMapper();
        	LinkedHashMap<String,Object> lkhMapTmp1 = null;
        	TypeReference<LinkedHashMap<String,Object>> tpRfTmp1  = new TypeReference<LinkedHashMap<String,Object>>() {};
        	//
        	try {
        		lkhMapTmp1 = mapperTmp.readValue(jsonRsTmp, tpRfTmp1);
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	System.out.println("received message: " + publish + "////"+ new String(publish.getPayloadAsBytes()));
        	System.out.println("received message content(state): " + lkhMapTmp1.get("state"));
        	//
        	//

        });
        /*.callback(s->{
        	String jsonRsTmp = new String(s.getPayloadAsBytes());
        	System.out.println("k"+ jsonRsTmp);
        });*/ 	// set callback
        c1.send();		//subscribe callback and something 
        
        
        //
        System.out.println("enter to exit!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        Scanner in =new Scanner(System.in) ;
        //client1.disconnect();
        //client1.
        
	}
}
