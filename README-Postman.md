# ACME Medical REST API - Postman Setup Guide

## 🚀 Quick Start

### 1. Import the Postman Collection
1. Open **Postman**
2. Click **Import** button
3. Select the file: `REST-ACMEMedical-Sample.postman_collection.json`
4. The collection will be imported with all endpoints ready to test

### 2. Prerequisites
Before testing, you need:
- **Application Server** running (GlassFish/Payara) on port 8080
- **Database** configured and running
- **ACME Medical application** deployed

### 3. Authentication Credentials

The collection uses **Basic Authentication** with these credentials:

#### Admin User
- **Username**: `admin`
- **Password**: `admin`
- **Role**: `ADMIN_ROLE`

#### Regular User
- **Username**: `cst8277`
- **Password**: `8277`
- **Role**: `USER_ROLE`

### 4. Available Endpoints

#### Physician Endpoints
- `GET /api/v1/physician` - Get all physicians
- `GET /api/v1/physician/{id}` - Get physician by ID
- `POST /api/v1/physician` - Create new physician
- `DELETE /api/v1/physician/{id}` - Delete physician

#### Patient Endpoints
- `GET /api/v1/patient` - Get all patients
- `GET /api/v1/patient/{id}` - Get patient by ID
- `POST /api/v1/patient` - Create new patient
- `DELETE /api/v1/patient/{id}` - Delete patient

#### Medicine Endpoints
- `GET /api/v1/medicine` - Get all medicines
- `GET /api/v1/medicine/{id}` - Get medicine by ID
- `POST /api/v1/medicine` - Create new medicine
- `DELETE /api/v1/medicine/{id}` - Delete medicine

#### Medical School Endpoints
- `GET /api/v1/medicalschool` - Get all medical schools
- `GET /api/v1/medicalschool/{id}` - Get medical school by ID
- `POST /api/v1/medicalschool` - Create new medical school
- `DELETE /api/v1/medicalschool/{id}` - Delete medical school

### 5. Testing Strategy

#### Start with Admin User
1. Test **GET** endpoints first to see existing data
2. Test **POST** endpoints to create new records
3. Test **GET by ID** to verify creation
4. Test **DELETE** endpoints (use admin role)

#### Test User Role Restrictions
1. Switch to user credentials (`cst8277` / `8277`)
2. Verify that user can access allowed endpoints
3. Verify that user is blocked from admin-only operations

### 6. Sample JSON Data

#### Create Physician
```json
{
    "firstName": "Michael",
    "lastName": "Smith"
}
```

#### Create Patient
```json
{
    "firstName": "Charles",
    "lastName": "Xavier",
    "year": 1978,
    "address": "456 Main St. Toronto",
    "height": 170,
    "weight": 90,
    "smoker": 1
}
```

#### Create Medicine
```json
{
    "drugName": "Biogesic",
    "manufacturerName": "Unilab",
    "dosageInformation": "Take 4 tablets per day with 6 hours interval",
    "chemicalName": null,
    "genericName": "Paracetamol"
}
```

#### Create Medical School
```json
{
    "entity-type": "private_school",
    "name": "Harvard Medical School"
}
```

### 7. Expected Responses

#### Success Responses
- **200 OK** - For successful GET, POST, DELETE operations
- **201 Created** - For successful resource creation

#### Error Responses
- **401 Unauthorized** - Invalid credentials
- **403 Forbidden** - Insufficient permissions
- **404 Not Found** - Resource not found
- **409 Conflict** - Duplicate resource (for medical schools)

### 8. Troubleshooting

#### Connection Issues
- Ensure application server is running on port 8080
- Check that the application is deployed successfully
- Verify database connection

#### Authentication Issues
- Double-check username/password
- Ensure the user exists in the database
- Verify user roles are properly assigned

#### Data Issues
- Check that required fields are provided in JSON
- Verify data types match entity requirements
- Ensure foreign key relationships are valid

### 9. Development Workflow

1. **Build the project**: `mvn clean compile`
2. **Deploy to application server**
3. **Import Postman collection**
4. **Test endpoints systematically**
5. **Verify security restrictions work correctly**

---

## 📝 Notes

- The collection includes both admin and user role tests
- All endpoints use Basic Authentication
- JSON responses will include entity data with IDs and timestamps
- The application uses Jakarta EE 9 with JPA and JAX-RS
- Security is implemented using Jakarta Security Enterprise 