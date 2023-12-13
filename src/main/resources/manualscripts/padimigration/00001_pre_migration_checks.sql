INSERT INTO remi.padi_res_header_prev
SELECT *
FROM remi.padi_res_header;

INSERT INTO remi.padi_res_data_header_prev
SELECT *
FROM remi.padi_res_data_header;

-- extract for quad block mapping
SELECT resource_id, REPLACE(block_polygon, ' ', ''), block_centroid, play_fairway
FROM remi.padi_res_header
WHERE block_polygon IS NOT NULL
ORDER BY resource_id;

SELECT resource_id, block_centroid
FROM remi.padi_res_header_prev;

SELECT h.resource_id, h.block_polygon
FROM remi.padi_res_header h
EXCEPT
SELECT p.resource_id, p.block_polygon
FROM remi.padi_res_header_prev p
ORDER BY 1;


SELECT
    h.resdat_id,
--     h.resource_id,
    h.stage_date,
    h.stage_name,
--     h.stage_choice,
--     h.name_alternative,
    h.company,
    h.o_well_number
--     h.application_ref,
--     h.hc_case,
--     h.hc_case_comment,
--     h.opportunity_type,
--     h.opportunity_comment,
--     h.key_evaluation_comment,
--     h.key_work_comment,
--     h.trap_type,
--     h.trap_type_comment,
--     h.latitude_c_of_i,
--     h.longitude_c_of_i
--     h.description
FROM remi.padi_res_data_header h
WHERE h.resdat_id <= 55731
EXCEPT
SELECT
    p.resdat_id,
--     p.resource_id,
    p.stage_date,
    p.stage_name,
--     p.stage_choice,
--     p.name_alternative,
    p.company,
    p.o_well_number
--     p.application_ref,
--     p.hc_case,
--     p.hc_case_comment,
--     p.opportunity_type,
--     p.opportunity_comment,
--     p.key_evaluation_comment,
--     p.key_work_comment,
--     p.trap_type,
--     p.trap_type_comment,
--     p.latitude_c_of_i,
--     p.longitude_c_of_i
--     p.description
FROM remi.padi_res_data_header_prev p
WHERE p.resdat_id <= 55731;

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

SELECT *
FROM remi.padi_operator_mapping pom
WHERE pom.agreed_match IS NULL
AND source_table IS NULL
AND padi_name IN (
    SELECT pom2.agreed_match
    FROM remi.padi_operator_mapping pom2
    WHERE pom2.agreed_match IS NOT NULL
);