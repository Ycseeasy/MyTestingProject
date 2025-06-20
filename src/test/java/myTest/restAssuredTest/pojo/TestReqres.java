package myTest.restAssuredTest.pojo;

import myTest.pojo.model.*;
import myTest.restAssuredTest.pojo.model.*;
import myTest.restAssuredTest.pojo.spec.Specification;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static io.restassured.RestAssured.given;

public class TestReqres {

    private final static String URL = "https://reqres.in/";
    private final static String apiKey = "reqres-free-v1";


    /**
     * Проверка списка пользователей на:
     * 1. Наличие id пользователя в пути его аватара;
     * 2. Почта пользователя содержит в себе его имя и фамилию, а также заканчивается на @reqres.in
     */
    @Test
    public void checkUsers() {
        Specification.installSpecification(Specification.requestSpec(URL, apiKey), Specification.responseSpecOK200());
        List<User> users = given()
                .when()
                .get("api/users?page=2")
                .then().log().all()
                .extract().body().jsonPath().getList("data", User.class);

        users.forEach(user -> Assertions.assertTrue(user.getAvatar().contains(user.getId().toString())));

        Assertions.assertTrue(users.stream().allMatch(user -> {
            String name = user.getFirst_name().toLowerCase();
            String lastName = user.getLast_name().toLowerCase();
            Assertions.assertTrue(user.getEmail().endsWith("@reqres.in"));
            return user.getEmail().contains(name) && user.getEmail().contains(lastName);
        }));
    }

    /**
     * Проверка сортировки выгруженного списка. По возрастанию значения year.
     * Проверка, что поле color записано в корректном для HEX-кода формате.
     * Проверка, что поле pantone_value записано в корректном формате для Pantone.
     */
    @Test
    public void checkResource() {
        Specification.installSpecification(Specification.requestSpec(URL, apiKey), Specification.responseSpecOK200());
        List<Resource> resources = given()
                .when()
                .get("api/unknown")
                .then().log().all()
                .extract().body().jsonPath().getList("data", Resource.class);

        for (int i = 0; i < resources.size() - 1; i++) {
            Assertions.assertTrue(resources.get(i).getYear() < resources.get(i + 1).getYear());
        }

        Assertions.assertTrue(resources.stream().allMatch(resource -> {
            String color = resource.getColor();
            return color.startsWith("#")
                    && !color.matches(".*[G-Zg-z].*")
                    && (color.length() == 3 || color.length() == 7 || color.length() == 9);
        }));

        Assertions.assertTrue(resources.stream().allMatch(resource ->
                resource.getPantone_value().matches("^..-.*")
                        && !resource.getPantone_value().matches(".*[a-z].*")));
    }

    /**
     * Проверка успешной регистрации пользователя в системе
     */
    @Test
    public void checkSuccessRegistration() {
        Specification.installSpecification(Specification.requestSpec(URL, apiKey), Specification.responseSpecOK200());
        Integer id = 4;
        String token = "QpwL5tke4Pnpja7X4";

        String email = "eve.holt@reqres.in";
        String password = "pistol";
        RegisterData user = new RegisterData(email, password);

        SuccessReg successReg = given()
                .body(user)
                .when()
                .post("/api/register")
                .then().log().all()
                .extract().as(SuccessReg.class);

        Assertions.assertEquals(successReg.getToken(), token);
        Assertions.assertEquals(successReg.getId(), id);
    }

    /**
     * Проверка неуспешной регистрации пользователя в системе
     */
    @Test
    public void checkUnsuccessfulRegistration() {
        Specification.installSpecification(Specification.requestSpec(URL, apiKey),
                Specification.responseSpecBADREQUEST400());
        String email = "sydney@fife";
        RegisterData user = new RegisterData(email, "");

        UnsuccessfulReg unsuccessfulReg = given()
                .body(user)
                .when()
                .post("/api/register")
                .then().log().all()
                .extract().as(UnsuccessfulReg.class);

        Assertions.assertEquals("Missing password", unsuccessfulReg.getError());
    }

    /**
     * Проверка ответа от сервера при удалении пользователя
     */
    @Test
    public void checkDelete() {
        Specification.installSpecification(Specification.requestSpec(URL, apiKey),
                Specification.responseSpecOK204());

        given()
                .when()
                .delete("/api/user/2")
                .then().log().all();
    }

    /**
     * Проверка на корректность время апдейта пользователя
     */
    @Test
    public void checkUpdate() {
        Specification.installSpecification(Specification.requestSpec(URL, apiKey),
                Specification.responseSpecOK200());
        String name = "morpheus";
        String job = "job";
        UserData userData = new UserData(name, job);

        UserDataResponse userDataResponse = given()
                .body(userData)
                .when()
                .patch("/api/users/2")
                .then().log().all()
                .extract().as(UserDataResponse.class);

        Instant timeResponse = Instant.parse(userDataResponse.getUpdatedAt());
        Instant time = Instant.now(Clock.systemUTC());

        long diffSeconds = Math.abs(Duration.between(time, timeResponse).getSeconds());
        Assertions.assertTrue(diffSeconds <= 1);
    }
}
