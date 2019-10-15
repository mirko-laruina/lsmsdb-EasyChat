import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Settings {
    private Properties properties;

    private String dbUser;
    private String dbPassword;
    private String dbName;
    private String dbUri;


    public Settings(String propFileName) throws IOException {
        this(propFileName, null);
    }

    public Settings(String propFileName, String defaultsFileName) throws IOException{
        InputStream inputStream = null;
        InputStream defaultsInputStream = null;

        try {
            if (defaultsFileName != null){
                Properties defaultProperties = new Properties();
                defaultsInputStream = new FileInputStream(defaultsFileName);
                defaultProperties.load(defaultsInputStream);
                properties = new Properties(defaultProperties);
            } else{
                properties = new Properties();
            }

            inputStream = new FileInputStream(propFileName);
            properties.load(inputStream);

            // get the property value and print it out
            dbUser = properties.getProperty("db_user");
            dbPassword = properties.getProperty("db_password");
            dbName = properties.getProperty("db_name");
            dbUri = properties.getProperty("db_uri");
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        } finally {
            if (inputStream != null)
                inputStream.close();
            if (defaultsInputStream != null)
                defaultsInputStream.close();
        }
    }

    public String getDbUser() {
        return dbUser;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public String getDbName() {
        return dbName;
    }

    public String getDbUri() {
        return dbUri;
    }

    public String getConnStr(){
        StringBuilder builder = new StringBuilder();
        builder.append("jdbc:mysql://");
        builder.append(dbUri);
        builder.append("/");
        builder.append(dbName);
        builder.append("?user=");
        builder.append(dbUser);
        if (dbPassword != null){
            builder.append("&password=");
            builder.append(dbPassword);
        }
        builder.append("&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
        return builder.toString();
    }
}
