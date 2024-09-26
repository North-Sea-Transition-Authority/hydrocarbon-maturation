DO $$
DECLARE

    pars_rec RECORD;
    resource_id_exists NUMERIC;
    resource_name_matches NUMERIC;
    undeveloped_discovery_id UUID;
    oil_equiv_recoverable_two_c DECIMAL;
    percentage_gas DECIMAL;

BEGIN

    FOR pars_rec IN (
        SELECT *
        FROM remi.pars_undeveloped_discoveries pud
    )
    LOOP

        resource_id_exists := NULL;
        resource_name_matches := NULL;
        undeveloped_discovery_id := NULL;
        oil_equiv_recoverable_two_c := NULL;
        percentage_gas := NULL;

        SELECT 1
        INTO resource_id_exists
        FROM remi.resources r
        WHERE r.id = pars_rec.resource_id
        AND r.type = 'DISCOVERY';

        IF resource_id_exists IS NULL THEN

            INSERT INTO remi.pars_migration_log (
                resource_id,
                migration_timestamp,
                status,
                failure_reason
            ) VALUES (
                pars_rec.resource_id,
                now(),
                'FAILED',
                'Resource does not exist in ReMI or is not a discovery resource'
            );

            CONTINUE;

        END IF;

        SELECT 1
        INTO resource_name_matches
        WHERE EXISTS (
            SELECT 1
            FROM remi.resources r
            JOIN remi.submissions s ON r.id = s.resource_id
            JOIN remi.submission_details sd ON sd.submission_id = s.id
            JOIN remi.resource_details rd ON rd.submission_detail_id = sd.id
            WHERE r.id = pars_rec.resource_id
            AND LOWER(rd.resource_name) = LOWER(pars_rec.resource_name)
        );

        IF resource_name_matches IS NULL THEN

            INSERT INTO remi.pars_migration_log (
                resource_id,
                migration_timestamp,
                status,
                failure_reason
            ) VALUES (
                 pars_rec.resource_id,
                 now(),
                 'FAILED',
                 'Resource name does not match the resource name in ReMI'
             );

            CONTINUE;

        END IF;
        
        IF pars_rec.oil_recoverable_one_c IS NOT NULL AND pars_rec.oil_recoverable_two_c IS NOT NULL AND pars_rec.oil_recoverable_three_c IS NOT NULL THEN
            
            IF pars_rec.oil_recoverable_one_c > pars_rec.oil_recoverable_two_c 
                   OR pars_rec.oil_recoverable_one_c > pars_rec.oil_recoverable_three_c 
                   OR pars_rec.oil_recoverable_two_c > pars_rec.oil_recoverable_three_c THEN
                       
                INSERT INTO remi.pars_migration_log (
                    resource_id,
                    migration_timestamp,
                    status,
                    failure_reason
                ) VALUES (
                     pars_rec.resource_id,
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
                    resource_id,
                    migration_timestamp,
                    status,
                    failure_reason
                ) VALUES (
                     pars_rec.resource_id,
                     now(),
                     'FAILED',
                     'Gas recoverable values are not in the correct order'
                 );

                CONTINUE;

            END IF;

        END IF;

        UPDATE remi.undeveloped_discoveries
        SET is_undeveloped_discovery = true
        WHERE resource_id = pars_rec.resource_id;

        SELECT ud.id
        INTO undeveloped_discovery_id
        FROM remi.undeveloped_discoveries ud
        WHERE ud.resource_id = pars_rec.resource_id;

        SELECT
            CASE WHEN pars_rec.oil_recoverable_two_c IS NOT NULL AND pars_rec.gas_recoverable_two_c IS NOT NULL THEN
                ROUND(pars_rec.oil_recoverable_two_c + (pars_rec.gas_recoverable_two_c/5.8), 2)
            END
        INTO oil_equiv_recoverable_two_c;

        IF pars_rec.oil_recoverable_two_c IS NULL OR oil_equiv_recoverable_two_c IS NULL THEN
            percentage_gas := NULL;
        ELSIF pars_rec.oil_recoverable_two_c = 0 THEN
            percentage_gas := 100;
        ELSE
            percentage_gas := ROUND(100 * (1 - (pars_rec.oil_recoverable_two_c / oil_equiv_recoverable_two_c)), 2);
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
            pars_rec.date_volumetric_information_relevant_to,
            ROUND(pars_rec.oil_in_place, 2),
            ROUND(pars_rec.oil_recoverable_one_c, 2),
            ROUND(pars_rec.oil_recoverable_two_c, 2),
            ROUND(pars_rec.oil_recoverable_three_c, 2),
            ROUND(pars_rec.gas_in_place, 2),
            ROUND(pars_rec.gas_recoverable_one_c, 2),
            ROUND(pars_rec.gas_recoverable_two_c, 2),
            ROUND(pars_rec.gas_recoverable_three_c, 2),
            NULL, -- TODO check this can be provided
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
            resource_id,
            migration_timestamp,
            status,
            failure_reason
        ) VALUES (
             pars_rec.resource_id,
             now(),
             'COMPLETED',
             NULL
         );

    END LOOP;

END
$$;