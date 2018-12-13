package com.github.tinymini.netty.core.web.client;
import java.util.concurrent.TimeUnit;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.timeout.ReadTimeoutHandler;

/**
 * http 클라이언트 초기화
 * 
 * @author shkim
 *
 */
public final class HttpClientInitializer extends ChannelInitializer<SocketChannel> {
  private HttpClientHandler httpClientHandler;
  private SslContext sslContext;

  public HttpClientInitializer(HttpClientHandler httpClientHandler, SslContext sslContext) {
    this.httpClientHandler = httpClientHandler;
    this.sslContext = sslContext;
  }

  @Override
  protected void initChannel(SocketChannel ch) throws Exception {
    ChannelPipeline p = ch.pipeline();
    if (sslContext != null) {
      p.addLast(sslContext.newHandler(ch.alloc()));
    }
    p.addLast(new HttpClientCodec());
    p.addLast(new HttpContentDecompressor());
    int readTimeout = this.httpClientHandler.getRequestModel().getReadTimeout();
    if (readTimeout > 0){
      p.addLast(new ReadTimeoutHandler(readTimeout, TimeUnit.MILLISECONDS));
    }
    p.addLast(this.httpClientHandler);
  }

}
