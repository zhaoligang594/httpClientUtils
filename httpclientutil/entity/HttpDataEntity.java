package com.gs.sy.utils.httpclientutil.entity;

import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author :zlg
 * @Description :  对与请求的数据进行封装
 * @Date : Create in 2017/10/18  下午1:54
 * @Modified By :
 */
@Slf4j
public class HttpDataEntity implements Serializable {


    private static final String CHINESE_PATTERN = "[\\u4e00-\\u9fa5]";

    private static final Pattern CHINESE_PATTERN_COMPILED = Pattern.compile(CHINESE_PATTERN);
    /**
     * 初始化请求的参数
     */
    private Map<String, Object> map = new HashMap<String, Object>(1 << 4);


    /**
     * 避免创建对象
     */
    private HttpDataEntity() {
    }

    /**
     * @param map
     */
    private HttpDataEntity(Map<String, Object> map) {
        this.map = map;
    }


    /**
     * 只能得到 不能设置
     *
     * @return Map
     */
    public Map<String, Object> getMap() {
        return map;
    }

    /**
     * 创建无参数的请求的对象
     *
     * @return HttpDataEntity
     */
    public static HttpDataEntity createHttpDataEntity() {
        return new HttpDataEntity();
    }

    /**
     * @param keyValues 请求的参数
     *                  如果有多个参数  用 逗号 分号 取址符 空格  换行\r\n |  \n    进行隔开即可    分隔开
     *                  eg: name=zlg  ||  name=zlg&age=23  name=zlg,age=23&realname=zzz;hhh=jjj          hshsh=uuu
     * @return {"name":"zlg","hhh":"jjj","hshsh":"uuu","age":"23","realname":"zzz"}
     */
    public static HttpDataEntity createHttpDataEntity(String keyValues) {

        Map<String, Object> map = new HashMap<String, Object>();

        if (null == keyValues) {
            return new HttpDataEntity();
        }


        if ("".equals(keyValues)) {
            return new HttpDataEntity();
        }


        /**
         * 去掉多余的空格
         */
        keyValues = keyValues.replaceAll(" +", " ");

        String[] split = keyValues.split("[&| +|;|,|\r\n|\n]");

        return getHttpDataEntity(map, split);

    }

    /**
     * @param keyValue 请求的参数
     *                 <p>
     *                 eg: name=zlg
     * @return
     */
    public static HttpDataEntity createHttpDataEntity(String... keyValue) {

        Map<String, Object> map = new HashMap<String, Object>();

        if (null == keyValue) {
            return new HttpDataEntity();
        }

        return getHttpDataEntity(map, keyValue);

    }


    private static HttpDataEntity getHttpDataEntity(Map<String, Object> map, String[] keyValue) {
        if (null != keyValue && keyValue.length > 0) {
            for (String str : keyValue) {
                if (null != str && str.length() > 0 && str.indexOf('=') > 0) {
                    String[] agrs = str.split("=");
                    if (null == agrs || 0 == agrs.length) {
                        log.warn("请求的参数错误----含有没有写nam与value的请求参数，已忽略");
                    } else {
                        if (null == agrs[0] || "".equals(agrs[0])) {
                            log.warn("请求的参数错误----含有没有写nam的请求参数，已忽略");
                        } else {
                            String key = agrs[0];
                            String value = agrs.length == 2 ? agrs[1] : "";
                            if (isHaveChinese(value)) {
                                try {
                                    value = URLEncoder.encode(value, "Utf-8");
                                } catch (UnsupportedEncodingException e) {
                                    log.error("发生错误");
                                    //e.printStackTrace();
                                }
                                map.put(key, value);
                            } else {
                                map.put(key, value);
                            }

                        }
                    }
                } else {
                    log.warn("请求的参数错误---请求的参数的格式错误");
                }

            }
        } else {
            log.error("请求的参数错误");
        }

        return new HttpDataEntity(map);
    }


    /**
     * 判断里面是否含有汉字
     *
     * @param entityFieldValue
     * @return
     */
    private static boolean isHaveChinese(String entityFieldValue) {

        Matcher matcher = CHINESE_PATTERN_COMPILED.matcher(entityFieldValue);

        if (matcher.find()) {
            return false;
            //return true;
        }
        return false;
    }

}
