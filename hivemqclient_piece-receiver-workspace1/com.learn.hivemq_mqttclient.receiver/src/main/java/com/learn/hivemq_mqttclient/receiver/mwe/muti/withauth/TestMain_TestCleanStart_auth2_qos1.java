package com.learn.hivemq_mqttclient.receiver.mwe.muti.withauth;

import java.net.InetSocketAddress;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient.Mqtt5SubscribeAndCallbackBuilder;
import com.hivemq.client.mqtt.mqtt5.message.auth.Mqtt5SimpleAuth;
import com.hivemq.client.mqtt.mqtt5.message.connect.Mqtt5Connect;
import com.hivemq.client.mqtt.mqtt5.message.connect.connack.Mqtt5ConnAck;
import com.hivemq.client.mqtt.mqtt5.message.disconnect.Mqtt5DisconnectReasonCode;
import com.hivemq.client.mqtt.mqtt5.message.subscribe.suback.Mqtt5SubAck;

public class TestMain_TestCleanStart_auth2_qos1 {
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
        /*
        Mqtt5AsyncClient client1 = Mqtt5Client.builder().serverAddress(LOCALHOST_EPHEMERAL1).identifier(clientId).automaticReconnectWithDefaultConfig()
        		.addConnectedListener(context -> {
        			System.out.println("kkk"+context.toString());
                })
        		.buildAsync();*/
        Mqtt5AsyncClient client1 = Mqtt5Client.builder().serverAddress(LOCALHOST_EPHEMERAL1).identifier(clientId).buildAsync();
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
        Mqtt5SimpleAuth simpleAuth = Mqtt5SimpleAuth.builder().username(myuserName).password(mypwd.getBytes()).build();
        Mqtt5Connect connectMessage = Mqtt5Connect.builder().cleanStart(false).sessionExpiryInterval(500).simpleAuth(simpleAuth).build();
        // 因为pahomqtt 我是用的 是不需要等待!!!!!
        // 例如 broker没打开, 就当做连接失败, 所以这里的connect也不需要做什么, 使用类似的操作就好
        CompletableFuture<Mqtt5ConnAck> cplfu_connect_rslt = client1.connect(connectMessage);
        
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

        //CompletableFuture<Mqtt5SubAck> cf1 = c1.send();		//subscribe callback and something 
		
		try {
			CompletableFuture<Mqtt5SubAck> cf1 = c1.send().orTimeout(2000, TimeUnit.MILLISECONDS);		//subscribe callback and something 
			System.out.println("gg:"+cf1.get().getReasonString().toString());
			System.out.println("gg:"+cf1.get().getUserProperties().toString());
			System.out.println("gg:"+cf1.get().getType().toString());
			/*
			while(cf1.get().get) {
				
			}*/
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
        String strAbsorbEnter = in.nextLine();
        
        // 这样子 是没有办法 disconnect之后就关闭的
        //client1.disconnect();
        
        // 这样就可以
        /*
        client1.disconnectWith()
        .reasonCode(Mqtt5DisconnectReasonCode.DISCONNECT_WITH_WILL_MESSAGE) // send the will message
        .sessionExpiryInterval(0)                                           // we want to clear the session
        .send();*/
        // 这样也不可以
        //client1.disconnectWith().reasonCode(Mqtt5DisconnectReasonCode.DISCONNECT_WITH_WILL_MESSAGE).send();
        client1.disconnectWith().sessionExpiryInterval(0).send();
        // 当设置 了.sessionExpiryInterval(0)  这样可以, 但是 这样会导致 sessionExpiryInterval 为0
        // client1.disconnectWith().reasonCode(Mqtt5DisconnectReasonCode.DISCONNECT_WITH_WILL_MESSAGE).sessionExpiryInterval(0).send();
        try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        // 
        // 我发现 当我前面设置了 cleanStart(false).sessionExpiryInterval(500) 
        // 如果不用exit 是不能结束程序的
        // 除非设置了  client1.disconnectWith().sessionExpiryInterval(0).send();
        // 所以 也算是bug吧
        // ref: hivemq-mqtt-client/examples/src/main/java/com/hivemq/client/mqtt/examples/Mqtt5Features.java /
        //System.exit(0);
	}
}
