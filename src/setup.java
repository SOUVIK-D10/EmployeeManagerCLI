import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Scanner;
public class setup {
    public static void main(String[] args){
        Properties pro = new Properties();
        Scanner in = new Scanner(System.in);
        System.out.println("Enter url of database");
        String url = in.nextLine().trim();
        System.out.println("Enter user for database");
        String user = in.nextLine().trim();
        System.out.println("Enter password for database");
        String password = in.nextLine().trim();
        pro.setProperty("db.url",url);
        pro.setProperty("db.user",user);
        pro.setProperty("db.password",password);
        File f;
        try {
            f = new File("EmployeeManager_jar","db.properties");
            f.createNewFile();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        try(FileOutputStream out = new FileOutputStream(f)){
            pro.store(out,"Database Configuration");
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (ClassNotFoundException e) {
            System.out.println("Error In DB");
        }
        try(Connection con = DriverManager.getConnection(url,user,password)){
        PreparedStatement s = con.prepareStatement("create table employee(uid INT AUTO_INCREMENT PRIMARY KEY,name VARCHAR(25) NOT NULL,roll VARCHAR(25),salary INT)");
        s.executeUpdate();
        s = con.prepareStatement("create table users(uid INT,users VARCHAR(25) PRIMARY KEY,roll ENUM(?,?,?),password VARCHAR(25) NOT NULL)");
        s.setString(1,"general");
        s.setString(2,"HR");
        s.setString(3,"ADMIN");
        s.executeUpdate();
        s = con.prepareStatement("insert into users(uid,users,roll,password) values(?,?,?,?)");
        System.out.println("Enter uid:");
        try{
            s.setInt(1,Integer.parseInt(in.nextLine().trim()));
        } catch (NumberFormatException e) {
            System.out.println("uid set to 0 as you have given invalid input");
        }
        System.out.println("Enter a user name");
        s.setString(2,in.nextLine().trim());
        s.setString(3,"ADMIN");
        System.out.println("Enter password");
        s.setString(4,in.nextLine().trim());
		s.executeUpdate();
        s.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
//jdbc:mysql://localhost:3306/?user=root
