DO $$
DECLARE

    pars_rec RECORD;
    undeveloped_discovery_id UUID;
    oil_equiv_recoverable_one_c DECIMAL;
    oil_equiv_recoverable_two_c DECIMAL;
    oil_equiv_recoverable_three_c DECIMAL;
    percentage_gas DECIMAL;
    ud_audit_rev INTEGER;
    info_audit_rev INTEGER;
    info_id UUID;

BEGIN

    FOR pars_rec IN (
        SELECT *
        FROM remi.pars_undeveloped_discoveries pud
        WHERE pud.remi_resource_id IS NOT NULL
    )
    LOOP

        undeveloped_discovery_id := NULL;
        oil_equiv_recoverable_two_c := NULL;
        percentage_gas := NULL;
        
        IF pars_rec.oil_recoverable_one_c IS NOT NULL AND pars_rec.oil_recoverable_two_c IS NOT NULL AND pars_rec.oil_recoverable_three_c IS NOT NULL THEN
            
            IF pars_rec.oil_recoverable_one_c > pars_rec.oil_recoverable_two_c 
                   OR pars_rec.oil_recoverable_one_c > pars_rec.oil_recoverable_three_c 
                   OR pars_rec.oil_recoverable_two_c > pars_rec.oil_recoverable_three_c THEN
                       
                INSERT INTO remi.pars_migration_log (
                    remi_resource_id,
                    remi_resource_name,
                    pars_resource_name,
                    migration_timestamp,
                    status,
                    failure_reason
                ) VALUES (
                    pars_rec.remi_resource_id,
                    pars_rec.remi_resource_name,
                    pars_rec.pars_resource_name,
                    now(),
                    'FAILED',
                    'Oil recoverable values are not in the correct order'
                );

                CONTINUE;
                
            END IF;
            
        END IF;

        IF pars_rec.gas_recoverable_one_c IS NOT NULL AND pars_rec.gas_recoverable_two_c IS NOT NULL AND pars_rec.gas_recoverable_three_c IS NOT NULL THEN

            IF pars_rec.gas_recoverable_one_c > pars_rec.gas_recoverable_two_c
                OR pars_rec.gas_recoverable_one_c > pars_rec.gas_recoverable_three_c
                OR pars_rec.gas_recoverable_two_c > pars_rec.gas_recoverable_three_c THEN

                INSERT INTO remi.pars_migration_log (
                    remi_resource_id,
                    remi_resource_name,
                    pars_resource_name,
                    migration_timestamp,
                    status,
                    failure_reason
                ) VALUES (
                     pars_rec.remi_resource_id,
                    pars_rec.remi_resource_name,
                    pars_rec.pars_resource_name,
                     now(),
                     'FAILED',
                     'Gas recoverable values are not in the correct order'
                 );

                CONTINUE;

            END IF;

        END IF;

        UPDATE remi.undeveloped_discoveries
        SET is_undeveloped_discovery = true
        WHERE resource_id = pars_rec.remi_resource_id;

        INSERT INTO remi.audit_revisions (
            updated_at,
            updated_by_user_id
        ) VALUES (
             now(),
             1
         ) RETURNING rev INTO ud_audit_rev;

        SELECT ud.id
        INTO undeveloped_discovery_id
        FROM remi.undeveloped_discoveries ud
        WHERE ud.resource_id = pars_rec.remi_resource_id;

        INSERT INTO remi.undeveloped_discoveries_aud (
            rev,
            revtype,
            id,
            is_undeveloped_discovery,
            resource_id
        ) VALUES (
            ud_audit_rev,
            1,
            undeveloped_discovery_id,
            true,
            pars_rec.remi_resource_id
        );

        oil_equiv_recoverable_one_c := ROUND(COALESCE(pars_rec.oil_recoverable_one_c, 0) + (COALESCE(pars_rec.gas_recoverable_one_c, 0)/5.8), 2);
        oil_equiv_recoverable_two_c := ROUND(COALESCE(pars_rec.oil_recoverable_two_c, 0) + (COALESCE(pars_rec.gas_recoverable_two_c, 0)/5.8), 2);
        oil_equiv_recoverable_three_c := ROUND(COALESCE(pars_rec.oil_recoverable_three_c, 0) + (COALESCE(pars_rec.gas_recoverable_three_c, 0)/5.8), 2);

        IF oil_equiv_recoverable_two_c = 0 THEN
            percentage_gas := 0;
        ELSIF COALESCE(pars_rec.oil_recoverable_two_c, 0) = 0 THEN
            percentage_gas := 100;
        ELSE
            percentage_gas := ROUND(100 * (1 - (COALESCE(pars_rec.oil_recoverable_two_c, 0) / oil_equiv_recoverable_two_c)), 2);
        END IF;

        INSERT INTO remi.undeveloped_discovery_information (
            id,
            undeveloped_discovery_id,
            information_date,
            oil_in_place_p50,
            oil_recoverable_one_c,
            oil_recoverable_two_c,
            oil_recoverable_three_c,
            gas_in_place_p50,
            gas_recoverable_one_c,
            gas_recoverable_two_c,
            gas_recoverable_three_c,
            difference_reason,
            provided_discovery_complexity,
            status,
            oil_equivalent_recoverable_one_c,
            oil_equivalent_recoverable_two_c,
            oil_equivalent_recoverable_three_c,
            percentage_gas
        ) VALUES (
            gen_random_uuid(),
            undeveloped_discovery_id,
            TO_DATE(pars_rec.year_volumetric_information_relevant_to, 'YYYY'),
            ROUND(pars_rec.oil_in_place, 2),
            ROUND(pars_rec.oil_recoverable_one_c, 2),
            ROUND(pars_rec.oil_recoverable_two_c, 2),
            ROUND(pars_rec.oil_recoverable_three_c, 2),
            ROUND(pars_rec.gas_in_place, 2),
            ROUND(pars_rec.gas_recoverable_one_c, 2),
            ROUND(pars_rec.gas_recoverable_two_c, 2),
            ROUND(pars_rec.gas_recoverable_three_c, 2),
            NULL,
            false,
            'EXTANT',
            oil_equiv_recoverable_one_c,
            oil_equiv_recoverable_two_c,
            oil_equiv_recoverable_three_c,
            percentage_gas
        ) RETURNING id INTO info_id;

        INSERT INTO remi.audit_revisions (
            updated_at,
            updated_by_user_id
        ) VALUES (
             now(),
             1
         ) RETURNING rev INTO info_audit_rev;

        INSERT INTO remi.undeveloped_discovery_information_aud (
            rev,
            revtype,
            id,
            undeveloped_discovery_id,
            information_date,
            oil_in_place_p50,
            oil_recoverable_one_c,
            oil_recoverable_two_c,
            oil_recoverable_three_c,
            gas_in_place_p50,
            gas_recoverable_one_c,
            gas_recoverable_two_c,
            gas_recoverable_three_c,
            difference_reason,
            provided_discovery_complexity,
            status,
            oil_equivalent_recoverable_one_c,
            oil_equivalent_recoverable_two_c,
            oil_equivalent_recoverable_three_c,
            percentage_gas
        ) VALUES (
             info_audit_rev,
            0,
            info_id,
             undeveloped_discovery_id,
             TO_DATE(pars_rec.year_volumetric_information_relevant_to, 'YYYY'),
             ROUND(pars_rec.oil_in_place, 2),
             ROUND(pars_rec.oil_recoverable_one_c, 2),
             ROUND(pars_rec.oil_recoverable_two_c, 2),
             ROUND(pars_rec.oil_recoverable_three_c, 2),
             ROUND(pars_rec.gas_in_place, 2),
             ROUND(pars_rec.gas_recoverable_one_c, 2),
             ROUND(pars_rec.gas_recoverable_two_c, 2),
             ROUND(pars_rec.gas_recoverable_three_c, 2),
             NULL,
             false,
             'EXTANT',
             CASE WHEN pars_rec.oil_recoverable_one_c IS NOT NULL AND pars_rec.gas_recoverable_one_c IS NOT NULL THEN
                      ROUND(pars_rec.oil_recoverable_one_c + (pars_rec.gas_recoverable_one_c/5.8), 2)
                 END,
             oil_equiv_recoverable_two_c,
             CASE WHEN pars_rec.oil_recoverable_three_c IS NOT NULL AND pars_rec.gas_recoverable_three_c IS NOT NULL THEN
                      ROUND(pars_rec.oil_recoverable_three_c + (pars_rec.gas_recoverable_three_c/5.8), 2)
                 END,
             percentage_gas
         );

        INSERT INTO remi.pars_migration_log (
            remi_resource_id,
            remi_resource_name,
            pars_resource_name,
            migration_timestamp,
            status,
            failure_reason
        ) VALUES (
            pars_rec.remi_resource_id,
            pars_rec.remi_resource_name,
            pars_rec.pars_resource_name,
            now(),
            'COMPLETED',
            NULL
        );

    END LOOP;

END
$$;