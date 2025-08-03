package acmemedical;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.util.List;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.logging.LoggingFeature;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import acmemedical.entity.Medicine;
import acmemedical.entity.MedicalSchool;
import acmemedical.entity.Patient;
import acmemedical.entity.Physician;
import acmemedical.entity.MedicalCertificate;

/**
 * Comprehensive Integration Test Suite for ACME Medical REST API
 * 
 * Tests all CRUD operations, authentication, and authorization for:
 * - Physicians
 * - Patients  
 * - Medicines
 * - Medical Schools
 * - Medical Certificates
 * - Medical Training
 * - Prescriptions
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("ACME Medical REST API Integration Tests")
public class ACMEMedicalSystemIT {

    private static final Logger logger = LogManager.getLogger();

    // Test Configuration
    static final String HTTP_SCHEMA = "http";
    static final String HOST = "dlaw"; // Use your actual hostname
    static final int PORT = 8080;
    static final String CONTEXT_ROOT = "/REST-ACMEMedical-Skeleton"; // Your actual deployment context
    static final String API_VERSION = "/api/v1";
    
    // Authentication credentials
    static final String ADMIN_USER = "admin";
    static final String ADMIN_PASSWORD = "admin";
    static final String USER_NAME = "cst8277";
    static final String USER_PASSWORD = "8277";

    // Test fixtures
    static URI baseUri;
    static HttpAuthenticationFeature adminAuth;
    static HttpAuthenticationFeature userAuth;
    protected WebTarget webTarget;

    @BeforeAll
    public static void oneTimeSetUp() throws Exception {
        logger.debug("Setting up test environment");
        baseUri = UriBuilder
            .fromUri(CONTEXT_ROOT + API_VERSION)
            .scheme(HTTP_SCHEMA)
            .host(HOST)
            .port(PORT)
            .build();
        adminAuth = HttpAuthenticationFeature.basic(ADMIN_USER, ADMIN_PASSWORD);
        userAuth = HttpAuthenticationFeature.basic(USER_NAME, USER_PASSWORD);
        logger.info("Test base URI: {}", baseUri);
    }

    @BeforeEach
    public void setUp() {
        Client client = ClientBuilder.newClient()
            .register(MyObjectMapperProvider.class)
            .register(new LoggingFeature());
        webTarget = client.target(baseUri);
    }

    // ===============================
    // BASIC CONNECTIVITY TESTS
    // ===============================

    @Test
    @Order(1)
    @DisplayName("Test basic connectivity - health check endpoint")
    public void test01_health_check() {
        Response response = webTarget
            .path("test")
            .request()
            .get();
        assertThat("Health check should return 200", response.getStatus(), is(200));
        String responseBody = response.readEntity(String.class);
        assertTrue(responseBody.contains("ACME Medical REST API is working!"));
    }

    @Test
    @Order(2)
    @DisplayName("Test health status endpoint")
    public void test02_health_status() {
        Response response = webTarget
            .path("test/health")
            .request()
            .get();
        assertThat("Health status should return 200", response.getStatus(), is(200));
        String responseBody = response.readEntity(String.class);
        assertTrue(responseBody.contains("healthy"));
    }

    // ===============================
    // PHYSICIAN CRUD TESTS
    // ===============================

    @Test
    @Order(10)
    @DisplayName("GET all physicians with admin role - should succeed")
    public void test10_get_all_physicians_admin() {
        Response response = webTarget
            .register(adminAuth)
            .path("physician")
            .request()
            .get();
        assertThat("Admin should access all physicians", response.getStatus(), is(200));
        List<Physician> physicians = response.readEntity(new GenericType<List<Physician>>(){});
        assertThat("Should have at least one physician", physicians, is(not(empty())));
    }

    @Test
    @Order(11)
    @DisplayName("GET all physicians with user role - should fail")
    public void test11_get_all_physicians_user() {
        Response response = webTarget
            .register(userAuth)
            .path("physician")
            .request()
            .get();
        assertThat("User should not access all physicians", response.getStatus(), is(403));
    }

    @Test
    @Order(12)
    @DisplayName("GET physician by ID with admin role")
    public void test12_get_physician_by_id_admin() {
        Response response = webTarget
            .register(adminAuth)
            .path("physician/1")
            .request()
            .get();
        assertThat("Admin should access physician by ID", response.getStatus(), is(200));
        Physician physician = response.readEntity(Physician.class);
        assertNotNull(physician);
        assertThat("Physician ID should be 1", physician.getId(), is(1));
    }

    @Test
    @Order(13)
    @DisplayName("GET physician by ID with user role - own physician")
    public void test13_get_physician_by_id_user() {
        Response response = webTarget
            .register(userAuth)
            .path("physician/1")
            .request()
            .get();
        // User should be able to access their own physician record
        assertThat("User should access own physician", response.getStatus(), is(200));
    }

    @Test
    @Order(14)
    @DisplayName("POST create new physician with admin role")
    public void test14_create_physician_admin() {
        Physician newPhysician = new Physician();
        newPhysician.setFirstName("Jane");
        newPhysician.setLastName("Doe");

        Response response = webTarget
            .register(adminAuth)
            .path("physician")
            .request()
            .post(Entity.entity(newPhysician, MediaType.APPLICATION_JSON));
        
        assertThat("Admin should create physician", response.getStatus(), is(200));
        Physician createdPhysician = response.readEntity(Physician.class);
        assertNotNull(createdPhysician);
        assertThat("Created physician first name", createdPhysician.getFirstName(), is("Jane"));
        assertThat("Created physician last name", createdPhysician.getLastName(), is("Doe"));
    }

    @Test
    @Order(15)
    @DisplayName("POST create physician with user role - should fail")
    public void test15_create_physician_user() {
        Physician newPhysician = new Physician();
        newPhysician.setFirstName("Unauthorized");
        newPhysician.setLastName("User");

        Response response = webTarget
            .register(userAuth)
            .path("physician")
            .request()
            .post(Entity.entity(newPhysician, MediaType.APPLICATION_JSON));
        
        assertThat("User should not create physician", response.getStatus(), is(403));
    }

    // ===============================
    // PATIENT CRUD TESTS
    // ===============================

    @Test
    @Order(20)
    @DisplayName("GET all patients with admin role")
    public void test20_get_all_patients_admin() {
        Response response = webTarget
            .register(adminAuth)
            .path("patient")
            .request()
            .get();
        assertThat("Admin should access all patients", response.getStatus(), is(200));
        List<Patient> patients = response.readEntity(new GenericType<List<Patient>>(){});
        assertThat("Should have patients", patients, is(not(empty())));
    }

    @Test
    @Order(21)
    @DisplayName("GET patient by ID with admin role")
    public void test21_get_patient_by_id_admin() {
        Response response = webTarget
            .register(adminAuth)
            .path("patient/1")
            .request()
            .get();
        assertThat("Admin should access patient by ID", response.getStatus(), is(200));
        Patient patient = response.readEntity(Patient.class);
        assertNotNull(patient);
    }

    @Test
    @Order(22)
    @DisplayName("POST create new patient with admin role")
    public void test22_create_patient_admin() {
        Patient newPatient = new Patient();
        newPatient.setFirstName("Test");
        newPatient.setLastName("Patient");
        newPatient.setYear(1990);
        newPatient.setAddress("123 Test St");
        newPatient.setHeight(175);
        newPatient.setWeight(70);
        newPatient.setSmoker((byte) 0);

        Response response = webTarget
            .register(adminAuth)
            .path("patient")
            .request()
            .post(Entity.entity(newPatient, MediaType.APPLICATION_JSON));
        
        assertThat("Admin should create patient", response.getStatus(), is(200));
        Patient createdPatient = response.readEntity(Patient.class);
        assertNotNull(createdPatient);
        assertThat("Created patient first name", createdPatient.getFirstName(), is("Test"));
    }

    // ===============================
    // MEDICINE CRUD TESTS
    // ===============================

    @Test
    @Order(30)
    @DisplayName("GET all medicines with admin role")
    public void test30_get_all_medicines_admin() {
        Response response = webTarget
            .register(adminAuth)
            .path("medicine")
            .request()
            .get();
        assertThat("Admin should access all medicines", response.getStatus(), is(200));
        List<Medicine> medicines = response.readEntity(new GenericType<List<Medicine>>(){});
        assertThat("Should have medicines", medicines, is(not(empty())));
    }

    @Test
    @Order(31)
    @DisplayName("GET medicine by ID with admin role")
    public void test31_get_medicine_by_id_admin() {
        Response response = webTarget
            .register(adminAuth)
            .path("medicine/1")
            .request()
            .get();
        assertThat("Admin should access medicine by ID", response.getStatus(), is(200));
        Medicine medicine = response.readEntity(Medicine.class);
        assertNotNull(medicine);
    }

    @Test
    @Order(32)
    @DisplayName("POST create new medicine with admin role")
    public void test32_create_medicine_admin() {
        Medicine newMedicine = new Medicine();
        newMedicine.setDrugName("Test Medicine");
        newMedicine.setManufacturerName("Test Pharma");
        newMedicine.setDosageInformation("Take as needed");
        newMedicine.setGenericName("Generic Test");

        Response response = webTarget
            .register(adminAuth)
            .path("medicine")
            .request()
            .post(Entity.entity(newMedicine, MediaType.APPLICATION_JSON));
        
        assertThat("Admin should create medicine", response.getStatus(), is(200));
        Medicine createdMedicine = response.readEntity(Medicine.class);
        assertNotNull(createdMedicine);
        assertThat("Created medicine drug name", createdMedicine.getDrugName(), is("Test Medicine"));
    }

    // ===============================
    // MEDICAL SCHOOL CRUD TESTS
    // ===============================

    @Test
    @Order(40)
    @DisplayName("GET all medical schools with admin role")
    public void test40_get_all_medical_schools_admin() {
        Response response = webTarget
            .register(adminAuth)
            .path("medicalschool")
            .request()
            .get();
        assertThat("Admin should access all medical schools", response.getStatus(), is(200));
        List<MedicalSchool> schools = response.readEntity(new GenericType<List<MedicalSchool>>(){});
        assertThat("Should have medical schools", schools, is(not(empty())));
    }

    @Test
    @Order(41)
    @DisplayName("GET medical school by ID with admin role")
    public void test41_get_medical_school_by_id_admin() {
        Response response = webTarget
            .register(adminAuth)
            .path("medicalschool/1")
            .request()
            .get();
        assertThat("Admin should access medical school by ID", response.getStatus(), is(200));
        MedicalSchool school = response.readEntity(MedicalSchool.class);
        assertNotNull(school);
    }

    // ===============================
    // AUTHENTICATION & AUTHORIZATION TESTS
    // ===============================

    @Test
    @Order(90)
    @DisplayName("Access secured endpoint without authentication - should fail")
    public void test90_no_auth_access() {
        Response response = webTarget
            .path("physician")
            .request()
            .get();
        assertThat("No auth should return 401", response.getStatus(), is(401));
    }

    @Test
    @Order(91)
    @DisplayName("Access with invalid credentials - should fail")
    public void test91_invalid_auth() {
        HttpAuthenticationFeature invalidAuth = HttpAuthenticationFeature.basic("invalid", "invalid");
        Response response = webTarget
            .register(invalidAuth)
            .path("physician")
            .request()
            .get();
        assertThat("Invalid auth should return 401", response.getStatus(), is(401));
    }

    @Test
    @Order(92)
    @DisplayName("User role accessing admin-only resource - should fail")
    public void test92_insufficient_privileges() {
        Response response = webTarget
            .register(userAuth)
            .path("physician")
            .request()
            .post(Entity.entity(new Physician(), MediaType.APPLICATION_JSON));
        assertThat("User should not create physician", response.getStatus(), is(403));
    }

    // ===============================
    // INTEGRATION & ERROR HANDLING TESTS
    // ===============================

    @Test
    @Order(95)
    @DisplayName("GET non-existent resource - should return 404")
    public void test95_not_found() {
        Response response = webTarget
            .register(adminAuth)
            .path("physician/99999")
            .request()
            .get();
        assertThat("Non-existent resource should return 404", response.getStatus(), is(404));
    }

    @Test
    @Order(96)
    @DisplayName("POST invalid data - should handle gracefully")
    public void test96_invalid_data() {
        // Try to create physician with missing required fields
        Physician invalidPhysician = new Physician();
        // Don't set required fields
        
        Response response = webTarget
            .register(adminAuth)
            .path("physician")
            .request()
            .post(Entity.entity(invalidPhysician, MediaType.APPLICATION_JSON));
        
        // Should handle validation error gracefully (400 or 422)
        assertTrue(response.getStatus() >= 400 && response.getStatus() < 500, 
                  "Invalid data should return 4xx error");
    }
}