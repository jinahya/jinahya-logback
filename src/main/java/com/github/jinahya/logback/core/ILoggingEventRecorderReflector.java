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


/**
 *
 * @author Jin Kwon &lt;jinahya_at_gmail.com&gt;
 */
public class ILoggingEventRecorderReflector {


    private static final String I_LOGGING_EVENT_RECORDER_NAME
        = "com.github.jinahya.logback.core.ILoggingEventRecorder";


    private static final Class<?> I_LOGGING_EVENT_RECORDER_CLASS;


    private static final String LOGBACK_LOGGER_NAME
        = "ch.qos.logback.classic.Logger";


    private static final Class<?> LOGBACK_LOGGER_CLASS;


    private static final String SLF4J_LOGGER_NAME = "org.slf4j.Logger";


    private static final Class<?> SLF4J_LOGGER_CLASS;


    private static final Method START_WITH_LOGBACK_LOGGER;


    private static final Method START_WITH_SLF4J_LOGGER;


    private static final Method FINISH;


//    private static final Method FINISH_WITH_LIST;
    private static final Method FINISH_WITH_LAYOUT_AND_APPENDABLE;


    private static final Method FINISH_WITH_PATTERN_AND_APPENDABLE;


    static {
        try {
            I_LOGGING_EVENT_RECORDER_CLASS
                = Class.forName(I_LOGGING_EVENT_RECORDER_NAME);
            LOGBACK_LOGGER_CLASS = Class.forName(LOGBACK_LOGGER_NAME);
            SLF4J_LOGGER_CLASS = Class.forName(SLF4J_LOGGER_NAME);
            START_WITH_LOGBACK_LOGGER = I_LOGGING_EVENT_RECORDER_CLASS.getMethod("start", LOGBACK_LOGGER_CLASS, Integer.TYPE);
            START_WITH_SLF4J_LOGGER = I_LOGGING_EVENT_RECORDER_CLASS.getMethod("start", SLF4J_LOGGER_CLASS, Integer.TYPE);
            FINISH = I_LOGGING_EVENT_RECORDER_CLASS.getMethod("finish", I_LOGGING_EVENT_RECORDER_CLASS);
//            FINISH_WITH_LIST = I_LOGGING_EVENT_RECORDER_CLASS.getMethod("finsih", I_LOGGING_EVENT_RECORDER_CLASS, List.class);
            FINISH_WITH_LAYOUT_AND_APPENDABLE = I_LOGGING_EVENT_RECORDER_CLASS.getMethod("finish", I_LOGGING_EVENT_RECORDER_CLASS, Class.forName("ch.qos.logback.core.Layout"), Appendable.class);
            FINISH_WITH_PATTERN_AND_APPENDABLE = I_LOGGING_EVENT_RECORDER_CLASS.getMethod("finish", I_LOGGING_EVENT_RECORDER_CLASS, String.class, Appendable.class);
        } catch (final ReflectiveOperationException roe) {
            roe.printStackTrace(System.err);
            throw new InstantiationError(
                "reflective operation exception: " + roe.getMessage());
        }
    }


    public static Object start(final Object logger, final int maxSize)
        throws ReflectiveOperationException {

        if (SLF4J_LOGGER_CLASS.isInstance(logger)) {
            return START_WITH_SLF4J_LOGGER.invoke(null, logger, maxSize);
        }

        if (LOGBACK_LOGGER_CLASS.isInstance(logger)) {
            return START_WITH_LOGBACK_LOGGER.invoke(null, logger, maxSize);
        }

        throw new IllegalArgumentException(
            "unknown logger type: " + logger.toString());
    }


    public static Object finish(final Object recorder)
        throws ReflectiveOperationException {

        return FINISH.invoke(null, recorder);
    }


//    public static Object finishWithList(final Object recorder,
//                                        final Object list)
//        throws ReflectiveOperationException {
//
//        return FINISH_WITH_LIST.invoke(null, recorder, list);
//    }
    public static Object finishWithLayoutAndAppendable(final Object recorder,
                                                       final Object layout,
                                                       final Object appendable)
        throws ReflectiveOperationException {

        return FINISH_WITH_LAYOUT_AND_APPENDABLE.invoke(null, recorder, layout,
                                                        appendable);
    }


    public static Object finishWithPatternAndAppendable(final Object recorder,
                                                        final Object pattern,
                                                        final Object appendable)
        throws ReflectiveOperationException {

        return FINISH_WITH_PATTERN_AND_APPENDABLE.invoke(
            null, recorder, pattern, appendable);
    }


    private ILoggingEventRecorderReflector() {

        super();
    }


}

