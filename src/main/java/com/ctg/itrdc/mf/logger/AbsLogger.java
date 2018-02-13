package com.ctg.itrdc.mf.logger;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * Created by YoungSheep on 2017/4/5.
 */

public abstract class AbsLogger implements Printer {
    /**
     * It is used to determine log settings such as method count, thread info visibility
     */
    protected static  ThreadLocal<Settings>settings = new ThreadLocal<Settings>(){
        @Override
        protected Settings initialValue() {
            return new Settings();
        }
    };

    /**
     * Localize single tag and method count for each thread
     */
    protected static  ThreadLocal<String> LOCAL_TAG = new ThreadLocal<>();
    /**
     * It is used for json pretty print
     */
    protected static final int JSON_INDENT = 4;



    protected static ThreadLocal<Integer> LOCAL_METHOD_COUNT = new ThreadLocal<>();


    @Override
    public Printer t(int methodCount) {
        LOCAL_METHOD_COUNT.set(methodCount);
        return this;
    }

    /**
     * @return the appropriate tag based on local or global
     */
    private String getTag() {
        String tag = LOCAL_TAG.get();
        if (tag != null) {
            return tag;
        }else{
            return "";
        }
    }

    @Override
    public void d(String message, Object... args) {
        log( android.util.Log.DEBUG, message, args);
    }

    @Override
    public void e(String message, Object... args) {
        log( android.util.Log.ERROR, message, args);
    }

    @Override
    public void e(Throwable throwable, String message, Object... args) {
        if (throwable != null && message != null) {
            message += " : " + Log.getStackTraceString(throwable);
        }
        if (throwable != null && message == null) {
            message = Log.getStackTraceString(throwable);
        }
        if (message == null) {
            message = "No message/exception is set";
        }
        log( android.util.Log.ERROR, message, args);
    }

    @Override
    public void w(String message, Object... args) {
        log( android.util.Log.WARN, message, args);
    }

    @Override
    public void w(Throwable throwable, String message, Object... args) {
        if (throwable != null && message != null) {
            message += " : " + throwable.toString();
        }
        if (throwable != null && message == null) {
            message = throwable.toString();
        }
        if (message == null) {
            message = "No message/exception is set";
        }
        log( Log.WARN, message, args);
    }

    @Override
    public void i(String message, Object... args) {
        log( android.util.Log.INFO, message, args);
    }

    @Override
    public void v(String message, Object... args) {
        log( android.util.Log.VERBOSE, message, args);
    }

    @Override
    public void wtf(String message, Object... args) {
        log( android.util.Log.ASSERT, message, args);
    }

    /**
     * Formats the json content and print it
     *
     * @param json the json content
     */
    @Override
    public void json(String json) {
        if (TextUtils.isEmpty(json)) {
            d("", "Empty/Null json content");
            return;
        }
        try {
            if (json.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(json);
                String message = jsonObject.toString(JSON_INDENT);
                d(message);
                return;
            }
            if (json.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(json);
                String message = jsonArray.toString(JSON_INDENT);
                d(message);
            }
        } catch (JSONException e) {
            e(e.getCause().getMessage() + "\n" + json);
        }
    }

    /**
     * Formats the json content and print it
     *
     * @param xml the xml content
     */
    @Override
    public void xml(String xml) {
        if (TextUtils.isEmpty(xml)) {
            d("Empty/Null xml content");
            return;
        }
        try {
            Source xmlInput = new StreamSource(new StringReader(xml));
            StreamResult xmlOutput = new StreamResult(new StringWriter());
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(xmlInput, xmlOutput);
            d(xmlOutput.getWriter().toString().replaceFirst(">", ">\n"));
        } catch (TransformerException e) {
            e(e.getCause().getMessage() + "\n" + xml);
        }
    }

    @Override
    synchronized  public Printer setLevel(int level) {
        settings.get().setLogLevel(level);
        return this;
    }

    @Override
    public Printer setTag(String tag) {
        LOCAL_TAG.set(tag);
        return this;
    }

    /**
     * This method is synchronized in order to avoid messy of logs' order.
     */
    protected synchronized void log(int logLevel, String msg, Object... args) {

        if (logLevel < settings.get().getLogLevel() || msg == null) {
            return;
        }

        String message = createMessage(msg, args);
        logChunk(logLevel, getTag(), message);
    }


    abstract protected void logChunk(int logType, String tag, String chunk);

    private String createMessage(String message, Object... args) {
        if(message.contains("%d") ||  message.contains("%s") ||  message.contains("%c") ||
                message.contains("%x") ||  message.contains("%o") ||  message.contains("%f")  ){
            return args.length == 0 ? message : String.format(message, args);
        }else{
            StringBuilder sb = new StringBuilder(message);
            for (int i = 0; i < args.length; i++) {
                sb.append(args[i]);
            }
            return sb.toString();
        }
    }

}
