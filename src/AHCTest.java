import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Vector;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;


public class AHCTest {

	public void work(){
		try{
			HttpClient client = new DefaultHttpClient();
			client.getParams().setParameter(ClientPNames.COOKIE_POLICY,CookiePolicy.BROWSER_COMPATIBILITY);
			System.out.println("登录校内网");
			HttpPost post = new HttpPost("http://www.renren.com/PLogin.do");
			Header head1 = new BasicHeader("Accept","image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/x-shockwave-flash, application/msword, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/QVOD, application/QVOD, */*");
			Header head2 = new BasicHeader("Referer","http://www.renren.com/PLogin.do");
			Header head3 = new BasicHeader("Accept-Language", "zh-cn");
			Header head4 = new BasicHeader("Content-Type","application/x-www-form-urlencoded");
			Header head5 = new BasicHeader("UA-CPU", "x86");
			Header head6 = new BasicHeader("Accept-Encoding", "gzip, deflate");
			Header head7 = new BasicHeader("User-Agent","Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; .NET CLR 2.0.50727)");
			Header head8 = new BasicHeader("Host", "www.renren.com");
			Header head9 = new BasicHeader("Connection", "Keep-Alive");
			Header head10 = new BasicHeader("Cache-Control", "no-cache");
			Header head11 = new BasicHeader("Cookie","feedType=249131885_hot; JSESSIONID=abc4AqtDXYyxf6YOEnmtt; _r01_=1; ick=438d6b80-9213-46da-ad91-50b3c5f668c5; depovince=GUZ; jebecookies=e0ef4367-35b3-4aeb-8f1b-eed3c31b2eab|||||");
			/*post.addHeader(head1);
			post.addHeader(head2);
			post.addHeader(head3);
			post.addHeader(head4);
			post.addHeader(head5);
			post.addHeader(head6);
			post.addHeader(head7);
			post.addHeader(head8);
			post.addHeader(head9);
			post.addHeader(head10);
			post.addHeader(head11);*/
			List<NameValuePair> namevaluepair=new Vector<NameValuePair>();
			namevaluepair.add(new BasicNameValuePair("email", "yrjie@qq.com"));
			namevaluepair.add(new BasicNameValuePair("password", "123"));
			namevaluepair.add(new BasicNameValuePair("origURL","http%3A%2F%2Fwww.renren.com%2Fhome"));
			namevaluepair.add(new BasicNameValuePair("domain", "renren.com"));
			namevaluepair.add(new BasicNameValuePair("formName",""));
			namevaluepair.add(new BasicNameValuePair("method", ""));
			namevaluepair.add(new BasicNameValuePair("isplogin", "true"));
			//namevaluepair.add(new BasicNameValuePair("submit", "%E7%99%BB%E5%BD%95"));
			post.setEntity(new UrlEncodedFormEntity(namevaluepair, "US-ASCII"));
			HttpResponse response = client.execute(post);
			Header[] headers = response.getAllHeaders();
			for (int i = 0; i < headers.length; i++) {
				System.out.println("___" + headers[i].getName() + "___"	+ headers[i].getValue());
			}
			HttpEntity entity = response.getEntity();
			System.out.println("执行回复 : " + response.getStatusLine());
			if (entity != null) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
				String line = null;
				while ((line = reader.readLine()) != null) {
					System.out.println(line);
				}
				reader.close();
			}
			System.out.println("提交后的Cookies :");
			List<Cookie> cookies = ((AbstractHttpClient) client).getCookieStore().getCookies();
			if (cookies.isEmpty()) {
				System.out.println("None");
			} else {
				for (int i = 0; i < cookies.size(); i++) {
					System.out.println("- " + cookies.get(i).toString());
				}
			}
		}
		catch (Exception ex){
			ex.printStackTrace();
		}
	}

	public static void main(String args[]){
		AHCTest ahct1=new AHCTest();
		ahct1.work();
	}
}
