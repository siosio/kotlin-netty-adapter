package siosio

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.channel.ChannelOutboundHandlerAdapter
import io.netty.channel.ChannelPipeline
import siosio.handler.ChannelRead
import siosio.handler.InboundExceptionCaught
import siosio.handler.OutboundExceptionCaught

class ChannelPipelineWrapper(val pipeline: ChannelPipeline) : ChannelPipeline by pipeline {

  /**
   * add channel read handler.
   */
  fun <T> addLast(name: String, handler: ChannelRead<T>): Unit {
    addLast(name, object : ChannelInboundHandlerAdapter() {
      override fun channelRead(ctx: ChannelHandlerContext?, msg: Any?) {
        @Suppress("UNCHECKED_CAST")
        handler.invoke(ctx, msg as T)
      }
    })
  }

  /**
   * add inbound exception caught handler.
   */
  fun addLast(name: String, handler: InboundExceptionCaught) {
    addLast(name, object : ChannelInboundHandlerAdapter() {
      override fun exceptionCaught(ctx: ChannelHandlerContext?, cause: Throwable?) {
        handler.invoke(ctx, cause)
      }
    })
  }

  /**
   * add outbound exception caught handler.
   */
  fun addLast(name: String, handler: OutboundExceptionCaught) {
    addLast(name, object : ChannelOutboundHandlerAdapter() {
      override fun exceptionCaught(ctx: ChannelHandlerContext?, cause: Throwable?) {
        handler.invoke(ctx, cause)
      }
    })
  }
}



