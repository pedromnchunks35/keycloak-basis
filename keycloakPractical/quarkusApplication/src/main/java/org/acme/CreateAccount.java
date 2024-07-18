package org.acme;

import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;

@Path("account")
public class CreateAccount {
    Keycloak keycloak;

    @PostConstruct
    public void initKeyCloak() {
        keycloak = KeycloakBuilder.builder()
                .serverUrl("http://172.24.166.62:8080")
                .realm("master")
                .clientId("admin-cli")
                .clientSecret("Z59eE2tuhlPsIjxTW9URy0X5LYM2PKGg")
                .grantType("password")
                .username("admin")
                .password("admin")
                .build();
    }

    @PreDestroy
    public void closeKeyCloak() {
        keycloak.close();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response createAccount() {
        // ? User representation
        UserRepresentation user = new UserRepresentation();
        user.setUsername("piroca2");
        user.setEmail("piroca2@gmail.com");
        user.setFirstName("piroca2");
        user.setLastName("silva");
        user.setEnabled(true);
        // ? Define password
        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false);
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue("12341234");
        user.setCredentials(Arrays.asList(passwordCred));
        // ? Create user
        RealmResource realmResource = keycloak.realm("myrealm");
        UsersResource usersResource = realmResource.users();
        Response response = usersResource.create(user);
        // ? Get user id
        String userId = CreatedResponseUtil.getCreatedId(response);
        // ? Get user
        UserResource userResource = usersResource.get(userId);
        // ? Add roles
        RoleRepresentation role = realmResource.roles()
                .get("admin").toRepresentation();
        userResource.roles().realmLevel().add(Arrays.asList(role));

        return Response.ok("Done").status(Response.Status.ACCEPTED).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("username")
    public Response queryByUsername() {
        List<UserRepresentation> result = keycloak.realm("myrealm").users().search("pedro");
        return Response.ok(result).status(Response.Status.ACCEPTED).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("token")
    public Response getToken() {
        Keycloak newUser = KeycloakBuilder
                .builder()
                .serverUrl("http://172.24.166.62:8080")
                .realm("myrealm")
                .clientId("quarkus-be")
                .clientSecret("seJ0fxOV5sQqgWVc0kIY4VKNCO2RQjuN")
                .grantType("password")
                .username("pedro")
                .password("12341234")
                .build();
        AccessTokenResponse token = newUser.tokenManager().grantToken();
        return Response.ok(token).status(Response.Status.ACCEPTED).build();
    }
}
