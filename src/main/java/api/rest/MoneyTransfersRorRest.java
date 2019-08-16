package api.rest;

import api.model.User;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static api.exception.MoneyTransferExceptionGenerator.generateMissingMandatoryParametersException;
import static api.helper.IdGenerator.generateUserId;
import static spark.Spark.*;

public class MoneyTransfersRorRest {
    static Logger logger = LoggerFactory.getLogger(MoneyTransfersRorRest.class);
    private static Map<Long, User> users = new HashMap();
    private static Gson gson = new Gson();

    public static void main(String[] args) {
        port(8082);

        post("/user", (request, response) -> {
            String json_user = request.body();

            User user = gson.fromJson(json_user, User.class);
            if (user == null) {
                return generateMissingMandatoryParametersException(response);
            }
            long userId = generateUserId();
            user.setId(userId);
            users.put(userId, user);
            response.status(200);

            return gson.toJson(users.get(userId));
        });

        get("/users", (request, response) ->
                gson.toJson(users.values()));

    }


}
