DO $$
    DECLARE

        company_diff_rec RECORD;

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

    END;
$$;