import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import javax.imageio.ImageIO;

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


public class ReadName0 {
    
    HttpClient client;
    String[] type={"_Pvt_0","_Ent_0"};
    String[] outfile={"个体名.txt","企业名.txt"};
    FileOutputStream cout;
    static int beg,end,end0;
    String state=null,validate=null;
    Pattern p1=Pattern.compile("[\\d]+/([\\d]+)页"),p2=Pattern.compile("共([\\d]+)条记录");
    Matcher m1,m2;
    
    //checkCode
    int[][][] mat;
    int[][] cc;
    String allC="2468BDFHJLNPpRTVXZ";
    final int w=10,h=12,n=18;
    int[] zero = {-3355444,-6710887,-1};
    
    public int getCheckCode(){
        int i,j,pixel,ccH,ccW;
        FileOutputStream cout1;
        String gifName="信用网.gif";
        try {
            cout1=new FileOutputStream(gifName);
            HttpPost post1 = new HttpPost("http://www.szcredit.com.cn/WebPages/Member/CheckCode.aspx");
            HttpResponse rsp=client.execute(post1);
            HttpEntity entity = rsp.getEntity();
            if (entity != null) {
                InputStream is=entity.getContent();
                int b;
                while (true){
                    b=is.read();
                    if (b==-1) break;
                    cout1.write(b);
                }
                is.close();
            }
            cout1.close();
            File fi=new File(gifName);
            BufferedImage bi=ImageIO.read(fi);
            ccH=bi.getHeight();
            ccW=bi.getWidth();
            cc=new int[ccH][ccW];
            for (i=0;i<ccH;i++)
                for (j=0;j<ccW;j++){
                    pixel=bi.getRGB(j, i);
                    if (pixel==zero[0]||pixel==zero[1]||pixel==zero[2]) cc[i][j]=0;
                    else cc[i][j]=1;
                }
            return 0;
        }
        catch (Exception ex){
            //System.out.println("read gif error: "+ioe.toString());
            client=null;
            client = new DefaultHttpClient();
            client.getParams().setParameter(ClientPNames.COOKIE_POLICY,
                            CookiePolicy.BROWSER_COMPATIBILITY);
            return -1;
        }        
    }
        
    public String Extract(){
        String ret="";
        int i,x1,y1,j,x2,y2,dx,a,b,ind,readG=-1;
        while (readG<0)
            readG=getCheckCode();
        a=b=0;
        for (i=0;i<cc[0].length;i++){
            if (cc[3][i]==1) a++;
            if (cc[4][i]==1) b++;
        }
        if (b>a*2) y1=4;
        else y1=3;
        //System.out.println(y1);
        for (i=0;i<5;i++){
            b=0;
            ind=-1;
            for (dx=-3;dx<3;dx++){
                x1=5+10*i+dx;
                for (j=0;j<n;j++){
                    a=0;
                    for (y2=0;y2<h;y2++)
                        for (x2=0;x2<w;x2++){
                            if (cc[y1+y2][x1+x2]==mat[j][y2][x2]) a++;
                        }
                    if (b<a){
                        b=a;
                        ind=j;
                    }
                }
            }
            //System.out.println(b);
            ret=ret+allC.charAt(ind);
        }
        return ret;
    }

    public void Login(int op){
        List<NameValuePair> login,query;
        HttpResponse response;
        HttpEntity entity;
        HttpPost post;
        Scanner cin1,cin2;
        String line;
        String checkCode;
        int trial;
        try{            
            //post = new HttpPost("http://www.szcredit.com.cn/WebPages/Search/SZManyCondition1.aspx");
            post = new HttpPost("http://www.szcredit.com.cn/WebPages/Search/SZManyConditionList.aspx");
            /*cin1=new Scanner(new FileInputStream("include\\state2.txt"));
            cin2=new Scanner(new FileInputStream("include\\validate2.txt"));
            login=new Vector<NameValuePair>();
            login.add(new BasicNameValuePair("txtUserName","10000929"));
            login.add(new BasicNameValuePair("txtPassword","128265"));
            login.add(new BasicNameValuePair("btnlogin"," 登录 "));
            //login.add(new BasicNameValuePair("txtCheckCode","AAAAA"));
            login.add(new BasicNameValuePair("__VIEWSTATE",cin1.nextLine()));
            login.add(new BasicNameValuePair("__EVENTVALIDATION",cin2.nextLine()));			
            cin1.close();
            cin2.close();
            trial=0;
            //while (trial>=0){
                //checkCode=Extract();
                //trial++;
                //login.set(3, new BasicNameValuePair("txtCheckCode",checkCode));
                post.setEntity(new UrlEncodedFormEntity(login, "GBK"));
                response = client.execute(post);
                entity = response.getEntity();
                //System.out.println("执行回复 : " + response.getStatusLine());
                if (entity != null) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(
                                        entity.getContent(),"GBK"));
                        while ((line = reader.readLine()) != null){
                                System.out.println(line);
                                if (line.contains("here")){
                                    //System.out.println(trial+" try for checkCode");
                                    trial=-1;
                                    break;
                                }
                                else if (line.contains("验证码错误")) break;
                        }
                        reader.close();
                }
            //}*/
            cin1=null;
            cin2=null;
            cin1=new Scanner(new FileInputStream("include\\state3.txt"));
            cin2=new Scanner(new FileInputStream("include\\validate3.txt"));
            query=new Vector<NameValuePair>();
            query.add(new BasicNameValuePair("lstQYLX","1100"));
            query.add(new BasicNameValuePair("txtDiZhi","深圳"));
            //query.add(new BasicNameValuePair("btnQuery","开始查询"));
            //query.add(new BasicNameValuePair("txtCheckCode1","ABCD"));
            query.add(new BasicNameValuePair("__VIEWSTATE",cin1.nextLine()));
            //query.add(new BasicNameValuePair("__EVENTVALIDATION",cin2.nextLine()));
            query.add(new BasicNameValuePair("__EVENTTARGET",""));
            query.add(new BasicNameValuePair("__EVENTARGUMENT",""));
            cin1.close();
            cin2.close();
            response=null;
            entity=null;
            post.setEntity(new UrlEncodedFormEntity(query, "GBK"));
            response = client.execute(post);
            entity = response.getEntity();
            //System.out.println("执行回复 : " + response.getStatusLine());
            if (entity != null) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                                    entity.getContent(),"GBK"));
                    while ((line = reader.readLine()) != null)
                            System.out.println(line);
                    reader.close();
            }
        }
        catch (IOException ioe){
            System.out.println(ioe.toString());
        }
    }
    
    public void initialize(int op){
        int i,j,k;
        try{
            Scanner cin0=new Scanner(new FileInputStream("include\\standard.txt"));
            mat=new int[n][h][w];
            for (i=0;i<n;i++)
                for (j=0;j<h;j++)
                    for (k=0;k<w;k++)
                        mat[i][j][k]=cin0.nextInt();
            cout=new FileOutputStream(outfile[op]);
            client = new DefaultHttpClient();
            client.getParams().setParameter(ClientPNames.COOKIE_POLICY,
                            CookiePolicy.BROWSER_COMPATIBILITY);
            Login(op);
        }
        catch (IOException ioe){
            System.out.println(ioe.toString());
        }
    }
    
    public String getB64(String line){
        int beg;
        beg=line.indexOf("value=");
        if (beg<0) return "";
        else return line.substring(beg+7, line.length()-4);
    }
    
    public int work(int ind,int op){
        List<NameValuePair> nvpair;
        HttpResponse response;
        HttpEntity entity;
        HttpPost post;        
        String[] data=new String[15];
        boolean first;
        int i,a,b,cnt;
        post=null;
        post = new HttpPost("http://www.szcredit.com.cn/WebPages/Search/SZEntListManyCondition.aspx");
        nvpair=new Vector<NameValuePair>();
        nvpair.add(new BasicNameValuePair("__VIEWSTATE",state));
        nvpair.add(new BasicNameValuePair("__EVENTVALIDATION",validate));
        nvpair.add(new BasicNameValuePair("turnPageBar$txtPageNum",""+ind));
        nvpair.add(new BasicNameValuePair("turnPageBar$btnGoPage","GO"));
        try{
            while (true){
                first=(end==0);
                i=-2;
                response=null;
                entity=null;
                if (end>0) post.setEntity(new UrlEncodedFormEntity(nvpair));
                response = client.execute(post);
                entity = response.getEntity();
                //if (response.getStatusLine().getStatusCode()!=200){
                //    System.out.println("bad status code");
                //    return ind;
                //}
                if (entity != null) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                                    entity.getContent(),"GBK"));
                    String line = null;
                    cnt=0;
                    while ((line = reader.readLine()) != null) {
                            //System.out.println(line);
                            cnt++;
                            if (cnt>300) break;
                            if (end==0){
                                m1=p1.matcher(line);
                                if (m1.find()){
                                    end=Integer.parseInt(m1.group(1));
                                    System.out.println("number of pages: "+end);
                                    if (end0>0&&end0<end) end=end0;
                                }
                                m2=p2.matcher(line);
                                if (m2.find()){
                                    System.out.println("number of items: "+m2.group(1));
                                    System.out.println("from "+beg+" to "+end);
                                }
                                if (line.contains("__VIEWSTATE")){
                                    state=getB64(line);
                                    nvpair.set(0, new BasicNameValuePair("__VIEWSTATE",state));
                                }
                                if (line.contains("__EVENTVALIDATION")){
                                    validate=getB64(line);
                                    nvpair.set(1, new BasicNameValuePair("__EVENTVALIDATION",validate));
                                }
                            }
                            else if (line.contains("页次1/")){
                                reader.close();
                                System.out.print("new login: ");
                                Login(op);
                                return ind;
                            }
                            if (line.contains("<tr")||line.contains("<TR")){
                                i++;
                                if (i<0) continue;
                                line=reader.readLine();
                                a=line.indexOf("<td>")+4;
                                b=line.indexOf("</td>");
                                data[i]=line.substring(a, b);
                            }
                    }
                    reader.close();
                }
                if (first&&beg!=1) continue;
                write2Txt(data,i);
                if (ind%1000==0) System.out.println(ind+" completed");
                ind++;                
                nvpair.set(2, new BasicNameValuePair("turnPageBar$txtPageNum",""+ind));
                if (end>0&&ind>end) return 0;
            }
        }
        catch (Exception ex){
            client=null;
            client = new DefaultHttpClient();
            client.getParams().setParameter(ClientPNames.COOKIE_POLICY,
                            CookiePolicy.BROWSER_COMPATIBILITY);
            ex.printStackTrace();
            return ind;
        }
    }
    
    public void write2Txt(String[] data, int n){
        int i;
        try {
            for (i=0;i<=n;i++){
                cout.write((data[i]+"\r\n").getBytes());
            }
        }
        catch (IOException ioe){
            System.out.println(ioe.toString());
        }
    }
    
    public void finish(){
        try{
            cout.close();
            System.out.println("finished");
        }
        catch (IOException ioe){
            System.out.println(ioe.toString());
        }
    }
    
    public static void main(String args[]){
        ReadName0 rn1=new ReadName0();
        int op,ret;
        if (args.length>0) op=Integer.parseInt(args[0]);
        else op=0;
        if (args.length>1) beg=Integer.parseInt(args[1]);
        else beg=1;
        if (args.length>2) end0=Integer.parseInt(args[2]);
        else end0=0;
        ret=beg;
        end=0;
        rn1.initialize(op);
        while (true){
            ret=rn1.work(ret,op);         
            if (ret==0) break;            
            else System.out.println(ret);
        }
        rn1.finish();
    }
}
