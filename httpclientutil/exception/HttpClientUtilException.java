package com.gs.sy.utils.httpclientutil.exception;

/**
 * @Author :zlg
 * @Description :  httpclient exception
 * @Date : Create in 2017/10/30  下午2:42
 * @Modified By :
 */
public class HttpClientUtilException extends RuntimeException {
    public HttpClientUtilException(String message) {
        super(message);
    }

    public HttpClientUtilException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpClientUtilException(Throwable cause) {
        super(cause);
    }
}
