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


import ch.qos.logback.core.OutputStreamAppender;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import static java.lang.invoke.MethodHandles.lookup;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;


/**
 * An appender for buffering records.
 *
 * @author Jin Kwon &lt;jinahya_at_gmail.com&gt;
 * @param <E> event type parameter
 */
public class BufferedOutputStreamAppender<E> extends OutputStreamAppender<E> {


    @Override
    public void start() {

        setOutputStream(buffer);

//        // oh, i can't help but doin' this.
//        try {
//            final Method method
//                = OutputStreamAppender.class.getDeclaredMethod("encoderInit");
//            if (!method.isAccessible()) {
//                method.setAccessible(true);
//            }
//            method.invoke(this);
//        } catch (final ReflectiveOperationException roe) {
//            logger.error("failed to invoke encoderInit", roe);
//        }

        super.start();
    }


    @Override
    protected void append(final E eventObject) {

        super.append(eventObject);

        // there is no other way to check
        // whether eventObject actaully appended or not.
        if (!isStarted()) {
            return;
        }

        final byte[] record = buffer.toByteArray();
        buffer.reset();
        records.add(record);
        length += record.length;

        while (limit >= 0 && length > limit && !records.isEmpty()) {
            length -= records.remove(0).length;
        }
    }


    /**
     * Writes buffered records to specified {@code otuput}.
     *
     * @param output the output stream to which buffered records are written.
     *
     * @throws IOException if an I/O error occurs.
     */
    public void write(final OutputStream output) throws IOException {

        for (final byte[] record : records) {
            output.write(record);
        }
    }


    /**
     * Returns a concatenated buffered records.
     *
     * @return a concatenated buffered records.
     */
    public byte[] toByteArray() {

        final byte[] array = new byte[length];

        int position = 0;
        for (final byte[] record : records) {
            System.arraycopy(record, 0, array, position, record.length);
            position += record.length;
        }

        return array;
    }


    /**
     * Returns a string representing buffered records.
     *
     * @param charset the charset to encode.
     *
     * @return a string representing buffered records.
     *
     * @see #toByteArray()
     */
    public String toString(final Charset charset) {

        final byte[] bytes = toByteArray();

        if (charset == null) {
            return new String(bytes);
        }

        return new String(bytes, charset);
    }


    /**
     * Returns current value of {@code limit}.
     *
     * @return current value of {@code limit}.
     */
    public int getLimit() {

        return limit;
    }


    /**
     * Sets a new value for {@code limit}.
     *
     * @param limit new value of {@code limit}. Negative value for no limit.
     */
    public void setLimit(final int limit) {

        this.limit = limit;

        if (this.limit < 0) {
            logger.warn("negative limit: {}", this.limit);
        }
    }


    /**
     * Returns the number of bytes buffered so far.
     *
     * @return the number of bytes buffered so far.
     */
    public int getLength() {

        return length;
    }


    /**
     * logger.
     */
    private transient final Logger logger = getLogger(lookup().lookupClass());


    /**
     * the output stream to buffer.
     */
    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();


    /**
     * the maximum number of bytes to buffer.
     */
    private int limit = 8192;


    /**
     * the buffered record list.
     */
    private final List<byte[]> records = new ArrayList<>();


    /**
     * the number of bytes buffered so far.
     */
    private int length = 0;


}

