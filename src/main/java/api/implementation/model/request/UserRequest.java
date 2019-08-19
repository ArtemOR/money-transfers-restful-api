package api.implementation.model.request;

import java.util.Objects;

public class UserRequest {


    private String name;
    private String passportId;

    public UserRequest(String name, String passportId) {

        this.name = name;
        this.passportId = passportId;
    }

    public String getPassportId() {
        return passportId;
    }

    public void setPassportId(String passportId) {
        this.passportId = passportId;
    }

    public UserRequest() {
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserRequest user = (UserRequest) o;
        return Objects.equals(name, user.name) &&
                Objects.equals(passportId, user.passportId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, passportId);
    }

    @Override
    public String toString() {
        return "User{" +
                ", name='" + name + '\'' +
                ", passportId='" + passportId + '\'' +
                '}';
    }
}
