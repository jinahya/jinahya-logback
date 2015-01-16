/*
 * Copyright 2015 Jin Kwon &lt;jinahya_at_gmail.com&gt;.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.github.jinahya.logback.core;


import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import static java.lang.invoke.MethodHandles.lookup;
import java.nio.charset.Charset;
import static org.slf4j.LoggerFactory.getLogger;


/**
 *
 * @author Jin Kwon &lt;jinahya_at_gmail.com&gt;
 * @see <a href="https://gist.github.com/olim7t/881318">olim7t /
 * LogbackCapture.java</a>
 */
public final class LogRecorder {


    /**
     * Starts recoding logs for given {@code logger}.
     *
     * @param logger the logger instance.
     * @param pattern the pattern
     * @param charset the charset
     * @param limit the maximum number of bytes to hold.
     *
     * @return a LogRecoder instance.
     */
    public static LogRecorder start(final Logger logger, final String pattern,
                                    final Charset charset, final int limit) {

        if (logger == null) {
            throw new NullPointerException("null logger");
        }

        final PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(logger.getLoggerContext());
        encoder.setPattern(pattern);
        encoder.setCharset(charset);
        encoder.setImmediateFlush(true);

        final BufferedOutputStreamAppender<ILoggingEvent> appender
            = new BufferedOutputStreamAppender<>();
        appender.setContext(logger.getLoggerContext());
        appender.setLimit(limit);
        appender.setEncoder(encoder);

        final LogRecorder recorder = new LogRecorder(logger, appender, encoder);

        recorder.start();

        return recorder;
    }


    /**
     * Starts recording log for given {@code logger}.
     *
     * @param logger the logger
     * @param pattern the pattern
     * @param charset the charset
     * @param limit the maximum byte to hold
     *
     * @return a LogRecord instance.
     */
    public static LogRecorder start(final org.slf4j.Logger logger,
                                    final String pattern, final Charset charset,
                                    final int limit) {

        if (logger == null) {
            throw new NullPointerException("null logger");
        }

        if (!Logger.class.isInstance(logger)) {
            throw new IllegalArgumentException(
                "logger(" + logger + ") is not an instance of " + Logger.class);
        }

        return start(Logger.class.cast(logger), pattern, charset, limit);
    }


    /**
     * Finishes recording for given {@code logger} and returns the records.
     *
     * @param recorder the logger.
     *
     * @return the buffered records.
     */
    public static String finish(final LogRecorder recorder) {

        final org.slf4j.Logger logger = getLogger(lookup().lookupClass());

        logger.trace("finish({})", recorder);

        if (recorder == null) {
            throw new NullPointerException("null recorder");
        }

        recorder.stop();

        return new String(recorder.appender.toByteArray(),
                          recorder.encoder.getCharset());
    }


    private LogRecorder(
        final Logger logger,
        final BufferedOutputStreamAppender<ILoggingEvent> appender,
        final PatternLayoutEncoder encoder) {

        super();

        if (logger == null) {
            throw new NullPointerException("null logger");
        }

        if (appender == null) {
            throw new NullPointerException("null appender");
        }

        this.logger = logger;
        this.appender = appender;
        this.encoder = encoder;
    }


    private synchronized void start() {

        logger_.trace("start()");

        if (started) {
            logger_.trace("already started");
            return;
        }

        encoder.start();
        logger_.trace("encoder startede");

        appender.start();
        logger_.trace("appender started");

        logger.addAppender(appender);
        logger_.trace("appender attached");

        started = true;
    }


    private synchronized void stop() {

        logger_.trace("stop()");

        if (!started) {
            logger_.trace("not started yet");
            return;
        }

        final boolean detached = logger.detachAppender(appender);
        logger_.trace("appender detached: {}", detached);

        appender.stop();

        encoder.stop();

        started = false;
    }


    @Override
    protected void finalize() throws Throwable {

        try {
            stop();
        } catch (final Exception e) {
            e.printStackTrace(System.err);
        }

        super.finalize();
    }


    private transient final org.slf4j.Logger logger_
        = getLogger(lookup().lookupClass());


    private final Logger logger;


    private final BufferedOutputStreamAppender<ILoggingEvent> appender;


    private final PatternLayoutEncoder encoder;


    private volatile boolean started = false;


}

