package com.learn.hivemq_mqttclient.receiver.mwe.muti.withauth.couldbedeleted;

import java.net.InetSocketAddress;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient.Mqtt5SubscribeAndCallbackBuilder;
import com.hivemq.client.mqtt.mqtt5.message.auth.Mqtt5SimpleAuth;
import com.hivemq.client.mqtt.mqtt5.message.connect.Mqtt5Connect;
import com.hivemq.client.mqtt.mqtt5.message.connect.connack.Mqtt5ConnAck;
import com.hivemq.client.mqtt.mqtt5.message.subscribe.suback.Mqtt5SubAck;

public class TestMain_TestCleanStart_auth_qos1 {
	public static void main(String[] args) {

        //String topic        = "MQTT Examples";
        String topic        = "sensors/temperature";
        //String content      = "Message from MqttPublishSample";
        String content      = "receiver";
        int qos             = 1;
        //String broker       = "tcp://iot.eclipse.org:1883";
        String broker       = "tcp://localhost:1883";
        //String clientId     = "JavaSample";
        String clientId     = "JavaSample_revcevier";
        //MemoryPersistence persistence = new MemoryPersistence();
        
        
        String myuserName	= "IamPublisherOne";
        String mypwd		= "123456";
        
        
        
        final InetSocketAddress LOCALHOST_EPHEMERAL1 = new InetSocketAddress("localhost",1883);
        //Mqtt5Client sampleClient = new Mqtt5Client(broker, clientId, persistence);
        //
        //Mqtt5Client client1 = Mqtt5Client.builder().identifier(clientId).build();
        //ref: AsyncDemo
        //Mqtt5AsyncClient client1 = Mqtt5Client.builder().serverHost("localhost").identifier(clientId).buildAsync();
        //.useMqttVersion5()
        Mqtt5AsyncClient client1 = Mqtt5Client.builder().serverHost("localhost").identifier(clientId).automaticReconnectWithDefaultConfig()
        		.addConnectedListener(context -> {
        			System.out.println("kkk"+context.toString());
                })
        		.buildAsync();
        
        // authentication
        //Mqtt5SimpleAuth simpleAuth = Mqtt5SimpleAuth.builder().username(myuserName).password(mypwd.getBytes()).build();
        //Mqtt5AsyncClient client1 = Mqtt5Client.builder().serverAddress(LOCALHOST_EPHEMERAL1).identifier(clientId).simpleAuth(simpleAuth).buildAsync();
        
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
        //
        // 然后我选择用clean start 的时候 还需要设置 Mqtt5Connect
        // ref: https://www.hivemq.com/blog/hivemq-mqtt-client-features/fluent-api/
        //Mqtt5Connect connectMessage = Mqtt5Connect.builder().cleanStart(false).sessionExpiryInterval(500).build();
        //CompletableFuture<Mqtt5ConnAck> cplfu_connect_rslt = client1.connect(connectMessage);
        //CompletableFuture<Mqtt5ConnAck> cplfu_connect_rslt = client1.connectWith().cleanStart(false).sessionExpiryInterval(500L).keepAlive(600).send();
        //CompletableFuture<Mqtt5ConnAck> cplfu_connect_rslt = client1.connectWith().send();
        
        //CompletableFuture<Mqtt5ConnAck> cplfu_connect_rslt = client1.connectWith().cleanStart(true).send();
        //
    
			client1.connectWith()
			.cleanStart(false)
			.sessionExpiryInterval(500)
			.simpleAuth()
			    .username(myuserName)
			    .password(mypwd.getBytes())
			    .applySimpleAuth().send();

        //
        //
        //wait
        // 注意!!!!!!!!!
    	// while (cplfu_connect_rslt.isDone() == false) {
        // 如果 像上面这句, 不加上 cplfu_connect_rslt.isCompletedExceptionally() == true , 
        // 就会出现 broker 即使关闭了, 它也会认为是 isDone, 只不过是属于 complete exceptionally, 
        // for循环结束, 进行发送, 然而此时是connect失败的, 这种发送 broker是接受不到的
        // 
        // 所以 我要加多一个条件就是 || cplfu_connect_rslt.isCompletedExceptionally() == true 
        // 如果 未完成 或者 complete exceptionally 则一直等待, 有一点点类似于 MqttAsyncClient sampleClient.connect(connOpts, null, null).waitForCompletion(-1);
    	// 但我暂时还 不知道 paho mqtt 会不会有 complete exceptionally 而且相关的处理方式!!!!
        /*
        while (cplfu_connect_rslt.isDone() == false || cplfu_connect_rslt.isCompletedExceptionally() == true){
    		// 这里的 sleep 可以不用, 不影响主逻辑
    		// 只不过 这里加了个 sleep, 可以减少 不停地loop, 因为太多loop会给计算机带来的资源消耗
        	try {
        		Thread.sleep(1000);
    		} catch (InterruptedException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}*/
        /*
        try {
			cplfu_connect_rslt.get().getResponseInformation();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
    	
    	// 或者 注意这里的 timeout填-1的效果 和 pahomqtt不同, 这里timeout填-1 就相当于不wait, 好像跟0一样
    	// CompletableFuture<Mqtt5ConnAck> cplfu_connect_rslt1 = client1.connect().orTimeout(timeout, TimeUnit.MILLISECONDS);
    	
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
        c1.callback(publish -> System.out.println("received message: " + publish + "////"+ new String(publish.getPayloadAsBytes())) ); 	// set callback
        CompletableFuture<Mqtt5SubAck> cf1 = c1.send();		//subscribe callback and something 
        try {
			System.out.println("gg:"+cf1.get().getReasonString().toString());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        

        //
        System.out.println("enter to exit!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        Scanner in =new Scanner(System.in) ;
        //client1.disconnect();
        //client1.
        
	}
}
