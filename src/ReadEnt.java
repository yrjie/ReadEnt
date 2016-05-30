
import java.io.*;
import java.util.regex.*;
import java.util.Scanner;
import HTTPClient.*;

public class ReadEnt {
    static String[] outfile;
    static FileOutputStream[] cout;
    static Scanner cin;
    static NVPair form_data1[] = new NVPair[2];
    static NVPair form_data2[] = new NVPair[5];
    static NVPair form_data3[] = new NVPair[9];
    static Pattern p1;
    public int work(int ind,int beg){
        int i,cnt,a=25,b;
        Matcher m1;        
        boolean flag,found,fixed;
        String line=null,now;
        HTTPConnection con=null;
        HTTPResponse rsp;
        InputStream stream;
        BufferedReader reader;
        try{
            con = new HTTPConnection("www.szcredit.com.cn");
            rsp = con.Post("/busi/queryKeyword.TJ?method=queryFirst", form_data1);
            if (rsp.getStatusCode() >= 300)
            {
                System.err.println("Received Error: "+rsp.getReasonLine());
                System.err.println(rsp.getText());
            }
            else stream = rsp.getInputStream();
            rsp = con.Post("/busi/queryKeyword.TJ?method=querySecond", form_data2);
            if (rsp.getStatusCode() >= 300)
            {
                System.err.println("Received Error: "+rsp.getReasonLine());
                System.err.println(rsp.getText());
            }
            else stream = rsp.getInputStream();
            
            found=false;
            if (ind!=1) fixed=false;
            else fixed=true;
            while (cin.hasNext()){
                if (fixed){
                    now=cin.nextLine();
                    m1=p1.matcher(now);
                    if (!m1.find()) continue;
                    if (found){
                        found=false;
                        continue;
                    }                    
                    form_data3[0]=new NVPair("entID", m1.group(1));
                    form_data3[1] = new NVPair("entStatusCode", m1.group(2));
                }
                fixed=true;
                found=true;
                if (ind<beg){
                    ind++;
                    continue;
                }
                rsp = con.Post("/busi/queryKeyword.TJ?method=queryThree", form_data3);
                if (rsp.getStatusCode() >= 300)
                {
                    System.err.println("Received Error: "+rsp.getReasonLine());
                    System.err.println(rsp.getText());
                }
                else{
                    stream = rsp.getInputStream();
                    reader=new BufferedReader(new InputStreamReader(stream,"UTF-8"));
                    flag=false;
                    cnt=0;
                    while ((line=reader.readLine())!=null){
                        if (!flag){
                            if (line.contains("background: #ffffff; padding-left:"))
                                    flag=true;
                            continue;
                        }                        
                        b=line.length();
                        if (cnt==1){
                            for (i=a;i<line.length();i++){
                                if (line.charAt(i)=='<'){
                                    b=i-2;
                                    break;
                                }
                            }
                        }
                        cout[cnt].write((line.substring(a, b).trim()+"\n").getBytes());
                        flag=false;
                        cnt++;
                        if (cnt==3) break;
                    }
                    ind++;
                }
            }
            return 0;
        }
        catch (IOException ioe)
        {
            System.err.println(ioe.toString());            
            return ind;
        }
        catch (ParseException pe)
        {
            System.err.println("Error parsing Content-Type: " + pe.toString());            
            return ind;
        }
        catch (ModuleException me)
        {
            System.err.println("Error handling request: " + me.getMessage());            
            return ind;
        }
    }
    
    public static void main(String args[]){
        int i,ret=1,beg=1;
        ReadEnt re=new ReadEnt();
        String[] temp=args[0].split("\\.");
        if (args.length==2)
            beg=Integer.parseInt(args[1]);
        p1=Pattern.compile("showItem\\(&#39;([\\w]+)&#39;,&#39;[\\w]+&#39;,&#39;[-\\w]+&#39;,&#39;([-\\w]+)&#39;");
        form_data1[0] = new NVPair("keyword", "%E6%B7%B1%E5%9C%B3");
        form_data1[1] = new NVPair("place","440300");

        form_data2[0] = new NVPair("entID", "440301001012010060900411");
        form_data2[1] = new NVPair("entTypeCode","1100");
        form_data2[2] = new NVPair("tabPro","-1");
        form_data2[3] = new NVPair("entName","%E6%B7%B1%E5%9C%B3%E5%B8%82%E4%B8%87%E5%85%B4%E9%80%9A%E7%A7%91%E6%8A%80%E6%9C%89%E9%99%90%E5%85%AC%E5%8F%B8");
        form_data2[4] = new NVPair("entStatusCode","0");

        form_data3[0] = new NVPair("entID", "440301001012010060900411");
        form_data3[1] = new NVPair("entStatusCode", "0");
        form_data3[2] = new NVPair("tabPro","-1");
        form_data3[3] = new NVPair("allMoney","0");
        form_data3[4] = new NVPair("maxMoney","0");
        form_data3[5] = new NVPair("userType","0");
        form_data3[6] = new NVPair("item","01002");
        form_data3[7] = new NVPair("item","01003");
        form_data3[8] = new NVPair("item","01004");
        try {
            outfile=new String[3];
            cout=new FileOutputStream[3];
            for (i=0;i<3;i++){
                outfile[i]=temp[0]+"_out"+(i+1)+".txt";
                cout[i]=new FileOutputStream(outfile[i]);
            }
            cin=new Scanner(new FileInputStream(args[0]),"UTF-8");
            while (true){
                ret=re.work(ret,beg);
                if (ret==0) break;
                System.out.println(Integer.toString(ret));
            }
            for (i=0;i<3;i++)
                cout[i].close();
            System.out.println("finished");
            cin.close();
            System.exit(0);
        }
        catch (IOException ioe){
            System.err.println(ioe.toString());
        }
    }
}
