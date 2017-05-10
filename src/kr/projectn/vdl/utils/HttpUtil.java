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
import java.util.List;

/**
 * Created by Kim.K on 2017-05-03.
 */
public class HttpUtil {
    private CloseableHttpClient client;
    private CloseableHttpResponse response;
    private String strUserAgent;
    private URIBuilder builder;
    private URI uri;

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

        try {
            response = client.execute(hGet);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.err.println("[HttpUtil @ " + client.toString() + "] Exception thrown\nMessage: " + e.getMessage());
            System.err.println("Error trace: \n");
            e.printStackTrace();
        }

        return this;
    }

    public HttpUtil requestByPost() {
        HttpPost hPost = new HttpPost(uri);
        hPost.addHeader("User-Agent", strUserAgent);
        hPost.addHeader("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4,ja;q=0.2");
        try {
            response = client.execute(hPost);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.err.println("[HttpUtil @ " + client.toString() + "] Exception thrown\nMessage: " + e.getMessage());
            System.err.println("Error trace: \n");
            e.printStackTrace();
        }

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
            System.err.println("[HttpUtil] Exception thrown.\nMessage: " + e.getMessage());
            System.err.println("Error trace: \n");
            e.printStackTrace();
        }
        finally {
            if(reader != null) {
                try {
                    reader.close();
                }
                catch (IOException e) {
                    System.err.println("[HttpUtil] Exception thrown.\nMessage: " + e.getMessage());
                    System.err.println("Error trace: \n");
                    e.printStackTrace();
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
            System.err.println("[HttpUtil] Exception thrown.\nMessage: " + e.getMessage());
            System.err.println("Error trace: \n");
            e.printStackTrace();
        }
        return ret;
    }

    public void setClientConnection(String url) {
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
            System.err.println("[HttpUtil -> UriBuilder @ " + builder.hashCode() + "] URI Syntax exception.\nMessage: " + e.getMessage());
            System.err.println("Error trace: \n");
            e.printStackTrace();
        }
    }

    public void setConnectionParameter(List<NameValuePair> param) {
        try {
        builder = builder.setParameters(param);
        uri = builder.build();
        } catch (URISyntaxException e) {
            System.err.println("[HttpUtil -> UriBuilder @ " + builder.hashCode() + "] URI Syntax exception.\nMessage: " + e.getMessage());
            System.err.println("Error trace: \n");
            e.printStackTrace();
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
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
