package com.learn.hivemq_mqttclient.sender.mwe.muti.withauth;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import com.hivemq.client.mqtt.mqtt5.message.auth.Mqtt5SimpleAuth;
import com.hivemq.client.mqtt.mqtt5.message.connect.Mqtt5Connect;
import com.hivemq.client.mqtt.mqtt5.message.connect.connack.Mqtt5ConnAck;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5PublishResult;

public class TestMain_Auth_SubOffl_SubOnl {

	public static void main(String[] args) {

        //String topic        = "MQTT Examples";
        String topic        = "sensors/temperature";
        //String content      = "Message from MqttPublishSample";
        //String content      = "你好";
        String content      = "hello";
        //String content      = "hi_myfriend";
        int qos             = 1;
        //String broker       = "tcp://iot.eclipse.org:1883";
        String broker       = "tcp://localhost:1883";
        //String broker       = "ssl://localhost:8883";
        String clientId     = "JavaSample_sender";
        //MemoryPersistence persistence = new MemoryPersistence();
        
        String myuserName	= "IamPublisherOne";
        String mypwd		= "123456";
        
        
        
        
        
        
        
        //------------------------------- 创建 mqtt client --------------------------------------
        /*
        Mqtt5BlockingClient client = Mqtt5Client.builder()
                .identifier(UUID.randomUUID().toString())
                .serverHost("broker.hivemq.com")
                .buildBlocking();
        */
        final InetSocketAddress LOCALHOST_EPHEMERAL1 = new InetSocketAddress("localhost",1883);
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
        //
        // authentication
        Mqtt5SimpleAuth simpleAuth = Mqtt5SimpleAuth.builder().username(myuserName).password(mypwd.getBytes()).build();
        Mqtt5AsyncClient client1 = Mqtt5Client.builder().serverAddress(LOCALHOST_EPHEMERAL1).identifier(clientId).simpleAuth(simpleAuth).buildAsync();

        //------------------------------- client connect --------------------------------------
        //
        // 一共有两种方式进行auth
        // 第一种 直接在 Mqtt5AsyncClient 	中使用 .simpleAuth(simpleAuth)
        // 第二种 Mqtt5AsyncClient 		不使用 .simpleAuth(simpleAuth) 
        //		然而在 Mqtt5Connect 中使用 .simpleAuth(simpleAuth)
        //			也就是
        //				Mqtt5Connect connectMessage = Mqtt5Connect.builder().cleanStart(false).sessionExpiryInterval(500).simpleAuth(simpleAuth).build();
        // 我在这里subscriber 这里尝试 使用 第二种, 当然 使用第一种也是可以的
        //
        /////////////////////////////////////////////////
        //
        // 一定要注意 connect之后 如果不用thenAccept之类的方法, 就一定要 让他 等一等 , 等connect成功!!!!!!!!!!!!!!!!
        // 不然刚connect 就去publish 会出现第一条无法publish, 然后成功publish第二条的现象
        // 有点像 MqttAsyncClient sampleClient.connect(connOpts, null, null).waitForCompletion(-1); 需要block自己然后直到连接成功才进行下一步
        // 只是我选择 用段时间等待而已
        //
        // 然后我选择用clean start 的时候 还需要设置 Mqtt5Connect
        // ref: https://www.hivemq.com/blog/hivemq-mqtt-client-features/fluent-api/
        Mqtt5Connect connectMessage = Mqtt5Connect.builder().cleanStart(true).build();
        
        //
        // 因为pahomqtt 我是用的 是不需要等待!!!!!
        // 例如 broker没打开, 就当做连接失败, 所以这里的connect也不需要做什么, 使用类似的操作就好
        CompletableFuture<Mqtt5ConnAck> cplfu_connect_rslt =   client1.connect(connectMessage);
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
        for(int i=0;i<=1000-1;i++) {
        	String str_content_tmp = content +":"+(i+1);
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
        	/*
        	 *
        	 这两个用来删除 Mosquitto里面的reatain message
        	c1.retain(true);
        	c1.payload(new byte[0]);
        	
        	*/
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
        		Thread.sleep(1000);
    		} catch (InterruptedException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        }
        

        client1.disconnect();
        

    }

}
