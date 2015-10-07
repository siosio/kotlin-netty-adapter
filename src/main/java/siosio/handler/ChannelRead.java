package siosio.handler;

import io.netty.channel.ChannelHandlerContext;

@FunctionalInterface
public interface ChannelRead {

    void invoke(ChannelHandlerContext ctx, Object msg);

}
