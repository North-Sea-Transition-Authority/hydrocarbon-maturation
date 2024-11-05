DO $$
DECLARE

    submission_rec RECORD;
    bid TEXT := 'Bid';
    pre_drill TEXT := 'Pre-drill';
    well_investment TEXT := 'Well Investment';
    relinquishment TEXT := 'Relinquishment';
    licence_rec RECORD;

BEGIN

    FOR submission_rec IN (
        SELECT
            sd.id,
            r.type,
            COALESCE(pmrd.resource_name, prdh.name_alternative) chosen_resource_name,
            CASE
                WHEN (
                    SELECT pqbm.shore_type
                    FROM  remi.padi_quad_block_mapping pqbm
                    WHERE pqbm.padi_resource_id = prdh.resource_id
                    GROUP BY pqbm.shore_type
                ) = 'ONSHORE' THEN 'ONSHORE'
                ELSE 'OFFSHORE'
            END onshore_offshore,
            (
                SELECT pom.org_unit_id
                FROM remi.padi_operator_mapping pom
                WHERE LOWER(pom.padi_name) = LOWER(prdh.company)
                GROUP BY pom.org_unit_id
            ) org_unit_id,
            (
                SELECT pom.agreed_match
                FROM remi.padi_operator_mapping pom
                WHERE LOWER(pom.padi_name) = LOWER(prdh.company)
                GROUP BY pom.agreed_match
            ) org_unit_name,
            (
                SELECT pom.registered_number
                FROM remi.padi_operator_mapping pom
                WHERE LOWER(pom.padi_name) = LOWER(prdh.company)
                GROUP BY pom.registered_number
            ) registered_number,
            CASE
                WHEN prdh.stage_name = bid THEN 'LICENCE_APPLICATION'
                WHEN prdh.stage_name = pre_drill THEN 'PRE_DRILL_SUBMISSION'
                WHEN prdh.stage_name = well_investment THEN 'WELL_INVESTMENT_DECISION'
                WHEN prdh.stage_name = relinquishment THEN 'RELINQUISHMENT'
                ELSE 'OTHER'
            END info_source,
            CASE
                WHEN prdh.stage_name NOT IN (bid, pre_drill, well_investment, relinquishment) THEN prdh.stage_name
            END other_info_source,
            prdh.application_ref licence_app_ref,
            CASE WHEN prdh.application_ref IS NULL AND prdh.reference_round != '0' THEN prdh.reference_round END licence_round,
            CASE WHEN prdh.stage_name = relinquishment THEN TO_DATE(prdh.stage_date, 'DD/MM/YYYY') END date_of_relinquishment
        FROM remi.submission_details sd
        JOIN remi.submissions s ON s.id = sd.submission_id
        JOIN remi.resources r ON r.id = s.resource_id
        JOIN remi.padi_res_data_header prdh ON prdh.resdat_id = sd.legacy_res_data_id
        LEFT JOIN remi.padi_multi_reservoir_discoveries pmrd ON pmrd.resdat_id = sd.legacy_res_data_id
    )
    LOOP

        INSERT INTO remi.resource_details (
            id,
            submission_detail_id,
            resource_name,
            onshore_offshore,
            organisation_id,
            organisation_name,
            organisation_registered_number
        ) VALUES (
            gen_random_uuid(),
            submission_rec.id,
            submission_rec.chosen_resource_name,
            submission_rec.onshore_offshore,
            submission_rec.org_unit_id,
            submission_rec.org_unit_name,
            submission_rec.registered_number
        );

        INSERT INTO remi.general_details (
            id,
            submission_detail_id,
            prospect_discovery_located_in_ukcs,
            prospect_discovery_located_in_england,
            percentage_located_in_ukcs,
            percentage_located_in_england,
            information_source,
            other_information_source,
            licence_application_reference,
            relinquishment_date
        ) VALUES (
            gen_random_uuid(),
            submission_rec.id,
            CASE WHEN submission_rec.onshore_offshore = 'OFFSHORE' THEN TRUE END, -- can this ever be set to "No" for "Is the prospect wholly located within UKCS?"
            CASE WHEN submission_rec.onshore_offshore = 'ONSHORE' THEN TRUE END,
            NULL, -- we don't capture this in PaDI,
            NULL,
            submission_rec.info_source,
            submission_rec.other_info_source,
            COALESCE(submission_rec.licence_app_ref, submission_rec.licence_round),
            submission_rec.date_of_relinquishment
        );

    END LOOP;

    FOR licence_rec IN (
        SELECT gd.id gd_id, plm.licence_id, plm.licence licence_ref
        FROM remi.submission_details sd2
        JOIN remi.padi_licence_mapping plm ON plm.padi_res_data_id = sd2.legacy_res_data_id
        JOIN remi.general_details gd ON sd2.id = gd.submission_detail_id
    )
    LOOP

        INSERT INTO remi.general_details_licences (
           id,
           general_details_id,
           licence_reference,
           licence_id
        ) VALUES (
            gen_random_uuid(),
            licence_rec.gd_id,
            licence_rec.licence_ref,
            licence_rec.licence_id
        );

    END LOOP;
END;
$$;