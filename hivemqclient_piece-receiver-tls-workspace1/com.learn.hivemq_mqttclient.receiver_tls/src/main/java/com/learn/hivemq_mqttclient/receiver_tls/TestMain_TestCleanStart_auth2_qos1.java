package com.learn.hivemq_mqttclient.receiver_tls;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.MqttClientBuilder;
import com.hivemq.client.mqtt.MqttClientSslConfig;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import com.hivemq.client.mqtt.mqtt5.Mqtt5RxClient;
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
        //String broker       = "tcp://localhost:1883";
        String brokerUri    = "ssl://192.168.239.137:8883";
        //String brokerUri    = "ssl://127.0.0.1:8883";				//我发现ca是noname servercrt虽然设置的是192.168.239.137:8883 但是还是可以用127.0.0.1来访问
        
        //String clientId     = "JavaSample";
        String clientId     = "JavaSample_recver";
        
        String myuserName	= "IamPublisherOne";
        String mypwd		= "123456";

        
    	//public String serverCaCrt_file					="server_cert.crt";
    	String serverCaCrt_file					="s_cacert.crt";
    	//public String serverCaCrt_file					="s_cacert.pem";
    	//public String serverCaCrt_file_dir				="/mycerts/my_own/samecn";	//从这里就可以看出, 我如果用不正确的证书会出问题的
    	String serverCaCrt_file_dir				="/mycerts/my_own";
    	String serverCaCrt_file_loc = null;
    	
    	
        
        
        
        String myusr_path = System.getProperty("user.dir");

		serverCaCrt_file_loc 							= 	myusr_path	+ serverCaCrt_file_dir		+"/" + 	serverCaCrt_file;
	         
        
		////////////////////file->FileInputStream->BufferedInputStream->X509Certificate //////////////////////////////////////
		// ref: https://gist.github.com/erickok/7692592
		
		FileInputStream fis= null;
		CertificateFactory cf = null;
		Certificate ca=null;
		try {
		cf = CertificateFactory.getInstance("X.509");
		// From https://www.washington.edu/itconnect/security/ca/load-der.crt
		fis = new FileInputStream(serverCaCrt_file_loc);
		InputStream caInput = new BufferedInputStream(fis);
		
		try {
			ca = cf.generateCertificate(caInput);
			// System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
		} finally {
			caInput.close();
		}
		} catch (FileNotFoundException e2) {
		// TODO Auto-generated catch block
		e2.printStackTrace();
		} catch (CertificateException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
		
		
		// Create a KeyStore containing our trusted CAs
		String keyStoreType = KeyStore.getDefaultType();
		KeyStore keyStore=null;
		TrustManagerFactory tmf = null;
		try {
			// Create a KeyStore containing our trusted CAs
			keyStoreType = KeyStore.getDefaultType();
			keyStore = KeyStore.getInstance(keyStoreType);
			keyStore.load(null, null);
			keyStore.setCertificateEntry("ca", ca);
			
			// Create a TrustManager that trusts the CAs in our KeyStore
			String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
			tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
			tmf.init(keyStore);
		} catch (KeyStoreException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		} catch (CertificateException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
		
		
		
		
		/*
		//hivemqtt client 不用这段
		
		// finally, create SSL socket factory
		SSLContext context=null;
		SSLSocketFactory mysocketFactory=null;
		try {
		//context = SSLContext.getInstance("SSL");
		context = SSLContext.getInstance("TLSv1.3");
		
		//context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
		//context.init(null,tmf.getTrustManagers(), new java.security.SecureRandom());
		//context.init(null,tmf.getTrustManagers(), null);
		context.init(null, tmf.getTrustManagers(), new java.security.SecureRandom());
		} catch (NoSuchAlgorithmException e2) {
		// TODO Auto-generated catch block
		e2.printStackTrace();
		} catch (KeyManagementException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
		mysocketFactory = context.getSocketFactory();
		*/        
        
        //
        
        
        
        //final InetSocketAddress LOCALHOST_EPHEMERAL1 = new InetSocketAddress("localhost",1883);
        final InetSocketAddress LOCALHOST_EPHEMERAL1 = new InetSocketAddress("192.168.239.137",8883);
        
        
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
        //Mqtt5AsyncClient client1 = Mqtt5Client.builder().serverAddress(LOCALHOST_EPHEMERAL1).identifier(clientId).buildAsync();
        // authentication
        //Mqtt5SimpleAuth simpleAuth = Mqtt5SimpleAuth.builder().username(myuserName).password(mypwd.getBytes()).build();
        //Mqtt5AsyncClient client1 = Mqtt5Client.builder().serverAddress(LOCALHOST_EPHEMERAL1).identifier(clientId).simpleAuth(simpleAuth).buildAsync();

        
        //换成
        
        //ref : getClient() from hivemq-mqtt-client/src/test/java/com/hivemq/client/example/Mqtt3ClientExample.java 
  		//MqttClientBuilder mqttClientBuilder = MqttClient.builder().identifier(UUID.randomUUID().toString()).serverHost(server).serverPort(port);
  		//Mqtt5AsyncClient client1 = Mqtt5Client.builder().serverAddress(LOCALHOST_EPHEMERAL1).identifier(clientId).simpleAuth(simpleAuth).buildAsync();
        // Mqtt5AsyncClient 不存在 trustManagerFactory 
        //Mqtt5AsyncClient client1 = Mqtt5Client.builder().serverAddress(LOCALHOST_EPHEMERAL1).identifier(clientId).simpleAuth(simpleAuth).trustManagerFactory(tmf).buildAsync();
  		
        MqttClientBuilder mqttClientBuilder = MqttClient.builder().serverAddress(LOCALHOST_EPHEMERAL1).identifier(clientId);
        //if (usesSsl) {
        mqttClientBuilder.sslConfig(MqttClientSslConfig.builder()
                      .keyManagerFactory(null)
                      .trustManagerFactory(tmf)		//.hostnameVerifier(hostnameVerifier)
                      .protocols(Arrays.asList("TLSv1.3"))		//这里指定TLSv1.3
                      .hostnameVerifier(new HostnameVerifier() {
                          public boolean verify(String s, SSLSession sslSession) {
                              return true;
                          }})
                      .build());
        //}

        //if (isNotUsingMqttPort(port)) {
        //    mqttClientBuilder.webSocketConfig(MqttWebSocketConfig.builder().serverPath(serverPath).build());
        //}
        //mqttClientBuilder.useMqttVersion3().buildRx();
        Mqtt5RxClient client1_rx = mqttClientBuilder.useMqttVersion5().buildRx();
        // 虽然Mqtt5RxClient 是 reactive 那一套做法, 可以避免一定的callback hell 
        // 因为其他的是用callback, 为了对比控制变量, 我就 打算用 callback 和 future的那一套做法 
        // 所以 再转成 async
        Mqtt5AsyncClient client1_asycn = client1_rx.toAsync();
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
        CompletableFuture<Mqtt5ConnAck> cplfu_connect_rslt = client1_asycn.connect(connectMessage);
        
        //------------------------------- 第 A2 种写法 --------------------------------------
		// 因为MqttAsyncClient.MqttSubscribeAndCallbackBuilder -> Mqtt5SubscribeAndCallbackBuilder.Start.Complete -> Mqtt5SubscribeAndCallbackBuilder.Start
        Mqtt5AsyncClient.Mqtt5SubscribeAndCallbackBuilder.Start subscribeBuilder1 = client1_asycn.subscribeWith();
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
        client1_asycn.disconnectWith().sessionExpiryInterval(0).send();
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
