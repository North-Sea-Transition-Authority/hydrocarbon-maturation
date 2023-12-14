DO $$
    DECLARE

        company_diff_rec RECORD;
        company_update_rec RECORD;
        company_org_rec RECORD;

    BEGIN

        FOR company_diff_rec IN (
            SELECT company
            FROM remi.padi_res_data_header
            WHERE company IS NOT NULL
            EXCEPT
            SELECT pom.padi_name
            FROM remi.padi_operator_mapping pom
        )
        LOOP

            INSERT INTO remi.padi_operator_mapping (padi_name)
            VALUES (company_diff_rec.company);

        END LOOP;

        FOR company_update_rec IN (
            SELECT pom.padi_name, pom2.agreed_match
            FROM remi.padi_operator_mapping pom
            JOIN remi.padi_operator_mapping pom2 ON UPPER(pom2.agreed_match) = UPPER(pom.padi_name)
            WHERE pom.agreed_match IS NULL
            AND pom.source_table IS NULL
            AND pom2.agreed_match IS NOT NULL
            GROUP BY pom.padi_name, pom2.agreed_match
        )
        LOOP

            UPDATE remi.padi_operator_mapping
            SET agreed_match = company_update_rec.agreed_match
            WHERE padi_name = company_update_rec.padi_name
            AND agreed_match IS NULL
            AND source_table IS NULL;

        END LOOP;

        FOR company_org_rec IN (
            SELECT pom.agreed_match, pom.org_unit_id, pom.registered_number, pom.org_grp_id, pom.org_grp_name
            FROM remi.padi_operator_mapping pom
            JOIN remi.padi_operator_mapping pom2 ON pom.agreed_match = pom2.agreed_match
            WHERE pom.agreed_match IS NOT NULL
            AND pom.org_unit_id IS NOT NULL
            AND pom2.org_unit_id IS NULL
            GROUP BY pom.agreed_match, pom.org_unit_id, pom.registered_number, pom.org_grp_id, pom.org_grp_name
        )
        LOOP

            UPDATE remi.padi_operator_mapping
            SET org_unit_id = company_org_rec.org_unit_id,
                registered_number = company_org_rec.registered_number,
                org_grp_id = company_org_rec.org_grp_id,
                org_grp_name = company_org_rec.org_grp_name
            WHERE agreed_match = company_org_rec.agreed_match
            AND org_unit_id IS NULL;

        END LOOP;

    END;
$$;