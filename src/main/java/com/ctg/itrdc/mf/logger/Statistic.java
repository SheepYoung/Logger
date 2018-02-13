package com.ctg.itrdc.mf.logger;

/**
 * Created by young on 2018/1/2.
 */

public interface Statistic  {

    //这些key用于跨进程的时候, 使用广播通知
    public static final String STATISTIC_START = "com.ctg.itrdc.mf.statistic.start";
    public static final String STATISTIC_COMPLETE = "com.ctg.itrdc.mf.statistic.complete";
    public static final String STATISTIC_ERROR = "com.ctg.itrdc.mf.statistic.error";

    public static final String STATISTIC_KEY = "com.ctg.itrdc.mf.statistic.kye";
    public static final String STATISTIC_VALUE = "com.ctg.itrdc.mf.statistic.value";
    public static final String STATISTIC_BUSINESS_ID = "com.ctg.itrdc.mf.statistic.businessId";

    /**
     * 开始统计
     * @param key 业务唯一的key
     * @param requestBusinessId 业务的唯一标识
     * @return 记录成功返回true else false
     */
    boolean statStart(String key, String requestBusinessId);

    /**
     * 统计失败
     * @param key 业务的key
     * @param requestBusinessId 业务请求的唯一标识
     * @return
     */
    boolean statError(String key, String requestBusinessId);


    /**
     * 统计失败
     * @param key 业务的key
     * @param requestBusinessId 业务请求的唯一标识
     * @param errorCode 业务异常码
     * @return
     */
    boolean statError(String key, String requestBusinessId, int errorCode);


    /**
     * 介绍统计
     * @param key 业务唯一的key
     * @param  requestId 业务请求的唯一标识
     * @return 记录成功返回true else false
     */
    boolean statComplete(String key, String requestId);
}
