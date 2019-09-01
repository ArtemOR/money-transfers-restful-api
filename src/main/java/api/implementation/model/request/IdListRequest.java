package api.implementation.model.request;

import java.util.List;
import java.util.Objects;

public class IdListRequest {
    List<String> ids;

    public IdListRequest(List<String> ids) {
        this.ids = ids;
    }

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IdListRequest that = (IdListRequest) o;
        return Objects.equals(ids, that.ids);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ids);
    }

    @Override
    public String toString() {
        return "IdListRequest{" +
                "ids=" + ids +
                '}';
    }
}
