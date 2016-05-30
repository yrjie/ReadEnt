
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Properties;
/**
 * 网络工具
 *
 * @author 梁栋
 * @version 1.0
 * @since 1.0
 */

public abstract class NetUtils {
    public static final String CHARACTER_ENCODING = "UTF-8";
    public static final String PATH_SIGN = "/";
    public static final String METHOD_POST = "POST";
    public static final String METHOD_GET = "GET";
    public static final String CONTENT_TYPE = "Content-Type";
    /**
     * 以POST方式向指定地址发送数据包请求,并取得返回的数据包
     *
     * @param urlString
     * @param requestData
     * @return 返回数据包
     * @throws Exception
     */
    public static byte[] requestPost(String urlString, byte[] requestData) throws Exception {
        Properties requestProperties = new Properties();
        requestProperties.setProperty(CONTENT_TYPE,
                "application/octet-stream; charset=utf-8");
        return requestPost(urlString, requestData, requestProperties);
    }
    /**
     * 以POST方式向指定地址发送数据包请求,并取得返回的数据包
     *
     * @param urlString
     * @param requestData
     * @param requestProperties
     * @return 返回数据包
     * @throws Exception
     */

    public static byte[] requestPost(String urlString, byte[] requestData,Properties requestProperties) throws Exception {
        byte[] responseData = null;
        HttpURLConnection con = null;
        try {
            URL url = new URL(urlString);
            con = (HttpURLConnection) url.openConnection();
            if ((requestProperties != null) && (requestProperties.size() > 0)) {
                for (Map.Entry<Object, Object> entry : requestProperties
                        .entrySet()) {
                    String key = String.valueOf(entry.getKey());
                    String value = String.valueOf(entry.getValue());
                    con.setRequestProperty(key, value);
                }
            }
            con.setRequestMethod(METHOD_POST); // 置为POST方法
            con.setDoInput(true); // 开启输入流
            con.setDoOutput(true); // 开启输出流
            // 如果请求数据不为空，输出该数据。
            if (requestData != null) {
                DataOutputStream dos = new DataOutputStream(con
                        .getOutputStream());
                dos.write(requestData);
                dos.flush();
                dos.close();
            }
            int length = con.getContentLength();
            // 如果回复消息长度不为-1，读取该消息。
            if (length != -1) {
                DataInputStream dis = new DataInputStream(con.getInputStream());
                responseData = new byte[length];
                dis.readFully(responseData);
                dis.close();
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (con != null) {
                con.disconnect();
                con = null;
            }
        }
        return responseData;
    }

    /**
     * 以POST方式向指定地址提交表单<br>
     * arg0=urlencode(value0)&arg1=urlencode(value1)
     *
     * @param urlString
     * @param formProperties
     * @return 返回数据包
     * @throws Exception
     */
    public static byte[] requestPostForm(String urlString,Properties formProperties) throws Exception {
        Properties requestProperties = new Properties();
        requestProperties.setProperty(CONTENT_TYPE,
                "application/x-www-form-urlencoded");
        return requestPostForm(urlString, formProperties, requestProperties);
    }

    /**
     * 以POST方式向指定地址提交表单<br>
     * arg0=urlencode(value0)&arg1=urlencode(value1)
     *
     * @param urlString
     * @param formProperties
     * @param requestProperties
     * @return 返回数据包
     * @throws Exception
     */
    public static byte[] requestPostForm(String urlString,Properties formProperties, Properties requestProperties)
            throws Exception {
        StringBuilder sb = new StringBuilder();
        if ((formProperties != null) && (formProperties.size() > 0)) {
            for (Map.Entry<Object, Object> entry : formProperties.entrySet()) {
                String key = String.valueOf(entry.getKey());
                String value = String.valueOf(entry.getValue());
                sb.append(key);
                sb.append("=");
                sb.append(encode(value));
                sb.append("&");
            }
        }
        String str = sb.toString();
        str = str.substring(0, (str.length() - 1)); // 截掉末尾字符&
        return requestPost(urlString, str.getBytes(CHARACTER_ENCODING),
                requestProperties);

    }

    /**
     * url解码
     *
     * @param str
     * @return 解码后的字符串,当异常时返回原始字符串。
     */
    public static String decode(String url) {
        try {
            return URLDecoder.decode(url, CHARACTER_ENCODING);
        } catch (UnsupportedEncodingException ex) {
            return url;
        }
    }

    /**
     * url编码
     *
     * @param str
     * @return 编码后的字符串,当异常时返回原始字符串。
     */
    public static String encode(String url) {
        try {
            return URLEncoder.encode(url, CHARACTER_ENCODING);
        } catch (UnsupportedEncodingException ex) {
            return url;
        }
    }

    public static void main(String args[]){
        Properties pro1 = new Properties();
        byte[] data={1,2,3};
        pro1.put("method", "queryFirst");
        pro1.put("place", "440300");
        pro1.put("keyword", "啊啊啊");        
        try {
            data=NetUtils.requestPost("http://www.szcredit.com.cn/busi/queryKeyword.TJ", null, pro1);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        System.out.println(data.toString());
    }
}
