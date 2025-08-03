/********************************************************************************************************
 * File:  TestResource.java Course Materials CST 8277
 *
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * @author (original) Mike Norman
 * 
 */
package acmemedical.rest.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Path("test")
@Produces(MediaType.APPLICATION_JSON)
public class TestResource {

    private static final Logger LOG = LogManager.getLogger();

    @GET
    public Response testEndpoint() {
        LOG.debug("Test endpoint accessed successfully!");
        String response = "{\"message\": \"ACME Medical REST API is working!\", \"status\": \"success\", \"timestamp\": \"" + java.time.LocalDateTime.now() + "\"}";
        return Response.ok(response).build();
    }
    
    @GET
    @Path("health")
    public Response healthCheck() {
        LOG.debug("Health check endpoint accessed!");
        String response = "{\"status\": \"healthy\", \"service\": \"ACME Medical REST API\", \"version\": \"1.0\"}";
        return Response.ok(response).build();
    }
} 