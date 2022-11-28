ALTER TABLE  trap_properties
    DROP CONSTRAINT trap_properties_fk2,
    ALTER COLUMN centre_of_interest_quadrant_block TYPE TEXT;