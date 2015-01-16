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


import java.lang.reflect.Method;
import java.nio.charset.Charset;


/**
 *
 * @author Jin Kwon &lt;jinahya_at_gmail.com&gt;
 */
public class LogRecorderReflector {


    private static final String LOG_RECORDER_NAME
        = "com.github.jinahya.logback.core.LogRecorder";


    private static final Class<?> LOG_RECORDER_CLASS;


    private static final String LOGBACK_LOGGER_NAME
        = "ch.qos.logback.classic.Logger";


    private static final Class<?> LOGBACK_LOGGER_CLASS;


    private static final String SLF4J_LOGGER_NAME = "org.slf4j.Logger";


    private static final Class<?> SLF4J_LOGGER_CLASS;


    private static final Method START_WITH_LOGBACK_LOGGER;


    private static final Method START_WITH_SLF4J_LOGGER;


    private static final Method FINISH;

//
//    private static final Method FINISH_WITH_LIST;
//
//
//    private static final Method FINISH_WITH_LAYOUT_AND_APPENDABLE;
//
//
//    private static final Method FINISH_WITH_PATTERN_AND_APPENDABLE;

    static {
        try {
            LOG_RECORDER_CLASS = Class.forName(LOG_RECORDER_NAME);
            LOGBACK_LOGGER_CLASS = Class.forName(LOGBACK_LOGGER_NAME);
            SLF4J_LOGGER_CLASS = Class.forName(SLF4J_LOGGER_NAME);
            START_WITH_LOGBACK_LOGGER = LOG_RECORDER_CLASS.getMethod("start", LOGBACK_LOGGER_CLASS, String.class, Charset.class, Integer.TYPE);
            START_WITH_SLF4J_LOGGER = LOG_RECORDER_CLASS.getMethod("start", SLF4J_LOGGER_CLASS, String.class, Charset.class, Integer.TYPE);
            FINISH = LOG_RECORDER_CLASS.getMethod("finish", LOG_RECORDER_CLASS);
        } catch (final ReflectiveOperationException roe) {
            roe.printStackTrace(System.err);
            throw new InstantiationError(
                "reflective operation exception: " + roe.getMessage());
        }
    }


    public static Object start(final Object logger, final String pattern,
                               final Charset charset, final int limit)
        throws ReflectiveOperationException {

        if (SLF4J_LOGGER_CLASS.isInstance(logger)) {
            return START_WITH_SLF4J_LOGGER.invoke(
                null, logger, pattern, charset, limit);
        }

        if (LOGBACK_LOGGER_CLASS.isInstance(logger)) {
            return START_WITH_LOGBACK_LOGGER.invoke(
                null, logger, pattern, charset, limit);
        }

        throw new IllegalArgumentException(
            "unknown logger type: " + logger.toString());
    }


    public static String finish(final Object recorder)
        throws ReflectiveOperationException {

        return (String) FINISH.invoke(null, recorder);
    }


    private LogRecorderReflector() {

        super();
    }


}

