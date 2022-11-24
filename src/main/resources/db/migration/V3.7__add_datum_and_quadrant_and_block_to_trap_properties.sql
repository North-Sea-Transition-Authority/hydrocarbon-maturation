ALTER TABLE trap_properties
    ADD datum TEXT,
    ADD centre_of_interest_quadrant_block UUID,
    ADD CONSTRAINT trap_properties_fk2 FOREIGN KEY  (centre_of_interest_quadrant_block) REFERENCES general_details_quadrant_blocks (id);