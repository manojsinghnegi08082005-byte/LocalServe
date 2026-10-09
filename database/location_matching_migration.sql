-- LocalServe: city / state / PIN code location matching
-- Migration for an EXISTING database (fresh installs just use schema.sql).
--
-- What it does
--   * adds 3 nullable location columns to provider_details
--   * adds 4 nullable location columns to bookings
--   * adds an index on provider_details.service_pin_code
-- What it does NOT do
--   * no table is dropped or recreated, no existing row is changed or deleted
--   * no location values are invented: old providers and old bookings keep NULL
--
-- How to run it safely
--   1. Stop Tomcat (or at least stop taking bookings) and make a backup:
--        mysqldump -u root -p localserve_db > localserve_backup.sql
--   2. Run this script ONCE:
--        mysql -u root -p localserve_db < database/location_matching_migration.sql
--   3. If you run it a second time MySQL stops with "Duplicate column name" or
--      "Duplicate key name". That is harmless: it means the change is already there.
--   4. Run the checks at the bottom.
--   5. Deploy the new WAR and ask every existing provider to open "Profile" and save
--      their service city, state and PIN code (see the last query).

USE localserve_db;

ALTER TABLE provider_details
    ADD COLUMN service_city VARCHAR(100) NULL,
    ADD COLUMN service_state VARCHAR(100) NULL,
    ADD COLUMN service_pin_code VARCHAR(6) NULL;

ALTER TABLE bookings
    ADD COLUMN service_city VARCHAR(100) NULL,
    ADD COLUMN service_state VARCHAR(100) NULL,
    ADD COLUMN service_pin_code VARCHAR(6) NULL,
    ADD COLUMN service_address VARCHAR(255) NULL;

CREATE INDEX idx_provider_pin ON provider_details (service_pin_code);

-- No backfill on purpose: the old tables hold no reliable location data
-- (users has no address, and a booking never stored where the work was needed).

-- ---------- checks ----------
SHOW COLUMNS FROM provider_details;
SHOW COLUMNS FROM bookings;

-- old data is still all there
SELECT COUNT(*) AS providers FROM provider_details;
SELECT COUNT(*) AS bookings, SUM(service_pin_code IS NULL) AS bookings_without_location FROM bookings;

-- providers who must still enter a location (they are hidden from customers until they do)
SELECT user_id, name, service
FROM provider_details
WHERE service_city IS NULL OR service_state IS NULL OR service_pin_code IS NULL;
