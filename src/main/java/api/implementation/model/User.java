package api.implementation.model;

import java.util.Collection;
import java.util.Objects;

public class User {

    private long id;
    private String name;
    private String passportId;
    private Collection<Account> accounts;

    public User(long id, String name, String passportId) {
        this.id = id;
        this.name = name;
        this.passportId = passportId;
    }

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

    public Collection<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(Collection<Account> accounts) {
        this.accounts = accounts;
    }

    public static User newInstance(User user) {
        return new User(user.getId(), user.getName(), user.getPassportId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id &&
                Objects.equals(name, user.name) &&
                Objects.equals(passportId, user.passportId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, passportId);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", passportId='" + passportId + '\'' +
                '}';
    }
}
