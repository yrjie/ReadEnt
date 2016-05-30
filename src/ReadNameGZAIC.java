
import java.io.*;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.impl.client.DefaultHttpClient;
import java.util.Scanner;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Date;
import java.util.Calendar;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

public class ReadNameGZAIC {
    
    HttpClient client;
    String head="http://www.gzaic.gov.cn/gsbm/FrmRegOpeninfoDetail.aspx";
    int numF=3;
    String[] filename={"注册号","名称","企业状态"};
    Scanner cin;
    FileOutputStream[] cout;
    Calendar beg,end;
    
    Pattern p1=Pattern.compile("([\\d]+)-([\\d]+)-([\\d]+)");
    Matcher m1;
    
    public void initialize(){
        int i,j;
        client = new DefaultHttpClient();
        client.getParams().setParameter(ClientPNames.COOKIE_POLICY,
                        CookiePolicy.BROWSER_COMPATIBILITY);
        try{
            beg=Calendar.getInstance();
            end=Calendar.getInstance();
            beg.set(2000, 0, 1);
            end.set(2000, 0, 1);
            cin=new Scanner(new FileInputStream("in日期.txt"));
            cout = new FileOutputStream[numF];
            for (i = 0; i < numF; i++) {
                cout[i] = new FileOutputStream((i+1) + filename[i] + ".txt");
            }
        }
        catch (Exception ex){
            //ex.printStackTrace();
            System.out.println(ex.toString());
            if (ex.toString().contains(".txt")) {
                System.exit(1);
            }
        }
    }
    
    public int getDate(){
        String temp;
        int year,mon,day,ret=0;
        beg.add(Calendar.DAY_OF_MONTH, 1);
        if (beg.after(end)){
            if (cin.hasNext()){
                temp=cin.next();
                m1=p1.matcher(temp);
                if (m1.find()){
                    year=Integer.parseInt(m1.group(1));
                    mon=Integer.parseInt(m1.group(2));
                    day=Integer.parseInt(m1.group(3));
                    beg.set(year, mon-1, day);
                }
                temp=cin.next();
                m1=p1.matcher(temp);
                if (m1.find()){
                    year=Integer.parseInt(m1.group(1));
                    mon=Integer.parseInt(m1.group(2));
                    day=Integer.parseInt(m1.group(3));
                    end.set(year, mon-1, day);
                }
            }
            else ret=-1;
        }
        return ret;
    }
    
    public int work(int ind){
        int i=0,j=0,a,b;
        List<NameValuePair> query;
        String line,date,temp;
        BufferedReader reader;
        HttpPost post1;
        HttpResponse rsp;
        HttpEntity entity;
        String[] data;
        boolean ok;
        try {
            query=new Vector<NameValuePair>();
            query.add(new BasicNameValuePair("txtDT",""));
            query.add(new BasicNameValuePair("__EVENTTARGET","QueryButton"));
            query.add(new BasicNameValuePair("__EVENTARGUMENT",""));
            query.add(new BasicNameValuePair("__VIEWSTATE","/wEPDwUJNTY3MzA5NTY4D2QWAgIDD2QWBAIFDzwrAA0BAA8WBB4LXyFEYXRhQm91bmRnHgt"
                    + "fIUl0ZW1Db3VudAIBZBYCZg9kFgZmDw9kFgIeBXN0eWxlBStiYWNrZ3JvdW5kLWltYWdlOnVybCgnaW1hZ2VzL2xpbmVfYmczLmdpZicpZAIB"
                    + "D2QWBmYPZBYCAgEPDxYCHgRUZXh0ZWRkAgEPZBYCAgEPDxYCHwNlZGQCAg9kFgICAQ8PFgIfA2VkZAICDw8WAh4HVmlzaWJsZWhkZAIHDw9kDx"
                    + "AWAWYWARYCHg5QYXJhbWV0ZXJWYWx1ZWQWAQICZGQYAQUJR3JpZFZpZXcxDzwrAAoCA2YIAgFk0tZPtn6lYm4Kj3HpjZwd1rULxvc="));
            query.add(new BasicNameValuePair("__EVENTVALIDATION","/wEWBAK1pvX0AwK8+7r/DwKAp+rKBAKrg7q+DN11VFy5SCXKiLzl2m3mIBUGjuQv"));
            query.add(new BasicNameValuePair("QueryButton","查询"));
            data=new String[numF];
            for (i=0;i<numF;i++)
                data[i]="";
            while (true){
                if (ind!=0)
                    ind=0;
                else if (getDate()<0) break;
                date=beg.get(Calendar.YEAR)+"-"+(beg.get(Calendar.MONTH)+1)+"-"+beg.get(Calendar.DAY_OF_MONTH);
                //System.out.println(date);
                ok=false;
                query.set(0, new BasicNameValuePair("txtDT",date));
                post1 = new HttpPost(head);
                post1.setEntity(new UrlEncodedFormEntity(query, "GBK"));
                //post1.addHeader("Referer", "http://www.gzaic.gov.cn/GZAIC_Portal/WebUI/service/zcdjxx.htm");
                rsp=client.execute(post1);
                entity = rsp.getEntity();
                if (entity != null) {
                    reader = new BufferedReader(new InputStreamReader(
                                        entity.getContent(),"UTF-8"));
                    while ((line=reader.readLine())!=null){
                        //System.out.println(line);
                        if (line.contains("_Label")){
                            a=line.indexOf("list_row_0\">")+12;
                            b=line.indexOf("</span>");
                            temp=line.substring(a, b);
                            if (line.contains("Label3"))
                                data[0]=temp;
                            else if (line.contains("Label1"))
                                data[1]=temp;
                            else data[2]=temp;
                        }
                        if (!data[2].isEmpty()){
                            if (!ok){
                                ok=true;
                                System.out.println(date);
                            }
                            for (i=0;i<numF;i++){
                                cout[i].write((data[i] + "\r\n").getBytes());
                                data[i]="";
                            }
                        }
                    }
                    reader.close();
                }
            }
            return 0;
        }
        catch (Exception ex){
            System.out.println(ex.toString());
            client=null;
            client = new DefaultHttpClient();
            client.getParams().setParameter(ClientPNames.COOKIE_POLICY,
                            CookiePolicy.BROWSER_COMPATIBILITY);
            return -1;
        }        
    }
    
    void finish(){
        int i,j;
        try{
            cin.close();
            for (i=0;i<numF;i++)
                cout[i].close();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
    public static void main(String args[]){
        int i,ret=0;
        ReadNameGZAIC rn=new ReadNameGZAIC();
        rn.initialize();
        while (true){
            ret=rn.work(ret);
            if (ret==0)
                break;
        }
        rn.finish();
    }
}
