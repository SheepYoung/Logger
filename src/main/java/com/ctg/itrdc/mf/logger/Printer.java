package com.ctg.itrdc.mf.logger;

/**
 * @author Orhan Obut
 */
public interface Printer {

    Printer t(int methodCount);

    void d(String message, Object... args);

    void e( String message, Object... args);

    void e(Throwable throwable, String message, Object... args);

    void w(Throwable throwable, String message, Object... args);

    void w(String message, Object... args);

    void i(String message, Object... args);

    void v(String message, Object... args);

    void wtf(String message, Object... args);

    void json(String json);

    void xml(String xml);

    /**
     * set log level
     * @see android.util.Log#ASSERT  etc
     * @param level
     */
    Printer setLevel(int level);

    Printer setTag(String tag);

}
