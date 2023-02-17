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
            prdh.name_alternative,
            CASE
                WHEN prdh.play_fairway = 'Onshore' THEN 'ONSHORE'
                ELSE 'OFFSHORE' -- raised https://ogajira2.atlassian.net/browse/HM-430 to determine onshore / offshore strategy
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
            CASE WHEN prdh.stage_name = bid THEN prdh.application_ref END licence_app_ref,
            CASE WHEN prdh.licence_list IS NOT NULL THEN TRUE ELSE FALSE END resource_on_licence
        FROM remi.submission_details sd
        JOIN remi.padi_res_data_header prdh ON prdh.resdat_id = sd.legacy_res_data_id
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
            submission_rec.name_alternative,
            submission_rec.onshore_offshore,
            submission_rec.org_unit_id,
            submission_rec.org_unit_name,
            submission_rec.registered_number
        );

        INSERT INTO remi.general_details (
            id,
            submission_detail_id,
            resource_located_in_ukcs,
            percentage_located_in_ukcs,
            information_source,
            other_information_source,
            licence_application_reference,
            resource_on_a_licence
        ) VALUES (
            gen_random_uuid(),
            submission_rec.id,
            TRUE, -- can this ever be set to "No" for "Is the prospect wholly located within UKCS?"
            NULL, -- we don't capture this in PaDI
            submission_rec.info_source,
            submission_rec.other_info_source,
            submission_rec.licence_app_ref,
            submission_rec.resource_on_licence
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