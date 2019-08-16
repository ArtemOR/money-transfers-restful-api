package api.model;

import java.util.ArrayList;
import java.util.List;


public class User {

    private long id;
    private String name;
    private List<Account> accounts = new ArrayList<>();
    private String passportId;

    public String getPassportId() {
        return passportId;
    }
    public void setPassportId(String passportId) {
        this.passportId = passportId;
    }

    public User() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }
}
