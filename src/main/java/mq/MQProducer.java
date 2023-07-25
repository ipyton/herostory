package mq;

import com.alibaba.fastjson.JSONObject;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.XmlType;

//produce a win/lose message
public class MQProducer {

    static private final Logger LOGGER = LoggerFactory.getLogger(MQProducer.class);

    static private DefaultMQProducer _producer = null;

    private MQProducer(){}

    static public void init() {
        try {
            DefaultMQProducer producer = new DefaultMQProducer("herostory");
            producer.setNamesrvAddr("10.0.1.10:9876");
            producer.start();
            producer.setRetryTimesWhenSendAsyncFailed(3);

            _producer = producer;
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    static public void sendMsg(String topic, Object msg) {
        if (null == topic || null== msg) return;

        Message newMsg = new Message();
        newMsg.setTopic(topic);
        newMsg.setBody(JSONObject.toJSONBytes(msg));

        try {
            _producer.send(newMsg);

        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }

    }




}
