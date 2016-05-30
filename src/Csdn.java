import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
//1.首先下载apache的httpClient。。
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

/**
*
* @author zk 自动回复灌水乐园帖子
*
*/
public class Csdn {
public static String COOKIE = "你的登陆后cookie";
private static final String CONTENT_CHARSET = "UTF-8";// httpclient使用的字符集

@SuppressWarnings("unchecked")
public static void main(String[] args) throws Exception, IOException {
List<String> urlList = TestUrl.getCardPostUrl(TestUrl.getCsdn(null));
getCard(urlList);
// getMethodTest();
}

public static void getMethodTest() throws Exception, IOException {
String html = "http://hi.csdn.net/my.html";
HttpClient hc = getHc();
GetMethod getMethod = new GetMethod(html);
List<Header> headers = new ArrayList<Header>();
headers.add(new Header("Proxy-Connection", "keep-alive"));
headers.add(new Header("Cookie", COOKIE));
hc.getHostConfiguration().getParams().setParameter(
"http.default-headers", headers);

int statusCode = hc.executeMethod(getMethod);
if (statusCode != HttpStatus.SC_OK) {
System.err.println("Method failed: " + getMethod.getStatusLine());
}
// 读取内容
byte[] responseBody = getMethod.getResponseBody();
// 处理内容

String hh = new String(responseBody);

System.out.println(hh);

}

public static HttpClient getHc() {
HttpClient httpClient = new HttpClient();
// java client将按照浏览器的方式来自动处理
httpClient.getParams().setCookiePolicy(
CookiePolicy.BROWSER_COMPATIBILITY);
httpClient.getHostConfiguration().setHost("http://www.csdn.net", 80,
"http");
return httpClient;
}

public static void getCard(List<String> urlList) throws Exception,
IOException {
HttpClient httpClient = null;
PostMethod p = null;
List<Header> headers = null;
NameValuePair __VIEWSTATE = null;
NameValuePair __EVENTVALIDATION = null;
NameValuePair BT_SUBMIT = null;
NameValuePair REPLYBODY = null;
NameValuePair[] params = null;
for (String url : urlList) {
headers = new ArrayList<Header>();
httpClient = getHc();
p = new PostMethod(url);
// 需要验证
// UsernamePasswordCredentials creds = new UsernamePasswordCredentials("chenlb", "123456");

headers.add(new Header(
"User-Agent",
"Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.1.7) Gecko/20091221 Firefox/3.5.7 GTB6 (.NET CLR 1.1.4322)"));
headers.add(new Header("Proxy-Connection", "keep-alive"));
headers.add(new Header("Cookie", COOKIE));
headers.add(new Header("Content-Type",
"application/x-www-form-urlencoded;charset=" + CONTENT_CHARSET));
httpClient.getHostConfiguration().getParams().setParameter(
"http.default-headers", headers);
__VIEWSTATE = new NameValuePair(
"__VIEWSTATE",
"/wEPDwUKMTA2MTA3Njg5NA9kFgICCQ9kFgJmD2QWAgIFD2QWAmYPZBYCZg8PFgIeBE1vZGULKiVTeXN0ZW0uV2ViLlVJLldlYkNvbnRyb2xzLlRleHRCb3hNb2RlARYCHgVzdHlsZQUYaGVpZ2h0OjE4MHB4O3dpZHRoOjEwMCU7ZGRpl2NuIb2XmIUODhEniCtEXExdOA==");
__EVENTVALIDATION = new NameValuePair(
"__EVENTVALIDATION",
"/wEWAwLtl7ScBQK6873ZCgK3mOXeAjqcUaoqnb3Nj0uKUrGKImKcexCG");

BT_SUBMIT = new NameValuePair("bt_submit", "提交回复");

REPLYBODY = new NameValuePair(
"tb_ReplyBody$_$Editor", "[img=http://forum.csdn.net/PointForum/ui/scripts/csdn/Plugin/003/monkey/1.gif][/img]");

params = new NameValuePair[] { __VIEWSTATE,
__EVENTVALIDATION, REPLYBODY, BT_SUBMIT };
p.setRequestBody(params);
int statusCode = httpClient.executeMethod(p);
if (statusCode != HttpStatus.SC_OK) {
System.err.println("Method failed: " + p.getStatusLine());
}
System.out.println("Hello,World");
// 读取内容
//byte[] responseBody = p.getResponseBody();
// 处理内容

//String hh = new String(responseBody);
//System.out.println(hh);
}

}
}
