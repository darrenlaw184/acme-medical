/********************************************************************************************************
 * File:  MedicalTrainingResource.java Course Materials CST 8277
 *
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * @author (original) Mike Norman
 * 
 */
package acmemedical.rest.resource;

import static acmemedical.utility.MyConstants.ADMIN_ROLE;
import static acmemedical.utility.MyConstants.MEDICAL_TRAINING_RESOURCE_NAME;
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

import acmemedical.ejb.ACMEMedicalService;
import acmemedical.entity.MedicalTraining;

@Path(MEDICAL_TRAINING_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MedicalTrainingResource {

    private static final Logger LOG = LogManager.getLogger();

    @EJB
    protected ACMEMedicalService service;

    @Inject
    protected SecurityContext sc;

    @GET
    // Any user can retrieve the list of MedicalTraining
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    public Response getMedicalTrainings() {
        LOG.debug("retrieving all medical trainings ...");
        List<MedicalTraining> medicalTrainings = service.getAllMedicalTrainings();
        Response response = Response.ok(medicalTrainings).build();
        return response;
    }

    @GET
    // Any user can retrieve the list of MedicalTraining
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response getMedicalTrainingById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        LOG.debug("try to retrieve specific medical training " + id);
        MedicalTraining medicalTraining = service.getMedicalTrainingById(id);
        Response response = Response.status(medicalTraining == null ? Status.NOT_FOUND : Status.OK).entity(medicalTraining).build();
        return response;
    }

    @POST
    // Only ADMIN can add new MedicalTraining
    @RolesAllowed({ADMIN_ROLE})
    public Response addMedicalTraining(MedicalTraining newMedicalTraining) {
        LOG.debug("adding new medical training = {}", newMedicalTraining);
        MedicalTraining newMedicalTrainingWithIdTimestamps = service.persistMedicalTraining(newMedicalTraining);
        Response response = Response.ok(newMedicalTrainingWithIdTimestamps).build();
        return response;
    }

    @PUT
    // Only ADMIN can update MedicalTraining
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response updateMedicalTraining(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id, MedicalTraining updatingMedicalTraining) {
        LOG.debug("updating medical training with id = {}", id);
        MedicalTraining updatedMedicalTraining = service.updateMedicalTraining(id, updatingMedicalTraining);
        Response response = Response.ok(updatedMedicalTraining).build();
        return response;
    }

    @DELETE
    // Only ADMIN can delete MedicalTraining
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response deleteMedicalTraining(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        LOG.debug("deleting medical training with id = {}", id);
        MedicalTraining deletedMedicalTraining = service.deleteMedicalTraining(id);
        Response response = Response.ok(deletedMedicalTraining).build();
        return response;
    }
}