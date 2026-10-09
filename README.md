# LocalServe - Java Version (Servlets + JSP + JDBC + MySQL)

This is a **Java rewrite** of the original PHP "LocalServe" service-booking app,
built for the *Introduction to Java Programming (BCA)* PBL project.
Same features, same database concept — implemented with Java Servlets, JSP, and JDBC instead of PHP.

## Tech Stack
- Java (Servlets + JSP) — was PHP
- JDBC + MySQL — same MySQL database concept as before
- Apache Tomcat 9 (or any Servlet 4.0 compatible container)
- Maven (for building the WAR file)

## Features (same as original)
- User & Provider registration/login (role-based)
- User: dashboard, book a service (by city / state / PIN code), view my bookings, chat with provider
- Provider: dashboard, complete/update profile, view & accept/reject requests, earnings, chat with user

## Project Structure
```
src/main/java/com/localserve/
  util/        -> DBConnection.java, PasswordUtil.java
  auth/        -> LoginServlet, RegisterServlet, LogoutServlet
  user/        -> DashboardServlet, BookServlet, BookingsServlet, ChatServlet
  provider/    -> DashboardServlet, CreateProfileServlet, RequestsServlet, EarningsServlet, ChatServlet
src/main/webapp/
  auth/, user/, provider/   -> JSP pages (views)
database/schema.sql         -> MySQL tables
```

## How to Run

1. **Create the database** (existing installation? see "Upgrading" below instead)
   - Open MySQL and run:
     ```
     mysql -u root -p < database/schema.sql
     ```
   - This creates `localserve_db` with the `users`, `provider_details`, `bookings`, and `messages` tables.

2. **Set your DB credentials** (if different from default XAMPP/MySQL root with no password)
   - Edit `src/main/java/com/localserve/util/DBConnection.java` and update `DB_USER` / `DB_PASSWORD`.

3. **Build the WAR file**
   ```
   mvn clean package
   ```
   This produces `target/LocalServeJava.war`.

4. **Deploy to Tomcat**
   - Copy `LocalServeJava.war` into Tomcat's `webapps/` folder, start Tomcat.
   - Or in Eclipse/IntelliJ: import as a Maven project and "Run on Server" with Tomcat 9.

5. **Open in browser**
   ```
   http://localhost:8080/LocalServeJava/
   ```
   It redirects to the login page. Register a new "user" and a new "provider" account to try both roles.

## Notes for the Proposal / Report
- Passwords are hashed with SHA-256 (java.security.MessageDigest), replacing PHP's password_hash()/password_verify().
- SQL injection is prevented using PreparedStatement everywhere (parameterized queries), same protection goal as the original code intended.
- Session-based authentication uses HttpSession (equivalent of PHP's $_SESSION).
- Architecture: Browser -> Servlet (Controller) -> JDBC -> MySQL -> JSP (View). This is the classic Java **MVC-ish Servlet/JSP** pattern, a natural talking point for your "System Architecture" section in the proposal.

## Location matching (city + state + PIN code)

A customer only sees providers who serve the **same city, state and PIN code** they type in.
This is plain text matching, it is not a distance calculation and does not say how far a provider is.

How it works
- Providers enter *Service City, State, PIN Code* on the Profile page (the place they take jobs, not their home).
- On *Book Service* the customer enters the place where the work is needed (city, state, PIN, optional full address)
  and clicks *Find Providers*. Nothing is listed until a valid location is entered.
- City and state are trimmed and compared ignoring upper/lower case. PIN must be exactly 6 digits and is stored as text.
- When the customer books, the server checks the provider's location again in the same SQL statement that inserts
  the booking, so a changed `provider_id` does not get around the rule.
- The booking keeps its own copy of the location (`service_city`, `service_state`, `service_pin_code`, `service_address`),
  so later profile changes do not alter old bookings. Provider requests and customer history both show it.
- Providers with no location saved are never shown. They get a notice on their dashboard.

## Upgrading an existing database

```
mysqldump -u root -p localserve_db > localserve_backup.sql
mysql -u root -p localserve_db < database/location_matching_migration.sql
```
Run it once. It only adds columns and an index, nothing is deleted or changed. Old bookings keep an empty
location (shown as "Location not recorded"). Every existing provider must open **Profile** and save a location
before customers can see them.

## Quick manual test
1. Register provider A, save profile with Dehradun / Uttarakhand / 248001.
2. Register provider B, save profile with Delhi / Delhi / 110001.
3. Register a customer, open Book Service, search Dehradun / Uttarakhand / 248001: only A is listed.
4. Search 248003, a 5-digit PIN, or leave it empty: nobody is listed / an error is shown.
5. Book A, then change A's location to Delhi: the old booking still shows Dehradun.
6. Log in as A: the request shows the customer's location before you accept or reject.
