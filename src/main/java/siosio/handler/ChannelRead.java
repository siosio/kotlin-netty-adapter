package siosio.handler;

import io.netty.channel.ChannelHandlerContext;

@FunctionalInterface
public interface ChannelRead<T> {

    void invoke(ChannelHandlerContext ctx, T msg);

}
