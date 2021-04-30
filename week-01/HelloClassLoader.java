package com.jianjoy.ch01;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author zhoujian
 * @date 2021/4/30 11:47
 */
public class HelloClassLoader extends ClassLoader {

    private static final String CLASS_FILE_NAME = "Hello.xlass";

    private static final int BYTE_OFFSET = 255;

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] bytes = getClassPathFileContent(CLASS_FILE_NAME);
        byte[] decodeBytes = decode(bytes);
        return defineClass(name, decodeBytes, 0, decodeBytes.length);
    }

    private byte[] decode(byte[] bytes) {
        byte[] decodeBytes = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            decodeBytes[i] = intToByte(BYTE_OFFSET - byteToInt(bytes[i]));
        }
        return decodeBytes;
    }

    private byte[] getClassPathFileContent(String xlassFileName) {
        InputStream inputStream = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            inputStream = getClass().getClassLoader().getResourceAsStream(xlassFileName);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(inputStream);
            closeQuietly(bos);
        }
        return bos.toByteArray();
    }

    private byte intToByte(int x) {
        return (byte) x;
    }

    private int byteToInt(byte b) {
        return b & 0xFF;
    }

    private void closeQuietly(Closeable closeable) {
        if (Objects.nonNull(closeable)) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Class helloClass = new HelloClassLoader().loadClass("Hello");
        Object helloObj = helloClass.newInstance();
        Method method = helloObj.getClass().getMethod("hello");
        method.invoke(helloObj);
    }
}
