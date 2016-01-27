package com.android.volley.extra;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicHeader;

import android.text.TextUtils;
/**
 * test:
 * <pre>
  MultipartEntity multipartEntity = new MultipartEntity();
// 文本参数
multipartEntity.addStringPart("type", "我的文本参数");
Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.thumb);
// 二进制参数
multipartEntity.addBinaryPart("images", bitmapToBytes(bmp));
// 文件参数
multipartEntity.addFilePart("images", new File("storage/emulated/0/test.jpg"));
 
// POST请求
HttpPost post = new HttpPost("url") ;
// 将multipartEntity设置给post
post.setEntity(multipartEntity);
// 使用http client来执行请求
HttpClient httpClient = new DefaultHttpClient() ;
httpClient.execute(post) ;
 </pre>
 <li>输出格式
 <pre>
 POST /api/feed/ HTTP/1.1
Content-Type: multipart/form-data; boundary=o3Fhj53z-oKToduAElfBaNU4pZhp4-
User-Agent: Dalvik/1.6.0 (Linux; U; Android 4.4.4; M040 Build/KTU84P)
Host: www.myhost.com
Connection: Keep-Alive
Accept-Encoding: gzip
Content-Length: 168518
 
--o3Fhj53z-oKToduAElfBaNU4pZhp4-
Content-Type: text/plain; charset=UTF-8
Content-Disposition: form-data; name="type"
Content-Transfer-Encoding: 8bit
 
This my type
--o3Fhj53z-oKToduAElfBaNU4pZhp4-
Content-Type: application/octet-stream
Content-Disposition: form-data; name="images"; filename="no-file"
Content-Transfer-Encoding: binary
 
这里是bitmap的二进制数据
--o3Fhj53z-oKToduAElfBaNU4pZhp4-
Content-Type: application/octet-stream
Content-Disposition: form-data; name="file"; filename="storage/emulated/0/test.jpg"
Content-Transfer-Encoding: binary
 
这里是图片文件的二进制数据
--o3Fhj53z-oKToduAElfBaNU4pZhp4---
 </pre>
 */
public class MultipartEntity implements HttpEntity {
 
    private final static char[] MULTIPART_CHARS = "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
            .toCharArray();
    /**
     * 换行符
     */
    private static final String NEW_LINE_STR = "\r\n";
    private static final String CONTENT_TYPE = "Content-Type: ";
    private static final String CONTENT_DISPOSITION = "Content-Disposition: ";
    /**
     * 文本参数和字符集
     */
    private static  final String TYPE_TEXT_CHARSET = "text/plain; charset=UTF-8";
 
    /**
     * 字节流参数
     */
    private static final String TYPE_OCTET_STREAM = "application/octet-stream";
    /**
     * 二进制参数
     */
    private static final byte[] BINARY_ENCODING = "Content-Transfer-Encoding: binary\r\n\r\n".getBytes();
    /**
     * 文本参数
     */
    private static final byte[] BIT_ENCODING = "Content-Transfer-Encoding: 8bit\r\n\r\n".getBytes();
 
    /**
     * 分隔符
     */
    private String mBoundary = null;
    /**
     * 输出流
     */
    ByteArrayOutputStream mOutputStream = new ByteArrayOutputStream();
 
    public MultipartEntity() {
        this.mBoundary = generateBoundary();
    }
 
    /**
     * 生成分隔符
     */
    private final String generateBoundary() {
        final StringBuffer buf = new StringBuffer();
        final Random rand = new Random();
        for (int i = 0; i < 30; i++) {
            buf.append(MULTIPART_CHARS[rand.nextInt(MULTIPART_CHARS.length)]);
        }
        return buf.toString();
    }
 
    /**
     * 参数开头的分隔符
     */
    private void writeFirstBoundary() throws IOException {
        mOutputStream.write(("--" + mBoundary + "\r\n").getBytes());
    }
 
    /**
     * 添加文本参数
     */
    public void addStringPart(final String paramName, final String value) {
        writeToOutputStream(paramName, value.getBytes(), TYPE_TEXT_CHARSET, BIT_ENCODING, "");
    }
 
    /**
     * 将数据写入到输出流中
     * 
     * @param key
     * @param rawData
     * @param type
     * @param encodingBytes
     * @param fileName
     */
    private void writeToOutputStream(String paramName, byte[] rawData, String type,
            byte[] encodingBytes,
            String fileName) {
        try {
            writeFirstBoundary();
            mOutputStream.write((CONTENT_TYPE + type + NEW_LINE_STR).getBytes());
            mOutputStream
                    .write(getContentDispositionBytes(paramName, fileName));
            mOutputStream.write(encodingBytes);
            mOutputStream.write(rawData);
            mOutputStream.write(NEW_LINE_STR.getBytes());
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
 
    /**
     * 添加二进制参数, 例如Bitmap的字节流参数
     * 
     * @param key
     * @param rawData
     */
    public void addBinaryPart(String paramName, final byte[] rawData) {
        writeToOutputStream(paramName, rawData, TYPE_OCTET_STREAM, BINARY_ENCODING, "no-file");
    }
    
    public void addFilePart(final String key, final File file,String mime) 
         throws IllegalStateException{
        InputStream fin = null;
        try {
            fin = new FileInputStream(file);
            writeFirstBoundary();
            final String type = CONTENT_TYPE + (mime!=null?mime:TYPE_OCTET_STREAM) + NEW_LINE_STR;
            mOutputStream.write(getContentDispositionBytes(key, file.getName()));
            mOutputStream.write(type.getBytes());
            mOutputStream.write(BINARY_ENCODING);
 
            final byte[] tmp = new byte[4096];
            int len = 0;
            while ((len = fin.read(tmp)) != -1) {
                mOutputStream.write(tmp, 0, len);
            }
            mOutputStream.write(NEW_LINE_STR.getBytes());//be careful don't forget this
            mOutputStream.flush();
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        } finally {
            closeSilently(fin);
        }
    }
    /**
     * default mime is {@link #TYPE_OCTET_STREAM}
     * @param key
     * @param file
     */
    public void addFilePart(final String key, final File file) {
    	addFilePart(key, file, null);
    }
 
    private void closeSilently(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
 
    private byte[] getContentDispositionBytes(String paramName, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(CONTENT_DISPOSITION + "form-data; name=\"" + paramName + "\"");
        // 文本参数没有filename参数,设置为空即可
        if (!TextUtils.isEmpty(fileName)) {
            stringBuilder.append("; filename=\""
                    + fileName + "\"");
        }
 
        return stringBuilder.append(NEW_LINE_STR).toString().getBytes();
    }
 
    @Override
    public long getContentLength() {
        return mOutputStream.toByteArray().length;
    }
 
    @Override
    public Header getContentType() {
        return new BasicHeader("Content-Type", "multipart/form-data; boundary=" + mBoundary);
    }
 
    @Override
    public boolean isChunked() {
        return false;
    }
 
    @Override
    public boolean isRepeatable() {
        return false;
    }
 
    @Override
    public boolean isStreaming() {
        return false;
    }
 
    @Override
    public void writeTo(final OutputStream outstream) throws IOException {
        // 参数最末尾的结束符
        final String endString = "--" + mBoundary + "--\r\n";
        // 写入结束符
        mOutputStream.write(endString.getBytes());
        //
        outstream.write(mOutputStream.toByteArray());
    }
 
    @Override
    public Header getContentEncoding() {
        return null;
    }
 
    @Override
    public void consumeContent() throws IOException,
            UnsupportedOperationException {
        if (isStreaming()) {
            throw new UnsupportedOperationException(
                    "Streaming entity does not implement #consumeContent()");
        }
    }
 
    @Override
    public InputStream getContent() {
        return new ByteArrayInputStream(mOutputStream.toByteArray());
    }
}