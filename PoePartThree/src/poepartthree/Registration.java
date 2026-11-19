package poepartthree;

/**
 *
 * @author Student
 */

public class Registration {
    String userName;
    String passWord;
    String cellPhone;
    String firstName = "";
    String lastName = "";

    public void setName(String first, String last) {
        this.firstName = first == null ? "" : first.trim();
        this.lastName = last == null ? "" : last.trim();
    }

    public boolean checkUserName(String username) {
        return username != null
                && username.contains("_")
                && username.length() <= 5;
    }

    public boolean checkPasswordComplexity(String password) {
        return password != null
                && password.length() >= 8
                && password.matches(".*[A-Z].*")
                && password.matches(".*[a-z].*")
                && password.matches(".*\\d.*")
                && password.matches(".*[^a-zA-Z0-9].*");
    }
 
    public boolean checkCellPhoneNumber(String cellphone) {
        return cellphone != null
                && (cellphone.matches("^\\+27[0-9]{9}$")
                || cellphone.matches("^0[0-9]{9}$"));
    }

    public String usernameMessage(String username) {
        if (checkUserName(username)) {
            return "Username successfully captured.";
        }
        return "Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.";
    }

    public String passwordMessage(String password) {
        if (checkPasswordComplexity(password)) {
            return "Password successfully captured.";
        }
        return "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
    }

    public String cellPhoneMessage(String cellphone) {
        if (checkCellPhoneNumber(cellphone)) {
            return "Cell number successfully captured.";
        }
        return "Cell number is incorrectly formatted or does not contain an international code; please correct the number and try again.";
    }

    public String registerUser(String username, String password, String cellphone) {
        String messages = usernameMessage(username) + "\n"
                + passwordMessage(password) + "\n"
                + cellPhoneMessage(cellphone) + "\n";
        if (checkUserName(username)
                && checkPasswordComplexity(password)
                && checkCellPhoneNumber(cellphone)) {
            this.userName = username;
            this.passWord = password;
            this.cellPhone = cellphone;
            return messages + "Registration was a success";
        } else {
            return messages + "Registration was unsuccessful";
        }
    }

    public boolean registerStatus(String username, String password, String cellphone) {
        return checkUserName(username)
                && checkPasswordComplexity(password)
                && checkCellPhoneNumber(cellphone);
    }
}