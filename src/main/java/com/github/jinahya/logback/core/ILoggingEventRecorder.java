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
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.read.CyclicBufferAppender;
import java.io.IOException;
import static java.lang.invoke.MethodHandles.lookup;
import java.util.ArrayList;
import java.util.List;
import static org.slf4j.LoggerFactory.getLogger;


/**
 *
 * @author Jin Kwon &lt;jinahya_at_gmail.com&gt;
 * @see <a href="https://gist.github.com/olim7t/881318">olim7t /
 * LogbackCapture.java</a>
 */
public final class ILoggingEventRecorder {


    public static ILoggingEventRecorder start(final Logger logger,
                                              final int maxSize) {

        if (logger == null) {
            throw new NullPointerException("null logger");
        }

        final CyclicBufferAppender<ILoggingEvent> appender
            = new CyclicBufferAppender<>();
        appender.setMaxSize(maxSize);
        appender.setContext(logger.getLoggerContext());

        final ILoggingEventRecorder recorder
            = new ILoggingEventRecorder(logger, appender);

        recorder.start();

        return recorder;
    }


    public static ILoggingEventRecorder start(final org.slf4j.Logger logger,
                                              final int maxSize) {

        if (logger == null) {
            throw new NullPointerException("null logger");
        }

        if (!Logger.class.isInstance(logger)) {
            throw new IllegalArgumentException(
                "logger(" + logger + ") is not an instance of " + Logger.class);
        }

        return start(Logger.class.cast(logger), maxSize);
    }


    public static List<ILoggingEvent> finish(
        final ILoggingEventRecorder recorder,
        final List<ILoggingEvent> events) {

        events.addAll(recorder.events());

        recorder.stop();

        return events;
    }


    public static List<ILoggingEvent> finish(
        final ILoggingEventRecorder recorder) {

        return finish(recorder, new ArrayList<ILoggingEvent>());
    }


    public static <T extends Appendable> T finish(
        final ILoggingEventRecorder recorder,
        final Layout<ILoggingEvent> layout,
        final T appendable)
        throws IOException {

        final org.slf4j.Logger logger = getLogger(lookup().lookupClass());

        logger.trace("finish({}, {}, {})", recorder, layout, appendable);

        if (recorder == null) {
            throw new NullPointerException("null recorder");
        }

        if (layout == null) {
            throw new NullPointerException("null layout");
        }

        if (appendable == null) {
            throw new NullPointerException("null appendable");
        }

        if (layout.getContext() == null) {
            layout.setContext(recorder.logger.getLoggerContext());
        }
        final boolean started = layout.isStarted();
        if (!started) {
            layout.start();
        }

        for (final ILoggingEvent event : recorder.events()) {
            appendable.append(layout.doLayout(event));
        }

        if (!started) {
            layout.stop();
        }

        recorder.stop();

        return appendable;
    }


    public static <T extends Appendable> T finish(
        final ILoggingEventRecorder recorder, final String pattern,
        final T appendable)
        throws IOException {

        final org.slf4j.Logger logger = getLogger(lookup().lookupClass());

        logger.trace("finish({}, {}, {})", recorder, pattern, appendable);

        if (recorder == null) {
            throw new NullPointerException("null recorder");
        }

        if (pattern == null) {
            throw new NullPointerException("null pattern");
        }

        if (appendable == null) {
            throw new NullPointerException("null appendable");
        }

        final PatternLayout layout = new PatternLayout();
        layout.setPattern(pattern);

        return finish(recorder, layout, appendable);
    }


    private ILoggingEventRecorder(
        final Logger logger,
        final CyclicBufferAppender<ILoggingEvent> appender) {

        super();

        if (logger == null) {
            throw new NullPointerException("null logger");
        }

        if (appender == null) {
            throw new NullPointerException("null appender");
        }

        this.logger = logger;
        this.appender = appender;
    }


    private List<ILoggingEvent> events() {

        logger_.debug("events()");

        final int length = appender.getLength();
        final List<ILoggingEvent> events = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            events.add(appender.get(i));
        }

        return events;
    }


    private synchronized void start() {

        logger_.debug("start()");

        if (started) {
            logger_.debug("already started");
            return;
        }

        appender.start();
        logger_.debug("appender started");

        logger.addAppender(appender);
        logger_.debug("appender attached");

        started = true;
    }


    private synchronized void stop() {

        logger_.debug("stop()");

        if (!started) {
            logger_.debug("not started yet");
            return;
        }

        final boolean detached = logger.detachAppender(appender);
        logger_.debug("appender detached: {}", detached);

        final int length = appender.getLength();
        final List<ILoggingEvent> list = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            list.add(appender.get(i));
        }

        appender.stop();
        logger_.debug("appender stopped");

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


    private final CyclicBufferAppender<ILoggingEvent> appender;


    private volatile boolean started = false;


}

