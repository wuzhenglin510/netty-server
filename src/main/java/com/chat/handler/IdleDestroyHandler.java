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

    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TopLevelDataOuterClass.TopLevelData topLevelData) throws Exception {
        lossPingTimes = 0;
        if (topLevelData.getType().equals(TopLevelDataOuterClass.TopLevelData.Type.PING)) {
            TopLevelDataOuterClass.TopLevelData pingMessage = TopLevelDataOuterClass.TopLevelData.newBuilder()
                    .setType(TopLevelDataOuterClass.TopLevelData.Type.PONG)
                    .setSendTime(System.nanoTime())
                    .build();
            channelHandlerContext.writeAndFlush(pingMessage);
        } else {
            channelHandlerContext.fireChannelRead(topLevelData);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        System.out.println(evt);
        if (evt instanceof IdleStateEvent) {
            System.out.println("链路空闲通知: " + lossPingTimes);
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state() == IdleState.READER_IDLE) {
                lossPingTimes++;
                if (lossPingTimes > maxLossPingTimes) {
                    System.out.println("因空闲多次，服务端主动关闭链接");
                    ctx.channel().close();
                }
            }
        }
    }


}
