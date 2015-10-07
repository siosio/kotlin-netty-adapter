package siosio

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.channel.ChannelPipeline
import siosio.handler.ChannelRead
import siosio.handler.InboundExceptionCaught

class ChannelPipelineWrapper(val pipeline: ChannelPipeline) : ChannelPipeline by pipeline {

  fun addLast(name: String, handler: ChannelRead): Unit {
    addLast(name, object: ChannelInboundHandlerAdapter() {
      override fun channelRead(ctx: ChannelHandlerContext?, msg: Any?) {
        handler.invoke(ctx, msg)
      }
    })
  }

  fun addLast(name:String, handler: InboundExceptionCaught) {
    addLast(name, object: ChannelInboundHandlerAdapter() {
      override fun exceptionCaught(ctx: ChannelHandlerContext?, cause: Throwable?) {
        handler.invoke(ctx, cause)
      }
    })
  }
}



