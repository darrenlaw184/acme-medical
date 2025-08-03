/********************************************************************************************************
 * File:  TestACMEMedicalSystem.java
 * Course Materials CST 8277
 * Teddy Yap
 * (Original Author) Mike Norman
 *
 */
package acmemedical;

import static acmemedical.utility.MyConstants.APPLICATION_API_VERSION;
import static acmemedical.utility.MyConstants.APPLICATION_CONTEXT_ROOT;
import static acmemedical.utility.MyConstants.DEFAULT_ADMIN_USER;
import static acmemedical.utility.MyConstants.DEFAULT_ADMIN_USER_PASSWORD;
import static acmemedical.utility.MyConstants.DEFAULT_USER;
import static acmemedical.utility.MyConstants.DEFAULT_USER_PASSWORD;
import static acmemedical.utility.MyConstants.PHYSICIAN_RESOURCE_NAME;
import static acmemedical.utility.MyConstants.PATIENT_RESOURCE_NAME;
import static acmemedical.utility.MyConstants.MEDICINE_RESOURCE_NAME;
import static acmemedical.utility.MyConstants.MEDICAL_SCHOOL_RESOURCE_NAME;
import static acmemedical.utility.MyConstants.MEDICAL_TRAINING_RESOURCE_NAME;
import static acmemedical.utility.MyConstants.MEDICAL_CERTIFICATE_RESOURCE_NAME;
import static acmemedical.utility.MyConstants.PRESCRIPTION_RESOURCE_NAME;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.List;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.logging.LoggingFeature;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import acmemedical.entity.Physician;
import acmemedical.entity.Patient;
import acmemedical.entity.Medicine;
import acmemedical.entity.MedicalSchool;
import acmemedical.entity.MedicalTraining;
import acmemedical.entity.MedicalCertificate;
import acmemedical.entity.Prescription;
import acmemedical.entity.PublicSchool;
import acmemedical.entity.PrivateSchool;

@SuppressWarnings("unused")

@TestMethodOrder(MethodOrderer.MethodName.class)
public class TestACMEMedicalSystem {
    private static final Class<?> _thisClaz = MethodHandles.lookup().lookupClass();
    private static final Logger logger = LogManager.getLogger(_thisClaz);

    static final String HTTP_SCHEMA = "http";
    static final String HOST = "localhost";
    static final int PORT = 8080;

    // Test fixture(s)
    static URI uri;
    static HttpAuthenticationFeature adminAuth;
    static HttpAuthenticationFeature userAuth;

    @BeforeAll
    public static void oneTimeSetUp() throws Exception {
        logger.debug("oneTimeSetUp");
        uri = UriBuilder
            .fromUri(APPLICATION_CONTEXT_ROOT + APPLICATION_API_VERSION)
            .scheme(HTTP_SCHEMA)
            .host(HOST)
            .port(PORT)
            .build();
        adminAuth = HttpAuthenticationFeature.basic(DEFAULT_ADMIN_USER, DEFAULT_ADMIN_USER_PASSWORD);
        userAuth = HttpAuthenticationFeature.basic(DEFAULT_USER, DEFAULT_USER_PASSWORD);
    }

    protected WebTarget webTarget;
    @BeforeEach
    public void setUp() {
        Client client = ClientBuilder.newClient().register(MyObjectMapperProvider.class).register(new LoggingFeature());
        webTarget = client.target(uri);
    }

    @Test
    public void test01_all_physicians_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            //.register(userAuth)
            .register(adminAuth)
            .path(PHYSICIAN_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        List<Physician> physicians = response.readEntity(new GenericType<List<Physician>>(){});
        assertThat(physicians, is(not(empty())));
        assertThat(physicians, hasSize(1));
    }

    @Test
    public void test02_all_physicians_with_userrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(userAuth)
            .path(PHYSICIAN_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(403)); // USER_ROLE should not be able to get all physicians
    }

    @Test
    public void test03_get_physician_by_id_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path(PHYSICIAN_RESOURCE_NAME + "/1")
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        Physician physician = response.readEntity(Physician.class);
        assertThat(physician.getId(), is(1));
    }

    @Test
    public void test04_get_physician_by_id_with_userrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(userAuth)
            .path(PHYSICIAN_RESOURCE_NAME + "/1")
            .request()
            .get();
        assertThat(response.getStatus(), is(200)); // USER_ROLE should be able to get their own physician
    }

    @Test
    public void test05_all_patients_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path(PATIENT_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        List<Patient> patients = response.readEntity(new GenericType<List<Patient>>(){});
        assertThat(patients, is(not(empty())));
    }

    @Test
    public void test06_all_patients_with_userrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(userAuth)
            .path(PATIENT_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(200)); // USER_ROLE should be able to get all patients
    }

    @Test
    public void test07_get_patient_by_id_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path(PATIENT_RESOURCE_NAME + "/1")
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        Patient patient = response.readEntity(Patient.class);
        assertThat(patient.getId(), is(1));
    }

    @Test
    public void test08_get_patient_by_id_with_userrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(userAuth)
            .path(PATIENT_RESOURCE_NAME + "/1")
            .request()
            .get();
        assertThat(response.getStatus(), is(200)); // USER_ROLE should be able to get patients
    }

    @Test
    public void test09_all_medicines_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path(MEDICINE_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        List<Medicine> medicines = response.readEntity(new GenericType<List<Medicine>>(){});
        assertThat(medicines, is(not(empty())));
    }

    @Test
    public void test10_all_medicines_with_userrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(userAuth)
            .path(MEDICINE_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(200)); // USER_ROLE should be able to get all medicines
    }

    @Test
    public void test11_get_medicine_by_id_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path(MEDICINE_RESOURCE_NAME + "/1")
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        Medicine medicine = response.readEntity(Medicine.class);
        assertThat(medicine.getId(), is(1));
    }

    @Test
    public void test12_get_medicine_by_id_with_userrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(userAuth)
            .path(MEDICINE_RESOURCE_NAME + "/1")
            .request()
            .get();
        assertThat(response.getStatus(), is(200)); // USER_ROLE should be able to get medicines
    }

    @Test
    public void test13_all_medical_schools_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path(MEDICAL_SCHOOL_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        List<MedicalSchool> medicalSchools = response.readEntity(new GenericType<List<MedicalSchool>>(){});
        assertThat(medicalSchools, is(not(empty())));
    }

    @Test
    public void test14_all_medical_schools_with_userrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(userAuth)
            .path(MEDICAL_SCHOOL_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(200)); // USER_ROLE should be able to get all medical schools
    }

    @Test
    public void test15_get_medical_school_by_id_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path(MEDICAL_SCHOOL_RESOURCE_NAME + "/1")
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        MedicalSchool medicalSchool = response.readEntity(MedicalSchool.class);
        assertThat(medicalSchool.getId(), is(1));
    }

    @Test
    public void test16_get_medical_school_by_id_with_userrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(userAuth)
            .path(MEDICAL_SCHOOL_RESOURCE_NAME + "/1")
            .request()
            .get();
        assertThat(response.getStatus(), is(200)); // USER_ROLE should be able to get medical schools
    }

    @Test
    public void test17_all_medical_trainings_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path(MEDICAL_TRAINING_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        List<MedicalTraining> medicalTrainings = response.readEntity(new GenericType<List<MedicalTraining>>(){});
        assertThat(medicalTrainings, is(not(empty())));
    }

    @Test
    public void test18_all_medical_trainings_with_userrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(userAuth)
            .path(MEDICAL_TRAINING_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(200)); // USER_ROLE should be able to get all medical trainings
    }

    @Test
    public void test19_get_medical_training_by_id_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path(MEDICAL_TRAINING_RESOURCE_NAME + "/1")
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        MedicalTraining medicalTraining = response.readEntity(MedicalTraining.class);
        assertThat(medicalTraining.getId(), is(1));
    }

    @Test
    public void test20_get_medical_training_by_id_with_userrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(userAuth)
            .path(MEDICAL_TRAINING_RESOURCE_NAME + "/1")
            .request()
            .get();
        assertThat(response.getStatus(), is(200)); // USER_ROLE should be able to get medical trainings
    }

    @Test
    public void test21_all_medical_certificates_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path(MEDICAL_CERTIFICATE_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        List<MedicalCertificate> medicalCertificates = response.readEntity(new GenericType<List<MedicalCertificate>>(){});
        assertThat(medicalCertificates, is(not(empty())));
    }

    @Test
    public void test22_all_medical_certificates_with_userrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(userAuth)
            .path(MEDICAL_CERTIFICATE_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(403)); // USER_ROLE should NOT be able to get all medical certificates
    }

    @Test
    public void test23_get_medical_certificate_by_id_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path(MEDICAL_CERTIFICATE_RESOURCE_NAME + "/1")
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        MedicalCertificate medicalCertificate = response.readEntity(MedicalCertificate.class);
        assertThat(medicalCertificate.getId(), is(1));
    }

    @Test
    public void test24_get_medical_certificate_by_id_with_userrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(userAuth)
            .path(MEDICAL_CERTIFICATE_RESOURCE_NAME + "/1")
            .request()
            .get();
        assertThat(response.getStatus(), is(200)); // USER_ROLE should be able to get their own medical certificate
    }

    @Test
    public void test25_all_prescriptions_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path(PRESCRIPTION_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        List<Prescription> prescriptions = response.readEntity(new GenericType<List<Prescription>>(){});
        assertThat(prescriptions, is(not(empty())));
    }

    @Test
    public void test26_all_prescriptions_with_userrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(userAuth)
            .path(PRESCRIPTION_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(403)); // USER_ROLE should NOT be able to get all prescriptions
    }

    // CREATE (POST) Tests

    @Test
    public void test27_add_physician_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Physician newPhysician = new Physician();
        newPhysician.setFirstName("John");
        newPhysician.setLastName("Doe");
        
        Response response = webTarget
            .register(adminAuth)
            .path(PHYSICIAN_RESOURCE_NAME)
            .request()
            .post(jakarta.ws.rs.client.Entity.json(newPhysician));
        assertThat(response.getStatus(), is(200));
        Physician createdPhysician = response.readEntity(Physician.class);
        assertThat(createdPhysician.getFirstName(), is("John"));
        assertThat(createdPhysician.getLastName(), is("Doe"));
    }

    @Test
    public void test28_add_physician_with_userrole() throws JsonMappingException, JsonProcessingException {
        Physician newPhysician = new Physician();
        newPhysician.setFirstName("Jane");
        newPhysician.setLastName("Smith");
        
        Response response = webTarget
            .register(userAuth)
            .path(PHYSICIAN_RESOURCE_NAME)
            .request()
            .post(jakarta.ws.rs.client.Entity.json(newPhysician));
        assertThat(response.getStatus(), is(403)); // USER_ROLE should NOT be able to add physicians
    }

    @Test
    public void test29_add_patient_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Patient newPatient = new Patient();
        newPatient.setFirstName("Alice");
        newPatient.setLastName("Johnson");
        newPatient.setYear(1990);
        newPatient.setAddress("123 Main St");
        newPatient.setHeight(170);
        newPatient.setWeight(65);
        newPatient.setSmoker((byte) 0);
        
        Response response = webTarget
            .register(adminAuth)
            .path(PATIENT_RESOURCE_NAME)
            .request()
            .post(jakarta.ws.rs.client.Entity.json(newPatient));
        assertThat(response.getStatus(), is(200));
        Patient createdPatient = response.readEntity(Patient.class);
        assertThat(createdPatient.getFirstName(), is("Alice"));
        assertThat(createdPatient.getLastName(), is("Johnson"));
    }

    @Test
    public void test30_add_patient_with_userrole() throws JsonMappingException, JsonProcessingException {
        Patient newPatient = new Patient();
        newPatient.setFirstName("Bob");
        newPatient.setLastName("Wilson");
        newPatient.setYear(1985);
        newPatient.setAddress("456 Oak Ave");
        newPatient.setHeight(180);
        newPatient.setWeight(75);
        newPatient.setSmoker((byte) 1);
        
        Response response = webTarget
            .register(userAuth)
            .path(PATIENT_RESOURCE_NAME)
            .request()
            .post(jakarta.ws.rs.client.Entity.json(newPatient));
        assertThat(response.getStatus(), is(403)); // USER_ROLE should NOT be able to add patients
    }

    @Test
    public void test31_add_medicine_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Medicine newMedicine = new Medicine();
        newMedicine.setDrugName("Aspirin");
        newMedicine.setManufacturerName("PharmaCorp");
        newMedicine.setDosageInformation("100mg daily");
        
        Response response = webTarget
            .register(adminAuth)
            .path(MEDICINE_RESOURCE_NAME)
            .request()
            .post(jakarta.ws.rs.client.Entity.json(newMedicine));
        assertThat(response.getStatus(), is(200));
        Medicine createdMedicine = response.readEntity(Medicine.class);
        assertThat(createdMedicine.getDrugName(), is("Aspirin"));
        assertThat(createdMedicine.getManufacturerName(), is("PharmaCorp"));
    }

    @Test
    public void test32_add_medicine_with_userrole() throws JsonMappingException, JsonProcessingException {
        Medicine newMedicine = new Medicine();
        newMedicine.setDrugName("Ibuprofen");
        newMedicine.setManufacturerName("MediCorp");
        newMedicine.setDosageInformation("200mg as needed");
        
        Response response = webTarget
            .register(userAuth)
            .path(MEDICINE_RESOURCE_NAME)
            .request()
            .post(jakarta.ws.rs.client.Entity.json(newMedicine));
        assertThat(response.getStatus(), is(403)); // USER_ROLE should NOT be able to add medicines
    }

    @Test
    public void test33_add_medical_school_with_adminrole() throws JsonMappingException, JsonProcessingException {
        PublicSchool newMedicalSchool = new PublicSchool();
        newMedicalSchool.setName("University Medical School");
        
        Response response = webTarget
            .register(adminAuth)
            .path(MEDICAL_SCHOOL_RESOURCE_NAME)
            .request()
            .post(jakarta.ws.rs.client.Entity.json(newMedicalSchool));
        assertThat(response.getStatus(), is(200));
        MedicalSchool createdMedicalSchool = response.readEntity(MedicalSchool.class);
        assertThat(createdMedicalSchool.getName(), is("University Medical School"));
    }

    @Test
    public void test34_add_medical_school_with_userrole() throws JsonMappingException, JsonProcessingException {
        PrivateSchool newMedicalSchool = new PrivateSchool();
        newMedicalSchool.setName("Private Medical College");
        
        Response response = webTarget
            .register(userAuth)
            .path(MEDICAL_SCHOOL_RESOURCE_NAME)
            .request()
            .post(jakarta.ws.rs.client.Entity.json(newMedicalSchool));
        assertThat(response.getStatus(), is(403)); // USER_ROLE should NOT be able to add medical schools
    }

    @Test
    public void test35_add_medical_training_with_adminrole() throws JsonMappingException, JsonProcessingException {
        MedicalTraining newMedicalTraining = new MedicalTraining();
        
        Response response = webTarget
            .register(adminAuth)
            .path(MEDICAL_TRAINING_RESOURCE_NAME)
            .request()
            .post(jakarta.ws.rs.client.Entity.json(newMedicalTraining));
        assertThat(response.getStatus(), is(200));
        MedicalTraining createdMedicalTraining = response.readEntity(MedicalTraining.class);
        assertThat(createdMedicalTraining.getId(), is(not(0)));
    }

    @Test
    public void test36_add_medical_training_with_userrole() throws JsonMappingException, JsonProcessingException {
        MedicalTraining newMedicalTraining = new MedicalTraining();
        
        Response response = webTarget
            .register(userAuth)
            .path(MEDICAL_TRAINING_RESOURCE_NAME)
            .request()
            .post(jakarta.ws.rs.client.Entity.json(newMedicalTraining));
        assertThat(response.getStatus(), is(403)); // USER_ROLE should NOT be able to add medical trainings
    }

    @Test
    public void test37_add_medical_certificate_with_adminrole() throws JsonMappingException, JsonProcessingException {
        MedicalCertificate newMedicalCertificate = new MedicalCertificate();
        newMedicalCertificate.setSigned((byte) 1);
        
        Response response = webTarget
            .register(adminAuth)
            .path(MEDICAL_CERTIFICATE_RESOURCE_NAME)
            .request()
            .post(jakarta.ws.rs.client.Entity.json(newMedicalCertificate));
        assertThat(response.getStatus(), is(200));
        MedicalCertificate createdMedicalCertificate = response.readEntity(MedicalCertificate.class);
        assertThat(createdMedicalCertificate.getSigned(), is((byte) 1));
    }

    @Test
    public void test38_add_medical_certificate_with_userrole() throws JsonMappingException, JsonProcessingException {
        MedicalCertificate newMedicalCertificate = new MedicalCertificate();
        newMedicalCertificate.setSigned((byte) 0);
        
        Response response = webTarget
            .register(userAuth)
            .path(MEDICAL_CERTIFICATE_RESOURCE_NAME)
            .request()
            .post(jakarta.ws.rs.client.Entity.json(newMedicalCertificate));
        assertThat(response.getStatus(), is(403)); // USER_ROLE should NOT be able to add medical certificates
    }

    // DELETE Tests

    @Test
    public void test39_delete_medicine_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path(MEDICINE_RESOURCE_NAME + "/1")
            .request()
            .delete();
        assertThat(response.getStatus(), is(200));
    }

    @Test
    public void test40_delete_medicine_with_userrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(userAuth)
            .path(MEDICINE_RESOURCE_NAME + "/2")
            .request()
            .delete();
        assertThat(response.getStatus(), is(403)); // USER_ROLE should NOT be able to delete medicines
    }

    @Test
    public void test41_delete_patient_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path(PATIENT_RESOURCE_NAME + "/1")
            .request()
            .delete();
        assertThat(response.getStatus(), is(200));
    }

    @Test
    public void test42_delete_patient_with_userrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(userAuth)
            .path(PATIENT_RESOURCE_NAME + "/2")
            .request()
            .delete();
        assertThat(response.getStatus(), is(403)); // USER_ROLE should NOT be able to delete patients
    }

    @Test
    public void test43_delete_medical_school_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path(MEDICAL_SCHOOL_RESOURCE_NAME + "/1")
            .request()
            .delete();
        assertThat(response.getStatus(), is(200));
    }

    @Test
    public void test44_delete_medical_school_with_userrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(userAuth)
            .path(MEDICAL_SCHOOL_RESOURCE_NAME + "/2")
            .request()
            .delete();
        assertThat(response.getStatus(), is(403)); // USER_ROLE should NOT be able to delete medical schools
    }

    @Test
    public void test45_delete_medical_training_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path(MEDICAL_TRAINING_RESOURCE_NAME + "/1")
            .request()
            .delete();
        assertThat(response.getStatus(), is(200));
    }

    @Test
    public void test46_delete_medical_training_with_userrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(userAuth)
            .path(MEDICAL_TRAINING_RESOURCE_NAME + "/2")
            .request()
            .delete();
        assertThat(response.getStatus(), is(403)); // USER_ROLE should NOT be able to delete medical trainings
    }

    @Test
    public void test47_delete_medical_certificate_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path(MEDICAL_CERTIFICATE_RESOURCE_NAME + "/1")
            .request()
            .delete();
        assertThat(response.getStatus(), is(200));
    }

    @Test
    public void test48_delete_medical_certificate_with_userrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(userAuth)
            .path(MEDICAL_CERTIFICATE_RESOURCE_NAME + "/2")
            .request()
            .delete();
        assertThat(response.getStatus(), is(403)); // USER_ROLE should NOT be able to delete medical certificates
    }

    // Error Handling Tests

    @Test
    public void test49_get_nonexistent_physician() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path(PHYSICIAN_RESOURCE_NAME + "/999")
            .request()
            .get();
        assertThat(response.getStatus(), is(404)); // Should return NOT_FOUND for non-existent resource
    }

    @Test
    public void test50_get_nonexistent_patient() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path(PATIENT_RESOURCE_NAME + "/999")
            .request()
            .get();
        assertThat(response.getStatus(), is(404)); // Should return NOT_FOUND for non-existent resource
    }

    @Test
    public void test51_get_nonexistent_medicine() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path(MEDICINE_RESOURCE_NAME + "/999")
            .request()
            .get();
        assertThat(response.getStatus(), is(404)); // Should return NOT_FOUND for non-existent resource
    }

    @Test
    public void test52_unauthorized_access_without_credentials() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .path(PHYSICIAN_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(401)); // Should return UNAUTHORIZED without credentials
    }

    // Additional comprehensive tests

    @Test
    public void test53_get_medical_certificate_with_wrong_owner() throws JsonMappingException, JsonProcessingException {
        // This test assumes that the user is trying to access a medical certificate that doesn't belong to them
        Response response = webTarget
            .register(userAuth)
            .path(MEDICAL_CERTIFICATE_RESOURCE_NAME + "/999")
            .request()
            .get();
        assertThat(response.getStatus(), is(403)); // Should return FORBIDDEN when accessing wrong resource
    }

    @Test
    public void test54_comprehensive_physician_crud_cycle() throws JsonMappingException, JsonProcessingException {
        // Create
        Physician newPhysician = new Physician();
        newPhysician.setFirstName("Test");
        newPhysician.setLastName("Doctor");
        
        Response createResponse = webTarget
            .register(adminAuth)
            .path(PHYSICIAN_RESOURCE_NAME)
            .request()
            .post(jakarta.ws.rs.client.Entity.json(newPhysician));
        assertThat(createResponse.getStatus(), is(200));
        Physician createdPhysician = createResponse.readEntity(Physician.class);
        int physicianId = createdPhysician.getId();
        
        // Read
        Response readResponse = webTarget
            .register(adminAuth)
            .path(PHYSICIAN_RESOURCE_NAME + "/" + physicianId)
            .request()
            .get();
        assertThat(readResponse.getStatus(), is(200));
        Physician readPhysician = readResponse.readEntity(Physician.class);
        assertThat(readPhysician.getFirstName(), is("Test"));
        assertThat(readPhysician.getLastName(), is("Doctor"));
    }

    @Test
    public void test55_comprehensive_patient_crud_cycle() throws JsonMappingException, JsonProcessingException {
        // Create
        Patient newPatient = new Patient();
        newPatient.setFirstName("Test");
        newPatient.setLastName("Patient");
        newPatient.setYear(1995);
        newPatient.setAddress("Test Address");
        newPatient.setHeight(175);
        newPatient.setWeight(70);
        newPatient.setSmoker((byte) 0);
        
        Response createResponse = webTarget
            .register(adminAuth)
            .path(PATIENT_RESOURCE_NAME)
            .request()
            .post(jakarta.ws.rs.client.Entity.json(newPatient));
        assertThat(createResponse.getStatus(), is(200));
        Patient createdPatient = createResponse.readEntity(Patient.class);
        int patientId = createdPatient.getId();
        
        // Read
        Response readResponse = webTarget
            .register(adminAuth)
            .path(PATIENT_RESOURCE_NAME + "/" + patientId)
            .request()
            .get();
        assertThat(readResponse.getStatus(), is(200));
        Patient readPatient = readResponse.readEntity(Patient.class);
        assertThat(readPatient.getFirstName(), is("Test"));
        assertThat(readPatient.getLastName(), is("Patient"));
        assertThat(readPatient.getYear(), is(1995));
    }
}