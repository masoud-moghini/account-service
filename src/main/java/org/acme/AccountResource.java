package org.acme;


import javax.annotation.PostConstruct;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Path("/accounts")
public class AccountResource {
    Set<Account> accounts = new HashSet<>();




    @Provider
    public static class ErrorMapper implements ExceptionMapper<Exception>{

        @Override
        public Response toResponse(Exception e) {
            int code = 500;
            if (e instanceof WebApplicationException){
                code = ((WebApplicationException) e).getResponse().getStatus();
            }
            JsonObjectBuilder builder = Json.createObjectBuilder()
                    .add("exceptionType",e.getClass().getName())
                    .add("code",code);

            if (e.getMessage() !=null ){
                builder.add("message",e.getMessage());
            }
            return Response.status(code).entity(builder.build()).build();
        }
    }
    @PostConstruct
    public void setup() {
        accounts.add(new Account(123456789L, 987654321L, "George Baird", new
                BigDecimal("354.23")));
        accounts.add(new Account(121212121L, 888777666L, "Mary Taylor", new
                BigDecimal("560.03")));
        accounts.add(new Account(545454545L, 222444999L, "Diana Rigg", new
                BigDecimal("422.00")));
    }
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response allAccounts() {
        return Response.status(200).entity(this.accounts).build();
    }


    @GET
    @Path("/{accountNumber}")
    @Produces(MediaType.APPLICATION_JSON)
    public Account getAccount(@PathParam("accountNumber") Long accountNumber) {
        Optional<Account> response = accounts.stream()
                .filter(acct -> acct.getAccountNumber().equals(accountNumber))
                .findFirst();
        return response.orElseThrow(()
                -> new WebApplicationException("Account with id of " + accountNumber +
                "does not exist.",404));
    }


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createAccount(Account account){
        if (account.getAccountNumber() == null){
            throw new WebApplicationException("Account id can not be null",400);
        }
        accounts.add(account);
        return Response.status(201).entity(account).build();
    }

}
