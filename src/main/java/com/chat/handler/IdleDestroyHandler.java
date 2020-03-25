package com.chat.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class IdleDestroyHandler extends ChannelInboundHandlerAdapter {

    private int continueIdleTimes = 0;
    private int maxIdleTimes = 3;

    public IdleDestroyHandler(int maxIdleTimes) {
        this.maxIdleTimes = maxIdleTimes;
    }

    public IdleDestroyHandler() {}

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state() == IdleState.READER_IDLE) {
                continueIdleTimes++;
                if (continueIdleTimes < maxIdleTimes) {
                    ctx.channel().close();
                }
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
