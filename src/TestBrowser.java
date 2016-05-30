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
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.util.NameValuePair;

import java.awt.image.BufferedImage;
import java.net.URLEncoder;
import javax.imageio.ImageIO;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

public class TestBrowser {
    
    WebClient webClient;
    CookieManager ckm;
    Log log;
    
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
            Page gif=webClient.getPage("http://www.szcredit.com.cn/WebPages/Member/CheckCode.aspx");
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
            ex.printStackTrace();
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
    
    public void genLog(String info){
        log.info(info);
    }
    
    public void initialize(){
        int i,j,k;
        ckm=new CookieManager();
        webClient = new WebClient(BrowserVersion.FIREFOX_3_6);
        webClient.setCssEnabled(false);
        webClient.setCookieManager(ckm);
        //System.out.println(webClient.getCache().getSize());
        log=LogFactory.getLog(TestBrowser.class);
        //webClient.setRedirectEnabled(true);
        //webClient.setIncorrectnessListener(new MyIncorrectnessListener());
        //System.getProperties().put("org.apache.commons.logging.simplelog.defaultlog", "error");
        /*org.apache.commons.logging.Log=org.apache.commons.logging.impl.SimpleLog;
        org.apache.commons.logging.simplelog.showdatetime=true;
        org.apache.commons.logging.simplelog.log.org.apache.http=DEBUG;
        org.apache.commons.logging.simplelog.log.org.apache.http.wire=ERROR;*/
        try{
            Scanner cin0=new Scanner(new FileInputStream("include\\standard.txt"));
            mat=new int[n][h][w];
            for (i=0;i<n;i++)
                for (j=0;j<h;j++)
                    for (k=0;k<w;k++)
                        mat[i][j][k]=cin0.nextInt();
        }
        catch (IOException ioe){
            System.out.println(ioe.toString());
        }
    }
    
    public void homePage() throws Exception {
        String line,req1,req2,checkCode,cond,addr,entType,cap1,cap2,date1,date2;
        HtmlPage page,p1;
        UnexpectedPage p2,p3;
        List alerts=new ArrayList();
        WebRequest checkCond,webreq1;
        List<NameValuePair> param;
        checkCode=Extract();
        System.out.println(checkCode);
        webClient.setAlertHandler(new CollectingAlertHandler(alerts));
        webreq1=new WebRequest(new URL("http://www.szcredit.com.cn/WebPages/Search/ajaxservice.svc/GetEntlistByManyCondition"),HttpMethod.POST);
        page=webClient.getPage("http://127.0.0.1:809/");
        //page = webClient.getPage("http://www.szcredit.com.cn/WebPages/Search/SZManyCondition1.aspx");
        param=new ArrayList<NameValuePair>();
        //param.add(new NameValuePair("sPassword","128265"));
        
        checkCond=new WebRequest(new URL("http://www.szcredit.com.cn/WebPages/Search/ajaxservice.svc/CheckCondition"),HttpMethod.POST);
        checkCond.setAdditionalHeader("X-Requested-With", "XMLHttpRequest");
        checkCond.setAdditionalHeader("Referer", "http://www.szcredit.com.cn/WebPages/Search/SZManyConditionList.aspx");
        checkCond.setAdditionalHeader("Content-Type", "application/json; charset=utf-8");
        addr=(URLEncoder.encode("深圳", "UTF-8").toLowerCase().replace("%", "][0x")+"]").substring(1);
        entType="";
        date1="";
        date2="";
        cap1="";
        cap2="";
        cond="{\"sNameKey\":\"\",\"sLerepName\":\"\",\"sAddr\":\""+ addr
                +"\",\"sEntType\":\""+ entType
                +"\",\"sIndClass\":\"\",\"sRegDate1\":\""+ date1
                +"\",\"sRegDate2\":\""+ date2
                +"\",\"sRegCap1\":\""+cap1
                +"\",\"sRegCap2\":\""+cap2
                +"\",\"sCode\":\""+ checkCode+"\"}";
        checkCond.setRequestBody(cond);
        
        webreq1.setAdditionalHeader("X-Requested-With", "XMLHttpRequest");
        webreq1.setAdditionalHeader("Referer", "http://www.szcredit.com.cn/WebPages/Search/SZManyConditionList.aspx");
        webreq1.setAdditionalHeader("Content-Type", "application/json; charset=utf-8");
        //webreq1.setRequestParameters(param);
        webreq1.setRequestBody("{\"sIPAddr\":\"137.132.250.123\"}");
        //p1=(HtmlPage)webClient.getPage(webreq1);
        //page = webClient.getPage("http://127.0.0.1:809/");
        req1="SZCredit.ajaxservice.CheckLogin(\'10000929\', \'128265\', OnLoginSucceed, OnLoginFailed);";
        req2="SZCredit.ajaxservice.CheckCondition(\'abc\', \'\', \'\', \'\', \'\', \'\', \'\',\'\',\'\',\'"
                +checkCode+"\', OnConditionSucceed, OnConditionFailed);";
        //req1="CheckCondition()";
        //req1="OnConditionSucceed(\'312\')";
        req1="write1()";
        //req1="this.location=\'http://127.0.0.1:809/\'";
        //req1="alert(\'123\')";
        //p1=(HtmlPage)page.executeJavaScript(req1).getNewPage();
        page.executeJavaScript(req1);
        System.out.println(alerts.get(0));
        //genLog("Login completed");
        //p1=(HtmlPage)page.executeJavaScript(req2).getNewPage();
        //p1=page.executeJavaScript("this.location=\'http://www.szcredit.com.cn/WebPages/Search/SZManyConditionList.aspx\'").getNewPage();
        //p1=page.refresh();
        //p1=webClient.getPage("http://www.szcredit.com.cn/WebPages/Search/SZManyConditionList.aspx");
        //page=(HtmlPage)p1;
        DomNodeList<HtmlElement> list;
        HtmlElement btn1=page.getElementById("btnLogin"),btn2;
        System.out.println(webClient.getCache().getSize());
        genLog("Before login");
        //Thread.sleep(5000);
        //page.getElementById("popup").click();
        //page=page.getElementById("popup").click();
        page.getElementById("txtUserName").setAttribute("value", "10000929");
        page.getElementById("txtPassword").setAttribute("value", "128265");
        btn1.click();
        genLog("Login completed");
        list=page.getElementsByTagName("input");
        btn2=list.get(12);
        //page.getElementById("txtMC").setAttribute("value", "abc");
        page.getElementById("txtDiZhi").setAttribute("value", "深圳");
        page.getElementById("txtCheckCode1").setAttribute("value", checkCode);
        //ckm.clearCookies();
        //p1=(HtmlPage)page.executeJavaScript("CheckCondition();").getNewPage();
        //p1=page.refresh();
        System.out.println(webClient.getCache().getSize());
        //webClient.getCache().clear();
        p1=btn2.click();
        //p1=webClient.getPage(checkCond);
        genLog("Request completed");
        //p1=(HtmlPage)page.executeJavaScript("GetListByCondition()").getNewPage();
        //p1.refresh();
        //page.refresh();
        Thread.sleep(10000);
        p2=webClient.getPage(webreq1);
        //System.out.println(p2.asText());
        //System.out.println(webClient.getWebWindows().get(0).getEnclosedPage().getUrl().getPath());
        //System.out.println(webClient.getCache().getCachedObject(page.getWebResponse().getWebRequest()));
        //System.out.println(p1.getElementById("div_data").toString());
        BufferedReader reader=new BufferedReader(new InputStreamReader(p2.getWebResponse().getContentAsStream(),"UTF-8"));
        while ((line=reader.readLine())!=null){
            System.out.println(line);
            //if (line.contains("VIEWSTATE"))
            //    genLog("At viewstate");
            //if (line.contains("当前位置"))
            //    genLog("At current");
            //if (line.contains("橡胶制品业"))
            //    genLog("At xiangjiao");
            //if (line.contains("return CheckCondition()"))
            //    genLog("At checkCondition");
        }
        reader.close();
        webClient.closeAllWindows();
    }
    
    public static void main(String argsp[]) throws Exception{
        TestBrowser t1=new TestBrowser();
        t1.initialize();
        t1.homePage();
    }
}
