
import java.io.*;
import java.net.URL;
import java.util.regex.*;
import org.apache.poi.xssf.usermodel.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import java.util.List;
import java.util.Scanner;
import java.util.Vector;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import sun.misc.BASE64Decoder;

public class ReadNameHY {    
    final String head="http://www.gdhygs.gov.cn/Search.aspx";
    Pattern p1=Pattern.compile("总记录&nbsp;([\\d]+).+共&nbsp;([\\d]+)&nbsp;页");
    Matcher m1;
    int cnt;
    
    // xlsx txt
    FileOutputStream cout;
    String filename="河源企业";
    String viewstate, valid;
    int numD;
    //long beg, end;
    int not,th;
    long nowR=0;
    
    //net
    HttpClient client;
    HttpPost post;
    List<NameValuePair> namevaluepair;
    List<String> data;
    
    public void initialize(){
        int i,j,k;
        client = new DefaultHttpClient();
        client.getParams().setParameter(ClientPNames.COOKIE_POLICY,
                            CookiePolicy.BROWSER_COMPATIBILITY);
        post = new HttpPost(head);
        try {
            //wb=new XSSFWorkbook(new FileInputStream(excel));
            //filebeg[0]=wb.getSheetAt(0).getLastRowNum();
            //filebeg[1]=wb.getSheetAt(1).getLastRowNum();
            cnt=0;
            cout = new FileOutputStream(filename + ".txt");
            data=new Vector<String>();
            Scanner cin1=new Scanner(new FileInputStream("include\\stateHY.txt"));
            Scanner cin2=new Scanner(new FileInputStream("include\\validateHY.txt"));
            Scanner cin3=new Scanner(new FileInputStream("include\\previousHY.txt"));
            namevaluepair=new Vector<NameValuePair>();
            namevaluepair.add(new BasicNameValuePair("__VIEWSTATE",cin1.nextLine()));
            namevaluepair.add(new BasicNameValuePair("__EVENTVALIDATION",cin2.nextLine()));
            namevaluepair.add(new BasicNameValuePair("__PREVIOUSPAGE",cin3.nextLine()));
            //namevaluepair.add(new BasicNameValuePair("chkEntname","checked"));
            namevaluepair.add(new BasicNameValuePair("ctl00$ContentPlaceHolder1$serimagebuttom.x","15"));
            namevaluepair.add(new BasicNameValuePair("ctl00$ContentPlaceHolder1$serimagebuttom.y","10"));
            //namevaluepair.add(new BasicNameValuePair("hfIP","183.62.57.2"));
            //namevaluepair.add(new BasicNameValuePair("txtEntName",""));
            //namevaluepair.add(new BasicNameValuePair("txtcheckCode",""));
            cin1.close();
            cin2.close();
            cin3.close();
            //System.out.println(namevaluepair.get(0).getValue());
        }
        catch (IOException ioe){
            System.err.println(ioe.toString());
        }
    }
    
    public int work(int ind){
        String checkCode,IP,temp,id=null;
        HttpResponse response;
        HttpEntity entity;
        int numR,numP,a,b;
        boolean ok=false;
        numR=numP=-1;
        try{
            while (true){
                if (ind<0) namevaluepair.set(2, new BasicNameValuePair("__EVENTTARGET",""));
                if (ind>0) namevaluepair.set(2, new BasicNameValuePair("__EVENTTARGET","ctl00$ContentPlaceHolder1$LinkButton2"));
                ind=1;
                post.setEntity(new UrlEncodedFormEntity(namevaluepair, "GBK"));
                response = client.execute(post);
                entity = response.getEntity();
                if (entity != null) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                                    entity.getContent(),"GBK"));
                    String line=null;
                    data.clear();
                    while ((line=reader.readLine())!=null){
                        if (line.contains("Runtime Error")){
                            ok=true;
                            break;
                        }
                        //System.out.println(line);
                        if (line.contains("总记录")&&numR<0){
                            m1=p1.matcher(line);
                            if (m1.find()){
                                numR=Integer.parseInt(m1.group(1));
                                numP=Integer.parseInt(m1.group(2));
                                System.out.println("number of records: "+numR);
                                System.out.println("number of pages: "+numP);
                            }
                        }
                        if (line.contains("__VIEWSTATE")){
                            viewstate=line.substring(line.indexOf("value=")+7,line.length()-4);
                            namevaluepair.set(0, new BasicNameValuePair("__VIEWSTATE",viewstate));
                        }
                        if (line.contains("__EVENTVALIDATION")){
                            valid=line.substring(line.indexOf("value=")+7,line.length()-4);
                            namevaluepair.set(1, new BasicNameValuePair("__EVENTVALIDATION",valid));
                        }
                        a=line.indexOf("color=\"#4A3C8C\">");
                        b=line.indexOf("</font>", a);
                        if (a<0||b<0) continue;
                        //System.out.println(line+a+" "+b);
                        data.add(line.substring(a+16,b));
                    }
                    reader.close();
                    write2Txt();
                }
                if (ok) break;
            }
            return 0;
        }
        catch (IOException ioe){
            ioe.printStackTrace();
            return -1;
        }
    }
    
    public void write2Txt(){
        int i;
        try{
            for (i=0;i<data.size();i++){
                cout.write((data.get(i) +"\r\n").getBytes());
            }
            cnt+=data.size();
            if (cnt>0&&cnt%1000==0) System.out.println(cnt+" completed");
        }
        catch (IOException ioe){
            System.out.println(ioe.toString());
        }
    }

    public void finish(){
        int i,j;
        try {
            //FileOutputStream fileOut = new FileOutputStream("data.xlsx");
            //wb.write(fileOut);
            //fileOut.close();            
            cout.close();
            System.out.println("finished");
            System.out.println("Total: " + cnt + " completed");
        }
        catch (IOException ioe){
            ioe.printStackTrace();
        }
    }
    
    public static void main(String args[]){
        int ret;
        ReadNameHY re=new ReadNameHY();
        re.initialize();
        ret = 0;
        while (true) {
            ret = re.work(ret);
            if (ret == 0) {
                break;
            }
        }
        re.finish();
    }
}
