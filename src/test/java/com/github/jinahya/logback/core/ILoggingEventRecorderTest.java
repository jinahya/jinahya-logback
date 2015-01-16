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


import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import java.io.IOException;
import static java.lang.invoke.MethodHandles.lookup;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.List;
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;
import static org.testng.FileAssert.fail;
import org.testng.annotations.Test;


/**
 *
 * @author Jin Kwon &lt;jinahya_at_gmail.com&gt;
 */
public class ILoggingEventRecorderTest {


    @Test(enabled = false)
    public void startAndWaitFinalization() {

        final ReferenceQueue<ILoggingEventRecorder> queue
            = new ReferenceQueue<>();
        ILoggingEventRecorder referent = ILoggingEventRecorder.start(
            (ch.qos.logback.classic.Logger) logger, 100);
        final WeakReference<ILoggingEventRecorder> reference
            = new WeakReference<>(referent, queue);
        referent = null;

        new Thread(() -> {
            for (int i = 0; i < 100 && queue.poll() == null; i++) {
                try {
                    Thread.sleep(1000);
                } catch (final InterruptedException ie) {
                    fail("interrupted", ie);
                }
            }
            logger.debug("end-of-while");
        }).start();
    }


    @Test(enabled = false)
    public void startAndFinish() {

        final ILoggingEventRecorder recorder = ILoggingEventRecorder.start(
            (ch.qos.logback.classic.Logger) logger, 100);

        logger.trace("trace");
        logger.debug("debug");
        logger.info("info");
        logger.warn("warn");
        logger.error("error");

        final List<ILoggingEvent> events
            = ILoggingEventRecorder.finish(recorder);
        logger.debug("events: {}", events);
    }


    @Test
    public void startAndFinishWithLayoutAndAppender() throws IOException {

        final ILoggingEventRecorder recorder = ILoggingEventRecorder.start(
            (ch.qos.logback.classic.Logger) logger, 100);

        logger.trace("trace");
        logger.debug("debug");
        logger.info("info");
        logger.warn("warn");
        logger.error("error");

        final PatternLayout layout = new PatternLayout();
        layout.setPattern("with layout: %message%n");

        final StringBuilder appender = ILoggingEventRecorder.finish(
            recorder, layout, new StringBuilder());
        logger.debug("appended: {}", appender.toString());
    }


    @Test
    public void startAndFinishWithPatternAndAppender() throws IOException {

        final ILoggingEventRecorder recorder = ILoggingEventRecorder.start(
            (ch.qos.logback.classic.Logger) logger, 100);

        logger.trace("trace");
        logger.debug("debug");
        logger.info("info");
        logger.warn("warn");
        logger.error("error");

        final StringBuilder appender = ILoggingEventRecorder.finish(
            recorder, "with pattern: %message%n", new StringBuilder());
        logger.debug("appended: {}", appender.toString());
    }


    private transient final Logger logger = getLogger(lookup().lookupClass());


}

