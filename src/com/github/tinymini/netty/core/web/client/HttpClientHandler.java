package com.github.tinymini.netty.core.web.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.github.tinymini.netty.core.web.model.RequestModel;
import com.github.tinymini.netty.core.web.model.ResponseModel;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.CharsetUtil;

/**
 * 클라이언트 핸들러
 * 
 * @author shkim
 *
 */
public class HttpClientHandler extends SimpleChannelInboundHandler<HttpContent> {
  private Log logger = LogFactory.getLog(getClass());

  /** 응답 결과 */
  private ByteBuf result = Unpooled.buffer();
  /** 응답 결과 모델 */
  private ResponseModel responseModel = new ResponseModel();
  /** 요청 모델 */
  private RequestModel requestModel;

  public HttpClientHandler(RequestModel requestModel) {
    this.requestModel = requestModel;
  }

  /**
   * 요청 결과 반환
   * 
   * @return
   */
  public ResponseModel getResult() {
    String responseBody = result.toString(CharsetUtil.UTF_8);
    if (logger.isDebugEnabled()) {
      logger.debug("responseBody: " + responseBody);
    }
    responseModel.setResponseBody(responseBody);
    return responseModel;
  }

  public RequestModel getRequestModel() {
    return requestModel;
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) {
    if (logger.isDebugEnabled()) {
      logger.debug("requestModel: " + this.requestModel);
    }
    ctx.writeAndFlush(this.requestModel.getHttpRequest());
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, HttpContent msg) throws Exception {
    if (msg instanceof HttpResponse) {
      HttpResponse response = (HttpResponse) msg;
      responseModel.setResponseCode(response.status().code());
      String cookie = response.headers().get(HttpHeaderNames.SET_COOKIE);
      if (cookie != null) {
        responseModel.setCookie(cookie);
      }
    }
    if (msg instanceof LastHttpContent) {
      ctx.close();
    }
    result.writeBytes(((HttpContent) msg).content());
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    logger.info(cause.getMessage());
    ctx.close();
  }
}
