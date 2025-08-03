/********************************************************************************************************
 * File:  PrescriptionResource.java Course Materials CST 8277
 *
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * @author (original) Mike Norman
 * 
 */
package acmemedical.rest.resource;

import static acmemedical.utility.MyConstants.ADMIN_ROLE;
import static acmemedical.utility.MyConstants.PRESCRIPTION_RESOURCE_NAME;
import static acmemedical.utility.MyConstants.PRESCRIPTION_RESOURCE_PATH_ID_PATH;
import static acmemedical.utility.MyConstants.PRESCRIPTION_RESOURCE_PATH_CREATE_PATH;
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
import acmemedical.entity.Medicine;
import acmemedical.entity.Patient;
import acmemedical.entity.Physician;
import acmemedical.entity.Prescription;
import acmemedical.entity.PrescriptionPK;

@Path(PRESCRIPTION_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PrescriptionResource {

    private static final Logger LOG = LogManager.getLogger();

    @EJB
    protected ACMEMedicalService service;

    @Inject
    protected SecurityContext sc;

    @GET
    // Only ADMIN can get all prescriptions
    @RolesAllowed({ADMIN_ROLE})
    public Response getPrescriptions() {
        LOG.debug("retrieving all prescriptions ...");
        List<Prescription> prescriptions = service.getAllPrescriptions();
        Response response = Response.ok(prescriptions).build();
        return response;
    }

    @GET
    // Only ADMIN can get specific prescription
    @RolesAllowed({ADMIN_ROLE})
    @Path(PRESCRIPTION_RESOURCE_PATH_ID_PATH)
    public Response getPrescriptionByPhysicianAndPatient(@PathParam("physician_id") int physicianId, @PathParam("patient_id") int patientId) {
        LOG.debug("try to retrieve prescription for physician {} and patient {}", physicianId, patientId);
        Prescription prescription = service.getPrescriptionByPhysicianAndPatient(physicianId, patientId);
        Response response = Response.status(prescription == null ? Status.NOT_FOUND : Status.OK).entity(prescription).build();
        return response;
    }

    @POST
    // Only ADMIN can create prescriptions
    @RolesAllowed({ADMIN_ROLE})
    @Path(PRESCRIPTION_RESOURCE_PATH_CREATE_PATH)
    public Response addPrescription(@PathParam("physician_id") int physicianId, 
                                   @PathParam("patient_id") int patientId, 
                                   @PathParam("medicine_id") int medicineId,
                                   Prescription newPrescription) {
        LOG.debug("adding new prescription for physician {}, patient {}, medicine {}", physicianId, patientId, medicineId);
        
        // Set up the prescription with proper relationships
        Physician physician = service.getPhysicianById(physicianId);
        Patient patient = service.getPatientById(patientId);
        Medicine medicine = service.getMedicineById(medicineId);
        
        if (physician == null || patient == null || medicine == null) {
            return Response.status(Status.BAD_REQUEST).entity("Invalid physician, patient, or medicine ID").build();
        }
        
        newPrescription.setPhysician(physician);
        newPrescription.setPatient(patient);
        newPrescription.setMedicine(medicine);
        
        Prescription newPrescriptionWithIdTimestamps = service.persistPrescription(newPrescription);
        Response response = Response.ok(newPrescriptionWithIdTimestamps).build();
        return response;
    }

    @PUT
    // Only ADMIN can update prescriptions
    @RolesAllowed({ADMIN_ROLE})
    @Path(PRESCRIPTION_RESOURCE_PATH_ID_PATH)
    public Response updatePrescription(@PathParam("physician_id") int physicianId, 
                                      @PathParam("patient_id") int patientId, 
                                      Prescription updatingPrescription) {
        LOG.debug("updating prescription for physician {} and patient {}", physicianId, patientId);
        Prescription updatedPrescription = service.updatePrescription(physicianId, patientId, updatingPrescription);
        Response response = Response.ok(updatedPrescription).build();
        return response;
    }

    @DELETE
    // Only ADMIN can delete prescriptions
    @RolesAllowed({ADMIN_ROLE})
    @Path(PRESCRIPTION_RESOURCE_PATH_ID_PATH)
    public Response deletePrescription(@PathParam("physician_id") int physicianId, @PathParam("patient_id") int patientId) {
        LOG.debug("deleting prescription for physician {} and patient {}", physicianId, patientId);
        Prescription deletedPrescription = service.deletePrescription(physicianId, patientId);
        Response response = Response.ok(deletedPrescription).build();
        return response;
    }
}