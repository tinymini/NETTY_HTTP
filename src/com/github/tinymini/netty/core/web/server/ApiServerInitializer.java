package com.github.tinymini.netty.core.web.server;


import com.github.tinymini.netty.core.web.service.RequestParser;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.cors.CorsConfig;
import io.netty.handler.codec.http.cors.CorsConfigBuilder;
import io.netty.handler.codec.http.cors.CorsHandler;
import io.netty.handler.ssl.SslContext;

public class ApiServerInitializer extends ChannelInitializer<SocketChannel> {
  /** ssl 컨텍스트 */
  private final SslContext sslCtx;

  public ApiServerInitializer(SslContext sslCtx) {
    this.sslCtx = sslCtx;
  }

  @Override
  protected void initChannel(SocketChannel ch) throws Exception {
    ChannelPipeline p = ch.pipeline();
    if (sslCtx != null) {
      p.addLast(sslCtx.newHandler(ch.alloc()));
    }
    p.addLast(new HttpRequestDecoder());
    p.addLast(new HttpObjectAggregator(65536));
    p.addLast(new HttpResponseEncoder());
    p.addLast(new HttpContentCompressor());
    CorsConfig corsConfig = CorsConfigBuilder.forAnyOrigin().allowNullOrigin().allowCredentials().build();
    p.addLast(new CorsHandler(corsConfig));
    p.addLast(new RequestParser());
  }
}
