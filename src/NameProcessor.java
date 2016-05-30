/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author yrjie
 */

import java.io.*;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Scanner;

public class NameProcessor {
    
    Hashtable ht;
    Scanner cin;
    FileOutputStream cout;
    final String infile="深圳企业名称.txt", outfile="单字.txt";
    
    void initialize(){
        try{
            cin=new Scanner(new FileInputStream(infile),"GBK");
            cout=new FileOutputStream(outfile);
            ht=new Hashtable(1000000,(float)0.8);
        }
        catch (Exception ex){
            System.out.println(ex.toString());
        }
    }
    
    void work(){
        int i,num,cnt;
        char now;
        String line;
        Enumeration enum1,enum2;
        try{
            cnt=0;
            while (cin.hasNext()){
                line=cin.nextLine();
                cnt++;
                //System.out.println(line.length());
                for (i=0;i<line.length();i++){
                    now=line.charAt(i);
                    if (now<0x4E00||now>0x9FCC)
                        continue;
                    //if ((now<0x4E00||now>0x9FCC)&&(now<'0'||now>'9')&&(now<'a'||now>'z')&&(now<'A'||now>'Z'))
                    //    continue;
                    if (ht.containsKey(now)){
                        num=(Integer)ht.get(now);
                        num++;
                        ht.put(now, num);
                    }
                    else {
                        ht.put(now, 1);
                    }
                    //System.out.print((now+1)+" ");
                }
                //System.out.println();
            }
            enum1=ht.keys();
            enum2=ht.elements();
            while (enum1.hasMoreElements()){
                cout.write((enum1.nextElement()+"\t"+enum2.nextElement()+"\r\n").getBytes());
            }
            System.out.println(cnt);
        }
        catch (Exception ex){
            System.out.println(ex.toString());
        }
    }
    
    void finish(){
        try{
            cin.close();
            cout.close();
        }
        catch (IOException ioe){
            System.out.println(ioe.toString());
        }
    }
    
    public static void main(String args[]){
        NameProcessor np=new NameProcessor();
        np.initialize();
        np.work();
        np.finish();
        //System.out.println((int)'倆');
    }
}
