package org.acme;


import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
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
@QuarkusTestResource(H2DatabaseTestResource.class)
public class AccountResourceTest {
    @Test
    void testRetrieveAll(){
         Response result = given()
            .when().get("/accounts")
            .then().statusCode(200)
                .body(
                        containsString("Debbie Hall"),
                        containsString("David Tennant")
                )
                .extract()
                .response();


         List<Account> accounts = result.jsonPath().getList("$");
         assertThat(accounts,not(empty()));
         assertThat(accounts,hasSize(9));
    }


    @Test
    void testRetrieveAccount(){
        Account result = given()
                .when().get("/accounts/{accountNumber}","123456789")
                .then().statusCode(200)
                .extract().as(Account.class);
        assertThat(result.getCustomerName(),equalTo("Debbie Hall"));
        assertThat(result.getAccountNumber(),equalTo(123456789L));
        assertThat(result.getBalance(),equalTo(new BigDecimal("550.78")));
        assertThat(result.getAccountStatus(),equalTo(AccountStatus.OPEN));
    }


    @Test
    void testCreationAccount(){
        Account test = new Account();
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
                .body(containsString("Debbie Hall"))
                .extract().response();

        List<Account> accounts = result.jsonPath().getList("$");
        assertThat(accounts,not(empty()));
        assertThat(accounts.size(),equalTo(9));
    }


}
