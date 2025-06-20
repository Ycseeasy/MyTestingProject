package myTest.nopojo;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import myTest.pojo.spec.Specification;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class TestReqresNoPojo {

    private final static String URL = "https://reqres.in/";
    private final static String apiKey = "reqres-free-v1";

    /**
     * Проверка списка пользователей на:
     * 1. Наличие id пользователя в пути его аватара;
     * 2. Почта пользователя содержит в себе его имя и фамилию, а также заканчивается на @reqres.in
     */
    @Test
    public void checkUsersNoPojo() {
        Specification.installSpecification(Specification.requestSpec(URL, apiKey), Specification.responseSpecOK200());

        Response response = given()
                .when()
                .get("api/users?page=2")
                .then().log().all()
                .body("page", equalTo(2))
                .body("data.id", notNullValue())
                .body("data.email", notNullValue())
                .body("data.first_name", notNullValue())
                .body("data.last_name", notNullValue())
                .body("data.avatar", notNullValue())
                .extract().response();
        JsonPath jsonPath = response.jsonPath();

        List<String> firstNames = jsonPath.getList("data.first_name");
        List<String> lastNames = jsonPath.getList("data.last_name");
        List<String> emails = jsonPath.get("data.email");
        List<Integer> ids = jsonPath.getList("data.id");
        List<String> avatars = jsonPath.getList("data.avatar");

        for (int i = 0; i < avatars.size(); i++) {
            Assertions.assertTrue(avatars.get(i).contains(ids.get(i).toString()));
        }

        for (int i = 0; i < emails.size(); i++) {
            Assertions.assertTrue(emails.get(i).contains(firstNames.get(i).toLowerCase()));
            Assertions.assertTrue(emails.get(i).contains(lastNames.get(i).toLowerCase()));
            Assertions.assertTrue(emails.get(i).endsWith("@reqres.in"));
        }
    }

    /**
     * Проверка сортировки выгруженного списка. По возрастанию значения year.
     * Проверка, что поле color записано в корректном для HEX-кода формате.
     * Проверка, что поле pantone_value записано в корректном формате для Pantone.
     */
    @Test
    public void checkResourceNoPojo() {
        Specification.installSpecification(Specification.requestSpec(URL, apiKey), Specification.responseSpecOK200());

        Response response = given()
                .when()
                .get("/api/unknown")
                .then().log().all()
                .body("page", equalTo(1))
                .body("data.name", notNullValue())
                .body("data.year", notNullValue())
                .body("data.color", notNullValue())
                .body("data.pantone_value", notNullValue())
                .extract().response();
        JsonPath jsonPath = response.jsonPath();

        List<Integer> years = jsonPath.getList("data.year");
        List<String> colors = jsonPath.getList("data.color");
        List<String> pantoneColors = jsonPath.getList("data.pantone_value");

        for (int i = 0; i < years.size() - 1; i++) {
            Assertions.assertTrue(years.get(i) < years.get(i + 1));
        }

        Assertions.assertTrue(colors.stream().allMatch(color -> color.startsWith("#")
                && !color.matches(".*[G-Zg-z].*")
                && (color.length() == 3 || color.length() == 7 || color.length() == 9)));

        Assertions.assertTrue(pantoneColors.stream().allMatch(pantoneColor -> pantoneColor.matches("^..-.*")
                && !pantoneColor.matches(".*[a-z].*")));
    }

    /**
     * Проверка успешной регистрации пользователя в системе
     */
    @Test
    public void checkSuccessRegistrationNoPojo() {
        Specification.installSpecification(Specification.requestSpec(URL, apiKey), Specification.responseSpecOK200());
        String token = "QpwL5tke4Pnpja7X4";

        Map<String, String> user = new HashMap<>() {{
            put("email", "eve.holt@reqres.in");
            put("password", "pistol");
        }};

        Response response = given()
                .body(user)
                .when()
                .post("/api/register")
                .then().log().all()
                .extract().response();
        JsonPath jsonPath = response.jsonPath();

        int id = jsonPath.get("id");
        String responseToken = jsonPath.getString("token");

        Assertions.assertEquals(token, responseToken);
        Assertions.assertEquals(4, id);
    }

    /**
     * Проверка неуспешной регистрации пользователя в системе
     */
    @Test
    public void checkUnsuccessfulRegistrationNoPojo() {
        Specification.installSpecification(Specification.requestSpec(URL, apiKey),
                Specification.responseSpecBADREQUEST400());

        Map<String, String> user = new HashMap<>() {{
            put("email", "sydney@fife");
            put("password", "");
        }};

        Response response = given()
                .body(user)
                .when()
                .post("/api/register")
                .then().log().all()
                .extract().response();
        JsonPath jsonPath = response.jsonPath();

        String errorTest = jsonPath.get("error");

        Assertions.assertEquals("Missing password", errorTest);
    }

    /**
     * Проверка на корректность время апдейта пользователя
     */
    @Test
    public void checkUpdateNoPojo() {
        Specification.installSpecification(Specification.requestSpec(URL, apiKey),
                Specification.responseSpecOK200());

        Map<String, String> user = new HashMap<>() {{
            put("name", "morpheus");
            put("job", "job");
        }};

        Response response = given()
                .body(user)
                .when()
                .patch("/api/users/2")
                .then().log().all()
                .extract().response();
        JsonPath jsonPath = response.jsonPath();

        Instant timeResponse = Instant.parse(jsonPath.getString("updatedAt"));
        Instant time = Instant.now(Clock.systemUTC());

        long diffSeconds = Math.abs(Duration.between(time, timeResponse).getSeconds());
        Assertions.assertTrue(diffSeconds <= 1);
    }
}

