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
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

public class ReadSGS {

    HttpClient client;
    String[] head = {"http://www.sgs.gov.cn/lz/etpsInfo.do?method=doSearch",
        "http://www.sgs.gov.cn/lz/etpsInfo.do?method=viewDetail&etpsId="},
            filename = {"ID", "注册号", "名称", "法人代表", "地址", "经营范围", "注册资本", "类型", "注册登记日期","企业状态"};
    static int numF = 10, cnt, numD;
    FileOutputStream[] cout;
    Scanner cin;
    String keyword, initReg, initNum;
    //long beg, end;
    long nowR;
    int not,th;
    Pattern p1 = Pattern.compile("viewDetail\\(\'([\\w]+)\'\\)"),p2=Pattern.compile("[\\d]+");
    Matcher m1;
    
    String realIP,nowIP;
    FileOutputStream foIP;

    public void initialize(int op) {
        int i, j;
        Scanner cin1;
        client = new DefaultHttpClient();
        client.getParams().setParameter(ClientPNames.COOKIE_POLICY,
                CookiePolicy.BROWSER_COMPATIBILITY);
        try {
            cnt = 0;
            if (op == 1) {
                cin = new Scanner(new FileInputStream("in企业名.txt"), "GBK");
            } else if (op == 2) {
                cin1=new Scanner(System.in);
                System.out.println("注册号数: ");
                th=cin1.nextInt();
                cin1.close();
                not=th;
                cin=new Scanner(new FileInputStream("in注册号.txt"),"GBK");
            } else if (op == 3) {
                cin = new Scanner(new FileInputStream("inID.txt"), "GBK");
            }
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
                    //System.out.println(initReg);
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
        if ((line.contains("注册号") || line.contains("字号") || line.contains("编号")) && !line.contains("母")) {
            ind = 1;
        } else if (line.contains("名称") && !line.contains("母") && !line.contains("派出")) {
            ind = 2;
        } else if (line.contains("代表人") || line.contains("合伙人") || line.contains("负责人")
                || line.contains("投资人") || (line.contains("首席代表") && !line.contains("国籍"))) {
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
        }
        return ind;
    }

    public int getSingleInfo(String id){
        int i, ind, a, b;
        boolean flag;
        String line, temp;
        String[] data;
        HttpPost post;
        BufferedReader reader;
        HttpResponse rsp;
        HttpEntity entity;

        data = new String[numF];
        for (i = 0; i < numF; i++) {
            data[i] = "";
        }
        data[0] = id;
        try{
            post = new HttpPost(head[1] + id);
            post.addHeader("Referer", "http://www.sgs.gov.cn/lz/etpsInfo.do?method=doSearch");
            rsp = client.execute(post);
            entity = rsp.getEntity();
            if (entity != null) {
                reader = new BufferedReader(new InputStreamReader(
                        entity.getContent(), "UTF-8"));
                ind = -1;
                flag = false;
                temp = "";
                while ((line = reader.readLine()) != null) {
                    //System.out.println(line);
                    /*if (line.contains("注销")||line.contains("吊销")||line.contains("迁往外省市")){
                     reader.close();
                     return;
                     }*/
                    if (line.contains("\"list_title_boeder\" ")) {
                        if (line.contains("</td>")) {
                            ind = getInd(line);
                            if (ind > -1 && !data[ind].isEmpty()) {
                                ind = -1;
                            }
                        } else {
                            flag = true;
                        }
                    }
                    if (line.contains("\"list_td_1\" ")) {
                        if (line.contains("</td>")) {
                            /*if (ind == -2) {
                                if (!line.contains("确立")){
                                    reader.close();
                                    return;
                                }
                                ind=-1;
                            }
                            else */
                            if (ind>-1){
                                a = line.indexOf("%\">") + 3;
                                b = line.length() - 5;
                                data[ind] = line.substring(a, b);
                                ind=-1;
                            }
                        }
                        else flag=true;
                    }
                    if (flag) {
                        temp = temp + line.trim();
                        if (line.contains("</td>")) {
                            if (ind == -1) {
                                ind = getInd(temp);
                            }
                            else if (ind == -2) {
                                if (!temp.contains("确立")) {
                                    reader.close();
                                    return 0;
                                }
                                ind=-1;
                            }
                            else {
                                a = temp.indexOf("%\">") + 3;
                                b = temp.length() - 5;
                                data[ind] = temp.substring(a, b);
                                ind = -1;
                            }
                            flag = false;
                            temp = "";
                        }
                    }
                }
                reader.close();
            }
            for (i = 0; i < numF; i++) {
                data[i] = data[i].replaceAll("&nbsp;", " ");
                cout[i].write((data[i] + "\r\n").getBytes());
            }
            return 0;
        }
        catch (Exception ex){
            System.out.println(ex.toString());
            return -1;
        }
    }

    public int work(int op, int ind) {
        int i, j;
        String nowid;
        List<NameValuePair> query;
        List<String> ID;
        String line;
        BufferedReader reader;
        HttpPost post1;
        HttpResponse rsp;
        HttpEntity entity;
        boolean ok=false;
        ID = new Vector<String>();
        try {
            query = new Vector<NameValuePair>();
            query.add(new BasicNameValuePair("searchType", "" + op));
            query.add(new BasicNameValuePair("keyWords", ""));
            post1 = new HttpPost(head[0]);
            while (true){
                if (getIP()) break;
                Thread.sleep(10000);
            }
            while (true) {
                if (ind != 0) {
                    ind = 0;
                } else if (getKeyword(op, ok) < 0) {
                    break;
                }
                ok=false;
                query.set(1, new BasicNameValuePair("keyWords", keyword));
                post1.setEntity(new UrlEncodedFormEntity(query, "UTF-8"));
                rsp = client.execute(post1);
                entity = rsp.getEntity();
                ID.clear();
                nowid = null;
                if (entity != null) {
                    reader = new BufferedReader(new InputStreamReader(
                            entity.getContent(), "UTF-8"));
                    while ((line = reader.readLine()) != null) {
                        //System.out.println(line);
                        m1 = p1.matcher(line);
                        if (m1.find()) {
                            //System.out.println(m1.group(1));
                            nowid = m1.group(1);
                            //ID.add(nowid);
                        }
                        if (line.contains("企业状态")){
                            ok=true;
                            if (nowid!=null) ID.add(nowid);
                            /*if ( line.contains("确立") && nowid != null) {
                                ID.add(nowid);
                            }*/
                        }
                    }
                    reader.close();
                }
                for (i = 0; i < ID.size(); i++) {
                    //System.out.println(ID.get(i));
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
        int ret, op = 2;
        ReadSGS rn = new ReadSGS();
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
