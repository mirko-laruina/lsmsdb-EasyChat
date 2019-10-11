public class LoginResult extends BooleanResult {
    String authcode;

    public LoginResult(boolean r, String authcode){
        super(r);
        this.authcode = authcode;
    }
}
