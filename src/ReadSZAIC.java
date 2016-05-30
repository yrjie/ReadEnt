
import java.io.*;
import java.net.URL;
import java.util.regex.*;
import org.apache.poi.xssf.usermodel.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import java.util.List;
import java.util.Scanner;
import java.util.Vector;
import java.util.Date;
import java.util.HashSet;
import java.net.SocketTimeoutException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;

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
import sun.misc.BASE64Encoder;

import com.sun.jna.Library;
import com.sun.jna.Native;

/*interface MUSICAC extends Library  {
 	MUSICAC INSTANCE = (MUSICAC) Native.loadLibrary("MusicAc", MUSICAC.class);   
    int LoadCdsFromFile(String netPath,String name);
    boolean GetVcodeFromFile(int index,String imgPath,byte[] code); 
}*/

public class ReadSZAIC {
    final String head="http://app01.szaic.gov.cn/aiceqmis.webui/WebPages/Search/frmEntDetailPage.aspx?entName=1&entRegNO=1&id=";
    static Scanner cin;
    Pattern p1,p2=Pattern.compile("[\\d]+");
    Matcher m1;
    int cnt;
    
    // xlsx txt
    FileOutputStream[] cout;
    String[] filename={"ID","注册号","名称","法人代表","地址","经营范围","注册资本","类型","注册登记日期", "核准日期","企业状态"};
    static int n_file=11;
    String keyword, initReg, initNum;
    int numD;
    //long beg, end;
    int not,th;
    long nowR=0;
    
    //checkCode
    String gifName;
    int[][][] mat;
    int[][] cc;
    String allC="2468BDFHJLNPpRTVXZ";
    final int w=10,h=12,n=18;
    int[] single={0x0000ff, 0x00ff00, 0xff0000};
    
    //net
    HttpClient client;
    HttpPost post;
    List<NameValuePair> namevaluepair;
    final int Tout=300000,Tfail=30;
    int fail, wait;
    //boolean second;
    String realIP,nowIP;
    FileOutputStream foIP;
    HashSet<String> IPset;
    
    public void initialize(int op){
        int i,j,k;
        Scanner cin3;
        gifName="szaic.gif";
        p1=Pattern.compile("id=([\\w]+)&entName=");
        client = new DefaultHttpClient();
        client.getParams().setParameter(ClientPNames.COOKIE_POLICY,
                            CookiePolicy.BROWSER_COMPATIBILITY);
        client.getParams().setParameter("http.socket.timeout",Tout);
        client.getParams().setParameter("http.connection.timeout",Tout);

        post = new HttpPost("http://app01.szaic.gov.cn/aiceqmis.webui/generalsearch.aspx");
        try {
            //wb=new XSSFWorkbook(new FileInputStream(excel));
            //filebeg[0]=wb.getSheetAt(0).getLastRowNum();
            //filebeg[1]=wb.getSheetAt(1).getLastRowNum();
            cnt=0;
            fail=0;
            wait=0;
            //second=false;
            cout=new FileOutputStream[n_file];
            for (i = 0; i < n_file; i++) {
                cout[i] = new FileOutputStream(i + filename[i] + ".txt");
            }
            if (op==1)
                cin=new Scanner(new FileInputStream("in企业名.txt"),"GBK");
            else if (op==2){
                cin3=new Scanner(System.in);
                System.out.println("注册号数: ");
                th=cin3.nextInt();
                cin3.close();
                not=th;
                cin=new Scanner(new FileInputStream("in注册号.txt"),"GBK");
            }
            else cin=new Scanner(new FileInputStream("inID.txt"),"GBK");
            
            Scanner cin0=new Scanner(new FileInputStream("include\\standard.txt"));
            mat=new int[n][h][w];
            for (i=0;i<n;i++)
                for (j=0;j<h;j++)
                    for (k=0;k<w;k++)
                        mat[i][j][k]=cin0.nextInt();
            cin0.close();
            //updateState();
            Scanner cin1=new Scanner(new FileInputStream("include\\state1.txt"));
            Scanner cin2=new Scanner(new FileInputStream("include\\validate1.txt"));
            namevaluepair=new Vector<NameValuePair>();
            namevaluepair.add(new BasicNameValuePair("__VIEWSTATE",cin1.nextLine()));
            namevaluepair.add(new BasicNameValuePair("__EVENTVALIDATION",cin2.nextLine()));
            namevaluepair.add(new BasicNameValuePair("chkEntname","checked"));
            namevaluepair.add(new BasicNameValuePair("button_Search.x","15"));
            namevaluepair.add(new BasicNameValuePair("button_Search.y","10"));
            namevaluepair.add(new BasicNameValuePair("hfIP","183.62.57.2"));
            namevaluepair.add(new BasicNameValuePair("txtEntName",""));
            namevaluepair.add(new BasicNameValuePair("txtcheckCode",""));
            cin1.close();
            cin2.close();
            foIP=new FileOutputStream("IP.txt");
            nowIP=realIP="";
            keyword="";
            IPset=new HashSet<String>();
            getIP("");
            realIP=nowIP;
            //System.out.println("Real IP: "+realIP);
            //System.out.println(namevaluepair.get(0).getValue());
        }
        catch (IOException ioe){
            System.err.println(ioe.toString());
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
    
    /*public void updateState(){
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
    }*/
    
    public int getCheckCode(){
        int i,j,k,pixel,ccH,ccW,numB,temp;
        BufferedInputStream bis;
        FileOutputStream cout1;
        byte[] data=new byte[1024];
        int[][] dir={{-1,-1},{-1,0},{-1,1},{0,-1},{0,1},{1,-1},{1,0},{1,1}};
        try {
            cout1=new FileOutputStream(gifName);
            HttpPost post1 = new HttpPost("http://app01.szaic.gov.cn/aiceqmis.webui/CheckCode.aspx");
            HttpResponse rsp=client.execute(post1);
            HttpEntity entity = rsp.getEntity();
            if (entity != null) {
                bis=new BufferedInputStream(entity.getContent());
                while (true){
                    numB=bis.read(data);
                    if (numB==-1) break;
                    cout1.write(data, 0, numB);
                }
                bis.close();
            }
            cout1.close();
            File fi=new File(gifName);
            BufferedImage bi=ImageIO.read(fi);
            ccH=bi.getHeight();
            ccW=bi.getWidth();
            cc=new int[ccH][ccW];
            for (i=0;i<ccH;i++)
                for (j=0;j<ccW;j++){
                    pixel=bi.getRGB(j, i);
                    temp=0;
                    for (k=0;k<3;k++)
                        temp+=(pixel&single[k])>>(k*8);
                    if (temp>350){
                        cc[i][j]=0;
                    }
                    else cc[i][j]=1;
                }
            for (i=1;i<ccH-1;i++)
                for (j=1;j<ccW-1;j++){
                    temp=0;
                    for (k=0;k<8;k++)
                        if (cc[i+dir[k][0]][j+dir[k][1]]==1)
                            temp++;
                    if ((cc[i][j]==0&&temp>4)||(cc[i][j]==1&&temp<2)) cc[i][j]=1-cc[i][j];
                }
            return 0;
        }
        catch (Exception ex){
            //System.out.println("read gif error: "+ex.toString());
            ex.printStackTrace();
            client=null;
            client = new DefaultHttpClient();
            client.getParams().setParameter(ClientPNames.COOKIE_POLICY,
                            CookiePolicy.BROWSER_COMPATIBILITY);
            updateFail();
            return -1;
        }        
    }
    
    public String Extract(){
        String ret="";
        int i,x1,y1,j,x2,y2,dx,a,b,ind,readG=-1,x0,temp;
        while (readG<0)
            readG=getCheckCode();
        /*int  netIndex = MUSICAC.INSTANCE.LoadCdsFromFile("include\\yzm.cds","123456");
        byte[] code = new byte[10];
        boolean result = MUSICAC.INSTANCE.GetVcodeFromFile(netIndex, gifName, code);
        int codeLength = 5;*/
        /*try{
            if (result) {
                ByteArrayInputStream is = new ByteArrayInputStream(code);
                StringWriter sw = new StringWriter();
                int len = 0;
                byte[] temp = new byte[1];
                while ((len = is.read(temp)) != -1) {
                    String strRead = new String(temp, 0, len).toUpperCase();
                    sw.write(strRead);
                    sw.flush();
                    if (sw.getBuffer().length() == codeLength) {
                        //System.out.println(sw.toString());
                        ret=sw.toString();
                        break;
                    }
                }
                is.close();
                sw.close();
            }
            return ret;
        }
        catch(IOException ioe){
            System.out.println(ioe.toString());
            return "";
        }*/
        a=b=0;
        for (i=0;i<cc[0].length;i++){
            if (cc[5][i]==1) a++;
            if (cc[6][i]==1) b++;
        }
        if (b>a*2) y1=6;
        else y1=5;
        //System.out.println(y1);
        temp=-5;
        for (i=0;i<5;i++){
            b=0;
            ind=-1;
            x0=temp;
            for (dx=-3;dx<3;dx++){
                x1=x0+10+dx;
                for (j=0;j<n;j++){
                    a=0;
                    for (y2=0;y2<h;y2++)
                        for (x2=0;x2<w&&x1+x2<cc[0].length;x2++){
                            if (cc[y1+y2][x1+x2]==mat[j][y2][x2]) a++;
                        }
                    if (b<a){
                        b=a;
                        ind=j;
                        temp=x1;
                    }
                }
            }
            //System.out.println(b);
            ret=ret+allC.charAt(ind);
        }
        return ret;
    }
    
    public String getID(String viewstate){
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] decoded=null;
        String ret=null;
        int beg=0;
        try{
            decoded=decoder.decodeBuffer(viewstate);
            ret=new String(decoded,"UTF-8");
            beg=ret.indexOf("strRecordID");
        }
        catch(IOException ioe){
            ioe.printStackTrace();
        }
        if (beg>=0) return ret.substring(beg+13, beg+45);
        else return "";
    }
    
    public String getFW(String id, String sta, String eval,String IP)throws Exception{
        HttpPost post1;
        HttpResponse response;
        HttpEntity entity;
        String line,ret,temp;
        int a=0,b=0;
        List<NameValuePair> jyfw;
        jyfw=new Vector<NameValuePair>();
        jyfw.add(new BasicNameValuePair("__EVENTTARGET","lbTag4"));
        jyfw.add(new BasicNameValuePair("__VIEWSTATE",sta));
        jyfw.add(new BasicNameValuePair("__EVENTVALIDATION",eval));
        if (IP!=null){
            jyfw.add(new BasicNameValuePair("hfIP",IP));
            post1 = new HttpPost("http://app01.szaic.gov.cn/aiceqmis.webui/GeneralSearch.aspx");
        }
        else post1 = new HttpPost(head+id);
        post1.setEntity(new UrlEncodedFormEntity(jyfw));
        response = client.execute(post1);
        entity = response.getEntity();
        ret="一般经营项目：";
        if (entity != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                            entity.getContent(),"UTF-8"));
            while ((line=reader.readLine())!=null){
                //System.out.println(line);
                if (line.contains("一般经营项目：")){
                    line=reader.readLine();
                    while (!line.contains("<font ")||!line.contains("</font>")){
                        temp=reader.readLine();
                        if (temp==null) break;
                        line+=temp;
                    }
                    a=line.indexOf(">",line.indexOf("<font "))+1;
                    b=line.indexOf("</font>",a);
                    if (a<0||b<0) break;
                    temp=line.substring(a,b).trim();
                    ret+=temp;
                }
                if (line.contains("许可经营项目：")){
                    line=reader.readLine();
                    while (!line.contains("<font ")||!line.contains("</font>")){
                        temp=reader.readLine();
                        if (temp==null) break;
                        line+=temp;
                    }
                    a=line.indexOf(">",line.indexOf("<font "))+1;
                    b=line.indexOf("</font>",a);
                    if (a<0||b<0) break;
                    temp=line.substring(a,b).trim();
                    if (temp.isEmpty()) continue;
                    ret+=("  许可经营项目："+temp);
                }
            }
            reader.close();
        }
        return ret;
    }
    
    public int getSingleInfo (String id, String name, BufferedReader reader, String sta, String eval,String IP){
        String line,temp;
        String[] data=new String[n_file];
        int num,i,j,b;
        boolean jjxz=false,date=false;
        for (i=0;i<n_file;i++)
            data[i]="";
        data[0]=id;
        try{
            while ((line = reader.readLine()) != null) {
                //System.out.println(line);
                /*if (line.contains("未查询到")||line.contains("注销")
                        ||line.contains("吊销")||line.contains("已变更")
                        ||line.contains("迁移异地")){
                    reader.close();
                    return 0;
                }*/
                if (line.contains("__VIEWSTATE")&&sta==null)
                    sta=line.substring(line.indexOf("value=")+7,line.length()-4);
                if (line.contains("__EVENTVALIDATION")&&eval==null)
                    eval=line.substring(line.indexOf("value=")+7,line.length()-4);
                if (line.contains("<td align=\"right\" height=")||line.contains("<TD align=\"right\" height=")){
                    num=-1;
                    if (line.contains("注册号")&&!line.contains("母")) num=1;
                    if (line.contains("名")&&!line.contains("隶属")&&!line.contains("母")) num=2;
                    if (line.contains("负责人")||line.contains("法定代表人")||line.contains("首席代表")
                            ||line.contains("投资人")||line.contains("合伙人")||line.contains("经营者")) num=3;
                    if (line.contains("地")||line.contains("场所")||line.contains("住所")) num=4;
                    if (line.contains("范围")) num=5;
                    if (line.contains("资金")||line.contains("注册资本")) num=6;
                    if (line.contains("经济性质")){
                        num=7;
                        jjxz=true;
                    }
                    if (!jjxz&&(line.contains("市场主体类型")||line.contains("组成形式")||line.contains("经营方式")||line.contains("企业类型"))) num=7;
                    if (!date&&line.contains("成立日期")||line.contains("注册登记日期")){
                        num=8;
                        date=true;
                    }
                    if (!date&&(line.contains("经营期限")||line.contains("营业期限"))) num=81;
                    if (line.contains("核准日期")) num = 9;
                    if (line.contains("备注")) num = 10;
                    if (line.contains("装配金额")) num=61;
                    if (line.contains("币种")) num=62;
                    if (num<0) continue;
                    line=reader.readLine();
                    while (!line.contains("</span>"))
                        line=line.concat(reader.readLine());
                    j=0;
                    for (i=0;i<line.length();i++){
                        if (line.charAt(i)=='>') j++;
                        if (j==3) break;
                    }
                    for (b=line.length()-1;b>0;b--){
                        if (line.charAt(b)=='>'&&line.charAt(b-1)=='n')
                            break;
                    }
                    temp=line.substring(i+1, b-6).trim();
                    if (num==81){
                        i=line.indexOf("自");
                        if (i==-1) continue;
                        j=line.indexOf("起至");
                        temp=line.substring(i+1, j);
                        //System.out.println(temp+temp.length());
                        num=8;
                    }
                    if (num<n_file) data[num]=temp;
                    if (num==61) data[6]=temp;
                    if (num==62) data[6]=data[6].concat("("+temp+")");
                }
            }
            reader.close();
            if (data[2].isEmpty())
                data[2]=name.trim();
            if (data[5].isEmpty())
                data[5]=getFW(id,sta,eval,IP);
            if (data[10].isEmpty())
                data[10]="成立";
            write2Txt(data);
            fail=0;
            wait=0;
            //second=false;
            return 0;
        }
        catch (Exception ex){
            ex.printStackTrace();
            updateFail();
            return -1;
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
                keyword=keyword.replaceAll("&", "＆");
                keyword=keyword.replaceAll(",", "，");
                keyword=keyword.replaceAll("/", "／");
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
    
    public int work(int op, int ind){
        String checkCode="",IP,id=null,sta=null,eval=null;
        HttpResponse response;
        HttpEntity entity;
        int i;
        URL url=null;
        BufferedReader ureader;
        boolean ok=false;
        List<String> ID;
        String local;
        //getKeyword(op,ok);
        if (ind==0) ind=-2;
        ID=new Vector<String>();
        //restartRouter();
        try{
            //local=InetAddress.getLocalHost().getHostAddress();
            //System.out.println(local);
            while (true){
                if (getIP(realIP)) break;
                Thread.sleep(10000);
            }
            while (true){
                if (ind==-1) {
                    ind = 0;
                } else {
                    if (ind==-2) checkCode=Extract();
                    if (getKeyword(op,ok) < 0) 
                        break;
                }
                //System.out.println(checkCode);
                ok=false;
                ID.clear();
                IP=((int)(Math.random()*210)+1)+"."
                        +((int)(Math.random()*254)+1)+"."
                        +((int)(Math.random()*254)+1)+"."
                        +((int)(Math.random()*254)+1);
                //System.out.println(checkCode+" "+IP);
                namevaluepair.set(5, new BasicNameValuePair("hfIP",IP));
                if (op==1)
                    namevaluepair.set(6, new BasicNameValuePair("txtEntName",keyword));
                else namevaluepair.set(6, new BasicNameValuePair("txtEntRegNO",keyword));
                namevaluepair.set(7, new BasicNameValuePair("txtcheckCode",checkCode));
                post.setEntity(new UrlEncodedFormEntity(namevaluepair, "UTF-8"));
                response = client.execute(post);
                entity = response.getEntity();
                if (entity != null) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                                    entity.getContent(),"UTF-8"));
                    String line=null;
                    while ((line=reader.readLine())!=null){
                        //System.out.println(line);
                        if (line.contains("__VIEWSTATE")){
                            sta=line.substring(line.indexOf("value=")+7,line.length()-4);
                            id=getID(sta);
                        }
                        if (line.contains("__EVENTVALIDATION"))
                            eval=line.substring(line.indexOf("value=")+7,line.length()-4);
                        if (line.contains("信息查询单")){
                            ind=getSingleInfo(id,keyword,reader,sta,eval,IP);
                            ok=true;
                            //ID.add(id);
                            break;
                        }
                        if (line.contains("请您录入")){
                            ind=-2;
                            break;
                        }
                        if (line.contains("未查询到")||line.contains("暂不能提供查询"))
                            break;
                        m1=p1.matcher(line);
                        if (m1.find()){
                            ok=true;
                            for (i=0;i<3;i++)
                                line=reader.readLine();
                            /*if (line.contains("注销")||line.contains("吊销")
                                    ||line.contains("已变更")||line.contains("迁移异地")){
                                continue;
                            }*/
                            id=m1.group(1);
                            ID.add(id);
                            /*while (ind<0){
                                url=null;
                                url=new URL(head+id);
                                ureader=new BufferedReader(new InputStreamReader(url.openStream(),"UTF-8"));                   
                                ind=getSingleInfo(id, keyword,ureader,sta,eval);
                            }*/
                        }
                    }
                    reader.close();
                    for (i=0;i<ID.size();i++){
                        ind=-1;
                        while (ind<0){
                            while (true){
                                if (getIP(realIP)) break;
                                Thread.sleep(10000);
                            }
                            url=null;
                            url=new URL(head+ID.get(i));
                            ureader=new BufferedReader(new InputStreamReader(url.openStream(),"UTF-8"));
                            ind=getSingleInfo(ID.get(i), keyword,ureader,null,null,null);
                        }
                    }
                }
            }
            return 0;
        }
        catch (Exception ex){
            String exS=ex.toString();
            if (exS.contains("500 for URL")){
                System.out.println("failed at input: "+keyword);
                return -2;
            }
            client=null;
            client = new DefaultHttpClient();
            client.getParams().setParameter(ClientPNames.COOKIE_POLICY,
                            CookiePolicy.BROWSER_COMPATIBILITY);
            //ex.printStackTrace();
            updateFail();
            return -1;
        }
    }
    
    /*public void restartRouter(){
        String pass,line;
        HttpPost restart;
        HttpResponse response;
        HttpEntity entity;
        BufferedReader ureader;
        BASE64Encoder encoder = new BASE64Encoder();
        FileOutputStream fo;
        try{
            fo=new FileOutputStream("router.html");
            System.out.println("Change IP ...");
            restart=new HttpPost("http://192.168.1.1/userRpm/SysRebootRpm.htm?Reboot=%D6%D8%C6%F4%C2%B7%D3%C9%C6%F7");
            //restart=new HttpPost("http://localhost:809/htprotected/");
            pass="Basic "+encoder.encodeBuffer("admin1:admin".getBytes()).trim();
            //System.out.println(pass);
            restart.addHeader("Authorization", pass);
            response=client.execute(restart);
            entity=response.getEntity();
            if (entity!=null){
                ureader=new BufferedReader(new InputStreamReader(
                                    entity.getContent(),"GBK"));
                while ((line=ureader.readLine())!=null){
                    //System.out.println(line);
                    fo.write((line+"\r\n").getBytes());
                    if (line.contains("Authorization Required")){
                        System.out.println("failed");
                        break;
                    }
                }
                ureader.close();
            }
        }
        catch (Exception ex){
            System.out.println(ex.toString());
        }
    }*/
    
    public void write2Txt(String[] data){
        int i;
        try{
            for (i=0;i<n_file;i++){
                cout[i].write((data[i]+"\r\n").getBytes());
            }
        }
        catch (IOException ioe){
            System.out.println(ioe.toString());
        }
    }

    public int workID(int ind) {
        //URL url=null;
        //BufferedReader ureader;
        HttpPost postID;
        HttpResponse response;
        HttpEntity entity;
        try {
            while (true){
                if (getIP(realIP)) break;
                Thread.sleep(10000);
            }
            while (true) {
                if (ind ==-1) {
                    ind = 0;
                } else if (getKeyword(3,false) < 0) {
                    break;
                }
                //url=new URL(head+keyword);
                //ureader=new BufferedReader(new InputStreamReader(url.openStream(),"UTF-8"));
                postID = new HttpPost(head+keyword);
                response = client.execute(postID);
                entity = response.getEntity();
                if (entity != null) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                                    entity.getContent(),"UTF-8"));
                    ind=getSingleInfo(keyword,"",reader,null,null,null);
                }
            }
            return 0;
        } catch (Exception ex) {
            //ex.printStackTrace();
            String exS=ex.toString();
            if (exS.contains("500 for URL")){
                System.out.println("failed at id: "+keyword);
                return -2;
            }
            System.out.println(exS);
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
    
    public void finish(){
        int i,j;
        try {
            //FileOutputStream fileOut = new FileOutputStream("data.xlsx");
            //wb.write(fileOut);
            //fileOut.close();            
            cin.close();
            for (i=0;i<n_file;i++)
                cout[i].close();
            foIP.close();
            System.out.println("finished");
            System.out.println("Total: " + cnt + " completed");
        }
        catch (IOException ioe){
            ioe.printStackTrace();
        }
    }
    
    public static void main(String args[]){
        int op=2,ret;
        ReadSZAIC re=new ReadSZAIC();
        if (args.length>0)
            op=Integer.parseInt(args[0]);
        re.initialize(op);
        ret = 0;
        while (true) {
            if (op == 1 || op == 2) {
                ret = re.work(op,ret);
            } else {
                ret = re.workID(ret);
            }
            //if (ret==-1) re.updateFail();
            if (ret == 0) {
                break;
            }
        }
        re.finish();
    }
}
