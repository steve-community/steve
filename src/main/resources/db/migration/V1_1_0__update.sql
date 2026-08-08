START TRANSACTION;

-- Step 1: Add latitude and longitude columns to address table
ALTER TABLE address
    ADD COLUMN latitude DECIMAL(11, 8),
    ADD COLUMN longitude DECIMAL(11, 8);

-- Step 2: Migrate existing lat/long data from charge_box to address table
-- For charge boxes that already have an address
UPDATE address a
    INNER JOIN charge_box cb ON a.address_pk = cb.address_pk
    SET a.latitude = cb.location_latitude,
        a.longitude = cb.location_longitude
WHERE cb.location_latitude IS NOT NULL
  AND cb.location_longitude IS NOT NULL;

-- Step 3a: Create new address rows for charge boxes without an address but with lat/long
INSERT INTO address (latitude, longitude)
SELECT location_latitude, location_longitude
FROM charge_box
WHERE address_pk IS NULL
  AND location_latitude IS NOT NULL
  AND location_longitude IS NOT NULL;

-- Step 3b: Update charge_box to reference the newly created addresses
UPDATE charge_box cb
    INNER JOIN address a ON cb.location_latitude = a.latitude
    AND cb.location_longitude = a.longitude
    SET cb.address_pk = a.address_pk
WHERE cb.address_pk IS NULL
  AND cb.location_latitude IS NOT NULL
  AND cb.location_longitude IS NOT NULL;

-- Step 4: Drop the old columns from charge_box
ALTER TABLE charge_box
DROP COLUMN location_latitude,
DROP COLUMN location_longitude;

COMMIT;
