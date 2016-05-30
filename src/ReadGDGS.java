/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author yrjie
 */
import java.io.*;
import java.net.URL;
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
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.regex.*;

public class ReadGDGS {

    HttpClient client;
    //String head="http://wsnj.gdgs.gov.cn/aiccps/SearchServlet?service=getEntityInfoByPageForDG";
    //4419001000002
    String[] head={"http://wsnj.gdgs.gov.cn/aiccps/SearchServlet?service=getEntityInfoByPageForDG&corpName=",
    "http://wsnj.gdgs.gov.cn/aiccps/SearchServlet?service=getEntityInfoByPageForDG&registerNo=",
    "http://wsnj.gdgs.gov.cn/aiccps/jsp/pub/sjShowEntityView.jsp?entityno="};
    String[] filename = {"ID","注册号", "名称", "法人代表", "地址", "经营范围",
                "注册资本", "类型", "注册登记日期","企业状态"};
    static int numF = 10, cnt, numD;
    FileOutputStream[] cout;
    Scanner cin;
    String keyword,initReg, initNum;
    int not,th;
    long nowR;
    Pattern p1=Pattern.compile("[\\d]+");
    Matcher m1;
    
    final int Tfail=30;
    int fail, wait;
    //boolean second;
    String realIP,nowIP;
    FileOutputStream foIP;
    HashSet<String> IPset;

    public void initialize(int op) {
        int i, j;
        Scanner cin1;
        client = new DefaultHttpClient();
        client.getParams().setParameter(ClientPNames.COOKIE_POLICY,
                CookiePolicy.BROWSER_COMPATIBILITY);
        try {
            cnt = 0;
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
            keyword="";
            IPset=new HashSet<String>();
            getIP("");
            realIP=nowIP;
            //System.out.println("Real IP: "+realIP);
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

    public boolean getIP(String oldIP){
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
                System.out.println("now IP: "+ret);
                foIP.write((nowIP+"\t"+df.format(new Date())+"\r\n").getBytes());
                if (IPset.contains(ret))
                    return false;
                else {
                    IPset.add(ret);
                    if (!keyword.isEmpty()){
                        System.out.println("now at ("+cnt+"): "+ keyword);
                    }
                    //if (fail<Tfail) wait=0;
                    return true;
                }
            }
            return !ret.equals(oldIP);
        }
        catch (IOException ioe){
            ioe.printStackTrace();
            updateFail();
            return false;
        }
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
                    m1=p1.matcher(temp);
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
        if ((line.contains("注册号") || line.contains("字号") || line.contains("编号")) && !line.contains("母")) {
            ind = 1;
        } else if (line.contains("名称")||line.contains("企业名")) {
            ind = 2;
        } else if (line.contains("代表人") || line.contains("合伙人") || line.contains("负责人")
                || line.contains("投资人") || (line.contains("首席代表") && !line.contains("国籍"))
                || line.contains("经营者")) {
            ind = 3;
        } else if (line.contains("住所") || line.contains("场所") || line.contains("地址")) {
            ind = 4;
        } else if (line.contains("范围")) {
            ind = 5;
        } else if (line.contains("注册资") || line.contains("资金")) {
            ind = 6;
        } else if (line.contains("类型") || line.contains("经济性质")) {
            ind = 7;
        } else if (line.contains("成立日期") || line.contains("设立日期")) {
            ind = 8;
        } else if (line.contains("企业状态")) {
            ind = 9;
        } else if (line.contains("最后年检年度")) {
            ind = 10;
        }
        return ind;
    }

    public int getSingleInfo(String id){
        int i, ind, a=0, b;
        String line,temp="";
        String[] data;
        HttpPost post;
        BufferedReader reader;
        HttpResponse rsp;
        HttpEntity entity;

        data = new String[numF];
        for (i = 0; i < numF; i++) {
            data[i] = "";
        }
        data[0]=id;
        try{
            post = new HttpPost(head[2] + URLEncoder.encode(id,"GBK"));
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
                        entity.getContent(), "GBK"));
                ind = -1;
                while ((line = reader.readLine()) != null) {
                    //System.out.println(line);
                    /*if (line.contains("注销")||line.contains("吊销")||line.contains("迁往外省市")){
                     reader.close();
                     return;
                     }*/
                    //if (line.contains("&nbsp;"))
                    //    line=line.replaceAll("&nbsp;", "");
                    if (line.contains("align=\"right\"")){
                        temp=line;
                        while (!temp.contains("</td>")){
                            line=reader.readLine();
                            line=line.replaceAll("&nbsp;", "");
                            temp=temp+line.trim();
                        }
                        a=line.indexOf("align=\"right\"");
                        ind=getInd(temp);
                    }
                    if (ind<0||!data[ind].isEmpty()) continue;
                    temp="";
                    do {
                        line=reader.readLine();
                        line=line.replaceAll("&nbsp;", "");
                        temp=temp+line.trim();
                    }while (!temp.contains("</td>"));
                    a=temp.indexOf(">");
                    b=temp.indexOf("</td>", a);
                    data[ind]=temp.substring(a+1,b);
                    /*if (ind==2){
                        a=line.indexOf("Info(\'")+6;
                        b=line.indexOf("\')\">");
                        data[0]=line.substring(a,b);
                        a=line.indexOf("color=\"red\"");
                        if (a<0) a=line.indexOf(")\">");
                    }
                    else if (line.contains("valign=top")){
                        a=line.indexOf("color=\"red\"");
                        if (a<0) a=line.indexOf("valign=top");
                    }
                    a=line.indexOf(">", a)+1;
                    b=line.indexOf("</font>",a);
                    if (b<0) b=line.indexOf("</td>",a);
                    data[ind] = line.substring(a, b).trim();*/
                    /*if (ind==9&&(data[ind].contains("注销")||data[ind].contains("吊销"))){
                        ok=false;
                        break;
                    }*/
                    ind=-1;
                }
                reader.close();
            }
            for (i = 0; i < numF; i++) {
                cout[i].write((data[i] + "\r\n").getBytes());
            }
            return 0;
        }
        catch (Exception ex){
            System.out.println(ex.toString());
            updateFail();
            return -1;
        }
    }

    public int work(int op, int ind) {
        int i, j, a, b;
        List<String> ID;
        String line;
        BufferedReader reader;
        HttpPost post1;
        HttpResponse rsp;
        HttpEntity entity;
        boolean ok=false;
        ID = new Vector<String>();                
        try {
            while (true){
                if (getIP(realIP)) break;
                Thread.sleep(10000);
            }
            while (true) {
                if (ind != 0) {
                    ind = 0;
                } else if (getKeyword(op,ok) < 0) {
                    break;
                }
                post1 = new HttpPost(head[op-1]+URLEncoder.encode(keyword,"GBK"));
                rsp = client.execute(post1);
                entity = rsp.getEntity();
                ID.clear();
                if (entity != null) {
                    reader = new BufferedReader(new InputStreamReader(
                            entity.getContent(), "UTF-8"));
                    while ((line = reader.readLine()) != null) {
                        //System.out.println(line);
                        if (line.contains("找不到"))
                            break;
                        if (line.contains("onclick=\"sjShowEntityInfo")){
                            a=line.indexOf("Info(\'")+6;
                            b=line.indexOf("\')\">");
                            ID.add(line.substring(a, b));
                        }
                    }
                    reader.close();
                }
                ok=ID.size()>0;
                for (i = 0; i < ID.size(); i++) {
                    while (true){
                        if (getSingleInfo(ID.get(i))==0) break;
                        while (true){
                            if (getIP(realIP)) break;
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
            updateFail();
            return -1;
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
            updateFail();
            return -1;
        }
    }

    public void updateFail(){
        fail++;
        try{
            if (fail>Tfail){
                System.out.println("Failed to respond for "+Tfail+" requests");
                wait++;
                if (wait>=3){
                    System.out.println("Fail for 3 IP");
                    System.exit(1);
                }
                while (true){
                    if (getIP(nowIP)) break;
                    Thread.sleep(10000);
                }
                fail=0;
                /*if (!second){
                    fail=0;
                    second=true;
                    Thread.sleep(120000);
                }
                else {
                    finish();
                    System.exit(1);
                }*/
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
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

    public static void main(String args[]) {
        int ret, op = 1;
        ReadGDGS rn = new ReadGDGS();
        if (args.length >= 1) {
            op = Integer.parseInt(args[0]);
        }
        rn.initialize(op);
        ret = 0;
        while (true) {
            if (op == 1 || op == 2) {
                ret = rn.work(op, ret);
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
