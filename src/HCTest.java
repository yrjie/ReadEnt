
import HTTPClient.*;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

public class HCTest{
    
    public void getCheckCode(){
        URL ccURL=null;
        java.net.HttpURLConnection con;
        String line;
        BufferedInputStream bis;
        FileOutputStream cout;
        byte[] data=new byte[1024];
        int size;
        try {
            ccURL=new URL("http://app01.szaic.gov.cn/aiceqmis.webui/CheckCode.aspx");
            //con=(java.net.HttpURLConnection)ccURL.openConnection();
            //con.connect();
            bis=new BufferedInputStream(ccURL.openStream());
            cout=new FileOutputStream("a.gif");
            while (bis.read(data)!=-1){
                //System.out.println(line);
                //line.replaceAll("\n", "\r\n");
                cout.write(data);
            }
        }
        catch (IOException ioe){
            ioe.printStackTrace();
        }
    }
    
    public void work(){
        HTTPConnection con=null;
        HTTPResponse rsp=null;
        InputStream stream;
        String line=null,temp;
        BufferedReader reader;
        Scanner state;
        try{
            NVPair[] headers=new NVPair[7];
            headers[0]=new NVPair("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            headers[1]=new NVPair("Accept-Charset", "GBK,utf-8;q=0.7,*;q=0.3");
            headers[2]=new NVPair("Accept-Encoding", "gzip,deflate,sdch");
            headers[3]=new NVPair("Accept-Language", "zh-CN,zh;q=0.8");
            headers[4]=new NVPair("Connection", "keep-alive");
            headers[5]=new NVPair("Host", "www.szcredit.com.cn");
            headers[6]=new NVPair("User-Agent", "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/535.7 (KHTML, like Gecko) Chrome/16.0.912.63 Safari/535.7");
            
            /*headers[7]=new NVPair("Cache-Control", "private");
            headers[8]=new NVPair("Content-Length","142043");
            headers[9]=new NVPair("Server","Microsoft-IIS/6.0");
            headers[10]=new NVPair("X-AspNet-Version","4.0.30319");
            headers[11]=new NVPair("X-Powered-By","ASP.NET");
            headers[12]=new NVPair("Content-Type","text/html; charset=utf-8");*/
            
            NVPair[] form_data1 = new NVPair[5];
            //con = new HTTPConnection("app01.szaic.gov.cn");
            //form_data1[2] = new NVPair("hfIP","125.216.249.155");
            //form_data1[3] = new NVPair("__EVENTTARGET","lbTag0");
            //form_data1[4] = new NVPair("txtEntName","abc");
            
            con = new HTTPConnection("www.szcredit.com.cn");
            form_data1[0] = new NVPair("lstEntType","_Pvt_0");
            form_data1[1] = new NVPair("txtDiZhi","深圳abc");
            form_data1[2] = new NVPair("btnQuery"," 开始查询 ");
            //form_data1[2] = new NVPair("btnReset","");
            state=new Scanner(new FileInputStream("D:\\studio\\szcredit\\formState\\state1.txt"));
            temp=state.nextLine();
            form_data1[3] = new NVPair("__VIEWSTATE",temp);
            state.close();
            state=new Scanner(new FileInputStream("D:\\studio\\szcredit\\formState\\validate1.txt"));
            temp=state.nextLine();
            form_data1[4] = new NVPair("__EVENTVALIDATION",temp);
            state.close();

            //rsp = con.Post("/aiceqmis.webui/generalsearch.aspx", form_data1);
            rsp = con.Post("/WebPages/Search/SZManyCondition.aspx", form_data1,headers);
            if (rsp.getStatusCode() >= 300)
            {
                System.err.println("Received Error: "+rsp.getReasonLine());
                System.err.println(rsp.getText());
            }            
            else{
                stream = rsp.getInputStream();
                reader=new BufferedReader(new InputStreamReader(stream,"UTF-8"));
                while ((line=reader.readLine())!=null)
                        System.out.println(line);
            }
        }
        catch (IOException ioe)
        {
            System.err.println(ioe.toString());
        }
        catch (ParseException pe)
        {
            System.err.println("Error parsing Content-Type: " + pe.toString());
        }
        catch (ModuleException me)
        {
            System.err.println("Error handling request: " + me.getMessage());
        }
    }
    public static void main(String args[]){
        HCTest hct1=new HCTest();
        //hct1.getCheckCode();
        hct1.work();
    }
}
