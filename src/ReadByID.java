
import java.net.*;
import java.util.Scanner;
import java.io.*;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Administrator
 */
public class ReadByID {
    final String head="http://app01.szaic.gov.cn/aiceqmis.webui/WebPages/Search/frmEntDetailPage.aspx?entName=1&entRegNO=1&id=";
    Scanner cin;
    FileOutputStream[][] cout;
    String id;
    String[] filename={"ID","注册号","名称","法人代表","地址","经营范围","注册资本","类型","注册登记日期"};
    static int n_file;
    
    public void initialize(String infile){
        int i,j;
        String prefix=infile.split("\\.")[0];
        try{
            cin=new Scanner(new FileInputStream(infile),"GB2312");
            cout=new FileOutputStream[2][n_file];
            for (i=0;i<2;i++)
                for (j=0;j<n_file;j++){
                    if (i==0){
                        if (j==7) continue;
                        cout[i][j]=new FileOutputStream("个体\\"+prefix+"_"+j+filename[j]+".txt");
                    }                        
                    else cout[i][j]=new FileOutputStream("企业\\"+prefix+"_"+j+filename[j]+".txt");
                }            
        }
        catch (IOException ioe){
            System.err.println(ioe.toString());
        }
    }
    
    public int work(int beg,int ind){
        String line,temp;
        String[] data=new String[n_file];
        int num,i,j,b;
        URL url=null;
        BufferedReader reader;
        boolean flag,jjxz,date;
        try{
            if (ind!=0) flag=false;
            else flag=true;
            while (cin.hasNext()){
                if (flag){
                    id=cin.nextLine();
                    ind++;
                }                
                if (ind<beg) continue;
                flag=true;
                for (i=0;i<n_file;i++)
                    data[i]="";
                data[0]=id;
                url=new URL(head+id);
                reader=new BufferedReader(new InputStreamReader(url.openStream(),"UTF-8"));
                jjxz=false;
                date=(n_file==8);
                while ((line = reader.readLine()) != null) {
                    //System.out.println(line);
                    if (line.contains("未查询到")||line.contains("注销")
                            ||line.contains("吊销")||line.contains("已变更")
                            ||line.contains("迁移异地")){
                        reader.close();
                        break;
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
                write2Txt(data);
                if (ind%1000==0) System.out.println(ind+" completed");
            }
            return 0;
        }
        catch (IOException ioe){
            System.err.println(ioe.toString());
            return ind;
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
        catch (IOException ioe){
            System.out.println(ioe.toString());
        }
    }

    public void finish(){
        int i,j;
        try {
            cin.close();
            for (i=0;i<2;i++)
                for (j=0;j<n_file;j++){
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
        ReadByID re=new ReadByID();
        int beg=1,ret;
        if (args.length>1) n_file=Integer.parseInt(args[1]);
        else n_file=9;
        if (args.length>0) re.initialize(args[0]);
        else re.initialize("in3.txt");
        if (args.length==3) beg=Integer.parseInt(args[2]);
        ret=0;
        while (true){
            ret=re.work(beg,ret);
            if (ret==0) break;
            else System.out.println(ret);
        }
        re.finish();
    }
}
