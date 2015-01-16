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


import static java.lang.invoke.MethodHandles.lookup;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;
import static org.testng.FileAssert.fail;
import org.testng.annotations.Test;


/**
 *
 * @author Jin Kwon &lt;jinahya_at_gmail.com&gt;
 */
public class LogRecorderTest {


    @Test(enabled = false)
    public void startAndWaitFinalization() {

        final ReferenceQueue<LogRecorder> queue = new ReferenceQueue<>();
        LogRecorder referent = LogRecorder.start(
            logger, "%message%n", StandardCharsets.UTF_8, 1024);
        final WeakReference<LogRecorder> reference
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


    @Test(enabled = true)
    public void startAndFinish() {

        final LogRecorder recorder = LogRecorder.start(
            (ch.qos.logback.classic.Logger) logger, "%class %method %line %message%n",
            StandardCharsets.UTF_8, 1024);

        logger.trace("trace");
        logger.debug("debug");
        logger.info("info");
        logger.warn("warn");
        logger.error("error");

        final String records = LogRecorder.finish(recorder);
        logger.debug("records: {}", records);
    }


    private transient final Logger logger = getLogger(lookup().lookupClass());


}

