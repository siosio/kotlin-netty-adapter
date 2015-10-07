package siosio.servertest

import io.netty.bootstrap.Bootstrap
import io.netty.buffer.ByteBuf
import io.netty.buffer.PooledByteBufAllocator
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.channel.ChannelInitializer
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import siosio.Server
import siosio.handler.ChannelRead
import siosio.handler.InboundExceptionCaught
import java.nio.charset.Charset

object RunServer {
  @JvmStatic fun main(args: Array<String>) {
    Server.start {
      childThreadCount = 5
      port = 9999

      handlers = {
        it.addLast("reader", ChannelRead { ctx, msg ->
          val buf = msg as ByteBuf
          println("受信したよ -> ${buf.toString(Charset.forName("utf-8"))}")
          ctx.writeAndFlush(msg)
        })
      it.addLast("error", InboundExceptionCaught { ctx, cause -> println("cause = $cause") })
      }
    }
  }
}

object RunClient {
  @JvmStatic fun main(args: Array<String>) {
    val bootstrap = Bootstrap()
        .group(NioEventLoopGroup())
        .channel(NioSocketChannel::class.java)
        .handler(object : ChannelInitializer<SocketChannel>() {
          override fun initChannel(ch: SocketChannel) {
            ch.pipeline().addLast("read", object : ChannelInboundHandlerAdapter() {
              override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
                val buf = msg as ByteBuf
                val result = buf.readBytes(buf.readableBytes()).toString(Charset.forName("utf-8"))
                println("result = ${result}")
                ctx.channel().eventLoop().shutdownGracefully()
              }
            })
          }
        })

    val channelFuture = bootstrap.connect("localhost", 9999).sync()
    val channel = channelFuture.channel()
    val buf: ByteBuf = PooledByteBufAllocator().buffer(10)
    buf.writeBytes("siosio".toByteArray("utf-8"))
    channel.writeAndFlush(buf)
    channel.closeFuture().sync()
  }
}
