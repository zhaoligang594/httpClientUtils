package com.gs.sy.utils.httpclientutil.service;

import com.google.gson.Gson;
import com.gs.sy.utils.httpclientutil.contsant.MimeTypeEnum;
import com.gs.sy.utils.httpclientutil.entity.HttpDataEntity;
import com.gs.sy.utils.httpclientutil.exception.HttpClientUtilException;
import com.gs.sy.utils.httpclientutil.manage.LocalHttpConnectionManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpConnectionFactory;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.DefaultBHttpServerConnectionFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author :zlg
 * @Description :一些关于跨域请求的基本的操作
 * @Date : Create in 2017/10/18  下午1:49
 * @Modified By :
 */
@Slf4j
public class HttpClientUtil {


    /**
     * 请求超时时间
     */
    private static final Integer DEFAULT_REQUEST_TIMEOUT = 2000000;

    /**
     * 套接字超时时间
     */
    private static final Integer DEFAULT_SOCKET_TIMEOUT = 2000000;

    /**
     * 默认连接超时时间
     */
    private static final Integer DEFAULT_CONNECTION_TIMEOUT = 2000000;


    /**
     * @param doGetUrl       请求的URL
     * @param httpDataEntity 请求实体
     * @param contentType    contentType
     * @param timeOut        超时时间
     * @return
     * @throws HttpClientUtilException
     */
    public static String doGet(String doGetUrl, HttpDataEntity httpDataEntity, MimeTypeEnum contentType, int timeOut) throws HttpClientUtilException {
        /**
         * 响应的数据
         */
        String returnStr = null;

        /**
         * 创建请求的基本的对象
         */
        HttpClient httpClient = LocalHttpConnectionManager.getHttpClient();
        Map<String, Object> requestMap = httpDataEntity.getMap();

        if (null != requestMap && requestMap.size() > 0) {
            Set<String> keySet = requestMap.keySet();

            doGetUrl += "?";
            for (String key : keySet) {
                Object value = requestMap.get(key);
                doGetUrl += key + "=" + value + "&";
            }
        } else {
            log.info("没有请求参数，只有URL");
        }

        log.info("doGetUrl=" + doGetUrl);

        RequestConfig.Builder builder = RequestConfig.custom().setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT)
                .setSocketTimeout(DEFAULT_SOCKET_TIMEOUT);

        RequestConfig requestConfig = null;
        if (timeOut <= 0) {
            requestConfig = builder.setConnectionRequestTimeout(DEFAULT_REQUEST_TIMEOUT).build();
        } else {
            requestConfig = builder.setConnectionRequestTimeout(timeOut).build();
        }
        HttpGet httpGet = new HttpGet(doGetUrl);
        httpGet.setConfig(requestConfig);
        httpGet.addHeader("Accept-Charset", "UTF-8");
        if (null == contentType) {
            httpGet.addHeader("Content-Type", MimeTypeEnum.APPLICATION_FORM_URLENCODED.getMimeType());
        } else {
            httpGet.addHeader("Content-Type", contentType.getMimeType());
        }

        try {

            HttpResponse httpResponse = httpClient.execute(httpGet);

            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                // 获取服务端返回的数据,并返回
                return EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("请求失败，请重试");
            throw new HttpClientUtilException("请求失败，请重试");
        }
        return returnStr;
    }


    /**
     * @param doGetUrl 请求的URL
     * @return
     * @throws HttpClientUtilException
     */
    public static String doGet(String doGetUrl) throws HttpClientUtilException {
        return doGet(doGetUrl, HttpDataEntity.createHttpDataEntity(), MimeTypeEnum.APPLICATION_FORM_URLENCODED, DEFAULT_REQUEST_TIMEOUT);
    }

    /**
     * @param doGetUrl 请求的URL
     * @param timeOut  超时时间
     * @return
     * @throws HttpClientUtilException
     */
    public static String doGet(String doGetUrl, int timeOut) throws HttpClientUtilException {
        return doGet(doGetUrl, HttpDataEntity.createHttpDataEntity(), MimeTypeEnum.APPLICATION_FORM_URLENCODED, timeOut);
    }

    /**
     * @param doGetUrl       请求的URL
     * @param httpDataEntity 请求实体
     * @return
     * @throws HttpClientUtilException
     */
    public static String doGet(String doGetUrl, HttpDataEntity httpDataEntity) throws HttpClientUtilException {
        return doGet(doGetUrl, httpDataEntity, MimeTypeEnum.APPLICATION_FORM_URLENCODED, DEFAULT_REQUEST_TIMEOUT);
    }

    /**
     * @param doGetUrl       请求的URL
     * @param httpDataEntity 请求实体
     * @param timeOut        超时时间
     * @return
     * @throws HttpClientUtilException
     */
    public static String doGet(String doGetUrl, HttpDataEntity httpDataEntity, int timeOut) throws HttpClientUtilException {
        /**
         *
         */
        return doGet(doGetUrl, httpDataEntity, MimeTypeEnum.APPLICATION_FORM_URLENCODED, timeOut);
    }


    /**
     * @param doPostUrl      请求的URL
     * @param httpDataEntity 请求实体
     * @param contentType    contentType
     * @param timeOut        超时时间
     * @return
     * @throws HttpClientUtilException
     */
    public static String doPost(String doPostUrl, HttpDataEntity httpDataEntity, MimeTypeEnum contentType, int timeOut) throws HttpClientUtilException {

        /**
         *返回的数据
         */
        String returnStr = null;
        /**
         * 创建 请求的对象
         */
        HttpClient httpClient = LocalHttpConnectionManager.getHttpClient();

        RequestConfig.Builder builder = RequestConfig.custom().setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT)
                .setSocketTimeout(DEFAULT_SOCKET_TIMEOUT);
        RequestConfig requestConfig = null;
        if (timeOut <= 0) {
            builder.setConnectionRequestTimeout(DEFAULT_REQUEST_TIMEOUT).build();
        } else {
            builder.setConnectionRequestTimeout(timeOut).build();
        }

        /**
         * 设置请求的post
         */
        HttpPost httpPost = new HttpPost(doPostUrl);

        httpPost.setConfig(requestConfig);

        List<NameValuePair> params = new ArrayList<NameValuePair>(5);

        /**
         * 请求的数据
         */
        Map<String, Object> map = httpDataEntity.getMap();

        if (null != map && map.size() > 0) {
            Set<String> keys = map.keySet();
            for (String key : keys) {
                Object value = map.get(key);
                params.add(new BasicNameValuePair(key, String.valueOf(value)));
            }
        } else {
            log.info("没有请求参数，只有URL");
        }

        log.info("doPostUrl=" + doPostUrl);
        Gson gson = new Gson();
        log.info("params=" + gson.toJson(params));
        gson = null;
        if (params.size() > 0) {
            try {
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
                /**
                 * 设置轻松求的数据
                 */
                httpPost.setEntity(entity);
            } catch (UnsupportedEncodingException e) {
                log.error("设置的请求的数据失败");
                e.printStackTrace();
            }

        }

        httpPost.setHeader(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.118 Safari/537.36");

        if (null != contentType && !"".equals(contentType)) {

            httpPost.addHeader("Content-Type", contentType.getMimeType());

        } else {
            httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
        }


        httpPost.addHeader("Connection", "Keep-Alive");

        try {

            HttpResponse response = httpClient.execute(httpPost);

            if (response.getStatusLine().getStatusCode() == 200) {
                // 获取服务端返回的数据,并返回
                return EntityUtils.toString(response.getEntity(), "UTF-8");
            }

        } catch (Exception e) {
            log.error("在请求的数据的过程中失败");
            e.printStackTrace();
            throw new HttpClientUtilException("在请求的数据的过程中失败");
        }


        return returnStr;
    }

    /**
     * @param doPostUrl 请求的URL
     * @return
     * @throws HttpClientUtilException
     */
    public static String doPost(String doPostUrl) throws HttpClientUtilException {
        return doPost(doPostUrl, HttpDataEntity.createHttpDataEntity(), MimeTypeEnum.APPLICATION_FORM_URLENCODED, DEFAULT_REQUEST_TIMEOUT);
    }

    /**
     * @param doPostUrl 请求的URL
     * @param timeOut   超时时间
     * @return
     * @throws HttpClientUtilException
     */
    public static String doPost(String doPostUrl, int timeOut) throws HttpClientUtilException {
        return doPost(doPostUrl, HttpDataEntity.createHttpDataEntity(), MimeTypeEnum.APPLICATION_FORM_URLENCODED, timeOut);
    }

    /**
     * @param doPostUrl   请求的URL
     * @param contentType contentType
     * @return
     * @throws HttpClientUtilException
     */
    public static String doPost(String doPostUrl, MimeTypeEnum contentType) throws HttpClientUtilException {
        /**
         *
         */
        return doPost(doPostUrl, HttpDataEntity.createHttpDataEntity(), contentType, DEFAULT_REQUEST_TIMEOUT);
    }

    /**
     * @param doPostUrl   请求的URL
     * @param contentType contentType
     * @param timeOut     超时时间
     * @return
     * @throws HttpClientUtilException
     */
    public static String doPost(String doPostUrl, MimeTypeEnum contentType, int timeOut) throws HttpClientUtilException {
        /**
         *
         */
        return doPost(doPostUrl, HttpDataEntity.createHttpDataEntity(), contentType, timeOut);
    }


    /**
     * @param doPostUrl      请求的URL
     * @param httpDataEntity 请求实体
     * @return
     * @throws HttpClientUtilException
     */
    public static String doPost(String doPostUrl, HttpDataEntity httpDataEntity) throws HttpClientUtilException {

        return doPost(doPostUrl, httpDataEntity, MimeTypeEnum.APPLICATION_FORM_URLENCODED, DEFAULT_REQUEST_TIMEOUT);
    }

    /**
     * @param doPostUrl      请求的URL
     * @param httpDataEntity 请求实体
     * @param timeOut        超时时间
     * @return
     * @throws HttpClientUtilException
     */
    public static String doPost(String doPostUrl, HttpDataEntity httpDataEntity, int timeOut) throws HttpClientUtilException {

        return doPost(doPostUrl, httpDataEntity, MimeTypeEnum.APPLICATION_FORM_URLENCODED, timeOut);
    }

    /**
     * @param doPostUrl      请求的URL
     * @param httpDataEntity 请求实体
     * @param contentType    contentType
     * @return
     * @throws HttpClientUtilException
     */
    public static String doPost(String doPostUrl, HttpDataEntity httpDataEntity, MimeTypeEnum contentType) throws HttpClientUtilException {
        /**
         *
         */
        return doPost(doPostUrl, httpDataEntity, contentType, DEFAULT_REQUEST_TIMEOUT);
    }

}
