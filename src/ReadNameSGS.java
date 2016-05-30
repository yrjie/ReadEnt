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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

public class ReadNameSGS {
    
    HttpClient client;
    String[] head={"http://www.sgs.gov.cn/shaic/appStat!toNameAppList.action"
    ,"http://www.sgs.gov.cn/shaic/appStat!toEtpsAppList.action"},
            type={"000000","010000","040000","050000","060000"
            ,"070000","080000","090000","100000","120000","130000"
            ,"140000","150000","200000","260000","270000","280000"
            ,"290000","300000"},
            name={"上海市工商局","黄浦分局","徐汇分局","长宁分局","静安分局","普陀分局"
                    ,"闸北分局","虹口分局","杨浦分局","闵行分局","宝山分局","嘉定分局"
                    ,"浦东新区分局","机场分局","奉贤分局","松江分局","金山分局","青浦分局","崇明分局"},
            date;
    int numF=19;
    String fileName="上海企业.txt";
    FileOutputStream cout;
    static String[] dirName={"企业名称","企业登记"};
    
    Pattern p1=Pattern.compile("共([\\d]+)页");
    Matcher m1;
    
    public void initialize(){
        int i,j;
        Scanner cinD;
        client = new DefaultHttpClient();
        client.getParams().setParameter(ClientPNames.COOKIE_POLICY,
                        CookiePolicy.BROWSER_COMPATIBILITY);
        try{
            date=new String[2];
            cinD=new Scanner(System.in);
            System.out.print("Date1 (YYYY-MM-DD): ");
            date[0]=cinD.nextLine();
            System.out.print("Date2 (YYYY-MM-DD): ");
            date[1]=cinD.nextLine();
            cinD.close();
            cout=new FileOutputStream(fileName);
            /*for (i=0;i<2;i++){
                File dir=new File(dirName[i]);
                if (!dir.exists())
                    dir.mkdir();
                for (j=0;j<numF;j++)
                    cout[i][j]=new FileOutputStream(dirName[i]+"\\"+fileName[j]+".txt");
            }*/
        }
        catch (Exception ex){
            //ex.printStackTrace();
            System.out.println(ex.toString());
        }
    }
    
    public int[] work(int op, int[] ind){
        int i=0,j=0,end,cnt,a,b;
        List<NameValuePair> query;
        String line;
        BufferedReader reader;
        HttpPost post1;
        HttpResponse rsp;
        HttpEntity entity;
        int[] num={4,3};
        try {
            query=new Vector<NameValuePair>();
            if (op==0){
                query.add(new BasicNameValuePair("nameSearchCondition.acceptOrgan",""));
                query.add(new BasicNameValuePair("nameSearchCondition.startDate",date[0]));
                query.add(new BasicNameValuePair("nameSearchCondition.endDate",date[1]));
            }
            else {
                query.add(new BasicNameValuePair("appTotalSearchCondition.acceptOrgan",""));
                query.add(new BasicNameValuePair("appTotalSearchCondition.startDate",date[0]));
                query.add(new BasicNameValuePair("appTotalSearchCondition.endDate",date[1]));
            }
            query.add(new BasicNameValuePair("p",""));
            for (i=ind[0];i<numF;i++){
                end=-1;
                if (op==0)
                    query.set(0, new BasicNameValuePair("nameSearchCondition.acceptOrgan",type[i]));
                else query.set(0, new BasicNameValuePair("appTotalSearchCondition.acceptOrgan",type[i]));
                for (j=ind[1];j<=end||end<0;j++){
                    query.set(3, new BasicNameValuePair("p",""+j));
                    post1 = new HttpPost(head[op]);
                    post1.setEntity(new UrlEncodedFormEntity(query, "GBK"));
                    rsp=client.execute(post1);
                    entity = rsp.getEntity();
                    if (entity != null) {
                        reader = new BufferedReader(new InputStreamReader(
                                            entity.getContent(),"UTF-8"));
                        cnt=0;
                        while ((line=reader.readLine())!=null){
                            //System.out.println(line);
                            if (line.contains("系统出现异常错误")){
                                System.out.println("date error or web page error");
                                System.exit(0);
                            }
                            if (end<0){
                                m1=p1.matcher(line);
                                if (m1.find()){
                                    end=Integer.parseInt(m1.group(1));
                                    if (ind[1]==1)
                                        System.out.println(name[i]+": "+end+" pages");
                                }
                            }
                            if (line.contains("images/online_work/name-20.gif")||line.contains("images/online_work/name-6.gif"))
                                cnt=0;
                            if (cnt>=0&&line.contains("<td>"))
                                cnt++;
                            if (cnt==num[op]){
                                a=line.indexOf("<td>")+4;
                                b=line.length()-5;
                                cout.write((line.substring(a, b).trim()+"\r\n").getBytes());
                                cnt=0;
                            }
                        }
                    }
                }
                ind[1]=1;
            }
            ind[0]=-1;
            return ind;
        }
        catch (Exception ex){
            System.out.println(ex.toString());
            client=null;
            client = new DefaultHttpClient();
            client.getParams().setParameter(ClientPNames.COOKIE_POLICY,
                            CookiePolicy.BROWSER_COMPATIBILITY);
            ind[0]=i;
            ind[1]=j;
            return ind;
        }        
    }
    
    void finish(){
        int i,j;
        try{
            cout.close();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
    public static void main(String args[]){
        int i;
        int[] ret;
        ReadNameSGS rn=new ReadNameSGS();
        ret=new int[2];
        rn.initialize();
        for (i=0;i<2;i++){
            ret[0]=0;
            ret[1]=1;
            System.out.println("\n"+dirName[i]);
            while (true){
                ret=rn.work(i,ret);
                if (ret[0]==-1)
                    break;
            }
        }
        rn.finish();
    }
}
