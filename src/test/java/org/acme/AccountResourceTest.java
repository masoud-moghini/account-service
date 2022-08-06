package org.acme;


import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;


import io.restassured.response.Response;
import org.junit.jupiter.api.TestMethodOrder;

import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.StringContains.containsString;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@QuarkusTest
public class AccountResourceTest {
    @Test
    void testRetrieveAll(){
         Response result = given()
            .when().get("/accounts")
            .then().statusCode(200)
                .body(
                        containsString("George Baird"),
                        containsString("Mary Taylor"),
                        containsString("Diana Rigg")
                )
                .extract()
                .response();


         List<Account> accounts = result.jsonPath().getList("$");
         assertThat(accounts,not(empty()));
         assertThat(accounts,hasSize(4));
    }

    @Test
    void testRetrieveAccount(){
        Account result = given()
                .when().get("/accounts/{accountNumber}","121212121")
                .then().statusCode(200)
                .extract().as(Account.class);
        assertThat(result.getCustomerName(),equalTo("Mary Taylor"));
        assertThat(result.getAccountNumber(),equalTo(121212121L));
        assertThat(result.getBalance(),equalTo(new BigDecimal("560.03")));
        assertThat(result.getAccountStatus(),equalTo(AccountStatus.OPEN));
    }


    @Test
    void testCreationAccount(){
        Account test = new Account(16511561L,87461651L,"Masoud Moghini",new BigDecimal("1264163"));
        given().contentType(MediaType.APPLICATION_JSON)
                .body(test)
                .when()
                .post("/accounts")
                .then()
                .statusCode(201)
                .extract()
                .as(Account.class);

        Response result = given()
                .when()
                .get("/accounts")
                .then()
                .statusCode(200)
                .body(containsString("Masoud Moghini"))
                .extract().response();

        List<Account> accounts = result.jsonPath().getList("$");
        assertThat(accounts,not(empty()));
        assertThat(accounts.size(),equalTo(4));
    }
}
