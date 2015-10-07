package siosio.handler;

import io.netty.channel.ChannelHandlerContext;

@FunctionalInterface
public interface InboundExceptionCaught {
    void invoke(ChannelHandlerContext ctx, Throwable cause);
}
