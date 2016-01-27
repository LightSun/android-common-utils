package com.heaven7.third.pay;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by heaven7 on 2015/9/15.
 */
public class IoUtil {

    @Deprecated
    public static byte[] readFromStream(InputStream in) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int len ;
        byte[] buf = new byte[1024];
        while( ( len = in.read(buf) )!=-1){
            baos.write(buf, 0, len);
        }
        byte[] result = baos.toByteArray();
        baos.close();
        return result;
    }

    public static byte[] getBytesFromStreamAndClose(InputStream in) throws IOException{
        try {
            return getBytesFromStream(in);
        }finally{
            closeQuietly(in);
        }
    }

    /**
     * Efficiently fetch bytes from InputStream is by delegating to
     * getBytesFromStream(is, is.available())
     */
    public static byte[] getBytesFromStream(final InputStream is) throws IOException {
        return getBytesFromStream(is, is.available());
    }

    /**
     * Efficiently fetch the bytes from the InputStream, provided that caller can guess
     * exact numbers of bytes that can be read from inputStream. Avoids one extra byte[] allocation
     * that ByteStreams.toByteArray() performs.
     * @param hint - size of inputStream's content in bytes
     */
    public static byte[] getBytesFromStream(InputStream inputStream, int hint) throws IOException {
        // Subclass ByteArrayOutputStream to avoid an extra byte[] allocation and copy
        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream(hint) {
            @Override
            public byte[] toByteArray() {
                // Can only use the raw buffer directly if the size is equal to the array we have.
                // Otherwise we have no choice but to copy.
                if (count == buf.length) {
                    return buf;
                } else {
                    return super.toByteArray();
                }
            }
        };
        ByteStreams.copy(inputStream, byteOutput);
        return byteOutput.toByteArray();
    }

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (RuntimeException rethrown) {
                throw rethrown;
            } catch (Exception ignored) {
            }
        }
    }
}
