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
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.text.DecimalFormat;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class ReadName {

    WebClient webClient;
    List<String> alerts,addr;
    List<Integer> cap;
    HtmlPage page;
    WebRequest webreq1;
    FileOutputStream cout;
    String date,checkCode;
    int cnt,year,month,day1,day2;
    
    //checkCode
    int[][][] mat;
    int[][] cc;
    String allC="123456789+-";
    final int w=11,h=15;
    int n;
    int[] zero = {-3355444,-6710887,-1};
    
    public int getCheckCode(){
        int i,j,pixel,ccH,ccW;
        FileOutputStream cout1;
        String gifName="信用网.gif";
        try {
            cout1=new FileOutputStream(gifName);
            //Page gif=webClient.getPage("http://www.szcredit.com.cn/WebPages/Member/CheckCode.aspx");
            Page gif=webClient.getPage("http://www.szcredit.com.cn/WebPages/common/ValidCode.aspx");
            InputStream is=gif.getWebResponse().getContentAsStream();
            int b;
            while (true){
                b=is.read();
                if (b==-1) break;
                cout1.write(b);
            }
            is.close();
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
            webClient.closeAllWindows();
            return 0;
        }
        catch (Exception ex){
            //System.out.println("read gif error: "+ioe.toString());
            return -1;
        }        
    }
    
    public String Extract(){
        int i,x1,y1,j,x2,y2,dx,ma,temp,inc,ind,readG=-1;
        while (readG<0)
            readG=getCheckCode();
        int[] num;
        x1=18;
        y1=5;
        ma=0;
        ind=-1;
        for (i=9;i<=10;i++){
            temp=0;
            for (y2=0;y2<h;y2++)
                for (x2=0;x2<w;x2++){
                    if (cc[y1+y2][x1+x2]==mat[i][y2][x2]) temp++;
                }
            if (temp>ma){
                ma=temp;
                ind=i;
            }
        }
        if (ind==9) inc=25;
        else inc=20;
        num=new int[2];
        for (i=0;i<2;i++){
            ma=0;
            for (dx=-3;dx<3;dx++){
                x1=6+inc*i+dx;
                for (j=0;j<9;j++){
                    temp=0;
                    for (y2=0;y2<h;y2++)
                        for (x2=0;x2<w;x2++){
                            if (cc[y1+y2][x1+x2]==mat[j][y2][x2]) temp++;
                        }
                    if (temp>ma){
                        ma=temp;
                        num[i]=j+1;
                    }
                }
            }
        }
        //System.out.println(num[0]+" "+num[1]);
        if (ind==9) return (num[0]+num[1])+"";
        else return (num[0]-num[1])+"";
    }
    
    public void initialize(int op){
        int i,j,k;
        Scanner cin0,cin1=null,cin2=null,cinD;
        String temp;
        alerts=new ArrayList();
        webClient = new WebClient(BrowserVersion.FIREFOX_3_6);
        webClient.setCssEnabled(false);
        webClient.setAlertHandler(new CollectingAlertHandler(alerts));
        webClient.setIncorrectnessListener(new MyIncorrectnessListener());
        cnt=0;
        try{
            cinD=new Scanner(System.in);
            System.out.print("Date1 (YYYY-MM-DD): ");
            temp=cinD.nextLine();
            year=Integer.parseInt(temp.substring(0,4));
            month=Integer.parseInt(temp.substring(5, 7));
            day1=Integer.parseInt(temp.substring(8, 10));
            System.out.print("Date2 (YYYY-MM-DD): ");
            temp=cinD.nextLine();
            day2=Integer.parseInt(temp.substring(8, 10));
            if (year!=Integer.parseInt(temp.substring(0,4))||month!=Integer.parseInt(temp.substring(5, 7))||day1>day2){
                System.out.println("Date format error!");
                System.exit(1);
            }
            cinD.close();
            cin0=new Scanner(new FileInputStream("include\\standard+-.txt"));
            n=allC.length();
            mat=new int[n][h][w];
            for (i=0;i<n;i++)
                for (j=0;j<h;j++)
                    for (k=0;k<w;k++)
                        mat[i][j][k]=cin0.nextInt();
            cin0.close();
            webreq1=new WebRequest(new URL("http://www.szcredit.com.cn/WebPages/Search/ajaxservice.svc/GetEntlistByManyCondition"),HttpMethod.POST);
            webreq1.setAdditionalHeader("X-Requested-With", "XMLHttpRequest");
            webreq1.setAdditionalHeader("Referer", "http://www.szcredit.com.cn/WebPages/Search/SZManyConditionList.aspx");
            webreq1.setAdditionalHeader("Content-Type", "application/json; charset=utf-8");
            if (op==2){
                cout=new FileOutputStream("企业名.txt");
                cin1=new Scanner(new FileInputStream("include\\address.txt"));
                cin2=new Scanner(new FileInputStream("include\\capital_qy.txt"));
            }
            else if (op==49){
                cin1=new Scanner(new FileInputStream("include\\address.txt"));
                cin2=new Scanner(new FileInputStream("include\\capital_gt.txt"));
                cout=new FileOutputStream("个体名.txt");
            }
            else cout=new FileOutputStream("其他名.txt");
            if (cin1!=null){
                addr=new ArrayList<String>();
                while (cin1.hasNext())
                    addr.add(cin1.next());
                cin1.close();
            }
            if (cin2!=null){
                cap=new ArrayList<Integer>();
                while (cin2.hasNext())
                    cap.add(cin2.nextInt());
                cin2.close();
            }
        }
        catch (Exception ex){
            //ex.printStackTrace();
            System.out.println(ex.toString());
        }
    }
    
    public void Login() throws Exception{
        HtmlElement btn1=page.getElementById("btnLogin");
        page.getElementById("txtUserName").setAttribute("value", "10000929");
        page.getElementById("txtPassword").setAttribute("value", "128265");
        btn1.click();
        //Thread.sleep(3000);
    }
    
    public void setCond(int op,int i,int j) throws Exception{
        String[] condName={"txtDiZhi","txtZCRQ1","txtZCRQ2","txtZCZB1","txtZCZB2","txtCheckCode1"};
        HtmlSelect sel;
        HtmlElement[] cond;
        int k;
        page=(HtmlPage)page.refresh();
        cond=new HtmlElement[6];
        for (k=0;k<6;k++)
            cond[k]=page.getElementById(condName[k]);
        sel=(HtmlSelect)page.getElementById("lstQYLX");
        if (op==2||op==49){
            cond[0].setAttribute("value",addr.get(i));
            cond[1].setAttribute("value", date);
            cond[2].setAttribute("value", date);
            cond[3].setAttribute("value", ""+cap.get(j));
            cond[4].setAttribute("value", ""+cap.get(j+1));
            cond[5].setAttribute("value", checkCode);
            sel.getOption(op).setSelected(true);
        }
        else {
            cond[0].setAttribute("value", "深圳");
            cond[1].setAttribute("value", date);
            cond[2].setAttribute("value", date);
            cond[5].setAttribute("value", checkCode);
            sel.getOption(i).setSelected(true);
        }
    }
    
    public int query(int op,int i,int j){
        int b,pre,end;
        String line,IP,temp;
        WebWindow webwin1=null;
        WebResponse webres1;
        BufferedReader reader;
        HtmlElement btn2;
        try{
            setCond(op,i,j);
            //System.out.println(webClient.getWebWindows().size()+" "+page.getUrl().getPath());
            if (webClient.getWebWindows().size()==1){
                webwin1=webClient.openWindow(null, "query");
                webwin1.setEnclosedPage(page);
            }
            if (webwin1!=null) webClient.setCurrentWindow(webwin1);
            btn2=page.getElementsByTagName("input").get(12);
            btn2.click();
            Thread.sleep(3000);
            IP=((int)(Math.random()*210)+1)+"."
                    +((int)(Math.random()*254)+1)+"."
                    +((int)(Math.random()*254)+1)+"."
                    +((int)(Math.random()*254)+1);
            webreq1.setRequestBody("{\"sIPAddr\":\""+IP+"\"}");
            //System.out.println(page.asText().contains("郭明珠"));
            if (!alerts.isEmpty()){
                temp=alerts.remove(0);
                //System.out.println("alert:"+temp);
                if (temp.contains("验证码")) return 1;
                else if (temp.contains("会员")) return 2;
                else if (temp.contains("日期")) return 3;
            }
            webres1=webClient.loadWebResponse(webreq1);
            reader=new BufferedReader(new InputStreamReader(webres1.getContentAsStream(),"UTF-8"));
            while ((line=reader.readLine())!=null){
                if (!line.contains("div")){
                    System.out.println(line);
                    break;
                }
                //if (line.contains("共找到"))
                //    System.out.println(line.substring(line.indexOf("共找到"), line.indexOf("条符合")));
                pre=line.indexOf("<tr><td>");
                while (true){
                    end=line.indexOf("<\\/a><\\/td><\\/tr>", pre);
                    if (end==-1) break;
                    temp=line.substring(pre, end);
                    if (!(temp.contains("注销")||temp.contains("吊销"))){
                        b=line.indexOf("<\\/td><td>", pre);
                        cout.write((line.substring(pre+8, b).trim()+"\r\n").getBytes());
                        cnt++;
                        if (cnt%1000==0)
                            System.out.println(cnt+" completed");
                    }
                    //else System.out.println(temp);
                    pre=end+17;
                }
            }
            reader.close();
            return 0;
        }
        catch(Exception ex){
            ex.printStackTrace();
            return -1;
        }
    }
    
    public int work(int op){
        int i,j,d,ret;
        DecimalFormat df=new DecimalFormat("00");
        checkCode=Extract();
        try{
            page = webClient.getPage("http://www.szcredit.com.cn/WebPages/Search/SZManyCondition1.aspx");
            //btn2=page.getElementsByTagName("input").get(12);
            Login();
            for (d=day1;d<=day2;d++){
                date=""+year+"-"+df.format(month)+"-"+df.format(d);
                System.out.println(date);
                if (op==2||op==49){
                    for (i=0;i<addr.size();i++){
                        for (j=0;j<cap.size()-1;j++){
                            ret=query(op,i,j);
                            if (ret!=0){
                                if (ret==1){
                                    checkCode=Extract();
                                }
                                else if (ret==2) Login();
                                else if (ret==3) return 3;
                                j--;
                                continue;
                            }
                        }
                    }
                }
                else {
                    for (i=1;i<109;i++){
                        if (i==2||i==49) continue;
                        ret=query(op,i,0);
                        if (ret!=0){
                            if (ret==1){
                                checkCode=Extract();
                            }
                            else if (ret==2) Login();
                            else if (ret==3) return 3;
                            i--;
                            continue;
                        }
                    }
                }
            }
            webClient.closeAllWindows();
            return 0;
        }
        catch (Exception ex){
            webClient.closeAllWindows();
            System.out.println("reconnect");
            return -1;
        }
    }
    
    public void finish(){
        try{
            System.out.println("Total: "+cnt);
            cout.close();
        }
        catch (IOException ioe){
            ioe.printStackTrace();
        }
    }
    
    public static void main(String args[]){
        ReadName rn1=new ReadName();
        int op,ret;
        if (args.length<1) op=2;
        else op=Integer.parseInt(args[0]);
        rn1.initialize(op);
        while (true){
            ret=rn1.work(op);
            if (ret==0) break;
            if (ret==3){
                System.out.println("Date format error!");
                break;
            }
        }
        rn1.finish();
    }
}
