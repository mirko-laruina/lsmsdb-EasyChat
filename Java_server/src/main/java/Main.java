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
        Gson gson =  new Gson();
        if(user == "" || pw == ""){
            return gson.toJson(new BooleanResult(false));
        }
        String dbPw = dba.getUserDBPassword(user);
        BooleanResult br = new BooleanResult((dbPw != "" && dbPw.compareTo(pw) == 0));
        return gson.toJson(br);
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

}