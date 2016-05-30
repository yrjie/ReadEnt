/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author yrjie
 */
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
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import java.net.URLEncoder;
import java.util.regex.*;

public class ReadZHAIC {

    HttpClient client;
    String head="http://www.gdzhaic.gov.cn/check/info.jsp?regNo=";
    String[] filename = {"注册号", "名称", "法人代表", "地址", "经营范围",
                "注册资本", "类型", "注册登记日期","企业状态","最后年检年度","核准日期","经营期限"};
    static int numF = 12, cnt, numD;
    FileOutputStream[] cout;
    Scanner cin;
    String keyword,initReg, initNum;
    long beg, end;
    Pattern p1=Pattern.compile("[\\d]+");
    Matcher m1;

    public void initialize(int op) {
        int i, j;
        client = new DefaultHttpClient();
        client.getParams().setParameter(ClientPNames.COOKIE_POLICY,
                CookiePolicy.BROWSER_COMPATIBILITY);
        try {
            cnt = 0;
            beg=1;
            end=0;
            if (op == 1)
                cin = new Scanner(new FileInputStream("in企业名.txt"), "GBK");
            else if (op==2)
                cin=new Scanner(new FileInputStream("in注册号.txt"),"GBK");
            cout = new FileOutputStream[numF];
            for (i = 0; i < numF; i++) {
                cout[i] = new FileOutputStream((i+1) + filename[i] + ".txt");
            }
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
            if (beg > end) {
                if (!cin.hasNext())
                    ret=-1;
                else {
                    initReg=temp=cin.next();
                    m1=p1.matcher(temp);
                    if (m1.find())
                        initNum=temp=m1.group();
                    numD=temp.length();
                    beg=Long.parseLong(temp)-1;
                    temp=cin.next();
                    m1=p1.matcher(temp);
                    if (m1.find())
                        temp=m1.group();
                    end=Long.parseLong(temp);
                }
            }
            if (!initReg.equals(initNum)||!ok)
                beg++;
            else beg=(beg/10+1)*10;
            keyword = initReg.replace(initNum, String.format("%0"+numD+"d", beg));
        }
        if (ret!=-1) cnt++;
        //System.out.println(keyword);
        if (cnt % 1000 == 0) {
            System.out.println("now at ("+cnt+"): "+ keyword);
        }
        return ret;
    }

    int getInd(String line) {
        int ind = 0;
        if ((line.contains("注册号") || line.contains("字号") || line.contains("编号")) && !line.contains("母")) {
            ind = 1;
        } else if (line.contains("名称")) {
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
        } else if (line.contains("状态")) {
            ind = 9;
        } else if (line.contains("最后年检")) {
            ind = 10;
        } else if (line.contains("核准日期"))
            ind=11;
        else if (line.contains("经营期限"))
            ind=12;
        return ind-1;
    }

    public boolean getSingleInfo() throws Exception {
        int i, ind, a=0, b;
        boolean ok=true;
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
        post = new HttpPost(head + URLEncoder.encode(keyword,"GBK"));
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
                if (line.contains("未查询到")){
                    ok=false;
                    break;
                }
                /*if (line.contains("注销")||line.contains("吊销")||line.contains("迁往外省市")){
                 reader.close();
                 return;
                 }*/
                if (line.contains("align=\"right\""))
                    ind=getInd(line);
                if (ind<0) continue;
                temp="";
                do {
                    line=reader.readLine();
                    temp=temp+line.trim();
                }while (!temp.contains("</td>"));
                a=temp.indexOf(">");
                b=temp.indexOf("</td>", a);
                data[ind]=temp.substring(a+1,b);
                ind=-1;
            }
            reader.close();
        }
        if (ok){
            for (i = 0; i < numF; i++) {
                cout[i].write((data[i] + "\r\n").getBytes());
            }
        }
        return ok;
    }

    public int work(int op, int ind) {
        int i, j;
        boolean ok=false;
        try {
            while (true) {
                if (ind != 0) {
                    ind = 0;
                } else if (getKeyword(op,ok) < 0) {
                    break;
                }
                ok=getSingleInfo();
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
            System.out.println("finished");
            System.out.println("Total: " + cnt + " completed");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String args[]) {
        int ret, op = 2;
        ReadZHAIC rn = new ReadZHAIC();
        if (args.length >= 1) {
            op = Integer.parseInt(args[0]);
        }
        rn.initialize(op);
        ret = 0;
        while (true) {
            if (op == 1 || op == 2) {
                ret = rn.work(op, ret);
            }
            if (ret == 0) {
                break;
            }
        }
        rn.finish();
    }
}
