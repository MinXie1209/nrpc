package org.example.nrpc.common.util;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;


/**
 * 雪花算法ID
 *
 * @author 江南小俊
 * @since 2021/7/7
 **/
@Slf4j
public class SnowflakeUtil {
    private static long workerId;//为终端ID
    private static long datacenterId = 1;//数据中心ID
    private static Snowflake snowflake = IdUtil.createSnowflake(workerId, datacenterId);
    private int port;

    {
        port = 8000;
        init();
    }

    private void init() {
        try {
            workerId = Long.valueOf(NetUtil.ipv4ToLong(NetUtil.getLocalhostStr()) + "" + port);
        } catch (Exception e) {
            log.error("", e);
            workerId = NetUtil.ipv4ToLong(NetUtil.getLocalhostStr());
        }
        log.debug("当前机器的workId:{}", workerId);
    }

    /**
     * @return long
     * @author Jim
     * @since 2021/7/7 上午11:02
     **/

    public static synchronized long snowflakeId() {
        return snowflake.nextId();
    }

    /**
     * 获取雪花ID
     *
     * @param workerId     机器ID
     * @param datacenterId 数据中心ID
     * @return long
     * @author Jim
     * @since 2021/7/7 上午11:02
     **/

    public static synchronized long snowflakeId(long workerId, long datacenterId) {
        Snowflake snowflake = IdUtil.createSnowflake(workerId, datacenterId);
        return snowflake.nextId();
    }
}