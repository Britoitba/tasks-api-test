package org.brito.restassured.tests;

import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import org.brito.restassured.util.DataUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ApiTests {
    String description = new Faker().dragonBall().character();
    String date = DataUtils.getDataFormatada();

    @BeforeEach
    public void setup(){
        RestAssured.baseURI = "http://localhost:8001/tasks-backend";
    }

    @Test
    public void shouldGetTasks(){
        RestAssured.given()
                .contentType("application/json")
                .accept("application/json")
                .when()
                .get("/todo")
                .then()
                .statusCode(200)
        ;
    }

    @Test
    public void shouldInsertTask(){
        RestAssured.given()
                .contentType("application/json")
                .accept("application/json")
                .body("{\"task\":\""+ description +"\",\"dueDate\":\""+ date +"\"}")
                .when()
                .post("/todo")
                .then()
                .statusCode(201)
                .body("id", Matchers.notNullValue())
                .body("task", Matchers.is(description))
        ;
    }

    @Test
    public void shouldNotInsertTaskWithoutDate(){
        RestAssured.given()
                .contentType("application/json")
                .accept("application/json")
                .body("{\"task\":\"" + description + "\"}")
                .when()
                .post("/todo")
                .then()
                .statusCode(400)
                .body("error", Matchers.is("Bad Request"))
                .body("message", Matchers.is("Fill the due date"))
        ;
    }

    @Test
    public void shouldNotInsertTaskWithoutDescription(){
        RestAssured.given()
                .contentType("application/json")
                .accept("application/json")
                .body("{\"dueDate\":\"" + date + "\"}")
                .when()
                .post("/todo")
                .then()
                .statusCode(400)
                .body("error", Matchers.is("Bad Request"))
                .body("message", Matchers.is("Fill the task description"))
        ;
    }

    @Test
    public void shouldNotInsertTaskWithPastDuoDate(){
        RestAssured.given()
                .contentType("application/json")
                .accept("application/json")
                .body("{\"task\":\""+ description +"\",\"dueDate\":\"2000-10-21\"}")
                .when()
                .post("/todo")
                .then()
                .statusCode(400)
                .body("error", Matchers.is("Bad Request"))
                .body("message", Matchers.is("Due date must not be in past"))
        ;
    }
}
