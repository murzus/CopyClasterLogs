package copyclusterlogs;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
*
* @author sbt-bykov-vm
*/
public class CopyClusterLogs {
      
   CopyClusterLogs(String ar[]){
        PrintStream pr=null;
        ArrayList<BufferedReader> in = new ArrayList<> (); 
        ArrayList<String> dates = new ArrayList<> (); 
        ArrayList<String> names = new ArrayList<> ();
try {
     String path="";
    if (ar.length<1){ path=".";}else{path=ar[0];}       
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(path))) {        //input directory
                            for (Path file: stream) {
if(!file.toFile().isDirectory() && !file.toFile().getName().equalsIgnoreCase("CopyClusterLogs.jar")&& !file.toFile().getName().equalsIgnoreCase("result.log")){
                                    System.out.print("Find file: "+path+"\\"+file.getFileName()+"  Readability Checking...");
                            if(new BufferedReader(new FileReader(path+"\\"+file.getFileName().toString())).ready()){ //readable check
                            System.out.println(" -Ok");    
                            in.add(new BufferedReader(new FileReader(path+"\\"+file.getFileName().toString()))); //readersInit
                            dates.add(new BufferedReader(new FileReader(path+"\\"+file.getFileName().toString())).readLine());                                  
                            names.add(file.getFileName().toString());//.split("\\.")[0]);
                            } else{System.out.println("file unreadable!"); }
                        }}                                               
                        } catch (IOException | DirectoryIteratorException x) {
                                System.err.println(x);
                        } 
      int curFile=0;     
      boolean flag=true;
      String strBuf;
      File f =new File ("result.log");
      if (f.exists() && !f.isDirectory()){ f.delete();}
      pr = new PrintStream(new BufferedOutputStream(new FileOutputStream("result.log",false)));//output file
      System.out.print(" Merging "+in.size()+" files with sorting...");
   while(in.size()>0){   
         strBuf=null; 
         if(flag){
          for (int c=0;c<dates.size();c++){
                         if (dates.get(curFile).compareTo(dates.get(c)) > 0){  //find minimal date
                         curFile=c;   //make current point file with minimal date 
                     }                 
          }
          pr.println(names.get(curFile)+"  " +dates.get(curFile)); // print line into file-result
         } 
         if((strBuf=in.get(curFile).readLine())!=null){ 
               if(strBuf.matches("\\d\\d\\d\\d-\\d\\d-\\d\\d \\d\\d:\\d\\d:\\d\\d.*")){
                   flag=true;
                   dates.set(curFile,strBuf);
               }else{
                   flag=false;
                   pr.println(strBuf);
                   pr.flush();// print line into file-result
                }
          } else {  //file eof
             dates.remove(curFile);
             in.get(curFile).close();
             in.remove(curFile);
             curFile=0;
            }
      }
   pr.close();pr=null;
   System.out.println(" -Ok");
   System.out.println("Results were recorded in \"result.log\"");
   
 } catch ( Exception ex ) {
     System.err.println(ex);
     if (pr!=null) {pr.close();}
            for (BufferedReader in1 : in) {
                if (in1 != null) {
                    try {
                        in1.close();
                    }catch(Exception ex1){ System.err.println(ex1);}
                }
            }
   }
}
      public static void main(String[] args) {
          CopyClusterLogs copyClusterLogs = new CopyClusterLogs(args);
      }    
}

