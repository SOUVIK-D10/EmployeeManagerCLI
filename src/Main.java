import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main{
    //Class variables--------------------------------------------------------------
    public static Map<String,String> data = new HashMap<>();
    public static boolean flag = true;
    private static final DBM ob = new DBM();
    //------------------------------------------------------------------------------

    //Methods----------------------------------------------------------------------------
    public static void extractdata(String []s){
        data.clear();
        for(String i:s){
            String []k=i.split(":");
            data.put(k[0],k[1]);
        }
    }
    public static void convertToquery(String cm,int limit) throws IOException,SQLException,ArithmeticException {
        //General level
        if(cm.toLowerCase().startsWith("search") && limit>0){
            int i = cm.toLowerCase().indexOf("filter[") + 7;
            String condition = cm.substring(i,cm.indexOf("]",i));
            if(cm.toLowerCase().contains("sort by") && !cm.toLowerCase().contains("get")){
                int j = cm.toLowerCase().indexOf("sort by ")+7;
                String comp = cm.substring(j,cm.length()-2) + (cm.endsWith("+") ? "":" desc");
                if(condition.equalsIgnoreCase("null")) ob.show("1=1",comp,"*");
                else ob.show(condition,comp,"*");
            }
            else if(!cm.toLowerCase().contains("sort by") && cm.toLowerCase().contains("get")){
                int j = cm.toLowerCase().indexOf("get[")+4;
                String get = cm.substring(j,cm.indexOf("]",j));
                if(condition.equalsIgnoreCase("null")) ob.show("1=1 -- ","",get);
                else ob.show(condition+" -- ","",get);
            }
            else if(cm.toLowerCase().contains("sort by") && cm.toLowerCase().contains("get")){
                int k = cm.toLowerCase().indexOf("sort by ")+7;
                String comp = cm.substring(k,cm.length()-2) + (cm.endsWith("+") ? "":" desc");
                int j = cm.toLowerCase().indexOf("get[")+4;
                String get = cm.substring(j,cm.indexOf("]",j));
                if(condition.equalsIgnoreCase("null")) ob.show("1=1",comp,get);
                else ob.show(condition,comp,get);
            }
            else{
                if(condition.equalsIgnoreCase("null")) ob.show("1=1 --","","*");
                else ob.show(condition+" --","","*");
            }
            flag=false;
        }
        else if(cm.toLowerCase().startsWith("help") && limit>0){
            String []s = cm.split(":");
            String f = "Command not found";
            if(s.length==1){
                Scanner sc = new Scanner(new File("General Instructions"));
                while(sc.hasNextLine()){
                    String l = sc.nextLine().trim();
                    System.out.println(l);
                    if(l.equals("-x-") || l.equals("--"+limit+"--")) break;
                }
                sc.close();
            }
            else if(s.length==2){
                Scanner sc = new Scanner(new File("General Instructions"));
                while(sc.hasNextLine()){
                    String l = sc.nextLine().trim();
                    if(l.equalsIgnoreCase(s[1]+limit)){
                        f="";
                        break;
                    }
                }
                while(sc.hasNextLine()){
                    String l=sc.nextLine();
                    if(l.equals("-x-")) break;
                    System.out.println(l);
                }
                System.out.println(f);
                sc.close();
            }
            else{
                System.out.println("Wrong syntax,must use command \n"+"HELP -> get all methods list and syntax\n" +
                        " HELP:<Command> for getting more descriptive answer");
            }
            flag=false;
        }
        //HR level commands
        else if(cm.toLowerCase().startsWith("add") && limit>1){
            int i = cm.toLowerCase().indexOf("data[") + 5;
            String []s = cm.substring(i,cm.indexOf("]",i)).split(",");
            extractdata(s);
            ob.addemployee();
            flag = true;
        }
        else if(cm.toLowerCase().startsWith("update") && limit>1){
            int i = cm.toLowerCase().indexOf("change[") + 7;
            String changes = cm.substring(i,cm.indexOf("]",i));
            i = cm.toLowerCase().indexOf("filter[") + 7;
            String condition = cm.substring(i,cm.indexOf("]",i));
            if(condition.equalsIgnoreCase("null")) ob.updateit(changes,"1=1");
            else ob.updateit(changes,condition);
            flag = true;
        }
        else if(cm.toLowerCase().startsWith("remove") && limit>1){
            int i = cm.toLowerCase().indexOf("filter[") + 7;
            String condition = cm.substring(i,cm.indexOf("]",i));
            System.out.println("Preview : ");
            if(condition.equalsIgnoreCase("null")){
                ob.show("1=1 -- ","","*");
                ob.deleteit("1=1");
            }
            else{
                ob.show(condition+" -- ","","*");
                ob.deleteit(condition);
            }
            flag = true;
        }
        else if(cm.toLowerCase().startsWith("export") && limit>1){
            String []n = cm.split(" ");
                ob.exportit(n[1]);
        }
        else if(cm.toLowerCase().startsWith("import") && limit>1){
            String []n = cm.split(" ");
            try {
                ob.importit(n[1]);
            } catch (FileNotFoundException e) {
                System.out.println("File mentioned not found");
            }
            flag = true;
        }
        else if(cm.toLowerCase().startsWith("gen salary slip") && limit>1){
            int i = cm.toLowerCase().indexOf("filter[") + 7;
            String condition = cm.substring(i,cm.indexOf("]",i));
            if(condition.equalsIgnoreCase("null")) ob.salslip("1=1");
            else ob.salslip(condition);
        }
        else if(cm.equalsIgnoreCase("undo") && limit>1){
            ob.undo();
        }
        else if(cm.equalsIgnoreCase("save") && limit>1){
            ob.save(5);
        }
        //Admin Level
        else if(cm.toLowerCase().startsWith("new permit") && limit>2){
            int i = cm.toLowerCase().indexOf("data[") + 5;
            String []s = cm.substring(i,cm.indexOf("]",i)).split(",");
            extractdata(s);
            ob.addpermit();
            flag = true;
        }
        else if(cm.equalsIgnoreCase("view logs") && limit>2){
            Scanner sc = new Scanner(new File("logs"));
            while(sc.hasNextLine()) System.out.println(sc.nextLine());
            sc.close();
        }
        else if(cm.toLowerCase().startsWith("view permit") && limit>2){
            int i = cm.toLowerCase().indexOf("filter[") + 7;
            String condition = cm.substring(i,cm.indexOf("]",i));
            if(cm.toLowerCase().contains("sort by")){
                int j = cm.toLowerCase().indexOf("sort by ")+7;
                String comp = cm.substring(j,cm.length()-2) + (cm.endsWith("+") ? "":" desc");
                if(condition.equalsIgnoreCase("null")) ob.showUsers("1=1",comp);
                else ob.showUsers(condition,comp);
            }
            else{
                if(condition.equalsIgnoreCase("null")) ob.showUsers("1=1 --","");
                else ob.showUsers(condition+" --","");
            }
        }
        else if(cm.toLowerCase().startsWith("cancel permit") && limit>2){
            int i = cm.toLowerCase().indexOf("filter[") + 7;
            String condition = cm.substring(i,cm.indexOf("]",i));
            System.out.println("Preview : ");
            if(condition.equalsIgnoreCase("null")){
                ob.showUsers("1=1 -- ","");
                ob.deletepermit("1=1");
            }
            else{
                ob.showUsers(condition+" -- ","");
                ob.deletepermit(condition);
            }
            flag=true;
        }
        else if(cm.toLowerCase().startsWith("change permit") && limit>2){
            int i = cm.toLowerCase().indexOf("change[") + 7;
            String changes = cm.substring(i,cm.indexOf("]",i));
            i = cm.toLowerCase().indexOf("filter[") + 7;
            String condition = cm.substring(i,cm.indexOf("]",i));
            if(condition.equalsIgnoreCase("null")) ob.updatepermit(changes,"1=1");
            else ob.updatepermit(changes,condition);
            flag = true;
        }
        else System.out.println("No such command exists");
    }
    public static void main(String[] args) {
        //Variables Declaration--------
        String command="";
        String lastcommand = command;
        UTP u = new UTP();
        LocalDateTime tsmp;
        DateTimeFormatter df = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        int a=1;
        //---------------

        //Driver Loaded-------------
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (ClassNotFoundException e) {
            System.out.println("Error In DB");
        }
        //----------------------

       //Login Window---------
       while(true){
            try {
                command = u.strinput("Enter Username",true);
                a = ob.getUserLevel(command);
                tsmp = LocalDateTime.now();
                tsmp.format(df);
                FileWriter fr = new FileWriter("logs",true);
                if(a<1){
                    System.out.println("Access Denied");
                    fr.write("Login : "+command+" -> Access Denied at" + tsmp+"\n");
                    fr.close();
                    System.exit(0);
                }
                fr.write("Login : "+command+" at "+tsmp+"\n");
                fr.close();
                break;
            } catch (IOException e) {
                System.out.println("I/O error");
            }
        }
       //---------------

      //Commands Window---------------
       while(true){
            try(FileWriter fr = new FileWriter("logs",true)){
                command = u.strinput("|Enter command>",true);
                if(a>1){
                    if(flag&&(!lastcommand.equalsIgnoreCase("redo") || !lastcommand.equalsIgnoreCase("undo"))){
                        System.out.println("Saved");
                        ob.save(2);
                        flag = false;
                    }
                }
                if(command.equalsIgnoreCase("quit") || command.equalsIgnoreCase("exit")){
                   if(a>1){
                       ob.save(1);
                   }
                    tsmp = LocalDateTime.now();
                    tsmp.format(df);
                    fr.write("EXIT AT : "+tsmp+"\n");
                    fr.close();
                    break;
                }
                fr.write(command+"\n");
              convertToquery(command,a);
                if(a>1&&flag) {
                    if (!lastcommand.equalsIgnoreCase(command) && lastcommand.equalsIgnoreCase("undo")) ob.save(4);
                    lastcommand = command;
                }
              if(flag && a>1) ob.save(3);
            }
           catch (IOException e) {
               System.out.println("I/O exception");
           }
            catch (SQLException e) {
                System.out.println("Action Tracking Exception");
            }
            catch(Exception e){
                System.out.println("Wrong Syntax or Invalid data error");
            }
        }
     //-------------------------
    }
    //-----------------------------------------------------------------------------------
}
