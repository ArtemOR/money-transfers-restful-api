package api.implementation.model.request;

import java.util.Objects;

public class WhereRequest {
    IdListRequest where;

    public WhereRequest(IdListRequest where) {
        this.where = where;
    }

    public IdListRequest getWhere() {
        return where;
    }

    public void setWhere(IdListRequest where) {
        this.where = where;
    }

    @Override
    public String

    toString() {
        return "WhereRequest{" +
                "where=" + where +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WhereRequest that = (WhereRequest) o;
        return Objects.equals(where, that.where);
    }

    @Override
    public int hashCode() {
        return Objects.hash(where);
    }
}
