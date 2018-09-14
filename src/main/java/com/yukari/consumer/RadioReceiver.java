package com.yukari.consumer;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import com.yukari.entity.RadioMQModel;
import com.yukari.netty.NettyConfig;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RadioReceiver {


    // 接收消息
    @RabbitListener(
            bindings = @QueueBinding(value = @Queue(value = "gift_radio-queue",durable = "true"),
                    exchange = @Exchange(value = "gift_radio-exchange",durable = "true",type = "topic"),
                    key = "gift_radio.*"
            )
    )
    @RabbitHandler
    public void onBulletMessage (@Payload RadioMQModel radio, @Headers Map<String,Object> headers, Channel channel) throws Exception{
        // 消费者操作
        System.out.println("收到消息，开始工作...");

        System.out.println(radio.getGift_name());

        // ACK 手动签收消息需手动确认签收
        channel.basicAck((Long) headers.get(AmqpHeaders.DELIVERY_TAG),false);

        // 向websocket客户端发送消息
        if (NettyConfig.group.size() > 0) {
            TextWebSocketFrame tws = new TextWebSocketFrame(JSON.toJSONString(radio));
            NettyConfig.group.writeAndFlush(tws);
        }

    }



}
