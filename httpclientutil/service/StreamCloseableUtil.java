package com.gs.sy.utils.httpclientutil.service;

import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;

/**
 * @Author :zlg
 * @Description :
 * @Date : Create in 2017/10/18  下午3:57
 * @Modified By :
 */
@Slf4j
public class StreamCloseableUtil {
    /**
     * 统一关闭文件的流操作
     *
     * @param closeables
     */
    public static void closeStream(Closeable... closeables) {
        if (null != closeables && closeables.length > 0) {
            for (int i = 0; i < closeables.length; i++) {
                if (null != closeables[i]) {
                    try {
                        closeables[i].close();
                    } catch (IOException e) {
                        log.error("关闭流失败");
                    }
                }
            }
        }
    }
}
