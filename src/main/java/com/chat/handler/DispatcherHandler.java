package com.chat.handler;

import com.chat.proto.TopLevelDataOuterClass;
import com.chat.proto.business.WordMessageOuterClass;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class DispatcherHandler extends SimpleChannelInboundHandler<TopLevelDataOuterClass.TopLevelData> {

    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TopLevelDataOuterClass.TopLevelData topLevelData) throws Exception {
        System.out.println("读取到数据 【: " + topLevelData.toString() + "】");
        TopLevelDataOuterClass.TopLevelData message = TopLevelDataOuterClass.TopLevelData.newBuilder()
                .setType(TopLevelDataOuterClass.TopLevelData.Type.WORD)
                .setSendTime(System.nanoTime())
                .setWordMessage(WordMessageOuterClass.WordMessage.newBuilder().setText("hello client, i had receive you: " + topLevelData.getWordMessage().getText()).build())
                .build();
        channelHandlerContext.writeAndFlush(message);
    }
}
