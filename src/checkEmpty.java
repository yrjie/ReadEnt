
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

public class checkEmpty {
    
    Scanner cin1,cin2;
    FileOutputStream cout;
    
    public void initialize(String file1,String file2){
        try{
            cin1=new Scanner(new FileInputStream(file1));
            cin2=new Scanner(new FileInputStream(file2));
            cout=new FileOutputStream("out1.txt");
        }
        catch (IOException ioe){
            System.out.println(ioe.toString());
        }
    }
    
    public void work(){
        
    }
    
    public static void main(String[] args){
        checkEmpty ce=new checkEmpty();
        ce.initialize(args[0],args[1]);
        ce.work();
    }
    
}
