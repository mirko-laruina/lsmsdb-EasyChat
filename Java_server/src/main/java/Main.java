import com.google.gson.Gson;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.web.bind.annotation.*;

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

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

}