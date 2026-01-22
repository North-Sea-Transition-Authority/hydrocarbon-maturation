DO $$

DECLARE

    discovery_rec RECORD;
    has_gas_properties BOOLEAN := FALSE;
    has_oil_properties BOOLEAN := FALSE;

BEGIN

    FOR discovery_rec IN (
        SELECT rsd.id rsd_id, LOWER(prdh.hc_case) hydrocarbon_case
        FROM remi.reservoir_submission_details rsd
        JOIN remi.submission_details sd ON rsd.submission_detail_id = sd.id
        JOIN remi.submissions s ON s.id = sd.submission_id
        JOIN remi.resources r ON s.resource_id = r.id
        JOIN remi.padi_res_data_header prdh ON prdh.resdat_id = sd.legacy_res_data_id
        WHERE r.type = 'DISCOVERY'
    )
    LOOP

        SELECT TRUE
        INTO has_gas_properties
        WHERE EXISTS (
            SELECT 1
            FROM remi.probability_distributions pd
            WHERE pd.instance IN (
                'GAS_HYDROCARBON_SATURATION_DISCOVERY',
                'GAS_EXPANSION_FACTOR',
                'GAS_RECOVERY_FACTOR',
                'GAS_FORMATION_VOLUME_FACTOR_DISCOVERY',
                'CONDENSATE_GAS_RATIO_LIFE_OF_FIELD',
                'GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY',
                'GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY'
            )
            AND pd.reservoir_submission_detail_id = discovery_rec.rsd_id
            AND pd.distribution_type != 'NOT_CALCULATED'
        );

        SELECT TRUE
        INTO has_oil_properties
        WHERE EXISTS (
            SELECT 1
            FROM remi.probability_distributions pd
            WHERE pd.instance IN (
              'OIL_RECOVERY_FACTOR_DISCOVERY',
              'OIL_FORMATION_VOLUME_FACTOR_DISCOVERY',
              'OIL_HYDROCARBON_SATURATION_DISCOVERY',
              'ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD',
              'OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY',
              'OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY',
              'OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY'
            )
            AND pd.reservoir_submission_detail_id = discovery_rec.rsd_id
            AND pd.distribution_type != 'NOT_CALCULATED'
        );

        IF discovery_rec.hydrocarbon_case = 'mixed oil and gas' THEN

            INSERT INTO remi.reservoir_hydrocarbons (
                id,
                reservoir_submission_detail_id,
                hydrocarbons_discovered
            ) VALUES (
             gen_random_uuid(),
             discovery_rec.rsd_id,
             'MIXED_OIL_AND_GAS'
            );

        ELSIF has_gas_properties AND NOT(has_oil_properties) THEN

            INSERT INTO remi.reservoir_hydrocarbons (
                id,
                reservoir_submission_detail_id,
                hydrocarbons_discovered
            ) VALUES (
                gen_random_uuid(),
                discovery_rec.rsd_id,
                'GAS_ONLY'
            );

        ELSIF has_oil_properties AND NOT(has_gas_properties) THEN

            INSERT INTO remi.reservoir_hydrocarbons (
                id,
                reservoir_submission_detail_id,
                hydrocarbons_discovered
            ) VALUES (
                gen_random_uuid(),
                discovery_rec.rsd_id,
                'OIL_ONLY'
            );

        ELSIF has_oil_properties AND has_gas_properties THEN

            INSERT INTO remi.reservoir_hydrocarbons (
                id,
                reservoir_submission_detail_id,
                hydrocarbons_discovered
            ) VALUES (
                 gen_random_uuid(),
                 discovery_rec.rsd_id,
                 'MIXED_OIL_AND_GAS'
             );

        ELSIF discovery_rec.hydrocarbon_case IN ('gas','condensate') THEN

            INSERT INTO remi.reservoir_hydrocarbons (
                id,
                reservoir_submission_detail_id,
                hydrocarbons_discovered
            ) VALUES (
                gen_random_uuid(),
                discovery_rec.rsd_id,
                'GAS_ONLY'
            );

        ELSIF discovery_rec.hydrocarbon_case = 'oil' THEN

            INSERT INTO remi.reservoir_hydrocarbons (
                id,
                reservoir_submission_detail_id,
                hydrocarbons_discovered
            ) VALUES (
                gen_random_uuid(),
                discovery_rec.rsd_id,
                'OIL_ONLY'
            );

        END IF;

    END LOOP;

END;

$$;