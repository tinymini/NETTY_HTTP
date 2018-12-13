package com.github.tinymini.netty.core.web.client;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.github.tinymini.netty.core.web.model.RequestModel;
import com.github.tinymini.netty.core.web.model.ResponseModel;

/**
 * 다중 요청 유틸
 * 
 * @author shkim
 *
 */
public class MultipleHttpClientUtil {
  private Log logger = LogFactory.getLog(getClass());
  /** 요청 목록 */
  private List<RequestModel> requests;

  private boolean isConcurrent = true;

  public MultipleHttpClientUtil() {
    this(new LinkedList<RequestModel>());
  }

  public MultipleHttpClientUtil(List<RequestModel> requests) {
    this(requests, true);
  }

  public MultipleHttpClientUtil(List<RequestModel> requests, boolean isConcurrent) {
    this.requests = requests;
    this.isConcurrent = isConcurrent;
  }

  public void addRequest(RequestModel requestModel) {
    this.requests.add(requestModel);
  }

  public List<ResponseModel> request() {
    if (this.isConcurrent) {
      return requestConcurrent();
    } else {
      return requestInorder();
    }
  }

  public int getEfficientThreadSize() {
    int threadSize = Runtime.getRuntime().availableProcessors();
    if (threadSize == 1) {
      threadSize += 2;
    } else {
      threadSize *= 2;
    }
    return threadSize;
  }

  /**
   * 동시 요청
   * 
   * @return
   */
  private List<ResponseModel> requestConcurrent() {
    int size = this.requests.size();

    List<Future<ResponseModel>> futures = new ArrayList<>(size);
    List<ResponseModel> responses = new ArrayList<>(size);
    // thread 풀 확보
    ExecutorService threadPool = Executors.newCachedThreadPool();

    for (int i = 0; i < size; i++) {
      final RequestModel requestModel = this.requests.get(i);
      if (requestModel == null) {
        futures.add(null);
        continue;
      }
      logger.info(requestModel);
      Callable<ResponseModel> callable = null;
      try {
        callable = new Callable<ResponseModel>() {
          @Override
          public ResponseModel call() throws Exception {
            return new HttpClientUtil(requestModel).request();
          }
        };
      } catch (Exception e) {
        logger.warn(e.getMessage());
      }
      futures.add(threadPool.submit(callable));
    }
    threadPool.shutdown();
    // future에 담긴 결과 객체를 받아 List에 담는다.
    for (Future<ResponseModel> future : futures) {
      try {
        responses.add(future.get());
      } catch (InterruptedException | ExecutionException e) {
        logger.info(e.getMessage());
        responses.add(null);
      }
    }
    return responses;
  }

  /**
   * 순차 요청
   * 
   * @return
   */
  private List<ResponseModel> requestInorder() {
    int size = this.requests.size();
    List<ResponseModel> responses = new ArrayList<>(size);

    for (int i = 0; i < size; i++) {
      final RequestModel requestModel = this.requests.get(i);
      responses.add(new HttpClientUtil(requestModel).request());
    }

    return responses;
  }

}
