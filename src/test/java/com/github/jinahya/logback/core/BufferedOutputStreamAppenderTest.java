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


import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.util.ContextSelectorStaticBinder;
import static java.lang.invoke.MethodHandles.lookup;
import java.nio.charset.StandardCharsets;
import static java.util.concurrent.ThreadLocalRandom.current;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.slf4j.LoggerFactory.getLogger;
import org.testng.annotations.Test;


/**
 *
 * @author Jin Kwon &lt;jinahya_at_gmail.com&gt;
 */
public class BufferedOutputStreamAppenderTest {


    @Test(enabled = false, invocationCount = 128)
    public void test() {

        final LoggerContext context;
        switch (current().nextInt(3)) {
            case 0:
                context = (LoggerContext) LoggerFactory.getILoggerFactory();
                break;
            case 1:
                context = ContextSelectorStaticBinder.getSingleton()
                    .getContextSelector().getDefaultLoggerContext();
                break;
            default:
                context = ((ch.qos.logback.classic.Logger) logger)
                    .getLoggerContext();
                break;
        }

        final PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(context);
        encoder.setPattern("%msg%n");
        encoder.setCharset(StandardCharsets.UTF_8);
        encoder.start();

        final BufferedOutputStreamAppender<ILoggingEvent> appender
            = new BufferedOutputStreamAppender<>();
        appender.setContext(context);
        appender.setEncoder(encoder);
        appender.start();

        ((ch.qos.logback.classic.Logger) logger).addAppender(appender);

        logger.debug("debug");
        logger.info("info");
        logger.warn("warn", new Exception("error"));
        logger.error("error", new Exception("error"));

        ((ch.qos.logback.classic.Logger) logger).detachAppender(appender);

        appender.stop();

        encoder.stop();

        logger.debug("string: {}", appender.toString());
        logger.debug("string: {}", appender.toString(encoder.getCharset()));
    }


    private transient final Logger logger = getLogger(lookup().lookupClass());


}

