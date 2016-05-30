
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

public class ReadAIC {    
    final String head="http://app01.szaic.gov.cn/aiceqmis.webui/WebPages/Search/frmEntDetailPage.aspx?entName=1&entRegNO=1&id=";    
    static Scanner cin;
    Pattern p1;
    Matcher m1;
    
    // xlsx txt
    XSSFWorkbook wb;
    String excel;
    int[] filebeg;    
    FileOutputStream[][] cout;
    String[] filename={"ID","注册号","名称","法人代表","地址","经营范围","注册资本","类型","注册登记日期"};
    static int n_file=9;
    
    //checkCode
    int[][][] mat;
    int[][] cc;
    String allC="2468BDFHJLNPpRTVXZ",gifName;
    final int w=10,h=12,n=18;
    int[] zero = {-3355444,-6710887,-1};
    
    //net
    HttpClient client;
    HttpPost post;
    List<NameValuePair> namevaluepair;
    
    public void initialize(String infile){
        int i,j,k;
        String prefix=infile.split("\\.")[0];
        gifName=prefix+".gif";
        p1=Pattern.compile("id=([\\w]+)&entName=");
        excel="data.xlsx";
        filebeg=new int[2];
        client = new DefaultHttpClient();
        client.getParams().setParameter(ClientPNames.COOKIE_POLICY,
                            CookiePolicy.BROWSER_COMPATIBILITY);
        post = new HttpPost("http://app01.szaic.gov.cn/aiceqmis.webui/generalsearch.aspx");
        try {
            //wb=new XSSFWorkbook(new FileInputStream(excel));
            //filebeg[0]=wb.getSheetAt(0).getLastRowNum();
            //filebeg[1]=wb.getSheetAt(1).getLastRowNum();
            cout=new FileOutputStream[2][n_file];
            for (i=0;i<2;i++)
                for (j=0;j<n_file;j++){
                    if (i==0){
                        if (j==7) continue;
                        cout[i][j]=new FileOutputStream("个体\\"+prefix+"_"+j+filename[j]+".txt");
                    }                        
                    else cout[i][j]=new FileOutputStream("企业\\"+prefix+"_"+j+filename[j]+".txt");
                }            
            cin=new Scanner(new FileInputStream(infile),"GBK");
            
            Scanner cin0=new Scanner(new FileInputStream("include\\standard.txt"));
            mat=new int[n][h][w];
            for (i=0;i<n;i++)
                for (j=0;j<h;j++)
                    for (k=0;k<w;k++)
                        mat[i][j][k]=cin0.nextInt();
            cin0.close();
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
            //System.out.println(namevaluepair.get(0).getValue());
        }
        catch (IOException ioe){
            System.err.println(ioe.toString());
        }
    }

    public int getCheckCode(){
        int i,j,pixel,ccH,ccW;
        BufferedInputStream bis;
        FileOutputStream cout1;
        byte[] data=new byte[1024];
        try {
            cout1=new FileOutputStream(gifName);
            HttpPost post1 = new HttpPost("http://app01.szaic.gov.cn/aiceqmis.webui/CheckCode.aspx");
            HttpResponse rsp=client.execute(post1);
            HttpEntity entity = rsp.getEntity();
            if (entity != null) {
                bis=new BufferedInputStream(entity.getContent());
                while (bis.read(data)!=-1){
                    cout1.write(data);
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
                    if (pixel==zero[0]||pixel==zero[1]||pixel==zero[2]) cc[i][j]=0;
                    else cc[i][j]=1;
                }
            return 0;
        }
        catch (Exception ex){
            //System.out.println("read gif error: "+ioe.toString());
            client=null;
            client = new DefaultHttpClient();
            client.getParams().setParameter(ClientPNames.COOKIE_POLICY,
                            CookiePolicy.BROWSER_COMPATIBILITY);
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
            if (cc[5][i]==1) a++;
            if (cc[6][i]==1) b++;
        }
        if (b>a*2) y1=6;
        else y1=5;
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
    
    public int getSingleInfo (String id, String name, BufferedReader reader){
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
                if (line.contains("未查询到")||line.contains("注销")
                        ||line.contains("吊销")||line.contains("已变更")
                        ||line.contains("迁移异地")){
                    reader.close();
                    return 0;
                }
                if (line.contains("<td align=\"right\" height=")||line.contains("<TD align=\"right\" height=")){
                    num=-1;
                    if (line.contains("注册号")&&!line.contains("母")) num=1;
                    if (line.contains("名")&&!line.contains("隶属")&&!line.contains("母")) num=2;
                    if (line.contains("负责人")||line.contains("法定代表人")||line.contains("首席代表")
                            ||line.contains("投资人")||line.contains("执行合伙人")) num=3;
                    if (line.contains("地")||line.contains("场所")||line.contains("住所")) num=4;
                    if (line.contains("范围")) num=5;
                    if (line.contains("资金")||line.contains("注册资本")) num=6;
                    if (line.contains("经济性质")){
                        num=7;
                        jjxz=true;
                    }
                    if (!jjxz&&(line.contains("市场主体类型")||line.contains("组成形式")||line.contains("经营方式"))) num=7;
                    if (!date&&line.contains("成立日期")||line.contains("注册登记日期")||line.contains("核准日期")){
                        num=8;
                        date=true;
                    }
                    if (!date&&(line.contains("经营期限")||line.contains("营业期限"))) num=81;
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
            if (data[2].equals(""))
                data[2]=name.trim();
            write2Txt(data);
            return 0;
        }
        catch (Exception ex){
            client=null;
            client = new DefaultHttpClient();
            client.getParams().setParameter(ClientPNames.COOKIE_POLICY,
                            CookiePolicy.BROWSER_COMPATIBILITY);
            ex.printStackTrace();            
            return -1;
        }
    }
    
    public void work(int beg){
        String checkCode,IP,temp,id=null,name=null,last="";
        HttpResponse response;
        HttpEntity entity;
        int i,ind=0,ret=0;
        URL url=null;
        BufferedReader ureader;
        try{
            while (true){
                if (ret>=0){
                    if (!cin.hasNext()) break;
                    else {
                        name=cin.nextLine();
                        name=name.replaceAll("&", "＆");
                        name=name.replaceAll(",", "，");
                        name=name.replaceAll("/", "／");
                        ind++;
                        if (name.equals(last)||ind<beg) continue;
                    }
                }
                IP=((int)(Math.random()*210)+1)+"."
                        +((int)(Math.random()*254)+1)+"."
                        +((int)(Math.random()*254)+1)+"."
                        +((int)(Math.random()*254)+1);
                checkCode=Extract();
                //System.out.println(checkCode+" "+IP);
                namevaluepair.set(5, new BasicNameValuePair("hfIP",IP));
                namevaluepair.set(6, new BasicNameValuePair("txtEntName",name));
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
                            temp=line.substring(line.indexOf("value=")+7,line.length()-4);
                            id=getID(temp);
                        }
                        if (line.contains("深圳市市场监督管理局注册登记信息查询单")){
                            ret=getSingleInfo(id,name,reader);
                            break;
                        }
                        if (line.contains("请您录入")){
                            reader.close();
                            ret=-2;
                            break;
                        }
                        if (line.contains("未查询到")||line.contains("暂不能提供查询")){
                            ret=0;
                            break;
                        }
                        m1=p1.matcher(line);
                        if (m1.find()){
                            for (i=0;i<3;i++)
                                line=reader.readLine();
                            if (line.contains("注销")||line.contains("吊销")
                                    ||line.contains("已变更")||line.contains("迁移异地")){
                                ret=0;
                                continue;
                            }
                            ret=-1;
                            id=m1.group(1);
                            while (ret<0){
                                url=null;
                                url=new URL(head+id);
                                ureader=new BufferedReader(new InputStreamReader(url.openStream(),"UTF-8"));                            
                                ret=getSingleInfo(id,name,ureader);
                            }
                        }
                    }
                    last=name;
                    if (ret>=0&&ind%1000==0) System.out.println(ind+" completed");
                }
            }
        }
        catch (IOException ioe){
            ioe.printStackTrace();
        }
    }
    
    public void write2Txt(String[] data){
        int op,i;
        if (data[7].contains("个体"))
            op=0;
        else op=1;
        try{
            for (i=0;i<n_file;i++){
                if (op==0&&i==7) continue;
                cout[op][i].write((data[i]+"\r\n").getBytes());
            }
        }
        //XSSFRow row;
        //filebeg[op]++;
        //row=wb.getSheetAt(op).createRow(filebeg[op]);
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
            cin.close();
            for (i=0;i<2;i++)
                for (j=0;j<8;j++){
                    if (i==0&&j==7) continue;
                    cout[i][j].close();
                }
            System.out.println("finished");
            System.exit(0);
        }
        catch (IOException ioe){
            ioe.printStackTrace();
        }
    }
    
    public static void main(String args[]){
        int beg=1;
        ReadAIC re=new ReadAIC();

        if (args.length>0) re.initialize(args[0]);
        else re.initialize("in1.txt");
        if (args.length==2) beg=Integer.parseInt(args[1]);
        re.work(beg);
            /*if (temp>=0){
                ret=temp;
                if (!cin.hasNext()) break;
                name=cin.nextLine();
                //System.out.println(name);
                if (temp%1000==0) re.update(temp);
            }
            else if (temp==-1) System.out.println(ret+1);
            temp=re.getSingleInfo(name,ret);*/
        re.finish();
    }
}
