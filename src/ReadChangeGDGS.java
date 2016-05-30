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
import java.util.regex.*;
import java.util.Vector;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

public class ReadChangeGDGS {

    HttpClient client;
    String head ="http://wsnj.gdgs.gov.cn/aiccps/bizhtml/biz0";
    String[] filename = {"注册号", "名称", "登记机关"};
    static int numF = 3, cnt, numD;
    FileOutputStream[] cout;
    //long beg, end;
    long nowR;
    int not,th;
    Pattern p1 = Pattern.compile("etpsId=([\\w]+)"),p2=Pattern.compile("[\\d]+");
    Matcher m1;
    List<String>[] data;

    public void initialize() {
        int i, j;
        client = new DefaultHttpClient();
        client.getParams().setParameter(ClientPNames.COOKIE_POLICY,
                CookiePolicy.BROWSER_COMPATIBILITY);
        try {
            cnt = 0;
            cout = new FileOutputStream[numF];
            data=new Vector[numF];
            for (i = 0; i < numF; i++) {
                cout[i] = new FileOutputStream((i+1) + filename[i] + ".txt");
                data[i]=new Vector<String>();
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

    public void writeToFile()throws IOException{
        int i,j;
        for (i=0;i<numF;i++)
            for (j=0;j<data[0].size();j++)
                cout[i].write((data[i].get(j) +"\r\n").getBytes());
        cnt+=data[0].size();
        if (cnt%1000==0&&cnt>0) System.out.println(cnt+" completed");
    }
    
    public int[] work(int[] ind) {
        int i=3,j=1,k,a;
        String line;
        BufferedReader reader;
        HttpPost post1;
        HttpResponse rsp;
        HttpEntity entity;
        try {
            for (i=ind[0];i<=5;i++){
                if (i==ind[0]) j=ind[1];
                else j=1;
                for (;j<=10;j++){
                    post1 = new HttpPost(head+i+"_"+j+".html");
                    for (k=0;k<3;k++)
                        data[k].clear();
                    rsp = client.execute(post1);
                    entity = rsp.getEntity();
                    if (entity != null) {
                        reader = new BufferedReader(new InputStreamReader(
                                entity.getContent(), "GBK"));
                        while ((line = reader.readLine()) != null) {
                            //System.out.println(line);
                            k=-1;
                            if (line.contains("color=\"#6495ED\""))
                                k=1;
                            else if (line.contains("注&nbsp;册&nbsp;号"))
                                k=0;
                            else if (line.contains("登记机关"))
                                k=2;
                            if (k<0) continue;
                            if (k==1) a=line.indexOf("b>")+2;
                            else a=line.indexOf("：")+1;
                            data[k].add(line.substring(a).trim());
                        }
                        reader.close();
                    }
                    writeToFile();
                }
            }
            ind[0]=0;
            return ind;
        } catch (Exception ex) {
            //ex.printStackTrace();
            System.out.println(ex.toString());
            client = null;
            client = new DefaultHttpClient();
            client.getParams().setParameter(ClientPNames.COOKIE_POLICY,
                    CookiePolicy.BROWSER_COMPATIBILITY);
            ind[0]=i;
            ind[1]=j;
            return ind;
        }
    }

    void finish() {
        int i, j;
        try {
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
        int[] ret={3,1};
        ReadChangeGDGS rn = new ReadChangeGDGS();
        rn.initialize();
        while (true) {
            ret = rn.work(ret);
            if (ret[0] == 0) {
                break;
            }
        }
        rn.finish();
    }
}
