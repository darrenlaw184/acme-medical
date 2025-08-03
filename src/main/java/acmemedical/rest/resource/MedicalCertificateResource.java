/********************************************************************************************************
 * File:  MedicalCertificateResource.java Course Materials CST 8277
 *
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * @author (original) Mike Norman
 * 
 */
package acmemedical.rest.resource;

import static acmemedical.utility.MyConstants.ADMIN_ROLE;
import static acmemedical.utility.MyConstants.MEDICAL_CERTIFICATE_RESOURCE_NAME;
import static acmemedical.utility.MyConstants.RESOURCE_PATH_ID_ELEMENT;
import static acmemedical.utility.MyConstants.RESOURCE_PATH_ID_PATH;
import static acmemedical.utility.MyConstants.USER_ROLE;

import java.util.List;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.security.enterprise.SecurityContext;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.soteria.WrappingCallerPrincipal;

import acmemedical.ejb.ACMEMedicalService;
import acmemedical.entity.MedicalCertificate;
import acmemedical.entity.SecurityUser;

@Path(MEDICAL_CERTIFICATE_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MedicalCertificateResource {

    private static final Logger LOG = LogManager.getLogger();

    @EJB
    protected ACMEMedicalService service;

    @Inject
    protected SecurityContext sc;

    @GET
    // Only an 'ADMIN_ROLE' user can apply CRUD to one or all MedicalCertificate.
    @RolesAllowed({ADMIN_ROLE})
    public Response getMedicalCertificates() {
        LOG.debug("retrieving all medical certificates ...");
        List<MedicalCertificate> medicalCertificates = service.getAllMedicalCertificates();
        Response response = Response.ok(medicalCertificates).build();
        return response;
    }

    @GET
    @Path(RESOURCE_PATH_ID_PATH)
    public Response getMedicalCertificateById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        LOG.debug("try to retrieve specific medical certificate " + id);
        Response response = null;
        MedicalCertificate medicalCertificate = null;

        if (sc.isCallerInRole(ADMIN_ROLE)) {
            // Only an 'ADMIN_ROLE' user can apply CRUD to one or all MedicalCertificate.
            medicalCertificate = service.getMedicalCertificateById(id);
            response = Response.status(medicalCertificate == null ? Status.NOT_FOUND : Status.OK).entity(medicalCertificate).build();
        } else if (sc.isCallerInRole(USER_ROLE)) {
            // Only a 'USER_ROLE' user can read their own MedicalCertificate.
            WrappingCallerPrincipal wCallerPrincipal = (WrappingCallerPrincipal) sc.getCallerPrincipal();
            SecurityUser sUser = (SecurityUser) wCallerPrincipal.getWrapped();
            if (sUser.getPhysician() != null) {
                medicalCertificate = service.getMedicalCertificateById(id);
                // Check if this certificate belongs to the user's physician
                if (medicalCertificate != null && medicalCertificate.getOwner() != null && 
                    medicalCertificate.getOwner().getId() == sUser.getPhysician().getId()) {
                    response = Response.status(Status.OK).entity(medicalCertificate).build();
                } else {
                    throw new ForbiddenException("User trying to access resource it does not own");
                }
            } else {
                response = Response.status(Status.FORBIDDEN).build();
            }
        } else {
            response = Response.status(Status.BAD_REQUEST).build();
        }
        return response;
    }

    @POST
    // Only an 'ADMIN_ROLE' user can apply CRUD to one or all MedicalCertificate.
    @RolesAllowed({ADMIN_ROLE})
    public Response addMedicalCertificate(MedicalCertificate newMedicalCertificate) {
        LOG.debug("adding new medical certificate = {}", newMedicalCertificate);
        MedicalCertificate newMedicalCertificateWithIdTimestamps = service.persistMedicalCertificate(newMedicalCertificate);
        Response response = Response.ok(newMedicalCertificateWithIdTimestamps).build();
        return response;
    }

    @PUT
    // Only an 'ADMIN_ROLE' user can apply CRUD to one or all MedicalCertificate.
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response updateMedicalCertificate(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id, MedicalCertificate updatingMedicalCertificate) {
        LOG.debug("updating medical certificate with id = {}", id);
        MedicalCertificate updatedMedicalCertificate = service.updateMedicalCertificate(id, updatingMedicalCertificate);
        Response response = Response.ok(updatedMedicalCertificate).build();
        return response;
    }

    @DELETE
    // Only an 'ADMIN_ROLE' user can apply CRUD to one or all MedicalCertificate.
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response deleteMedicalCertificate(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        LOG.debug("deleting medical certificate with id = {}", id);
        MedicalCertificate deletedMedicalCertificate = service.deleteMedicalCertificate(id);
        Response response = Response.ok(deletedMedicalCertificate).build();
        return response;
    }
}