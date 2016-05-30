
import java.io.*;
import java.util.List;
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
import org.apache.http.entity.mime.MultipartEntity;  
import org.apache.http.entity.mime.content.FileBody;  
import org.apache.http.entity.mime.content.StringBody;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author yrjie
 */
public class CS5234 {
    HttpClient client;
    HttpPost post;
    String[] filename={"001","002","003","004","005","101","102","103","104","105"
    ,"144","145","146","151","153","154","161","165","167","172"};
    
    public void initialize(){
        int i,j,k;
        client = new DefaultHttpClient();
        client.getParams().setParameter(ClientPNames.COOKIE_POLICY,
                            CookiePolicy.BROWSER_COMPATIBILITY);
        post = new HttpPost("http://www-appn.comp.nus.edu.sg/~cs5234/cgi-bin/2012/bap-results.cgi");
        //namevaluepair1=new Vector<NameValuePair>();
        //namevaluepair2=new Vector<NameValuePair>();
        
        try {
            /*namevaluepair1=new Vector<NameValuePair>();
            namevaluepair1.add(new BasicNameValuePair("name","t06"));
            //namevaluepair.add(new BasicNameValuePair("sch","proj-d003-01.prj"));
            //namevaluepair.add(new BasicNameValuePair("upload_file","C:\\Users\\yrjie\\phd\\12-13\\1\\CS5234\\BAP\\sol\\part-d003-tv.sol"));
            //namevaluepair.add(new BasicNameValuePair(".submit","Send"));
            namevaluepair1.add(new BasicNameValuePair("challenge","Challenge proj-d003-01.prj"));
            
            namevaluepair2=new Vector<NameValuePair>();
            namevaluepair2.add(new BasicNameValuePair("name","t06"));
            namevaluepair2.add(new BasicNameValuePair("sch","proj-d003-01.prj"));
            namevaluepair2.add(new BasicNameValuePair("upload_file","C:\\Users\\yrjie\\phd\\12-13\\1\\CS5234\\BAP\\sol\\part-d003-tv.sol"));
            namevaluepair2.add(new BasicNameValuePair(".submit","Send"));*/
        }
        catch (Exception ioe){
            System.err.println(ioe.toString());
        }
    }
    
    public void work(){
        int i,n;
        HttpResponse response;
        HttpEntity entity;
        n=filename.length;
        try {
            for (i = 0; i < n; i++) {
                MultipartEntity reqEntity= new MultipartEntity();
                reqEntity.addPart("name", new StringBody("t06"));
                reqEntity.addPart("sch",new StringBody("proj-d" + filename[i] + "-01.prj"));
                reqEntity.addPart("personal",new StringBody("t06"));
                reqEntity.addPart("upload_file",new FileBody(new File(
                        "C:\\Users\\yrjie\\phd\\12-13\\1\\CS5234\\BAP\\solution\\part-d" + filename[i] + "-tv.sol")));
                //post.setEntity(new UrlEncodedFormEntity(namevaluepair1));
                post.setEntity(reqEntity);
                response = client.execute(post);
                entity = response.getEntity();
                if (entity != null) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            entity.getContent(), "UTF-8"));
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        //System.out.println(line);
                        if (line.contains("NEW HIGH-SCORE:"))
                            System.out.println(filename[i]);
                    }
                    reader.close();
                }
            }
        }
        catch (IOException ioe){
            ioe.printStackTrace();
        }
    }
    
    public static void main(String args[]){
        CS5234 cs=new CS5234();
        cs.initialize();
        cs.work();
    }
}
