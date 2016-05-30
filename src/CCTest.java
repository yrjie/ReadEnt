/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Administrator
 * check code test
 */
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.*;
import java.util.Scanner;

public class CCTest{
    int[][][] mat;
    int[][] cc;
    String allC="2468BDFHJLNPpRTVXZ";   //szaic
    //String allC="02468BDFHJLNPRTVXZ";  //gzaic
    //String allC="123456789+-";      //szcredit
    //final int w=11,h=15;          //szcredit
    final int w=10,h=12;
    int n;
    //int[] zero = {-3355444,-6710887,-1};
    int[] single={0x0000ff, 0x00ff00, 0xff0000};
    
    public void initialize(String infile){
        int i,j,k,pixel,ccH,ccW,cnt;
        int[][] dir={{-1,-1},{-1,0},{-1,1},{0,-1},{0,1},{1,-1},{1,0},{1,1}};
        try{
            Scanner cin=new Scanner(new FileInputStream("D:\\studio\\szaic\\121204checkCode\\standard2.txt"));
            File fi=new File("D:\\studio\\szaic\\121204checkCode\\"+infile+".gif");
            BufferedImage bi=ImageIO.read(fi);
            n=allC.length();
            mat=new int[n][h][w];
            for (i=0;i<n;i++)
                for (j=0;j<h;j++)
                    for (k=0;k<w;k++)
                        mat[i][j][k]=cin.nextInt();
            cin.close();
            ccH=bi.getHeight();
            ccW=bi.getWidth();
            cc=new int[ccH][ccW];
            for (i=0;i<ccH;i++)
                for (j=0;j<ccW;j++){
                    pixel=bi.getRGB(j, i);
                    cnt=0;
                    for (k=0;k<3;k++)
                        cnt+=(pixel&single[k])>>(k*8);
                    if (cnt>350){
                        cc[i][j]=0;
                    }
                    else cc[i][j]=1;
                }
            for (i=1;i<ccH-1;i++)
                for (j=1;j<ccW-1;j++){
                    cnt=0;
                    for (k=0;k<8;k++)
                        if (cc[i+dir[k][0]][j+dir[k][1]]==1)
                            cnt++;
                    if ((cc[i][j]==0&&cnt>4)||(cc[i][j]==1&&cnt<2)) cc[i][j]=1-cc[i][j];
                }
        }
        catch (IOException ioe){
            ioe.printStackTrace();
        }
    }
    
    public String Extract(){
        String ret="";
        int i,x1,y1,j,x2,y2,dx,a,b,ind,x0,temp;
        a=b=0;
        for (i=0;i<cc[0].length;i++){
            if (cc[5][i]==1) a++;
            if (cc[6][i]==1) b++;
        }
        if (b>a*2) y1=6;
        else y1=5;
        System.out.println(y1);
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
            System.out.println(b);
            ret=ret+allC.charAt(ind);
        }
        return ret;
    }
    
    /*public String Extract(){
        String ret="";
        int i,x1,y1,j,x2,y2,dx,temp,ma,ind,inc;
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
        System.out.println(num[0]+" "+num[1]);
        return ret;
    }*/

    public void getStd(String infile,int x1,int y1){
        int i,j;
        for (i=0;i<h;i++){
            for (j=0;j<w;j++)
                System.out.print(cc[y1+i][x1+j]+" ");
            System.out.println();
        }
    }

    public static void main(String args[]) {
        CCTest cct=new CCTest();
        String[] aa;
        aa="fdafdfda".split("o");
        cct.initialize(args[0]);
        if (args.length==1)
            System.out.println(cct.Extract());
        else if (args.length==3)
            cct.getStd(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]));
    }
}
