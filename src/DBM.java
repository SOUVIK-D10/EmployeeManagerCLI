import java.io.*;
import java.sql.*;
import java.util.Properties;
import java.util.Scanner;
import java.util.Stack;

public class DBM {
    //Class variable decleration--------------------------------------
    private final Stack<Savepoint> undo = new Stack<>();
    private int c=0;
    Savepoint s;
    private final UTP u = new UTP();
    Properties prop = new Properties();
    Connection con;
    DBM(){
        try(FileInputStream fis = new FileInputStream("db.properties")){
            prop.load(fis);
            String url = prop.getProperty("db.url");
            String user = prop.getProperty("db.user");
            String password = prop.getProperty("db.password");
            con = DriverManager.getConnection(url,user,password);
            con.setAutoCommit(false);
        } catch (SQLException e) {
            System.out.println("Connection error");
        } catch (IOException e) {
            System.out.println("I/O error");
        }
    }
    //----------------------------------------------------------------

    //Login Authentication Method------------------------------
    public int getUserLevel(String nm)throws IOException {
        try(PreparedStatement stat = con.prepareStatement("select password,roll from users where users = ?")){
            stat.setString(1,nm);
            ResultSet rs = stat.executeQuery();
            if(rs.next()){
                for(int i=1;i<=3;i++){
                    if(u.strinput("Enter password",true).equals(rs.getString("password"))){
                        switch(rs.getString("roll")){
                            case "ADMIN" :
                                rs.close();
                                return 3;
                            case "HR":
                                rs.close();
                                return 2;
                            case "general" :
                                rs.close();
                                return 1;
                        }
                    }
                    else{
                        rs.close();
                        System.out.println("password wrong");
                    }
                }
                return 0;
            }
            else{
                rs.close();
                System.out.println("User not found");
                return -1;
            }
        } catch (SQLException e) {
            System.out.println("Invalid");
            return -1;
        }
    }
    //---------------------------------------------------------

    //ADMIN LEVEL METHODS---------------------------------------------------------------
    public void showUsers(String condition,String comparator)throws ArithmeticException{
        try(PreparedStatement stat = con.prepareStatement(String.format("select * from users where %s order by %s",condition,comparator))){

            ResultSet rs = stat.executeQuery();
            for(int i=1;i<=118;i++){
                if(i==1 || i==5 || i==56 || i==107 || i==118) System.out.print("+");
                else System.out.print("-");
            }
            System.out.println();
            System.out.printf("|%3s|%50s|%50s|%10s|\n","uid","username","roll","password");
            for(int i=1;i<=118;i++){
                if(i==1 || i==5 || i==56 || i==107 || i==118) System.out.print("+");
                else System.out.print("-");
            }
            System.out.println();

            while(rs.next()){
                System.out.printf("|%-3d|%-50s|%-50s|%-10s|\n",rs.getInt("uid"), rs.getString("users")
                        ,rs.getString("roll"),rs.getString("password"));
            }
            for(int i=1;i<=118;i++){
                if(i==1 || i==5 || i==56 || i==107 || i==118) System.out.print("+");
                else System.out.print("-");
            }
            System.out.println();

        } catch (SQLException e) {
            System.out.println("Invalid");
            throw new ArithmeticException();
        }
        catch(NumberFormatException e){
            System.out.println("Salary in integer don't use \" \"");
        }
    }
    public void addpermit()throws ArithmeticException{
        try(PreparedStatement stat = con.prepareStatement("insert into users(users,password,roll,uid) values(?,?,?,?)")){
            stat.setString(1,Main.data.get("users").substring(1,Main.data.get("users").length()-1));
            stat.setString(2,Main.data.get("password").substring(1,Main.data.get("password").length()-1));
            stat.setString(3,Main.data.get("roll").substring(1,Main.data.get("roll").length()-1));
            stat.setInt(4,Integer.parseInt(Main.data.get("uid")));
            stat.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Invalid Entry");
            throw new ArithmeticException();
        }
    }
    public void deletepermit(String condition) throws IOException,ArithmeticException {
        try(PreparedStatement stat = con.prepareStatement("delete from users where "+condition)){
            while(true){
                String ch = u.strinput("Are you sure ?(Y/N)",true);
                if(ch.equalsIgnoreCase("y")){
                    int n = stat.executeUpdate();
                    System.out.printf("Deleted %d records\n",n);
                    return;
                }
                else if(ch.equalsIgnoreCase("n")){
                    System.out.println("OK");
                    return;
                }
            }
        } catch (SQLException e) {
            System.out.println("Not a valid condition");
            throw new ArithmeticException();
        }
    }
    public void updatepermit(String changes,String condition)throws ArithmeticException{
        String s = String.format("update users set %s where %s",changes,condition);
        try(PreparedStatement stat = con.prepareStatement(s)){
            stat.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Invalid Entry");
            throw new ArithmeticException();
        }
    }
    //--------------------------------------------------------------------------------------

    //HR LEVEL METHODS------------------------------------------------------------------
    public void addemployee()throws ArithmeticException{
        try(PreparedStatement stat = con.prepareStatement("insert into employee(name,roll,salary) values(?,?,?)")){
            stat.setString(1,Main.data.get("name").substring(1,Main.data.get("name").length()-1));
            stat.setString(2,Main.data.get("roll").substring(1,Main.data.get("roll").length()-1));
            stat.setInt(3,Integer.parseInt(Main.data.get("salary")));
            stat.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Invalid");
            throw new ArithmeticException();
        }
        catch(NumberFormatException e){
            System.out.println("Salary in integer don't use \" \"");
        }
    }
    public void updateit(String changes,String condition)throws ArithmeticException{
        String s = String.format("update employee set %s where %s",changes,condition);
        try(PreparedStatement stat = con.prepareStatement(s)){
            stat.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Invalid Entry");
            throw new ArithmeticException();
        }
    }
    public void exportit(String url) throws IOException,ArithmeticException {
        File f = new File(url);
        if(!f.isDirectory()) System.out.println("Error not directory");
        else{
            String s = u.strinput("Enter only name of exported file no need for file type",true).concat(".csv");
            f = new File(f,s);
            f.createNewFile();
            try(PreparedStatement stat = con.prepareStatement("select * from employee");
                FileWriter fr = new FileWriter(f)){
                ResultSet rs = stat.executeQuery();
                fr.write("UID,NAME,ROLL,SALARY\n");
                while(rs.next()){
                    fr.write(String.format("%d,%s,%s,%d\n",rs.getInt("uid"),rs.getString("name"),
                            rs.getString("roll"),rs.getInt("salary")));
                }
                rs.close();
            } catch (SQLException e) {
                System.out.println("Query Error");
                throw new ArithmeticException();
            }
        }
    }
    public void importit(String url) throws FileNotFoundException,ArithmeticException {
        File f = new File(url);
        Scanner sc = new Scanner(f);
        try(PreparedStatement stat = con.prepareStatement("insert into employee(name,roll,salary) values(?,?,?)")){
            while(sc.hasNextLine()){
                String []s = sc.nextLine().split(",");
                stat.setString(1,s[0]);
                stat.setString(2,s[1]);
                stat.setInt(3,Integer.parseInt(s[2]));
                stat.addBatch();
            }
            stat.executeBatch();
            sc.close();
        } catch (SQLException e) {
            System.out.println("Invalid");
            throw new ArithmeticException();
        }
        catch(NumberFormatException e){
            System.out.println("Salary in integer");
        }
    }
    public void deleteit(String condition) throws IOException,ArithmeticException {
        try(PreparedStatement stat = con.prepareStatement("delete from employee where "+condition)){
            while(true){
                String ch = u.strinput("Are you sure ?(Y/N)",true);
                if(ch.equalsIgnoreCase("y")){
                    int n = stat.executeUpdate();
                    System.out.printf("Deleted %d records\n",n);
                    return;
                }
                else if(ch.equalsIgnoreCase("n")){
                    System.out.println("OK");
                    return;
                }
            }
        } catch (SQLException e) {
            System.out.println("Not a valid condition");
            throw new ArithmeticException();
        }
    }
    public  void salslip(String condition) throws IOException,ArithmeticException {
        try(PreparedStatement stat = con.prepareStatement("select * from employee where "+condition)){
            ResultSet rs = stat.executeQuery();
            File f;
            while(true){
                String ch = u.strinput("location to put in",true);
                f = new File(ch);
                if(f.exists()){
                    if(f.isDirectory()){
                        break;
                    }
                    else System.out.println("Not a directory");
                }
                else System.out.println("File not found");
            }
            while(rs.next()){
                int uid=rs.getInt("uid");
                File ff=new File(f,"SalarySlip"+uid+".txt");
                ff.createNewFile();
                FileWriter fr = new FileWriter(ff);
                int sal=rs.getInt("salary");
                int tax;
                if(sal<1275001) tax=0;
                else if(sal>1275000 && sal<1350000) tax=sal-1275000;
                else tax=sal*(5/100);
                int pf = sal*(2/100);
                fr.write(String.format("UID:%d\nName:%s\nRoll:%s\nGross Salary:%d\nPF:%d\nTax:%d\nNet Salary:%d\n",uid
                        ,rs.getString("name"),rs.getString("roll"),sal,pf,tax,sal-pf-tax));
                fr.close();
                f.setReadOnly();
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println("Not a valid condition");
            throw new ArithmeticException();
        }
    }
    //----------------------------------------------------------------------------------

    //Action Manager Methods----------------------------
    public void undo() throws SQLException {
        if(!undo.isEmpty()){
                System.out.println("WAS IN:"+s.getSavepointName());
               // redo.push(s);
                s = undo.pop();
                System.out.println("NOW IN:"+s.getSavepointName());
                con.rollback(s);
        }
        else{
            System.out.println("Cannot undo");
        }
    }
    public void save(int mode)throws SQLException{
            if(mode == 1) {
                con.commit();
                con.close();
            }
            if(mode == 2){
               s = con.setSavepoint("point"+c);
               c++;
            }
            if(mode==3){
                undo.push(s);
            }
            if(mode==4){
                undo.push(s);
            }
            if(mode==5){
                con.commit();
                undo.clear();
            }
    }
    //--------------------------------------------------

    //GENERAL LEVEL METHODS---------------------------------------------------------------------------
    public void show(String condition,String comparator,String target)throws ArithmeticException{
        try(PreparedStatement stat = con.prepareStatement(String.format("select %s from employee where %s order by %s",target,condition,comparator))){
            ResultSet rs = stat.executeQuery();
            if(target.equals("*")) target = "uid,name,roll,salary";
            String []s = target.split(",");
            StringBuilder p = new StringBuilder("|");
            p.append("%-25s|".repeat(s.length)).append("\n");
            String format = p.toString();
            System.out.printf(format,s);
            System.out.println("-".repeat((s.length*26) + 1));
            String []data = new String[s.length];
            while(rs.next()){
                for(int i=0;i<s.length;i++) {
                    if(s[i].toLowerCase().startsWith("distinct")){
                        data[i]=rs.getString(s[i].toLowerCase().replace("distinct ",""));
                    }
                    else data[i]=rs.getString(s[i]);
                }
                System.out.printf(format,data);
            }
            System.out.println("-".repeat((s.length*26) + 1));
            rs.close();
        } catch (SQLException e) {
            System.out.println("Invalid");
            throw new ArithmeticException();
        }
        catch(NumberFormatException e){
            System.out.println("Salary in integer don't use \" \"");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("\n\n\n\n");
            throw new RuntimeException(e);
        }
    }
    //------------------------------------------------------------------------------------------------
}