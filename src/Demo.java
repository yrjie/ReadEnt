
import java.util.*;
import java.net.*;
import java.io.*;

public class Demo {

  public static void main(String[] args) throws Exception {
    Map m = new HashMap();
    String url = "http://www.szcredit.com.cn/busi/queryKeyword.TJ";
    String code = "UTF-8";

//    m.put("sel_zazhimc", "");
//
//    m.put("sel_niandu", "");
//
//    m.put("txt_qishiye", "");
//
//    m.put("txt_doi", "");
//    m.put("xueke", "");
//    m.put("zhuanye", "");
//    m.put("txt_zuozhe", "");
//    m.put("txt_zuozhe2", "");
//    m.put("txt_zuozhedw", "");
//
//    m.put("txt_zhaiyao", "");
//    m.put("txt_guanjianci", "");
//    m.put("txt_fenleihao", "");
//    m.put("sel_niandus", "");
//    m.put("sel_niandue", "");

    m.put("method", "queryThree");
    m.put("entID", "440301001012010060900411");
    m.put("entStatusCode", "0");
    m.put("tabPro", "-1");
    m.put("allMoney", "0");
    m.put("maxMoney", "0");
    m.put("userType", "0");
    m.put("item", "01002");
    String rus = doPost(url, m, code);

    System.out.println(rus);
  }

  public static String doPost(String reqUrl, Map parameters, String recvEncoding) {
    HttpURLConnection conn = null;
    String responseContent = null;
    try {
      StringBuffer params = new StringBuffer();
      for (Iterator iter = parameters.entrySet().iterator(); iter.hasNext();) {
        Map.Entry element = (Map.Entry) iter.next();
        params.append(element.getKey().toString());
        params.append("=");
        params.append(URLEncoder.encode(element.getValue().toString(), recvEncoding));
        params.append("&");
      }

      if (params.length() > 0) {
        params = params.deleteCharAt(params.length() - 1);
      }
      URL url = new URL(reqUrl);
      HttpURLConnection url_con = (HttpURLConnection) url.openConnection();
      url_con.setRequestMethod("POST");
      // System.setProperty("sun.net.client.defaultConnectTimeout", String
      // .valueOf(HttpRequestProxy.connectTimeOut));// （单位：毫秒）jdk1.4换成这个,连接超时
      // System.setProperty("sun.net.client.defaultReadTimeout", String
      // .valueOf(HttpRequestProxy.readTimeOut)); // （单位：毫秒）jdk1.4换成这个,读操作超时
      url_con.setConnectTimeout(5000);//（单位：毫秒）jdk
      // 1.5换成这个,连接超时
      url_con.setReadTimeout(5000);//（单位：毫秒）jdk 1.5换成这个,读操作超时
      url_con.setDoOutput(true);
      byte[] b = params.toString().getBytes();
      url_con.getOutputStream().write(b, 0, b.length);
      url_con.getOutputStream().flush();
      url_con.getOutputStream().close();

      InputStream in = url_con.getInputStream();
      BufferedReader rd = new BufferedReader(new InputStreamReader(in, recvEncoding));
      String tempLine = rd.readLine();
      StringBuffer tempStr = new StringBuffer();
      String crlf = System.getProperty("line.separator");
      while (tempLine != null) {
        tempStr.append(tempLine);
        tempStr.append(crlf);
        tempLine = rd.readLine();
      }
      responseContent = tempStr.toString();
      rd.close();
      in.close();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (conn != null) {
        conn.disconnect();
      }
    }
    return responseContent;
  }

}
