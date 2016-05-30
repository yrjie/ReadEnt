import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sun.misc.BASE64Decoder;
 
public class TestLog {
    private static Log log = LogFactory.getLog(TestLog.class);
    public void log(){
       log.debug("Debug info.");
       log.info("Info info");
       log.warn("Warn info");
       log.error("Error info");
       log.fatal("Fatal info");
    }
    
    public String testBase64(String viewstate){
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] decoded=null;
        char[] a=new char[3];
        String ret=null;
        int beg=0;
        a[0]='4';
        a[1]='h';
        try{
            decoded=decoder.decodeBuffer(viewstate);
            ret=new String(decoded,"UTF-8");
            System.out.println(ret.getBytes());
            beg=ret.indexOf("strRecordID");
        }
        catch(IOException ioe){
            ioe.printStackTrace();
        }
        if (beg>=0) return ret.substring(beg+13, beg+45);
        else return "";
    }
    
    public static void main(String[] args) {
       int i,j;
       TestLog test = new TestLog();
       //test.testBase64("ErV2yAqO3WKLbMx0wP7mNuzWZ4ITNhsezvSR4UsEojS3dL3F7KhDLaKTeHxOO+zN2uKn8TnDQcgkUXai6tqizJVobDEqpZF5WXZn5gGLOhpig4//4eAFnocRDvGuFc4btbyqeB+GSmursadRM6kKdEn4zH6+2R/pqB59qWP//7NjHq4HSIH2cDCo5zkkv712tETEpG/yxBfjNs4Ue5N3vhhEynslwzX3sytA0qtbWOIg0B0B+lP0QP6vdICNcxbk9yXVVQ==");
       test.testBase64("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");
       test.log();
       i=5;
       j=7;
       System.out.println(i+j);
    }
}
