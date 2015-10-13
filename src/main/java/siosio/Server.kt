package siosio

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel

class Server(val config: Configuration) {

  companion object {
    @JvmStatic fun start(init: Configuration.() -> Unit) {
      val configuration = Configuration()
      configuration.init()
      Server(configuration).start()
    }
  }

  /**
   * start server
   */
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
    serverBootstrap.group(
        NioEventLoopGroup(config.bossThreadCount),
        NioEventLoopGroup(config.childThreadCount))
    return serverBootstrap
  }
}

/**
 * Server config.
 */
class Configuration {

  var bossThreadCount: Int = Runtime.getRuntime().availableProcessors() * 2
  var childThreadCount: Int = Runtime.getRuntime().availableProcessors() * 2

  var port: Int = 9999

  var handlers: (ChannelPipelineWrapper) -> Unit = {}
}

