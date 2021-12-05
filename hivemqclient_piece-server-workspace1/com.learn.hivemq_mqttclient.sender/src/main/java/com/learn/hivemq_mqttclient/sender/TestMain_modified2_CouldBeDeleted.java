package com.learn.hivemq_mqttclient.server;

import java.net.InetSocketAddress;
import java.util.LinkedHashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hivemq.client.internal.mqtt.MqttRxClient;
import com.hivemq.client.internal.mqtt.message.publish.MqttPublishBuilder;

import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import com.hivemq.client.mqtt.mqtt5.message.connect.connack.Mqtt5ConnAck;
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
public class TestMain_modified2_CouldBeDeleted {


	public static void main(String[] args) {

        //String topic        = "MQTT Examples";
        String topic        = "zigbee2mqtt/0x00124b00250c256f";
        //String content      = "Message from MqttPublishSample";
        String content      = "你好";
        //String content      = "hi_myfriend";
        int qos             = 2;
        //String broker       = "tcp://iot.eclipse.org:1883";
        String broker       = "tcp://localhost:1883";
        //String broker       = "ssl://localhost:8883";
        String clientId     = "JavaSample";
        //MemoryPersistence persistence = new MemoryPersistence();

        
        
        
        
        
        
        
        //------------------------------- 创建 mqtt client --------------------------------------
        /*
        Mqtt5BlockingClient client = Mqtt5Client.builder()
                .identifier(UUID.randomUUID().toString())
                .serverHost("broker.hivemq.com")
                .buildBlocking();
        */
        final InetSocketAddress LOCALHOST_EPHEMERAL1 = new InetSocketAddress("135.0.237.84",1883);
        //
        //
        //
        //
        //
        //  builder.serverAddress是去   MqttRxClientBuilder 的父类的     MqttRxClientBuilderBase的 serverAddress
        //  builder.buildAsync   是去  MqttRxClientBuilder 的父类的     MqttRxClientBuilderBase的 buildAsync
        // 其过程中会经过
        // MqttAsyncClient(final @NotNull MqttRxClient delegate) {
        //    this.delegate = delegate;
        // }
        // 所以初步认为 MqttAsyncClient 是包含了 MqttRxClient 
        Mqtt5AsyncClient client1 = Mqtt5Client.builder().serverAddress(LOCALHOST_EPHEMERAL1).identifier(clientId).buildAsync();
        //Mqtt5AsyncClient client1 = Mqtt5Client.builder().serverAddress(LOCALHOST_EPHEMERAL1).buildAsync();
        
        
        
        
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
		
		/*
        // 这样还是不会显示第一条, 因为 它只是把 connect和thenaccept 看成一个总流程,
        // 在这段流程确实是阻塞的, 但是这段流程 和下面的for循环 是不阻塞的, 
        //所以还是要用个 sleep 来保证一定connect成功的情况下 再去publish
		client1.connect().thenAccept((result)->{

    		System.out.println(result.toString());

    		});
		*/
		
		
		
		
		//------------------------------- client publish --------------------------------------
        for(int i=0;i<=1;i++) {
        	LinkedHashMap<String,Object> lhmap1 = new LinkedHashMap<>();
        	lhmap1.put("linkquality", 132);
        	lhmap1.put("state", "OFF");
        	//String str_content_tmp = "{\"linkquality\":,\"state\":\"OFF\"}";
        	//
        	//
        	ObjectMapper mapperTmp = new ObjectMapper();
        	String str_content_tmp=null;
			try {
				str_content_tmp = mapperTmp.writeValueAsString(lhmap1);
			} catch (JsonProcessingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        	//client1.publishWith().topic(topic).qos(MqttQos.AT_LEAST_ONCE).payload(content.getBytes()).send();
        	
        	
        	/*
        	//------------------------------- 第 A1 种写法 --------------------------------------
        	// 第A1种写法 ref: https://github.com/hivemq/hivemq-mqtt-client 的下面
        	Mqtt5Publish publishMessage = Mqtt5Publish.builder()
        	        .topic(topic)
        	        .qos(MqttQos.AT_LEAST_ONCE)
        	        .payload(contentTmp.getBytes())
        	        .build();
        	client1.publish(publishMessage);
        	*/
        	//
        	//
        	//
        	//
        	//------------------------------- 第 A2 种写法 --------------------------------------
        	// 第A2种写法 是A1写法的拆分而已
        	/*
        	Mqtt5PublishBuilder publishBuilder1= Mqtt5Publish.builder();
        	//
        	com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5PublishBuilder.Complete c1 = publishBuilder1.topic(topic);
        	c1.qos(MqttQos.AT_LEAST_ONCE);
        	c1.payload(str_content_tmp.getBytes());
        	//
        	Mqtt5Publish publishMessage = c1.build();
        	client1.publish(publishMessage);
        	System.out.println(str_content_tmp);
        	*/
        	//
        	/*
        	//A2方法中的 
        	// client1.publish(publishMessage); 
        	// 和  
        	// System.out.println(str_content_tmp); 
        	// 可以换成下面这种, 看起来更清晰一些 
        	client1.publish(publishMessage).thenAccept((result)->{
        		String sendedCtnTemp = new String(result.getPublish().getPayloadAsBytes());		//如果用 Qos0(MqttQos.AT_MOST_ONCE) 也可以获得内容的
        		System.out.println(sendedCtnTemp);
        		
        		});
        	*/
        	//
        	//
        	//
        	//
        	//------------------------------- 第 B1 种写法 --------------------------------------
        	// 第B1种写法 ref: hivemq-mqtt-client/examples/src/main/java/com/hivemq/client/mqtt/examples/Mqtt5Features.java / 
        	// 这个是一口气就能写完的, 这种 和 A1和A2的调用效果是一样的  因为他说 Fluent counterpart of publish(Mqtt5Publish)
        	/*client1.publishWith().topic(topic).qos(MqttQos.AT_LEAST_ONCE).payload(str_content_tmp.getBytes()).send();*/
        	//
        	//
        	//
        	//
        	//------------------------------- 第 B2 种写法 --------------------------------------
        	
        	// 第B2种写法 ref: hivemq-mqtt-client/examples/src/main/java/com/hivemq/client/mqtt/examples/Mqtt5Features.java / 
        	// 首先这里先用了 pulishWith();
        	// 因为 MqttPublishBuilder.Send<P> -> Mqtt5PublishBuilder.Send.Complete<P> -> Mqtt5PublishBuilder.Send<P>
        	com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5PublishBuilder.Send<CompletableFuture<Mqtt5PublishResult>>  publishBuilder1 = client1.publishWith();
        	// 因为Mqtt5PublishBuilder.Send.Complete 	->  Mqtt5PublishBuilder.Send	-> Mqtt5PublishBuilderBase 
        	// ->MqttPublishBuilder.Send			->  MqttPublishBuilder.Base		-> MqttPublishBuilder
        	// 于是找到了topic(str_topic)的方法
        	com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5PublishBuilder.Send.Complete<CompletableFuture<Mqtt5PublishResult>> c1 = publishBuilder1.topic(topic);
        	c1.qos(MqttQos.AT_LEAST_ONCE);
        	c1.payload(str_content_tmp.getBytes());
        	

        	// send(): the result when the built Mqtt5Publish is sent by the parent
        	c1.send();
        	System.out.println(str_content_tmp);
        	
        	
        	
        	/*
        	//B2方法中 
        	// c1.send(); 
        	// 和   
        	// System.out.println(str_content_tmp); 
        	// 可以换成下面这种, 看起来更清晰一些
        	c1.send().thenAccept((result)->{
        		String sendedCtnTemp = new String(result.getPublish().getPayloadAsBytes());		//如果用 Qos0(MqttQos.AT_MOST_ONCE) 也可以获得内容的
        		System.out.println(sendedCtnTemp);
        		
        		});
        	*/
			
    		
        	try {
        		Thread.sleep(500000);
    		} catch (InterruptedException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        }
        

        client1.disconnect();
        

    }

}
