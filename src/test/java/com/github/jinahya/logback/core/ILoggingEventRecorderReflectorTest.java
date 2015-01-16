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
import static com.github.jinahya.logback.core.ILoggingEventRecorderReflector.finish;
import static com.github.jinahya.logback.core.ILoggingEventRecorderReflector.finishWithLayoutAndAppendable;
import static com.github.jinahya.logback.core.ILoggingEventRecorderReflector.finishWithPatternAndAppendable;
import static com.github.jinahya.logback.core.ILoggingEventRecorderReflector.start;
import static java.lang.invoke.MethodHandles.lookup;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;
import static org.testng.FileAssert.fail;
import org.testng.annotations.Test;


/**
 *
 * @author Jin Kwon &lt;jinahya_at_gmail.com&gt;
 */
public class ILoggingEventRecorderReflectorTest {


    @Test
    public void startAndWaitFinalization() throws ReflectiveOperationException {

        final ReferenceQueue<Object> queue = new ReferenceQueue<>();
        Object referent = ILoggingEventRecorderReflector.start(logger, 100);
        final WeakReference<Object> reference
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


    @Test
    public void startAndFinish() throws ReflectiveOperationException {

        final Object recorder = start(logger, 100);

        logger.trace("trace");
        logger.debug("debug");
        logger.info("info");
        logger.warn("warn");
        logger.error("error");

        final Object events = finish(recorder);
        logger.trace("events: {}", events);
    }


//    @Test
//    public void startAndFinishWithList() throws ReflectiveOperationException {
//
//        final Object recorder = start(logger, 100);
//
//        logger.trace("trace");
//        logger.debug("debug");
//        logger.info("info");
//        logger.warn("warn");
//        logger.error("error");
//
//        @SuppressWarnings("unchecked")
//        final List<ILoggingEvent> events = (List<ILoggingEvent>) finishWithList(
//            recorder, new ArrayList<ILoggingEvent>());
//
//        logger.trace("events: {}", events);
//    }
    @Test
    public void startAndFinishWithLayoutAndAppendable()
        throws ReflectiveOperationException {

        final Object recorder = start(logger, 100);

        logger.trace("trace");
        logger.debug("debug");
        logger.info("info");
        logger.warn("warn");
        logger.error("error");

        final PatternLayout layout = new PatternLayout();
        layout.setPattern("with layout: %message%n");

        final Object appendable = finishWithLayoutAndAppendable(
            recorder, layout, new StringBuilder());

        logger.trace("appendable: {}", appendable.toString());
    }


    @Test
    public void startAndFinishWithPatternAndAppendable()
        throws ReflectiveOperationException {

        final Object recorder = start(logger, 100);

        logger.trace("trace");
        logger.debug("debug");
        logger.info("info");
        logger.warn("warn");
        logger.error("error");

        final PatternLayout layout = new PatternLayout();
        layout.setPattern("with layout: %message%n");

        final Object appendable
            = finishWithPatternAndAppendable(
                recorder, "with pattern: %message%n", new StringBuilder());

        logger.trace("appendable: {}", appendable.toString());
    }


    private transient final Logger logger = getLogger(lookup().lookupClass());


}

