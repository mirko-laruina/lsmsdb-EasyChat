import com.google.gson.Gson;
import com.mysql.cj.x.protobuf.MysqlxSql;
import org.springframework.aop.scope.ScopedObject;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.web.bind.annotation.*;

import java.sql.*;

@RestController
@EnableAutoConfiguration
public class Main {

    @CrossOrigin(origins = "http://localhost:3000")
    @RequestMapping(value = "/")
    public String index() {
        return "index";
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @RequestMapping(value={"/api/v1/auth/login"}, method=RequestMethod.POST)
    public @ResponseBody String login(@RequestParam("username") String user, @RequestParam("password") String pw) throws Exception {
        //da rivedere il throws exception
        MySQLAdapter dba = new MySQLAdapter("jdbc:mysql://localhost:3306/Task0?user=root&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
        long userId = dba.getUserId(user);
        Gson gson =  new Gson();
        Boolean b = (dba.getUserDBPassword(userId) == pw);
        return gson.toJson("success: " + ( pw.compareTo( dba.getUserDBPassword(userId) ) == 0 ));
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

}