package com.chat.handler;

import com.chat.proto.TopLevelDataOuterClass;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class IdleDestroyHandler extends SimpleChannelInboundHandler<TopLevelDataOuterClass.TopLevelData> {

    private int lossPingTimes = 0;
    private int maxLossPingTimes = 3;

    public IdleDestroyHandler(int maxIdleTimes) {
        this.maxLossPingTimes = maxIdleTimes;
    }

    public IdleDestroyHandler() {}

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TopLevelDataOuterClass.TopLevelData topLevelData) throws Exception {
        lossPingTimes = 0;
        if (topLevelData.getType().equals(TopLevelDataOuterClass.TopLevelData.Type.PING)) {
            System.out.println("receive ping");
            TopLevelDataOuterClass.TopLevelData pongMessage = TopLevelDataOuterClass.TopLevelData.newBuilder()
                    .setType(TopLevelDataOuterClass.TopLevelData.Type.PONG)
                    .setSendTime(System.nanoTime())
                    .build();
            channelHandlerContext.channel().writeAndFlush(pongMessage);
            System.out.println("response pong");
        } else {
            channelHandlerContext.fireChannelRead(topLevelData);
        }
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state() == IdleState.READER_IDLE) {
                lossPingTimes++;
                if (lossPingTimes > maxLossPingTimes) {
                    System.out.println("close for long time read idle");
                    ctx.channel().close();
                }
            }
        }
    }


}
