package com.github.tinymini.netty.core.web.client;

import javax.net.ssl.SSLException;
import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.github.tinymini.netty.core.web.model.RequestModel;
import com.github.tinymini.netty.core.web.model.ResponseModel;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

/**
 * Http Client 유틸
 * 
 * @author shkim
 *
 */
public class HttpClientUtil {
  private Log logger = LogFactory.getLog(getClass());
  /** 요청 모델 */
  private RequestModel requestModel;
  /** 클라이언트 그룹 */
  private EventLoopGroup clientGroup;
  /** 클라이언크 부트스트랩 */
  private Bootstrap b;

  private static SslContext sslCtx;

  public HttpClientUtil() {
    this.clientGroup = new NioEventLoopGroup();
    this.b = new Bootstrap();
    try {
      sslCtx =
          SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
      //이딴거 쓰면 안되는데 ... 하
    } catch (SSLException e) {
      throw new RuntimeException(e.getMessage());
    }

  }

  public HttpClientUtil(RequestModel requestModel) {
    this();
    this.requestModel = requestModel;
  }

  public RequestModel getRequestModel() {
    return requestModel;
  }

  public HttpClientUtil setRequestModel(RequestModel requestModel) {
    this.requestModel = requestModel;
    return this;
  }

  /**
   * 요청
   * 
   * @return
   */
  public ResponseModel request() {
    if (this.requestModel == null) {
      throw new NullArgumentException("RequestModel is Null");
    }
    // 서버 설정 https
    HttpClientHandler hch = new HttpClientHandler(requestModel);
    SslContext localSslCtx = null;

    if ("https".equals(this.requestModel.getScheme())) {
      localSslCtx = sslCtx;
    }

    b.group(clientGroup).channel(NioSocketChannel.class)
        .handler(new HttpClientInitializer(hch, localSslCtx));

    if (requestModel.getConnectionTimeout() > 0) {
      b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, requestModel.getConnectionTimeout());
    }

    // 요청 정보 준비
    try {
      // 연결
      Channel ch = b.connect(requestModel.getHost(), requestModel.getPort()).sync().channel();
      // 완료 후 닫고 대기
      ch.closeFuture().sync();
    } catch (InterruptedException e) {
      logger.info(e.getMessage());
      throw new RuntimeException("Fail to connect to " + requestModel.getRawPath());
    }
    // 스레드 닫기
    clientGroup.shutdownGracefully();
    return hch.getResult();
  }
}
