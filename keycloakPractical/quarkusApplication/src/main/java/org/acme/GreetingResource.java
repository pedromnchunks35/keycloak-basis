package org.acme;

import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/hello")
@Authenticated
public class GreetingResource {
    @Inject
    SecurityIdentity identity;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @PermitAll
    public String hello() {
        return "Hello from Quarkus REST";
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/2/")
    @RolesAllowed("admin")
    public Response getForAdmin() {
        return Response.ok(identity.getPrincipal()).status(Response.Status.ACCEPTED).build();
    }
}
