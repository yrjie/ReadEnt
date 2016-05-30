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
import java.util.HashSet;
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
import sun.misc.BASE64Encoder;

public class ReadBAIC {

    HttpClient client;
    String[] head = {"http://qyxy.baic.gov.cn/zhcx/zhcxAction!list.dhtml?op=cx",
        "http://qyxy.baic.gov.cn/kscx/kscxAction!view.dhtml?kscxModel.reg_bus_ent_id="},
            filename = {"ID", "注册号", "名称", "法人代表", "地址", "经营范围", "注册资本", "类型", "注册登记日期", "企业状态"};
    static int numF = 10, cnt, numD;
    FileOutputStream[] cout;
    Scanner cin;
    String keyword, initReg, initNum;
    //long beg, end,wait;
    int not,th;
    final int thERR=100;
    long nowR;
    Pattern p1 = Pattern.compile("kscxModel.reg_bus_ent_id=([\\w]+)"),p2=Pattern.compile("[\\d]+");
    Matcher m1;
    
    FileOutputStream foIP;
    HashSet<String> IPset;
    final int Tout=300000,Tfail=30;
    int fail, wait;
    String realIP,nowIP;

    public void initialize(int op) {
        int i, j;
        double t1;
        Scanner cin1;
        client = new DefaultHttpClient();
        client.getParams().setParameter(ClientPNames.COOKIE_POLICY,
                CookiePolicy.BROWSER_COMPATIBILITY);
        try {
            cnt = 0;
            //System.out.println("Waiting time (seconds): ");
            //t1=cin1.nextDouble();
            //wait=(long)(t1*1000);
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
            keyword="";
            IPset=new HashSet<String>();
            getIP("");
            realIP=nowIP;
            //System.out.println("Real IP: "+realIP);
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
        long now=0;
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
        if (cnt % 1000 == 0) {
            System.out.println("now at ("+cnt+"): "+ keyword);
        }
        return ret;
    }

    int getInd(String line) {
        int ind = -1;
        if (line.contains("注册号") && !line.contains("母")) {
            ind = 1;
        } else if (line.contains("名称") && !line.contains("母")) {
            ind = 2;
        } else if (line.contains("代表人") || line.contains("合伙人") || line.contains("负责人")
                || line.contains("经营者") || line.contains("投资人") || line.contains("首席代表")) {
            ind = 3;
        } else if (line.contains("住所") || line.contains("场所") || line.contains("地址")) {
            ind = 4;
        } else if (line.contains("范围") && !line.contains("许可")) {
            ind = 5;
        } else if (line.contains("注册资") || line.contains("资金")) {
            ind = 6;
        } else if (line.contains("类型") || line.contains("经济性质")) {
            ind = 7;
        } else if (line.contains("成立日期") || line.contains("发照日期")) {
            ind = 8;
        } else if (line.contains("状态")) {
            ind = 9;
        }
        return ind;
    }

    String getData(String line) {
        int a, b;
        a = line.indexOf(">") + 1;
        b = line.length() - 5;
        //System.out.println(line);
        if (a > b) {
            return "";
        } else {
            return line.substring(a, b);
        }
    }

    public int getSingleInfo(String id){
        int i, ind;
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
            post = new HttpPost(head[1] + id + "&flag_cer=0");
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
                    if (line.contains("td class=\"td01\"")) {
                        if (line.contains("</td>")) {
                            ind = getInd(line);
                            if (ind > -1 && !data[ind].isEmpty()) {
                                ind = -1;
                            }
                        } else {
                            flag = true;
                        }
                    }
                    if (line.contains("td class=\"td02\"")) {
                        if (line.contains("</td>")) {
                            if (ind == -2) {
                                if (!line.contains("开业")){
                                    reader.close();
                                    return 0;
                                }
                                ind=-1;
                            }
                            else if (ind>-1){
                                data[ind] = getData(line);
                                ind=-1;
                            }
                        } else {
                            flag = true;
                        }
                    }
                    if (flag) {
                        temp = temp + line.trim();
                        if (line.contains("</td>")) {
                            if (ind > -1) {
                                data[ind] = getData(temp);
                                ind = -1;
                            }
                            else if (ind == -2) {
                                if (!temp.contains("开业")) {
                                    reader.close();
                                    return 0;
                                }
                                ind=-1;
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
                data[i] = data[i].replaceAll("&middot;", "·");
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
        int i, j, state;  // state: 0 normal, 1 got ID, 2 注销or吊销
        List<NameValuePair> query;
        List<String> ID;
        String line, temp = null;
        BufferedReader reader;
        HttpPost post1;
        HttpResponse rsp;
        HttpEntity entity;
        boolean ok=false;
        int numERR=0;
        ID = new Vector<String>();
        try {
            query = new Vector<NameValuePair>();
            query.add(new BasicNameValuePair("zhcxModel.ent_name", ""));
            post1 = new HttpPost(head[0]);
            post1.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.4 (KHTML, like Gecko) Chrome/22.0.1229.94 Safari/537.4");
            //post1.addHeader("Cookie", "wdcid=36a4e1d028315ee9; BIGipServerPool_xy=219523264.17695.0000; JSESSIONID=R2m2QQlpfvxn8tTT3QQGrfck7qkzhjrLmzJsl6HGMd1Q4pqk4nYV!301052780; wdlast=1351673662");
            //post1.addHeader("Host", "qyxy.baic.gov.cn");
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
                ok=false;
                if (op == 1) {
                    query.set(0, new BasicNameValuePair("zhcxModel.ent_name", keyword));
                } else {
                    query.set(0, new BasicNameValuePair("zhcxModel.lic_reg_no", keyword));
                }
                post1.setEntity(new UrlEncodedFormEntity(query, "UTF-8"));
                //post1.addHeader("Origin", "http://qyxy.baic.gov.cn");
                //post1.addHeader("Referer", "http://qyxy.baic.gov.cn/zhcx/zhcxAction!list.dhtml?op=cx");
                rsp = client.execute(post1);
                entity = rsp.getEntity();
                ID.clear();
                state = 0;
                if (entity != null) {
                    reader = new BufferedReader(new InputStreamReader(
                            entity.getContent(), "UTF-8"));
                    while ((line = reader.readLine()) != null) {
                        //System.out.println(line);
                        if (line.contains("访问异常")) {
                            System.out.println("访问异常("+cnt+"): "+keyword);
                            cnt--;
                            ind = -1;
                            numERR++;
                            /*if (numERR>thERR){
                                System.err.println("访问异常超过 "+thERR+" 次");
                                System.exit(1);
                            }*/
                            break;
                        }
                        m1 = p1.matcher(line);
                        if (m1.find()) {
                            //System.out.println(m1.group(1));
                            //ID.add(m1.group(1));
                            temp = m1.group(1);
                            state = 1;
                        }
                        /*if (state == 1 && (line.contains("注销") || line.contains("吊销"))) {
                            state = 2;
                        }*/
                        if (state > 0 && line.contains("</tr>")) {
                            ok=true;
                            if (state == 1) {
                                ID.add(temp);
                            }
                            state = 0;
                        }
                    }
                    reader.close();
                    if (ind==-1){
                        //restartRouter();
                        client = null;                        
                        client = new DefaultHttpClient();
                        client.getParams().setParameter(ClientPNames.COOKIE_POLICY,
                                CookiePolicy.BROWSER_COMPATIBILITY);
                    }
                }
                //if (ID.size()==0)
                //    Thread.sleep(wait);
                for (i = 0; i < ID.size(); i++) {
                    while (true){
                        if (getSingleInfo(ID.get(i))==0) break;
                        while (true){
                            if (getIP(realIP)) break;
                            Thread.sleep(10000);
                        }
                    }
                    //cnt++;
                    //if (cnt%1000==0)
                    //    System.out.println(cnt+" completed");
                }
            }
            return 0;
        } catch (Exception ex) {
            //ex.printStackTrace();
            System.out.println(ex.toString());
            //restartRouter();
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

    public int workID(int ind) {
        try {
            while (true) {
                if (ind != 0) {
                    ind = 0;
                } else if (getKeyword(3, false) < 0) {
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
        ReadBAIC rn = new ReadBAIC();
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
