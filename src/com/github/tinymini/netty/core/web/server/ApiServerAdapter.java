package com.github.tinymini.netty.core.web.server;

import java.io.File;
import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import com.github.tinymini.netty.common.util.BeanUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

public class ApiServerAdapter implements InitializingBean, DisposableBean {
  private final Log logger = LogFactory.getLog(getClass());

  private int serverPort;

  private File cert;
  private File key;
  private String passPhrase;
  private boolean ssl;


  private EventLoopGroup bossGroup;
  private EventLoopGroup workerGroup;
  private ServerBootstrap b;

  public int getServerPort() {
    return serverPort;
  }

  public void setServerPort(int serverPort) {
    this.serverPort = serverPort;
  }

  public void setCertPath(String certPath) throws IOException {
    this.cert = getFile(certPath);
  }

  public void setKeyPath(String keyPath) throws IOException {
    this.key = getFile(keyPath);;
  }

  public String getPassPhrase() {
    return passPhrase;
  }

  public void setPassPhrase(String passPhrase) {
    this.passPhrase = passPhrase;
  }

  public boolean getSsl() {
    return ssl;
  }

  public void setSsl(boolean ssl) {
    this.ssl = ssl;
  }

  public File getFile(String path) throws IOException {
    File result = null;
    if (path.startsWith("classpath:")) {
      result = BeanUtils.getContext().getResource(path.substring(10)).getFile();
    } else {
      result = new File(path);
    }
    return result;
  }


  @Override
  public void afterPropertiesSet() throws Exception {
    if (logger.isInfoEnabled()) {
      logger.info(this.serverPort + " port is started");
    }

    bossGroup = new NioEventLoopGroup(1);
    workerGroup = new NioEventLoopGroup();
    SslContext sslCtx = null;

    try {
      b = new ServerBootstrap();

      b.group(bossGroup, workerGroup);
      b.channel(NioServerSocketChannel.class);
      b.handler(new LoggingHandler(LogLevel.DEBUG));
      if (ssl) {
        sslCtx = SslContextBuilder.forServer(cert, key, passPhrase).build();
      }
      b.childHandler(new ApiServerInitializer(sslCtx));
      b.childOption(ChannelOption.TCP_NODELAY, true);
//      b.option(ChannelOption.SO_KEEPALIVE, true);
      b.option(ChannelOption.SO_REUSEADDR, true);
      b.option(ChannelOption.SO_BACKLOG, 512);

      b.bind(serverPort).sync();
    } catch (Exception ex) {
      if (logger.isInfoEnabled()) {
        ex.printStackTrace();
        logger.info("fail to bind port : " + this.serverPort);
      }
      throw ex;
    }

    if (logger.isInfoEnabled()) {
      logger.info("listening port : " + this.serverPort);
    }
  }

  @Override
  public void destroy() throws Exception {
    if (logger.isInfoEnabled()) {
      logger.info(this.serverPort + " is start destroy()");
    }

    int tryCount = 0;

    if (workerGroup != null && !workerGroup.isShutdown()) {
      workerGroup.shutdownGracefully();
    }

    while (!workerGroup.isShutdown() && tryCount <= 3) {
      tryCount++;

      Thread.sleep(3000);
    }

    logger.info("\n" + "workerGroup is Shutdown=[" + workerGroup.isShutdown() + "]\n"
        + "workerGroup is Shuttingdown=[" + workerGroup.isShuttingDown() + "]\n"
        + "workerGroup is Terminated=[" + workerGroup.isTerminated() + "]");

    if (bossGroup != null && !bossGroup.isShutdown()) {
      tryCount = 0;

      bossGroup.shutdownGracefully();
    }

    while (!bossGroup.isShutdown() && tryCount <= 3) {
      tryCount++;
      Thread.sleep(3000);
    }

    logger.info("\n" + "bossGroup is Shutdown=[" + bossGroup.isShutdown() + "]\n"
        + "bossGroup is Shuttingdown=[" + bossGroup.isShuttingDown() + "]\n"
        + "bossGroup is Terminated=[" + bossGroup.isTerminated() + "]");

    workerGroup = null;
    bossGroup = null;
    b = null;
  }
}
