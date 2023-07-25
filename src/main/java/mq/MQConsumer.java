package mq;

import com.alibaba.fastjson.JSONObject;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rank.AsyncRankService;

import java.util.List;

//get win/lose message from queue.
public class MQConsumer {

    static private final Logger LOGGER = LoggerFactory.getLogger(MQConsumer.class);

    MQConsumer(){
    }

    static public void init() {
        //create a queue consumer
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("herostory");

        consumer.setNamesrvAddr(" ");

        try {
            // you have to ensure this theme exists otherwise the listening process will not continue.
            consumer.subscribe("herostory_victor", "*");// sub expression will only process messages with specific tags.
            consumer.registerMessageListener(new MessageListenerConcurrently() {

                @Override
                public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                    for (MessageExt msgExt: msgs) {
                        if (null == msgExt) continue;

                        VictoryMessage victoryMessage = JSONObject.parseObject(msgExt.getBody(), VictoryMessage.class);

                        AsyncRankService.getInstance().refreshRank(
                                victoryMessage.winnerID,
                                victoryMessage.loserID
                        );
                    }
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
            });
            consumer.start();


        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }


    }
}
