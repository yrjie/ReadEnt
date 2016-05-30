import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;


public class TestUrl {

public static final String COOKIE = "登陆后cookie"
* 连接超时
*/
private static int connectTimeOut = 5000;

/**
* 读取数据超时
*/
private static int readTimeOut = 10000;

/**
* 请求编码
*/
private static String requestEncoding = "GBK";









/**
* 得到大分类的帖子 如：java
* @param java
* @return
* @throws Exception
*/
public static List getCsdn(String java) throws Exception {
List<String> urlList = new ArrayList<String>();
String url = "http://forum.csdn.net/SList/FreeZone";
String patternStrs = "<td class=\"caption\" style=\"word-break: break-all\">(.*?)</td>";
String p = "<a target=\"_blank\" title=\"(.*?)</a>";
String href = "<a target=\"_blank\" title=\"(.*?)\" href=\"(.*?)\" >(.*?)</a>";
String s = "";
String h = "";
URL u = new URL(url);
StringBuffer sTotalString = new StringBuffer("");
HttpURLConnection conn = (HttpURLConnection) u.openConnection();
conn.addRequestProperty("Cookie", COOKIE);

String sCurrentLine = "";
BufferedReader l_reader = new java.io.BufferedReader(
new java.io.InputStreamReader(u.openStream()));
while ((sCurrentLine = l_reader.readLine()) != null) {
sTotalString = sTotalString.append(new StringBuffer(sCurrentLine
+ "\n"));
s = RegexpCommon.getMatchString(sCurrentLine, p, 0);
if (StringUtils.isNotBlank(s)) {
h = RegexpCommon.getMatchString(s, href, 2);
System.out.println("得到的URL为:" + h);
urlList.add(h);
}

}

// byte[] b = (sTotalString.toString()).getBytes();
// BufferedOutputStream out = new BufferedOutputStream(
// new FileOutputStream("c:/test.html"));
// out.write(b);
return urlList;
}

/**
* 得到帖子的回复地址
* @param urlList
* @return
* @throws Exception
*/
public static List getCardPostUrl(List<String> urlList) throws Exception {
List<String> postList = new ArrayList<String>();
URL u = null;
HttpURLConnection conn = null;
BufferedReader l_reader = null;
String s = "";
for (String URL : urlList) {
u = new URL(URL);
StringBuffer sTotalString = new StringBuffer("");
conn = (HttpURLConnection) u.openConnection();
conn.addRequestProperty("Cookie", COOKIE);

String sCurrentLine = "";

String patternStrs = "iframe class=\"replyframe\" id=\"replyframe\" frameborder=\"0\" scrolling=\"no\" height=\"415px\" width=\"100%\" src=\"(.*?)\" csdnid=\"rframe\">";
l_reader = new java.io.BufferedReader(
new java.io.InputStreamReader(u.openStream()));
while ((sCurrentLine = l_reader.readLine()) != null) {
sTotalString = sTotalString.append(new StringBuffer(sCurrentLine
+ "\n"));
}
conn.disconnect();
s = RegexpCommon.getMatchString(sTotalString.toString(),
patternStrs, 0);
s = s.split("src=\"")[1].split("\" csdnid")[0];
System.out.println(s);
postList.add(s);
}
return postList;



}

public static void main(String[] args) throws Exception {
String s = "http://forum.csdn.net/PointForum/Forum/ReplyT.aspx?forumID=a3049f56-b572-48f5-89be-4797b70d71cd&topicID=b9fbc233-fadf-441b-aad8-2d6a77641f16&postDate=2010-02-01+08%3a40%3a49&v=13";
String d = "tb_ReplyBody___Editor=回复测试!!!";
// GetResponseDataByID(s, d);
//GetResponseDataByID(s, d);
//t();
List<String> urlList = getCsdn(null);
}

}
