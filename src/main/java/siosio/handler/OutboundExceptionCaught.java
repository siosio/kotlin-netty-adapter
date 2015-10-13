package siosio.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;

@FunctionalInterface
public interface OutboundExceptionCaught {
    void invoke(ChannelHandlerContext ctx, Throwable cause);
}
