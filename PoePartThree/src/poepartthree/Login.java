package poepartthree;

/**
 *
 * @author Student
 */
public class Login extends Registration {

    public boolean loginUser(String name, String password) {
        return name != null
                && password != null
                && name.equals(userName)
                && password.equals(passWord);
    }

    public String returnLoginStatus(boolean loginStatus) {
        if (loginStatus) {
            return "Welcome " + firstName + ", " + lastName + " it is great to see you.";
        } else {
            return "Username or password incorrect, please try again.";
        }
    }
}