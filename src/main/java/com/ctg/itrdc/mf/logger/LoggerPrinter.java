package com.ctg.itrdc.mf.logger;

import com.tencent.mars.xlog.Log;

/**
 * Logger is a wrapper of {@link Log}
 * But more pretty, simple and powerful
 *
 * @author Orhan Obut
 */
final class LoggerPrinter extends AbsLogger {

    /**
     * Android's max limit for a log entry is ~4076 bytes,
     * so 4000 bytes is used as chunk size since default charset
     * is UTF-8
     */
    private static final int CHUNK_SIZE = 4000;
    /**
     * The minimum stack trace index, starts at this class after two native calls.
     */
    private static final int MIN_STACK_OFFSET = 4;

    /**
     * Drawing toolbox
     */
    private static final char TOP_LEFT_CORNER = '╔';
    private static final char BOTTOM_LEFT_CORNER = '╚';
    private static final char MIDDLE_CORNER = '╟';
    private static final char HORIZONTAL_DOUBLE_LINE = '║';
    private static final String DOUBLE_DIVIDER = "════════════════════════════════════════════";
    private static final String SINGLE_DIVIDER = "────────────────────────────────────────────";
    private static final String TOP_BORDER = TOP_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
    private static final String BOTTOM_BORDER = BOTTOM_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
    private static final String MIDDLE_BORDER = MIDDLE_CORNER + SINGLE_DIVIDER + SINGLE_DIVIDER;

    /**
     * TAG is used for the Log, the name is a little different
     * in order to differentiate the logs easily with the filter
     */
    private static String TAG = "PRETTYLOGGER";





    /**
     * This method is synchronized in order to avoid messy of logs' order.
     */
    @Override
    protected synchronized void log(int logType, String msg, Object... args) {


        if (logType < settings.get().getLogLevel() ) {
            return;
        }
        String message = createMessage(msg, args);
        int methodCount = getMethodCount();

        logTopBorder(logType);
        logHeaderContent(logType, methodCount);

        //get bytes of message with system's default charset (which is UTF-8 for Android)
        byte[] bytes = message.getBytes();
        int length = bytes.length;
        if (length <= CHUNK_SIZE) {
            if (methodCount > 0) {
                logDivider(logType);
            }
            logContent(logType, message);
            logBottomBorder(logType);
            return;
        }
        if (methodCount > 0) {
            logDivider(logType);
        }
        for (int i = 0; i < length; i += CHUNK_SIZE) {
            int count = Math.min(length - i, CHUNK_SIZE);
            //create a new String with system's default charset (which is UTF-8 for Android)
            logContent(logType, new String(bytes, i, count));
        }
        logBottomBorder(logType);
    }

    private void logTopBorder(int logType) {
        logChunk(logType, getTag(), TOP_BORDER);
    }

    private void logHeaderContent(int logType, int methodCount) {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        if (settings.get().isShowThreadInfo()) {
            logChunk(logType, getTag(), HORIZONTAL_DOUBLE_LINE + " Thread: " + Thread.currentThread().getName());
            logDivider(logType);
        }
        String level = "";

        int stackOffset = getStackOffset(trace);

        //corresponding method count with the current stack may exceeds the stack trace. Trims the count
        if (methodCount + stackOffset > trace.length) {
            methodCount = trace.length - stackOffset - 1;
        }

        for (int i = methodCount; i > 0; i--) {
            int stackIndex = i + stackOffset;
            if (stackIndex >= trace.length) {
                continue;
            }
            StringBuilder builder = new StringBuilder();
            builder.append("║ ")
                    .append(level)
                    .append(getSimpleClassName(trace[stackIndex].getClassName()))
                    .append(".")
                    .append(trace[stackIndex].getMethodName())
                    .append(" ")
                    .append(" (")
                    .append(trace[stackIndex].getFileName())
                    .append(":")
                    .append(trace[stackIndex].getLineNumber())
                    .append(")");
            level += "   ";
            logChunk(logType, getTag(), builder.toString());
        }
    }

    private void logBottomBorder(int logType) {
        logChunk(logType, getTag(), BOTTOM_BORDER);
    }

    private void logDivider(int logType) {
        logChunk(logType, getTag(), MIDDLE_BORDER);
    }

    private void logContent(int logType, String chunk) {
        String[] lines = chunk.split(System.getProperty("line.separator"));
        for (String line : lines) {
            logChunk(logType, getTag(), HORIZONTAL_DOUBLE_LINE + " " + line);
        }
    }

    @Override
    protected void logChunk(int logType, String finalTag, String chunk) {
        switch (logType) {
            case android.util.Log.ERROR:
                Log.e(finalTag, chunk);
                break;
            case android.util.Log.INFO:
                Log.i(finalTag, chunk);
                break;
            case android.util.Log.VERBOSE:
                Log.v(finalTag, chunk);
                break;
            case android.util.Log.WARN:
                Log.w(finalTag, chunk);
                break;
            case android.util.Log.ASSERT:
                Log.e(finalTag, chunk);
                break;
            case android.util.Log.DEBUG:
                // Fall through, log debug by default
            default:
                Log.d(finalTag, chunk);
                break;
        }
    }

    private String getSimpleClassName(String name) {
        int lastIndex = name.lastIndexOf(".");
        return name.substring(lastIndex + 1);
    }


    /**
     * @return the appropriate tag based on local or global
     */
    private String getTag() {
        String tag = LOCAL_TAG.get();
        if (tag != null) {
            return tag;
        }else{
            return TAG;
        }

    }

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

    private int getMethodCount() {
        Integer count = LOCAL_METHOD_COUNT.get();
        int result = settings.get().getMethodCount();
        if (count != null) {
            LOCAL_METHOD_COUNT.remove();
            result = count;
        }
        if (result < 0) {
            throw new IllegalStateException("methodCount cannot be negative");
        }
        return result;
    }

    /**
     * Determines the starting index of the stack trace, after method calls made by this class.
     *
     * @param trace the stack trace
     * @return the stack offset
     */
    private int getStackOffset(StackTraceElement[] trace) {
        int index = MIN_STACK_OFFSET;
        for (int i = MIN_STACK_OFFSET; i < trace.length; i++) {
            StackTraceElement e = trace[i];
            String name = e.getClassName();
            if (!name.equals(AbsLogger.class.getName())) {
                index--;
            }else{
                return index;
            }
        }
        return -1;
    }

}
