package com.dempe.ocean.bus;

import com.dempe.ocean.common.pack.Unpack;
import com.dempe.ocean.common.protocol.Request;
import com.dempe.ocean.common.protocol.mqtt.PublishMessage;
import com.dempe.ocean.core.NettyUtils;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * Created with IntelliJ IDEA.
 * User: Dempe
 * Date: 2016/2/23
 * Time: 11:29
 * To change this template use File | Settings | File Templates.
 */
public class BusBusinessHandler extends ChannelHandlerAdapter {

    public static final Logger LOGGER = LoggerFactory.getLogger(BusBusinessHandler.class);

    DispatcherProcessor processor;

    public BusBusinessHandler(DispatcherProcessor processor) {
        this.processor = processor;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        LOGGER.info("msg:{}", msg.toString());
        if (msg instanceof PublishMessage) {
            PublishMessage message = (PublishMessage) msg;
            LOGGER.info("message>>>>>>>>>>>>>>>>>>>>>{}", message);
            ByteBuffer payload = message.getPayload();
            payload.flip();
            Integer messageID = message.getMessageID();
            String clientID = NettyUtils.clientID(ctx.channel());
            LOGGER.info("messageID:{}, ClientID:{}", messageID, clientID);

            Unpack unpack = new Unpack(payload.array());
            Short aShort = unpack.popShort();
            Request request =new Request();
            request.unmarshal(unpack);
            LOGGER.info("length:{}",aShort);
            String name = request.getName();

            LOGGER.info("dispatcher msg to {}", name);

            processor.dispatcher(ctx.channel(), name, request);
//

        }

    }


}

