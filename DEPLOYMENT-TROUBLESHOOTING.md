# 🚀 ACME Medical - Payara Deployment Troubleshooting Guide

## **Current Issue: HTTP 404 - Not Found**

Your application builds successfully but returns 404 when accessed. Here's how to fix it:

## **🔍 Step-by-Step Troubleshooting**

### **1. Verify Payara Server Status**

#### Check if Payara is Running:
1. Open **NetBeans**
2. Go to **Services** tab
3. Expand **Servers**
4. Right-click on **Payara Server**
5. Select **Start** if not running

#### Check Payara Admin Console:
- Open browser and go to: `http://localhost:4848`
- Username: `admin`
- Password: `admin` (or your configured password)

### **2. Check Application Deployment**

#### In NetBeans:
1. **Right-click** on your project
2. Select **Clean and Build**
3. **Right-click** on your project again
4. Select **Deploy**

#### Check Deployment Status:
1. In **Services** tab, expand **Servers** → **Payara Server** → **Applications**
2. Look for `rest-acmemedical` application
3. Status should be **Running**

### **3. Verify Application Context**

#### Check Application URL:
- **Correct URL**: `http://localhost:8080/rest-acmemedical/api/v1/physician`
- **Wrong URL**: `http://localhost:8080/api/v1/physician`

#### Test Base URL:
- Try: `http://localhost:8080/rest-acmemedical/`
- Should show a welcome page or redirect

### **4. Database Connection Issues**

#### Check MySQL Database:
1. **Start MySQL** if not running
2. **Verify database exists**:
   ```sql
   CREATE DATABASE IF NOT EXISTS acmemedical;
   ```
3. **Check user credentials**:
   ```sql
   CREATE USER IF NOT EXISTS 'cst8277'@'localhost' IDENTIFIED BY '8277';
   GRANT ALL PRIVILEGES ON acmemedical.* TO 'cst8277'@'localhost';
   FLUSH PRIVILEGES;
   ```

#### Check Payara JDBC Resources:
1. Go to **Payara Admin Console**: `http://localhost:4848`
2. Navigate to **Resources** → **JDBC** → **JDBC Connection Pools**
3. Look for `acmemedicalDB` pool
4. Click **Ping** to test connection

### **5. Check Application Logs**

#### In NetBeans:
1. **Right-click** on your project
2. Select **View Deployment Log**
3. Look for **ERROR** or **WARNING** messages

#### In Payara Admin Console:
1. Go to **Log Viewer**
2. Check **Server Log** for deployment errors

### **6. Common Deployment Issues & Solutions**

#### Issue 1: "Application failed to deploy"
**Solution:**
- Check if port 8080 is already in use
- Restart Payara server
- Check database connectivity

#### Issue 2: "Persistence unit not found"
**Solution:**
- Verify `persistence.xml` is in `META-INF` folder
- Check JNDI name: `java:app/jdbc/acmemedical`

#### Issue 3: "Class not found"
**Solution:**
- Clean and rebuild project
- Check if all dependencies are included in WAR

#### Issue 4: "Database connection failed"
**Solution:**
- Start MySQL service
- Verify database credentials
- Check MySQL is running on port 3306

### **7. Manual Deployment Test**

#### Step 1: Build WAR File
```bash
mvn clean package
```

#### Step 2: Deploy WAR Manually
1. Go to **Payara Admin Console**: `http://localhost:4848`
2. Navigate to **Applications**
3. Click **Deploy**
4. Choose the WAR file: `target/rest-acmemedical-0.0.1-SNAPSHOT.war`
5. Click **OK**

#### Step 3: Test Deployment
1. Go to **Applications** list
2. Find `rest-acmemedical`
3. Click **Launch** button
4. Should open: `http://localhost:8080/rest-acmemedical/`

### **8. Test REST Endpoints**

#### Test with Browser:
- Try: `http://localhost:8080/rest-acmemedical/api/v1/physician`
- Should return JSON or authentication prompt

#### Test with Postman:
1. Import the collection: `REST-ACMEMedical-Sample.postman_collection.json`
2. Test: `GET http://localhost:8080/rest-acmemedical/api/v1/physician`
3. Use Basic Auth: `admin` / `admin`

### **9. Environment Checklist**

#### ✅ Required Services:
- [ ] **Payara Server** running on port 8080
- [ ] **MySQL Database** running on port 3306
- [ ] **Database** `acmemedical` exists
- [ ] **User** `cst8277` with password `8277` exists
- [ ] **JDBC Pool** `acmemedicalDB` configured in Payara

#### ✅ Application Files:
- [ ] **WAR file** builds successfully
- [ ] **persistence.xml** in `META-INF` folder
- [ ] **payara-resources.xml** in `WEB-INF` folder
- [ ] **All entity classes** properly annotated
- [ ] **RestConfig.java** with `@ApplicationPath("/api/v1")`

### **10. Quick Fix Commands**

#### Restart Everything:
```bash
# Stop Payara
# Start Payara
# Clean and Build project in NetBeans
# Deploy project
```

#### Test Database:
```sql
mysql -u cst8277 -p8277
USE acmemedical;
SHOW TABLES;
```

#### Test Application:
```bash
curl -u admin:admin http://localhost:8080/rest-acmemedical/api/v1/physician
```

## **🚨 Emergency Fixes**

### **If Nothing Works:**

1. **Delete and Redeploy:**
   - Undeploy application from Payara
   - Delete from Payara applications
   - Clean and rebuild project
   - Redeploy

2. **Reset Payara:**
   - Stop Payara server
   - Delete Payara domain (backup first!)
   - Create new domain
   - Reconfigure JDBC resources
   - Redeploy application

3. **Check Firewall:**
   - Ensure ports 8080 and 3306 are not blocked
   - Check Windows Firewall settings

## **📞 Still Having Issues?**

If you're still getting 404 errors after following these steps:

1. **Check the exact error message** in Payara logs
2. **Verify the exact URL** you're trying to access
3. **Test with a simple endpoint** first
4. **Check if other applications** deploy successfully on Payara

---

**Remember:** The correct base URL is `http://localhost:8080/rest-acmemedical/` and the API endpoints are at `http://localhost:8080/rest-acmemedical/api/v1/` 