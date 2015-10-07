package siosio

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import kotlin.properties.Delegates

class Server(val config: Configuration) {

  companion object {
    fun start(init: Configuration.() -> Unit) {
      val configuration = Configuration()
      configuration.init()
      Server(configuration).start()
    }
  }

  fun start() {
    val bootstrap = createServerBootstrap()
    bootstrap.channel(NioServerSocketChannel::class.java)
    bootstrap.childHandler(object : ChannelInitializer<SocketChannel>() {
      override fun initChannel(ch: SocketChannel) {
        config.handlers(ChannelPipelineWrapper(ch.pipeline()))
      }
    })
    val channelFuture = bootstrap.bind(config.port).sync()
    channelFuture.channel().closeFuture().sync()
  }

  private fun createServerBootstrap(): ServerBootstrap {
    val serverBootstrap = ServerBootstrap()
    serverBootstrap.group(NioEventLoopGroup(), NioEventLoopGroup(config.childThreadCount))
    return serverBootstrap
  }
}

class Configuration {
  var childThreadCount: Int by Delegates.notNull()

  var port: Int by Delegates.notNull()

  lateinit var handlers: (ChannelPipelineWrapper) -> Unit
}

