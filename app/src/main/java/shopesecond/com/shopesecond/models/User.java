package shopesecond.com.shopesecond.models;

public class User {
    private String id;
    private String email;
    private String username;
    private String password;
    private String country;

    public User() {

    }

    public User(String id, String email, String username, String password, String country) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.password = password;
        this.country = country;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCounty() {
        return country;
    }

    public void setCounty(String county) {
        this.country = county;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
