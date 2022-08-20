package com.learn.hivemq_mqttclient.sender_tls;

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
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import com.hivemq.client.internal.mqtt.MqttClientSslConfigImpl;
import com.hivemq.client.internal.mqtt.MqttClientSslConfigImplBuilder;
import com.hivemq.client.internal.mqtt.MqttClientTransportConfigImpl;
import com.hivemq.client.internal.util.collections.ImmutableList;
import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.MqttClientBuilder;
import com.hivemq.client.mqtt.MqttClientSslConfig;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import com.hivemq.client.mqtt.mqtt5.Mqtt5RxClient;
import com.hivemq.client.mqtt.mqtt5.message.auth.Mqtt5SimpleAuth;
import com.hivemq.client.mqtt.mqtt5.message.connect.Mqtt5Connect;
import com.hivemq.client.mqtt.mqtt5.message.connect.connack.Mqtt5ConnAck;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5PublishResult;



public class TestMain_Auth_SubOffl_SubOnl {

	public static void main(String[] args) {

        //String topic        = "MQTT Examples";
        String topic        = "sensors/temperature";
        //String content      = "Message from MqttPublishSample";
        String content      = "你好";
        //String content      = "hi_myfriend";
        int qos             = 1;
        //String broker       = "tcp://iot.eclipse.org:1883";
        String broker       = "ssl://192.168.239.137:8883";
        //String broker       = "ssl://localhost:8883";
        String clientId     = "JavaSample";
        //MemoryPersistence persistence = new MemoryPersistence();
        
        String myuserName	= "IamPublisherOne";
        String mypwd		= "123456";
        
        
        //
        
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
		        
        
        //
        /*
		final List<String> expectedProtocols = Arrays.asList("TLSv1.2", "TLSv1.1");

        final MqttClientSslConfig sslConfig = MqttClientSslConfig.builder().protocols(expectedProtocols).build();
        
        
        //ref: https://github.com/hivemq/hivemq-mqtt-client/blob/00c0ec30e6bc5e9212532f60ebc815ff40fd24c1/src/main/java/com/hivemq/client/internal/mqtt/MqttClientSslConfigImpl.java
        //MqttClientSslConfigImpl DEFAULT = new MqttClientSslConfigImpl(null, null, null, null, (int)10_000 , null);
        //MqttClientSslConfigImpl DEFAULT = MqttClientSslConfigImpl
        		
        		
        //ref : hivemq-mqtt-client/src/test/java/com/hivemq/client/internal/mqtt/handler/ssl/MqttSslInitializerTest.java
        TrustManagerFactory tmf = null;

        ImmutableList<String> protocols = ImmutableList.of("TLSv1.1", "TLSv1.2");

		//打开ssl通道
		EmbeddedChannel embeddedChannel=embeddedChannel = new EmbeddedChannel();

        SSLEngine sslEngine = createSslEngine(embeddedChannel,
                new MqttClientSslConfigImplBuilder.Default().trustManagerFactory(tmf).protocols(protocols).build());
		*/
		
		
        
        //------------------------------- 创建 mqtt client --------------------------------------
        /*
        Mqtt5BlockingClient client = Mqtt5Client.builder()
                .identifier(UUID.randomUUID().toString())
                .serverHost("broker.hivemq.com")
                .buildBlocking();
        */
        //final InetSocketAddress LOCALHOST_EPHEMERAL1 = new InetSocketAddress("localhost",1883);
        final InetSocketAddress LOCALHOST_EPHEMERAL1 = new InetSocketAddress("192.168.239.137",8883);
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
        Mqtt5RxClient client1_rx = mqttClientBuilder.useMqttVersion5().simpleAuth(simpleAuth).buildRx();
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
        Mqtt5Connect connectMessage = Mqtt5Connect.builder().cleanStart(true).build();
        
        //
        // 因为pahomqtt 我是用的 是不需要等待!!!!!
        // 例如 broker没打开, 就当做连接失败, 所以这里的connect也不需要做什么, 使用类似的操作就好
        CompletableFuture<Mqtt5ConnAck> cplfu_connect_rslt =   client1_asycn.connect(connectMessage);
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
        	com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5PublishBuilder.Send<CompletableFuture<Mqtt5PublishResult>>  publishBuilder1 = client1_asycn.publishWith();
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
        

        client1_asycn.disconnect();
        

    }

}
