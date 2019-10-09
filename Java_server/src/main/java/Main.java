import com.google.gson.Gson;
import org.springframework.aop.scope.ScopedObject;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.web.bind.annotation.*;

import java.sql.*;

@RestController
@EnableAutoConfiguration
public class Main {

    @RequestMapping("/")
    String home() {
        return "Hello World!";
    }

    @RequestMapping(value={"/{echo}"}, method=RequestMethod.GET)
    public @ResponseBody String getAttr(@PathVariable(value="echo") final String text) {
        Gson gson = new Gson();
        return gson.toJson(text);
    }

    @RequestMapping(value={"/login"}, method=RequestMethod.POST)
    public @ResponseBody String login(@RequestParam("username") String user, @RequestParam("password") String pw) {
        String connStr = "jdbc:mysql://localhost:3306/Task0?user=root&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        Connection conn = null;
        String ret = "";

        try {
            conn = DriverManager.getConnection(connStr);
            PreparedStatement ps = conn.prepareStatement("SELECT userId FROM Users WHERE username=? AND password=?");
            ps.setString(1, user);
            ps.setString(2, pw);
            ps.execute();

            ResultSet rs = ps.getResultSet();
            while(rs.next()){
                ret += rs.getInt("userId");
            }
            rs.close();
            ps.close();
            conn.close();
        } catch (SQLException ex){
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        if (ret == ""){
            ret = "Authentication failed";
        }
        Gson gson = new Gson();
        return gson.toJson(ret);
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

}