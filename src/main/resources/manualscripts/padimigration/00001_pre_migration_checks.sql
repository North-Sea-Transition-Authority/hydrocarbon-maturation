TRUNCATE remi.padi_res_header_prev;

TRUNCATE remi.padi_res_data_header_prev;

INSERT INTO remi.padi_res_header_prev
SELECT *
FROM remi.padi_res_header;

INSERT INTO remi.padi_res_data_header_prev
SELECT *
FROM remi.padi_res_data_header;

-- quad/block checks --

SELECT prh.resource_id, prh.block_polygon, prh.block_centroid
FROM remi.padi_res_header prh
EXCEPT
SELECT prhv.resource_id, prhv.block_polygon, prhv.block_centroid
FROM remi.padi_res_header_prev prhv;

INSERT INTO remi.padi_quad_block_mapping (
                                          padi_resource_id,
                                          padi_block_polygon,
                                          padi_block_centroid,
                                          play_fairway,
                                          block_polygon_match,
                                          block_centroid_match,
                                          shore_type
)
SELECT prh.resource_id, unnest(string_to_array(prh.block_polygon, ', ')), prh.block_centroid, prh.play_fairway, REPLACE(prh.block_polygon, ' ', ''), prh.block_centroid, 'OFFSHORE'
FROM remi.padi_res_header prh
WHERE prh.resource_id NOT IN (
    SELECT p.resource_id
    FROM remi.padi_res_header_prev p
);

SELECT prh.resource_id, block_polygon, block_centroid, play_fairway
FROM remi.padi_res_header prh;

-- extract for quad block mapping
SELECT resource_id, REPLACE(block_polygon, ' ', ''), block_centroid, play_fairway
FROM remi.padi_res_header
WHERE block_polygon IS NOT NULL
ORDER BY resource_id;

SELECT resource_id, block_polygon, block_centroid, play_fairway
FROM remi.padi_res_header prh
WHERE prh.block_centroid NOT IN (
    SELECT prhp.block_centroid
    FROM remi.padi_res_header_prev prhp
    );

SELECT resource_id, block_centroid
FROM remi.padi_res_header_prev;

SELECT h.resource_id, REPLACE(h.block_polygon, ' ', '')
FROM remi.padi_res_header h
EXCEPT
SELECT p.resource_id, REPLACE(p.block_polygon, ' ', '')
FROM remi.padi_res_header_prev p
ORDER BY 1;

-- quad / block checks --

-- well checks --

SELECT
    h.resdat_id,
    h.o_well_number
FROM remi.padi_res_data_header h
WHERE h.o_well_number NOT IN (SELECT prdhp.o_well_number
                              FROM remi.padi_res_data_header_prev prdhp);

SELECT prdh.resdat_id, prdh.resource_id, prdh.o_well_number
FROM remi.padi_res_data_header prdh
WHERE REPLACE(LOWER(prdh.o_well_number), ' ', '') NOT IN (
    SELECT REPLACE(LOWER(pwm.well_registration_no), ' ', '')
    FROM remi.padi_well_mapping pwm
    );

SELECT resdat_id, resource_id, o_well_number
FROM remi.padi_res_data_header
WHERE o_well_number IS NOT NULL
EXCEPT
SELECT pwm.padi_resdat_id, pwm.padi_resource_id, pwm.well_registration_no
FROM remi.padi_well_mapping pwm
ORDER BY 1;

SELECT
    prdh.resdat_id padi_resdat_id,
    prdh.resource_id padi_resource_id,
    prdh.o_well_number padi_well
FROM remi.padi_res_data_header prdh
WHERE prdh.o_well_number IS NOT NULL;

-- well checks --

-- operator checks --
UPDATE remi.padi_res_data_header prdh
SET company = TRIM(company);

UPDATE remi.padi_operator_mapping
SET padi_name = TRIM(padi_name);

INSERT INTO remi.padi_operator_mapping (padi_name)
SELECT TRIM(prdh.company)
FROM remi.padi_res_data_header prdh
WHERE lower(prdh.company) NOT IN  (
    SELECT lower(pom.padi_name)
    FROM remi.padi_operator_mapping pom
);

-- operator checks --

-- licence checks --
SELECT prdh.resdat_id, prdh.resource_id, prdh.licence_list
FROM remi.padi_res_data_header prdh
WHERE prdh.licence_list IS NOT NULL;

SELECT prdh.resdat_id, prdh.licence_list
FROM remi.padi_res_data_header prdh
WHERE prdh.licence_list IS NOT NULL
AND prdh.resdat_id NOT IN (
    SELECT plm.padi_res_data_id
    FROM remi.padi_licence_mapping plm
);

WITH new_rows AS (
    SELECT *
    FROM remi.padi_res_data_header prdh
    WHERE prdh.licence_list IS NOT NULL AND
    prdh.resdat_id NOT IN (
        SELECT plm.padi_res_data_id
        FROM remi.padi_licence_mapping plm
    )
)
SELECT unnest(string_to_array(nr.licence_list, ','))
FROM new_rows nr
EXCEPT
SELECT plm2.licence
FROM remi.padi_licence_mapping plm2

-- licence checks --