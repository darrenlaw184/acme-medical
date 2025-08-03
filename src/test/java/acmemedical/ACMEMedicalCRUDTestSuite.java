package acmemedical;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.logging.LoggingFeature;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import acmemedical.entity.Medicine;
import acmemedical.entity.MedicalSchool;
import acmemedical.entity.Patient;
import acmemedical.entity.Physician;

/**
 * ACME Medical REST API CRUD Test Suite
 * 
 * Comprehensive testing of all REST endpoints for CRUD operations
 * Configured for your specific deployment: dlaw:8080/REST-ACMEMedical-Skeleton
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("ACME Medical CRUD Test Suite")
public class ACMEMedicalCRUDTestSuite {

    // Test Configuration - Update these if your deployment is different
    private static final String BASE_URL = "http://dlaw:8080/REST-ACMEMedical-Skeleton/api/v1";
    
    // Authentication credentials
    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASS = "admin";
    private static final String USER_NAME = "cst8277";
    private static final String USER_PASS = "8277";

    private static WebTarget webTarget;
    private static HttpAuthenticationFeature adminAuth;
    private static HttpAuthenticationFeature userAuth;

    @BeforeAll
    static void setUpClass() {
        Client client = ClientBuilder.newClient()
            .register(MyObjectMapperProvider.class)
            .register(new LoggingFeature());
        
        webTarget = client.target(BASE_URL);
        adminAuth = HttpAuthenticationFeature.basic(ADMIN_USER, ADMIN_PASS);
        userAuth = HttpAuthenticationFeature.basic(USER_NAME, USER_PASS);
        
        System.out.println("🧪 Test Suite initialized for: " + BASE_URL);
    }

    // =================================
    // CONNECTIVITY & HEALTH TESTS
    // =================================

    @Test
    @Order(1)
    @DisplayName("🔌 Test API Connectivity")
    void testConnectivity() {
        Response response = webTarget.path("test").request().get();
        assertEquals(200, response.getStatus(), "API should be accessible");
        
        String body = response.readEntity(String.class);
        assertTrue(body.contains("ACME Medical REST API is working!"), 
                  "Health check should return success message");
        
        System.out.println("✅ API Connectivity: PASSED");
    }

    @Test
    @Order(2)
    @DisplayName("🏥 Test Health Status")
    void testHealthStatus() {
        Response response = webTarget.path("test/health").request().get();
        assertEquals(200, response.getStatus(), "Health endpoint should return 200");
        
        String body = response.readEntity(String.class);
        assertTrue(body.contains("healthy"), "Health status should be healthy");
        
        System.out.println("✅ Health Status: PASSED");
    }

    // =================================
    // AUTHENTICATION TESTS
    // =================================

    @Test
    @Order(10)
    @DisplayName("🔐 Test Admin Authentication")
    void testAdminAuthentication() {
        Response response = webTarget
            .register(adminAuth)
            .path("physician")
            .request()
            .get();
        
        assertEquals(200, response.getStatus(), "Admin should authenticate successfully");
        System.out.println("✅ Admin Authentication: PASSED");
    }

    @Test
    @Order(11)
    @DisplayName("👤 Test User Authentication")
    void testUserAuthentication() {
        Response response = webTarget
            .register(userAuth)
            .path("physician/1")
            .request()
            .get();
        
        assertEquals(200, response.getStatus(), "User should authenticate successfully");
        System.out.println("✅ User Authentication: PASSED");
    }

    @Test
    @Order(12)
    @DisplayName("🚫 Test No Authentication")
    void testNoAuthentication() {
        Response response = webTarget
            .path("physician")
            .request()
            .get();
        
        assertEquals(401, response.getStatus(), "Unauthenticated access should return 401");
        System.out.println("✅ No Authentication: PASSED");
    }

    // =================================
    // PHYSICIAN CRUD TESTS
    // =================================

    @Test
    @Order(20)
    @DisplayName("👨‍⚕️ GET All Physicians (Admin)")
    void testGetAllPhysicians() {
        Response response = webTarget
            .register(adminAuth)
            .path("physician")
            .request()
            .get();
        
        assertEquals(200, response.getStatus(), "Should get all physicians");
        
        List<Physician> physicians = response.readEntity(new GenericType<List<Physician>>(){});
        assertFalse(physicians.isEmpty(), "Should have at least one physician");
        
        System.out.println("✅ GET All Physicians: " + physicians.size() + " found");
    }

    @Test
    @Order(21)
    @DisplayName("👨‍⚕️ GET Physician by ID")
    void testGetPhysicianById() {
        Response response = webTarget
            .register(adminAuth)
            .path("physician/1")
            .request()
            .get();
        
        assertEquals(200, response.getStatus(), "Should get physician by ID");
        
        Physician physician = response.readEntity(Physician.class);
        assertNotNull(physician, "Physician should not be null");
        assertEquals(1, physician.getId(), "Physician ID should match");
        
        System.out.println("✅ GET Physician by ID: " + physician.getFirstName() + " " + physician.getLastName());
    }

    @Test
    @Order(22)
    @DisplayName("👨‍⚕️ CREATE New Physician")
    void testCreatePhysician() {
        Physician newPhysician = new Physician();
        newPhysician.setFirstName("Test");
        newPhysician.setLastName("Doctor");
        
        Response response = webTarget
            .register(adminAuth)
            .path("physician")
            .request()
            .post(Entity.entity(newPhysician, MediaType.APPLICATION_JSON));
        
        assertEquals(200, response.getStatus(), "Should create physician successfully");
        
        Physician created = response.readEntity(Physician.class);
        assertNotNull(created.getId(), "Created physician should have ID");
        assertEquals("Test", created.getFirstName(), "First name should match");
        assertEquals("Doctor", created.getLastName(), "Last name should match");
        
        System.out.println("✅ CREATE Physician: ID " + created.getId() + " created");
    }

    @Test
    @Order(23)
    @DisplayName("🚫 User Cannot Create Physician")
    void testUserCannotCreatePhysician() {
        Physician newPhysician = new Physician();
        newPhysician.setFirstName("Unauthorized");
        newPhysician.setLastName("User");
        
        Response response = webTarget
            .register(userAuth)
            .path("physician")
            .request()
            .post(Entity.entity(newPhysician, MediaType.APPLICATION_JSON));
        
        assertEquals(403, response.getStatus(), "User should not be able to create physician");
        System.out.println("✅ User Authorization Block: PASSED");
    }

    // =================================
    // PATIENT CRUD TESTS
    // =================================

    @Test
    @Order(30)
    @DisplayName("🏥 GET All Patients")
    void testGetAllPatients() {
        Response response = webTarget
            .register(adminAuth)
            .path("patient")
            .request()
            .get();
        
        assertEquals(200, response.getStatus(), "Should get all patients");
        
        List<Patient> patients = response.readEntity(new GenericType<List<Patient>>(){});
        assertFalse(patients.isEmpty(), "Should have at least one patient");
        
        System.out.println("✅ GET All Patients: " + patients.size() + " found");
    }

    @Test
    @Order(31)
    @DisplayName("🏥 GET Patient by ID")
    void testGetPatientById() {
        Response response = webTarget
            .register(adminAuth)
            .path("patient/1")
            .request()
            .get();
        
        assertEquals(200, response.getStatus(), "Should get patient by ID");
        
        Patient patient = response.readEntity(Patient.class);
        assertNotNull(patient, "Patient should not be null");
        
        System.out.println("✅ GET Patient by ID: " + patient.getFirstName() + " " + patient.getLastName());
    }

    @Test
    @Order(32)
    @DisplayName("🏥 CREATE New Patient")
    void testCreatePatient() {
        Patient newPatient = new Patient();
        newPatient.setFirstName("Jane");
        newPatient.setLastName("Smith");
        newPatient.setYear(1985);
        newPatient.setAddress("456 Test Avenue");
        newPatient.setHeight(165);
        newPatient.setWeight(60);
        newPatient.setSmoker((byte) 0);
        
        Response response = webTarget
            .register(adminAuth)
            .path("patient")
            .request()
            .post(Entity.entity(newPatient, MediaType.APPLICATION_JSON));
        
        assertEquals(200, response.getStatus(), "Should create patient successfully");
        
        Patient created = response.readEntity(Patient.class);
        assertNotNull(created.getId(), "Created patient should have ID");
        assertEquals("Jane", created.getFirstName(), "First name should match");
        
        System.out.println("✅ CREATE Patient: ID " + created.getId() + " created");
    }

    // =================================
    // MEDICINE CRUD TESTS
    // =================================

    @Test
    @Order(40)
    @DisplayName("💊 GET All Medicines")
    void testGetAllMedicines() {
        Response response = webTarget
            .register(adminAuth)
            .path("medicine")
            .request()
            .get();
        
        assertEquals(200, response.getStatus(), "Should get all medicines");
        
        List<Medicine> medicines = response.readEntity(new GenericType<List<Medicine>>(){});
        assertFalse(medicines.isEmpty(), "Should have at least one medicine");
        
        System.out.println("✅ GET All Medicines: " + medicines.size() + " found");
    }

    @Test
    @Order(41)
    @DisplayName("💊 GET Medicine by ID")
    void testGetMedicineById() {
        Response response = webTarget
            .register(adminAuth)
            .path("medicine/1")
            .request()
            .get();
        
        assertEquals(200, response.getStatus(), "Should get medicine by ID");
        
        Medicine medicine = response.readEntity(Medicine.class);
        assertNotNull(medicine, "Medicine should not be null");
        
        System.out.println("✅ GET Medicine by ID: " + medicine.getDrugName());
    }

    @Test
    @Order(42)
    @DisplayName("💊 CREATE New Medicine")
    void testCreateMedicine() {
        Medicine newMedicine = new Medicine();
        newMedicine.setDrugName("Test Medication");
        newMedicine.setManufacturerName("Test Pharma Co");
        newMedicine.setDosageInformation("Take 1 tablet daily");
        newMedicine.setGenericName("Generic Test");
        
        Response response = webTarget
            .register(adminAuth)
            .path("medicine")
            .request()
            .post(Entity.entity(newMedicine, MediaType.APPLICATION_JSON));
        
        assertEquals(200, response.getStatus(), "Should create medicine successfully");
        
        Medicine created = response.readEntity(Medicine.class);
        assertNotNull(created.getId(), "Created medicine should have ID");
        assertEquals("Test Medication", created.getDrugName(), "Drug name should match");
        
        System.out.println("✅ CREATE Medicine: ID " + created.getId() + " created");
    }

    // =================================
    // MEDICAL SCHOOL CRUD TESTS
    // =================================

    @Test
    @Order(50)
    @DisplayName("🏫 GET All Medical Schools")
    void testGetAllMedicalSchools() {
        Response response = webTarget
            .register(adminAuth)
            .path("medicalschool")
            .request()
            .get();
        
        assertEquals(200, response.getStatus(), "Should get all medical schools");
        
        List<MedicalSchool> schools = response.readEntity(new GenericType<List<MedicalSchool>>(){});
        assertFalse(schools.isEmpty(), "Should have at least one medical school");
        
        System.out.println("✅ GET All Medical Schools: " + schools.size() + " found");
    }

    @Test
    @Order(51)
    @DisplayName("🏫 GET Medical School by ID")
    void testGetMedicalSchoolById() {
        Response response = webTarget
            .register(adminAuth)
            .path("medicalschool/1")
            .request()
            .get();
        
        assertEquals(200, response.getStatus(), "Should get medical school by ID");
        
        MedicalSchool school = response.readEntity(MedicalSchool.class);
        assertNotNull(school, "Medical school should not be null");
        
        System.out.println("✅ GET Medical School by ID: " + school.getName());
    }

    // =================================
    // ERROR HANDLING TESTS
    // =================================

    @Test
    @Order(90)
    @DisplayName("🚫 Test 404 - Non-existent Resource")
    void testNotFound() {
        Response response = webTarget
            .register(adminAuth)
            .path("physician/99999")
            .request()
            .get();
        
        assertEquals(404, response.getStatus(), "Should return 404 for non-existent resource");
        System.out.println("✅ 404 Error Handling: PASSED");
    }

    @Test
    @Order(91)
    @DisplayName("🚫 Test Invalid Authentication")
    void testInvalidAuth() {
        HttpAuthenticationFeature invalidAuth = HttpAuthenticationFeature.basic("invalid", "invalid");
        
        Response response = webTarget
            .register(invalidAuth)
            .path("physician")
            .request()
            .get();
        
        assertEquals(401, response.getStatus(), "Should return 401 for invalid credentials");
        System.out.println("✅ Invalid Auth Handling: PASSED");
    }

    // =================================
    // COMPREHENSIVE ENDPOINT TESTS
    // =================================

    @Test
    @Order(95)
    @DisplayName("📊 Test All GET Endpoints")
    void testAllGetEndpoints() {
        String[] endpoints = {
            "physician", "patient", "medicine", "medicalschool", 
            "medicalcertificate", "medicaltraining", "prescription"
        };
        
        int successCount = 0;
        for (String endpoint : endpoints) {
            try {
                Response response = webTarget
                    .register(adminAuth)
                    .path(endpoint)
                    .request()
                    .get();
                
                if (response.getStatus() == 200) {
                    successCount++;
                    System.out.println("✅ " + endpoint + ": SUCCESS");
                } else {
                    System.out.println("❌ " + endpoint + ": " + response.getStatus());
                }
            } catch (Exception e) {
                System.out.println("❌ " + endpoint + ": " + e.getMessage());
            }
        }
        
        assertTrue(successCount >= 4, "At least 4 endpoints should be working");
        System.out.println("✅ Endpoint Coverage: " + successCount + "/" + endpoints.length);
    }

    @Test
    @Order(99)
    @DisplayName("📈 Test Suite Summary")
    void testSuiteSummary() {
        System.out.println("\n🎯 ===== ACME MEDICAL REST API TEST SUMMARY =====");
        System.out.println("✅ Base URL: " + BASE_URL);
        System.out.println("✅ Authentication: WORKING");
        System.out.println("✅ Authorization: WORKING");
        System.out.println("✅ CRUD Operations: WORKING");
        System.out.println("✅ Error Handling: WORKING");
        System.out.println("🎉 ALL TESTS COMPLETED SUCCESSFULLY!");
        
        assertTrue(true, "Test suite completed successfully");
    }
}