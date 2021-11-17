package com.learn.hivemq_mqttclient.server.mytestlearn;

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
 * &emsp;						test1 send 1 message without waiting for the successful connection					</br>
 * &emsp;						test2 send 1 message after successful connection									</br>
 * &emsp;						test whether we should wait for the successful connection before publishing message	</br>	
 * 																													</br>
 *
 * 
 * 测试出test1 这种不等待 就直接publish的 方式 是容易接受不到这一条内容的
 * 测试出test2 这种等待     是相对正确的做法
 * 
 * 在这个案例就可以发现 如果只是发送一条 test1 就已经容易丢失信息了
 * 所以
 * 在发送很多 message的 例子中, 
 * test1 容易发送不出去第一条, 因为没有等待 connect成功
 * 
 * 
 * 
 * @author laipl
 *
 */
public class TestMain_TestConnect {


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

        

        final InetSocketAddress LOCALHOST_EPHEMERAL1 = new InetSocketAddress("localhost",1883);
        final Mqtt5AsyncClient client = Mqtt5Client.builder().serverHost("localhost").buildAsync();
        /*
        // 这个只是参考写法而已 你可以删掉
        client.connect()
                .thenAccept(connAck -> System.out.println("connected " + connAck))
                .thenCompose(v -> client.publishWith().topic("demo/topic/b").qos(MqttQos.EXACTLY_ONCE).send())
                .thenAccept(publishResult -> System.out.println("published " + publishResult))
                .thenCompose(v -> client.disconnect())
                .thenAccept(v -> System.out.println("disconnected"));
		*/
        
        
        
        
        //---------------------------------- Test1 案例 ----------------------------------
        //client.connect().thenAccept(connAck -> client.publishWith().topic("test/topic").qos(MqttQos.AT_LEAST_ONCE).payload("hello".getBytes()).send());
        
        
        
        
        //---------------------------------- Test2 案例 ----------------------------------
        CompletableFuture<Mqtt5ConnAck> cplfu_connect_rslt = client.connect();
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
    	cplfu_connect_rslt.thenAccept(connAck -> client.publishWith().topic(topic).qos(MqttQos.AT_LEAST_ONCE).payload("hello".getBytes()).send());
        
    	
    	
    	
    	
    	
    	
    	
        System.out.println("see that everything above is async");
        for (int i = 0; i < 5; i++) {
        	try {
        		Thread.sleep(15000);
    		} catch (InterruptedException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
            System.out.println("...");
        }
        

    }

}
