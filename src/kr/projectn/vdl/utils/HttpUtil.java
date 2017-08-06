package kr.projectn.vdl.utils;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Http request utility class
 *
 * Created by qscx9512 on 2017-05-03.
 */

public class HttpUtil {
    private CloseableHttpClient client;
    private CloseableHttpResponse response;
    private String strUserAgent;
    private URIBuilder builder;
    private URI uri;
    private Map<String, String> customHeader;

    private HttpUtil() {
        strUserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.81 Safari/537.36";
        builder = new URIBuilder();
    }

    private static class Singleton{
        private static final HttpUtil instance = new HttpUtil();
    }

    public static HttpUtil getInstance() {
        return Singleton.instance;
    }

    public HttpUtil requestByGet() {
        HttpGet hGet = new HttpGet(uri);
        hGet.addHeader("User-Agent", strUserAgent);
        hGet.addHeader("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4,ja;q=0.2");
        //http://stove99.tistory.com/96
        if (!customHeader.isEmpty()) {
            for (Map.Entry<String, String> el : customHeader.entrySet()) {
                hGet.addHeader(el.getKey(), el.getValue());
            }
        }
        try {
            response = client.execute(hGet);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            ExceptionReportUtil.reportExceptionToFile(e);
        }

        return this;
    }

    public HttpUtil requestByPost() {
        HttpPost hPost = new HttpPost(uri);
        hPost.addHeader("User-Agent", strUserAgent);
        hPost.addHeader("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4,ja;q=0.2");
        //http://stove99.tistory.com/96
        if (!customHeader.isEmpty()) {
            for (Map.Entry<String, String> el : customHeader.entrySet()) {
                hPost.addHeader(el.getKey(), el.getValue());
            }
        }
        try {
            response = client.execute(hPost);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            ExceptionReportUtil.reportExceptionToFile(e);
        }

        return this;
    }

    public HttpUtil requestByGet(Map<String, String> header) {
        customHeader = header;
        this.requestByGet();
        return this;
    }

    public HttpUtil requestByPost(Map<String, String> header) {
        customHeader = header;
        this.requestByPost();
        return this;
    }

    public String getAsString() {
        //https://www.mkyong.com/java/how-to-convert-inputstream-to-string-in-java
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        String str;

        try {
            reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),
                    "utf-8"));
            while((str = reader.readLine()) != null) {
                builder.append(str);
                //http://stackoverflow.com/questions/6402277/how-to-save-newlines-from-xml
                builder.append("\n");
            }
        }
        catch (IOException e) {
            ExceptionReportUtil.reportExceptionToFile(e);
        }
        finally {
            if(reader != null) {
                try {
                    reader.close();
                }
                catch (IOException e) {
                    ExceptionReportUtil.reportExceptionToFile(e);
                }
            }
        }
        return builder.toString();
    }

    public InputStream getAsInputStream() {
        InputStream ret = null;
        try {
            ret = response.getEntity().getContent();
        } catch (UnsupportedOperationException | IOException e) {
            // TODO Auto-generated catch block
            ExceptionReportUtil.reportExceptionToFile(e);
        }
        return ret;
    }

    public void setClientConnection(String url) {
        customHeader = new HashMap<String, String>();
        builder = new URIBuilder();
        try {
            if (url.contains("https")) {  //Determine SSL Connection
                // Trust own CA and all self-signed certs
                builder = builder.setScheme("https");
                builder = builder.setHost(url.split("\\:\\/\\/")[1]);
                client = HttpClients.custom()
                        .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                        .build();
            } else if (url.contains("http")) {
                builder = builder.setScheme("http");
                builder = builder.setHost(url.split("\\:\\/\\/")[1]);
                client = HttpClients.createDefault();
            } else {
                builder = builder.setScheme("http");
                builder = builder.setHost(url);
                client = HttpClients.createDefault();
            }

            uri = builder.build();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            ExceptionReportUtil.reportExceptionToFile(e);
        }
    }

    public void setConnectionParameter(List<NameValuePair> param) {
        try {
        builder = builder.setParameters(param);
        uri = builder.build();
        } catch (URISyntaxException e) {
            ExceptionReportUtil.reportExceptionToFile(e);
        }
    }

    public boolean writeFile(String title) {
        try {
            InputStream ret = response.getEntity().getContent();
            BufferedInputStream in = new BufferedInputStream(ret);
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(title)));
            int inByte;
            while((inByte = in.read()) != -1) out.write(inByte);
            in.close();
            out.close();
        } catch (UnsupportedOperationException | IOException e) {
            ExceptionReportUtil.reportExceptionToFile(e);
            return false;
        }
        return true;
    }

    public boolean writeStream(String title) {
        try {
            InputStream ret = response.getEntity().getContent();
            BufferedInputStream in = new BufferedInputStream(ret);
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(title), true));
            int inByte;
            while ((inByte = in.read()) != -1) out.write(inByte);
            in.close();
            out.close();
        } catch (UnsupportedOperationException | IOException e) {
            // TODO Auto-generated catch block
            ExceptionReportUtil.reportExceptionToFile(e);
            return false;
        }
        return true;
    }
}
