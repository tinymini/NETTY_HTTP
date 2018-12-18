package com.github.tinymini.netty.core.web.service;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import com.github.tinymini.netty.common.HttpCode;
import com.github.tinymini.netty.common.util.BeanUtils;
import com.github.tinymini.netty.common.util.LoggingUtils;
import com.github.tinymini.netty.web.WebConstants;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.CharsetUtil;

public final class RequestParser extends SimpleChannelInboundHandler<FullHttpMessage>
    implements WebConstants, HttpCode {
  protected final Log logger = LogFactory.getLog(getClass());

  private ByteBuf requestBody = Unpooled.buffer();
  private Map<String, Object> requestData;
  private HttpRequest request;
  private ServiceDispatcher serviceDispatcher;
  private static final String CONTENT_TYPE_JSON = "application/json; charset=UTF-8";

  public RequestParser() {
    serviceDispatcher = BeanUtils.getBean(ServiceDispatcher.class);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    logger.error(LoggingUtils.stackTraceToString(cause.getStackTrace(), 20));
    ctx.close();
  }

  private boolean writeResponse(HttpObject currentObj, ChannelHandlerContext ctx,
      HttpRequest request, Map<String, Object> apiResult)
      throws JsonGenerationException, JsonMappingException, IOException {
    // Decide whether to close the connection or not.
    boolean keepAlive = HttpUtil.isKeepAlive(request);
    // Build the response object.
    HttpResponseStatus status = (HttpResponseStatus) apiResult.get(HTTP_STATUS);
    ObjectMapper mapper = new ObjectMapper();

    FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
        currentObj.decoderResult().isSuccess() ? status : BAD_REQUEST,
        Unpooled.copiedBuffer(mapper.writeValueAsString(apiResult), CharsetUtil.UTF_8));

    response.headers().set(CONTENT_TYPE, CONTENT_TYPE_JSON);

    if (keepAlive) {
      response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
      response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
    }
    ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);

    return keepAlive;
  }

  public String getProxyIp(FullHttpMessage msg, String hostAddress) {
    HttpHeaders headers = msg.headers();
    String ip = headers.get("X-Forwarded-For");
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = headers.get("Proxy-Client-IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = headers.get("WL-Proxy-Client-IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = headers.get("HTTP_CLIENT_IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = headers.get("HTTP_X_FORWARDED_FOR");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = hostAddress;
    }
    return ip;
  }

  @Override
  public void channelRead0(ChannelHandlerContext ctx, FullHttpMessage msg) throws Exception {
    Map<String, Object> apiResult = null;
    if (msg instanceof HttpRequest) {
      this.request = (HttpRequest) msg;
      requestData = new HashMap<>();

      if (HttpUtil.is100ContinueExpected(request)) {
        ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
        return;
      }

      InetSocketAddress remoteAddress = (InetSocketAddress) ctx.channel().remoteAddress();
      requestData.put(REQUEST_URI, request.uri());
      requestData.put(REQUEST_METHOD, request.method().name());

      requestData.put(REMOTE_HOST_ADDRESS,
          getProxyIp(msg, remoteAddress.getAddress().getHostAddress()));
      requestData.put(REMOTE_HOST_PORT, remoteAddress.getPort());

      // Request content 처리.
      if (msg instanceof HttpContent) {
        requestBody.writeBytes(((HttpContent) msg).content());

        if (msg instanceof LastHttpContent) {
          requestData.put(REQUEST_BODY, requestBody.toString(CharsetUtil.UTF_8));

          try {
            apiResult = serviceDispatcher.dispatchAndExecute(requestData).getResult();
          } finally {
            this.requestData.clear();
          }

          if (!writeResponse(msg, ctx, this.request, apiResult)) {
            ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
          }
          this.request = null;
        }
      }
    } else {
      return;
    }
  }
}
