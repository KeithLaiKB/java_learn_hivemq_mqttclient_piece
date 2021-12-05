package com.learn.hivemq_mqttclient.server.mwe.qos;

import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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
public class Test1BuildPublishChainedMessages {

	public static void main(String[] args) {

		//String topic        = "MQTT Examples";
		String topic = "sensors/temperature";
		//String content      = "Message from MqttPublishSample";
		String content = "你好";
		//String content      = "hi_myfriend";
		MqttQos qos = MqttQos.AT_MOST_ONCE;
		//String broker       = "tcp://iot.eclipse.org:1883";
		String broker = "tcp://localhost:1883";
		//String broker       = "ssl://localhost:8883";
		String clientId = "JavaSample";
		//MemoryPersistence persistence = new MemoryPersistence();

		//------------------------------- 创建 mqtt client --------------------------------------
		final InetSocketAddress LOCALHOST_EPHEMERAL1 = new InetSocketAddress("localhost", 1883);

		Mqtt5AsyncClient client1 = Mqtt5Client.builder().serverAddress(LOCALHOST_EPHEMERAL1).identifier(clientId)
				.buildAsync();

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
		for (int i = 0; i <= 1000 - 1; i++) {
			String str_content_tmp = content + ":" + (i + 1);
			
			Mqtt5Publish publishMessage = Mqtt5Publish.builder().topic(topic).qos(MqttQos.AT_LEAST_ONCE)
					.payload(str_content_tmp.getBytes()).build();
			client1.publish(publishMessage);
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		client1.disconnect();

	}

}
