/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author yrjie
 */
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.impl.client.DefaultHttpClient;
import java.util.Scanner;
import java.util.regex.*;
import java.util.Vector;
import javax.imageio.ImageIO;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

public class ReadGZAIC {

    HttpClient client;
    String head = "http://www.gzaic.gov.cn/GZCX/WebUI/Content/Handler/ZTHandler.ashx";
    String[] filename = {"ID","注册号", "名称", "法人代表", "地址", "经营范围",
                "注册资本", "类型", "注册登记日期","企业状态","最后年检年度","核准日期"};
    static int numF = 12, cnt, numD;
    FileOutputStream[] cout;
    Scanner cin;
    String keyword,initReg, initNum;
    //long beg, end;
    int not,th;
    long nowR;
    Pattern p2=Pattern.compile("[\\d]+");
    Matcher m1;

    //checkCode
    int[][][] mat;
    int[][] cc;
    String allC="02468BDFHJLNPRTVXZ",gifName;
    final int w=10,h=12,n=18;
    int[] zero = {-3355444,-6710887,-1};
    
    String realIP,nowIP;
    FileOutputStream foIP;
    
    public void initialize(int op) {
        int i, j, k;
        Scanner cin0,cin1;
        client = new DefaultHttpClient();
        client.getParams().setParameter(ClientPNames.COOKIE_POLICY,
                CookiePolicy.BROWSER_COMPATIBILITY);
        try {
            cnt = 0;
            //beg=1;
            //end=0;
            gifName="gzaic.gif";
            cin0=new Scanner(new FileInputStream("include\\standard0.txt"));
            mat=new int[n][h][w];
            for (i=0;i<n;i++)
                for (j=0;j<h;j++)
                    for (k=0;k<w;k++)
                        mat[i][j][k]=cin0.nextInt();
            cin0.close();
            if (op == 1)
                cin = new Scanner(new FileInputStream("in企业名.txt"), "GBK");
            else if (op==2){
                cin1=new Scanner(System.in);
                System.out.println("注册号数: ");
                th=cin1.nextInt();
                cin1.close();
                not=th;
                cin=new Scanner(new FileInputStream("in注册号.txt"),"GBK");
            }
            else if (op==3)
                cin=new Scanner(new FileInputStream("inID.txt"),"GBK");
            cout = new FileOutputStream[numF];
            for (i = 0; i < numF; i++) {
                cout[i] = new FileOutputStream(i + filename[i] + ".txt");
            }
            foIP=new FileOutputStream("IP.txt");
            nowIP=realIP="";
            getIP();
            realIP=nowIP;
            System.out.println("Real IP: "+realIP);
            /*for (i=0;i<2;i++){
             File dir=new File(dirName[i]);
             if (!dir.exists())
             dir.mkdir();
             for (j=0;j<numF;j++)
             cout[i][j]=new FileOutputStream(dirName[i]+"\\"+fileName[j]+".txt");
             }*/
        } catch (Exception ex) {
            //ex.printStackTrace();
            System.out.println(ex.toString());
            if (ex.toString().contains(".txt")) {
                System.exit(1);
            }
        }
    }
    
    public boolean getIP(){
        URL url;
        String line,ret="";
        BufferedReader ureader;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        int a,b;
        try{
            url=new URL("http://www.baidu.com/baidu?word=ip");
            ureader=new BufferedReader(new InputStreamReader(url.openStream(),"GBK"));
            while ((line=ureader.readLine())!=null){
                //System.out.println(line);
                if (line.contains("fk=\"")){
                    a=line.indexOf("fk=\"")+4;
                    b=line.indexOf("\"", a);
                    ret=line.substring(a, b);
                    break;
                }
            }
            ureader.close();
            if (!ret.equals(nowIP)){
                nowIP=ret;
                foIP.write((nowIP+"\t"+df.format(new Date())+"\r\n").getBytes());
            }
            return !ret.equals(realIP);
        }
        catch (IOException ioe){
            ioe.printStackTrace();
            return false;
        }
    }

    public int getCheckCode(){
        int i,j,pixel,ccH,ccW;
        BufferedInputStream bis;
        FileOutputStream cout1;
        byte[] data=new byte[1024];
        try {
            cout1=new FileOutputStream(gifName);
            HttpPost post1 = new HttpPost("http://www.gzaic.gov.cn/GZCX/WebUI/Content/Handler/ValidateCode.ashx");
            HttpResponse rsp=client.execute(post1);
            HttpEntity entity = rsp.getEntity();
            if (entity != null) {
                bis=new BufferedInputStream(entity.getContent());
                while (bis.read(data)!=-1){
                    cout1.write(data);
                }
                bis.close();
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
            if (cc[5][i]==1) a++;
            if (cc[6][i]==1) b++;
        }
        if (b>a*2) y1=6;
        else y1=5;
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
    
    int getKeyword(int op, boolean ok) {
        int ret = 0;
        String temp;
        if (op == 1 || op == 3) {
            if (!cin.hasNext()) {
                ret = -1;
            } else {
                keyword = cin.next();
            }
        } else {
            if (ok) not=0;
            else not++;
            if (not >= th) {
                if (!cin.hasNext())
                    ret=-1;
                else {
                    not=-1;
                    initReg=temp=cin.next();
                    m1=p2.matcher(temp);
                    if (m1.find())
                        initNum=temp=m1.group();
                    numD=temp.length();
                    nowR=Long.parseLong(temp)-1;
                    /*temp=cin.next();
                    m1=p2.matcher(temp);
                    if (m1.find())
                        temp=m1.group();
                    end=Long.parseLong(temp);*/
                }
            }
            if (!initReg.equals(initNum)||!ok)
                nowR++;
            else nowR=(nowR/10+1)*10;
            //if ((nowR+"").length()>numD) not=th;
            keyword = initReg.replace(initNum, String.format("%0"+numD+"d", nowR));
            //System.out.println(nowR);
        }
        if (ret!=-1) cnt++;
        //System.out.println(keyword);
        if (cnt % 1000 == 0) {
            System.out.println("now at ("+cnt+"): "+ keyword);
        }
        return ret;
    }

    int getInd(String line) {
        int ind = -1;
        if (line.contains("ztbm")) {
            ind = 0;
        } else if (line.contains("zch")) {
            ind = 1;
        } else if (line.contains("qymc")||line.contains("zhmc")) {
            ind = 2;
        } else if (line.contains("qyfr")) {
            ind = 3;
        } else if (line.contains("qyzz")) {
            ind = 4;
        } else if (line.contains("jyfw")) {
            ind = 5;
        } else if (line.contains("zczb") ) {
            ind = 61;
        } else if (line.contains("zblx") ) {
            ind = 62;
        } else if (line.contains("qylx")) {
            ind = 7;
        } else if (line.contains("clrq")) {
            ind = 8;
        } else if (line.contains("qyzt")) {
            ind = 9;
        } else if (line.contains("njnd")) {
            ind = 10;
        } else if (line.contains("hzrq")) {
            ind = 11;
        }
        return ind;
    }

    public int getSingleInfo(String id){
        int i, ind;
        String line,ziben="",temp;
        String[] data,sp;
        HttpPost post;
        BufferedReader reader;
        HttpResponse rsp;
        HttpEntity entity;
        
        data = new String[numF];
        for (i = 0; i < numF; i++) {
            data[i] = "";
        }
        try{
            post = new HttpPost(head + "?op="+Math.random()+"&_k="+id+"&type=true");
            //get = new HttpGet(head[op] + "%C6%F3%CD%E2%D4%C1%DD%B8%D7%A4%D7%D6%B5%DA031368%BA%C5");
            //get.addHeader("User-Agent","Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)");
            //get = new HttpGet(head);
            /*query = new Vector<NameValuePair>();
            if (op==0)
                query.add(new BasicNameValuePair("corpName", keyword));
            else query.add(new BasicNameValuePair("registerNo",keyword));
            post.setEntity(new UrlEncodedFormEntity(query, "GBK"));*/
            //post = new HttpPost(head[op] + keyword);
            //System.out.println(keyword);
            //rsp = client.execute(post);
            rsp = client.execute(post);
            entity = rsp.getEntity();
            if (entity != null) {
                reader = new BufferedReader(new InputStreamReader(
                        entity.getContent(), "UTF-8"));
                ind = -1;
                while ((line = reader.readLine()) != null) {
                    //System.out.println(line);
                    /*if (line.contains("注销")||line.contains("吊销")||line.contains("迁往外省市")){
                     reader.close();
                     return;
                     }*/               
                    sp=line.split("\',\'");
                    for (i=0;i<sp.length;i++){
                        ind=getInd(sp[i]);
                        if (ind<0) continue;
                        if (sp[i].split("\':\'").length<2)
                            temp="";
                        else temp=sp[i].split("\':\'")[1].trim();
                        if (ind==61)
                            ziben=temp+"万";
                        else {
                            if (ind==62){
                                temp=ziben+temp;
                                ind/=10;
                            }
                            if (ind==10)
                                temp=temp.substring(0, temp.length()-2);
                            data[ind]=temp;
                        }
                    }
                }
                reader.close();
            }
            for (i = 0; i < numF; i++)
                cout[i].write((data[i] + "\r\n").getBytes());
            return 0;
        }
        catch (Exception ex){
            System.out.println(ex.toString());
            return -1;
        }
    }

    public int work(String qylx, int op, int ind) {
        int i, j,a,b;
        String url;
        String[] sp;
        List<String> ID;
        String line,checkCode="";
        BufferedReader reader;
        HttpPost post1;
        HttpResponse rsp;
        HttpEntity entity;
        boolean ok=false;
        ID = new Vector<String>();
        try {
            while (true){
                if (getIP()) break;
                Thread.sleep(10000);
            }
            while (true) {
                if (cnt%1000==0)
                    checkCode=Extract();
                if (ind != 0) {
                    ind = 0;
                } else if (getKeyword(op, ok) < 0) {
                    break;
                }
                if (op==2)
                    url=head+"?"+Math.random()+"&Cpager=1&HID=ZT&where=&qymc=&zch="+keyword+"&qylx="+qylx+"&bl=true&VC="+checkCode;
                else url=head+"?"+Math.random()+"&Cpager=1&HID=ZT&where=&qymc="+URLEncoder.encode(keyword,"UTF-8")+"&zch=&qylx="+qylx+"&bl=true&VC="+checkCode;
                post1 = new HttpPost(url);
                rsp = client.execute(post1);
                entity = rsp.getEntity();
                ID.clear();
                if (entity != null) {
                    reader = new BufferedReader(new InputStreamReader(
                            entity.getContent(), "UTF-8"));
                    while ((line = reader.readLine()) != null) {
                        //System.out.println(line);
                        if (line.equals("false")){
                            checkCode=Extract();
                            ind=-1;
                            break;
                        }
                        sp=line.split("\\},\\{");
                        for (i=0;i<sp.length;i++){
                            a=sp[i].indexOf("all\':\'")+6;
                            b=sp[i].indexOf("\'",a);
                            if (a>=0&&b>=0)
                                ID.add(sp[i].substring(a, b));
                        }
                    }
                    reader.close();
                }
                ok=ID.size()>0;
                for (i = 0; i < ID.size(); i++) {
                    while (true){
                        if (getSingleInfo(ID.get(i))==0) break;
                        while (true){
                            if (getIP()) break;
                            Thread.sleep(10000);
                        }
                    }
                }
            }
            return 0;
        } catch (Exception ex) {
            //ex.printStackTrace();
            System.out.println(ex.toString());
            client = null;
            client = new DefaultHttpClient();
            client.getParams().setParameter(ClientPNames.COOKIE_POLICY,
                    CookiePolicy.BROWSER_COMPATIBILITY);
            return -1;
        }
    }

    void finish() {
        int i, j;
        try {
            cin.close();
            for (i = 0; i < numF; i++) {
                cout[i].close();
            }
            foIP.close();
            System.out.println("finished");
            System.out.println("Total: " + cnt + " completed");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public int workID(int ind) {
        try {
            while (true) {
                if (ind != 0) {
                    ind = 0;
                } else if (getKeyword(3,false) < 0) {
                    break;
                }
                getSingleInfo(keyword);
            }
            return 0;
        } catch (Exception ex) {
            //ex.printStackTrace();
            System.out.println(ex.toString());
            client = null;
            client = new DefaultHttpClient();
            client.getParams().setParameter(ClientPNames.COOKIE_POLICY,
                    CookiePolicy.BROWSER_COMPATIBILITY);
            return -1;
        }
    }

    public static void main(String args[]) {
        int ret, op = 1;
        String qylx="gt";
        ReadGZAIC rn = new ReadGZAIC();
        if (args.length >= 1) {
            op = Integer.parseInt(args[0]);
        }
        if (args.length>=2)
            qylx=args[1];
        rn.initialize(op);
        ret = 0;
        while (true) {
            if (op == 1 || op == 2) {
                ret = rn.work(qylx, op, ret);
            } else {
                ret = rn.workID(ret);
            }
            if (ret == 0) {
                break;
            }
        }
        rn.finish();
    }
}
