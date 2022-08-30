package org.acme;


import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Path("/accounts")
public class AccountResource {

    @Inject AccountRepository accountRepository;
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
    }
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Account> allAccounts() {
        return accountRepository.listAll();
    }


    @GET
    @Path("/{accountNumber}")
    @Produces(MediaType.APPLICATION_JSON)
    public Account getAccount(@PathParam("accountNumber") Long accountNumber) {
        try{
            return accountRepository.findByAccountNumber(accountNumber);
        }catch (NoResultException nre){
            throw new WebApplicationException("Account with "+accountNumber+" does not exist",404);
        }
    }


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response createAccount(Account account){
        if (account.getAccountNumber() == null){
            throw new WebApplicationException("Account id can not be null",400);
        }
        accountRepository.persist(account);
        return Response.status(201).entity(account).build();
    }
    @PUT()
    @Path("/withdraw/{accountNumber}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Account withdrawal(@PathParam("accountNumber") Long accountNumber,String amount){
        Account entity = getAccount(accountNumber);
        entity.withdrawFunds(new BigDecimal(amount));
        return entity;
    }

    @PUT()
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Account update(Account account){
        accountRepository.persist(account);
        return account;
    }

}
