package com.gs.sy.utils.httpclientutil.manage;

import com.gs.sy.utils.gopay.MyX509TrustManager;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * httpclient 采用连接池进行管理
 *
 * @author :breakpoint/赵立刚
 * @date : 2018/01/18
 */
public class LocalHttpConnectionManager {

    private static PoolingHttpClientConnectionManager manager = null;


    static {
        LayeredConnectionSocketFactory socketFactory = null;

        try {
            socketFactory = new SSLConnectionSocketFactory(SSLContext.getDefault());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }


        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("https", socketFactory)
                .register("http", new PlainConnectionSocketFactory())
                .build();
        manager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        manager.setMaxTotal(200);
        manager.setDefaultMaxPerRoute(20);

        manager.setValidateAfterInactivity(60000);
    }

    private static HttpRequestRetryHandler getHttpRequestRetryHandler() {

        HttpRequestRetryHandler httpRequestRetryHandler = new HttpRequestRetryHandler() {

            @Override
            public boolean retryRequest(IOException exception,
                                        int executionCount, HttpContext context) {
                /**
                 * 设置重试的次数
                 */
                if (executionCount >= 5) {
                    return false;
                }
                /**
                 * 如果服务器丢掉了连接，那么就重试
                 */
                if (exception instanceof NoHttpResponseException) {
                    return true;
                }
                /**
                 * 不要重试SSL握手异常
                 */
                if (exception instanceof SSLHandshakeException) {
                    return false;
                }
                /**
                 * 超时
                 */
                if (exception instanceof InterruptedIOException) {
                    return false;
                }
                /**
                 * 目标服务器不可达
                 */
                if (exception instanceof UnknownHostException) {
                    return false;
                }
                /**
                 * 连接被拒绝
                 */
                if (exception instanceof ConnectTimeoutException) {
                    return false;
                }
                /**
                 * SSL握手异常
                 */
                if (exception instanceof SSLException) {
                    return false;
                }

                HttpClientContext clientContext = HttpClientContext
                        .adapt(context);
                HttpRequest request = clientContext.getRequest();
                // 如果请求是幂等的，就再次尝试
                if (!(request instanceof HttpEntityEnclosingRequest)) {
                    return true;
                }
                return false;
            }
        };

        return httpRequestRetryHandler;
    }

    public static CloseableHttpClient getHttpClient(){


        try {

            //创建SSLContext
            SSLContext sslContext=SSLContext.getInstance("SSL");
            TrustManager[] tm={new MyX509TrustManager()};
            //初始化
            sslContext.init(null, tm, new java.security.SecureRandom());;
            //获取SSLSocketFactory对象
            SSLSocketFactory ssf=sslContext.getSocketFactory();



            /*SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                // 信任所有
                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();*/

            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
            CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslsf)
                    .setConnectionManager(manager).setRetryHandler(getHttpRequestRetryHandler())
                    .build();

        /*CloseableHttpClient httpClient = HttpClients.createDefault();//如果不采用连接池就是这种方式获取连接*/
            return httpClient;
        }catch (Exception e){

        }

        return null;
    }

}
