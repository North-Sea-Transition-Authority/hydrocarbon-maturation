DO $$

DECLARE

    discovery_rec RECORD;

    has_mixed_case_properties BOOLEAN;
    has_oil_case_params BOOLEAN;
    has_oil_case_fluid_properties BOOLEAN;
    has_gas_case_params BOOLEAN;
    has_gas_case_fluid_properties BOOLEAN;
    data_header_hc_case TEXT;

BEGIN

    FOR discovery_rec IN (
        SELECT rsd.id rsd_id
        FROM remi.reservoir_submission_details rsd
        JOIN remi.submission_details sd ON rsd.submission_detail_id = sd.id
        JOIN remi.submissions s ON s.id = sd.submission_id
        JOIN remi.resources r ON s.resource_id = r.id
        WHERE r.type = 'DISCOVERY'
    )
    LOOP

        has_mixed_case_properties := FALSE;
        has_oil_case_params := FALSE;
        has_oil_case_fluid_properties := FALSE;
        has_gas_case_params := FALSE;
        has_gas_case_fluid_properties := FALSE;

        INSERT INTO remi.reservoir_hydrocarbons (
            id,
            reservoir_submission_detail_id,
            hydrocarbons_discovered
        ) VALUES (
            gen_random_uuid(),
            discovery_rec.rsd_id,
            NULL --update based on probability distributions found
        );


            SELECT TRUE
            INTO has_mixed_case_properties
            FROM remi.probability_distributions pd
            WHERE pd.instance IN (
                'MIXED_FORMATION_VOLUME_FACTOR_DISCOVERY',
                'MIXED_OIL_HYDROCARBON_SATURATION_DISCOVERY',
                'MIXED_GAS_HYDROCARBON_SATURATION_DISCOVERY',
                'MIXED_ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD',
                'MIXED_GAS_EXPANSION_FACTOR',
                'MIXED_CONDENSATE_GAS_RATIO_LIFE_OF_FIELD',
                'MIXED_GAS_RECOVERY_FACTOR',
                'MIXED_OIL_RECOVERY_FACTOR'
            )
        AND pd.reservoir_submission_detail_id = discovery_rec.rsd_id
        AND pd.distribution_type != 'NOT_CALCULATED';

        IF has_mixed_case_properties THEN

            UPDATE remi.reservoir_hydrocarbons
            SET hydrocarbons_discovered = 'MIXED_OIL_AND_GAS'
            WHERE reservoir_submission_detail_id = discovery_rec.rsd_id;

        ELSE

            SELECT TRUE
            INTO has_oil_case_params
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

            has_oil_case_params := COALESCE(has_oil_case_params, FALSE);

            SELECT TRUE
            INTO has_gas_case_params
            WHERE EXISTS (
                SELECT 1
                FROM remi.probability_distributions pd
                WHERE pd.instance IN (
                    'GAS_HYDROCARBON_SATURATION_DISCOVERY',
                    'GAS_EXPANSION_FACTOR',
                    'GAS_RECOVERY_FACTOR',
                    'CONDENSATE_GAS_RATIO_LIFE_OF_FIELD',
                    'GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY',
                    'GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY'
                )
                AND pd.reservoir_submission_detail_id = discovery_rec.rsd_id
                AND pd.distribution_type != 'NOT_CALCULATED'
            );

            has_gas_case_params := COALESCE(has_gas_case_params, FALSE);

            SELECT TRUE
            INTO has_oil_case_fluid_properties
            WHERE EXISTS(
                SELECT 1
                FROM remi.oil_fluid_properties ofp
                WHERE ofp.reservoir_submission_detail_id = discovery_rec.rsd_id
            );

            has_oil_case_fluid_properties := COALESCE(has_oil_case_fluid_properties, FALSE);

            SELECT TRUE
            INTO has_gas_case_fluid_properties
            WHERE EXISTS(
                SELECT 1
                FROM remi.gas_fluid_properties gfp
                WHERE gfp.reservoir_submission_detail_id = discovery_rec.rsd_id
            );

            has_gas_case_fluid_properties := COALESCE(has_gas_case_fluid_properties, FALSE);

            -- if both oil properties and gas properties exists then it is a mixed case
            IF (has_oil_case_params OR has_oil_case_fluid_properties) AND (has_gas_case_params OR has_gas_case_fluid_properties) THEN

                UPDATE remi.reservoir_hydrocarbons
                SET hydrocarbons_discovered = 'MIXED_OIL_AND_GAS'
                WHERE reservoir_submission_detail_id = discovery_rec.rsd_id;

            -- if any oil properties exist and no gas properties exist then it is an oil only case
            ELSIF (has_oil_case_params OR has_oil_case_fluid_properties) AND NOT(has_gas_case_params) AND NOT(has_gas_case_fluid_properties) THEN

                UPDATE remi.reservoir_hydrocarbons
                SET hydrocarbons_discovered = 'OIL_ONLY'
                WHERE reservoir_submission_detail_id = discovery_rec.rsd_id;

            -- if any gas properties exist and no oil properties exist then it is an gas only case
            ELSIF (has_gas_case_params OR has_gas_case_fluid_properties) AND NOT(has_oil_case_params) AND NOT(has_oil_case_fluid_properties) THEN

                UPDATE remi.reservoir_hydrocarbons
                SET hydrocarbons_discovered = 'GAS_ONLY'
                WHERE reservoir_submission_detail_id = discovery_rec.rsd_id;

            -- fall back to PaDI hydrocarbon case if can't determine from probability distributions or fluid properties
            ELSE

                SELECT LOWER(prdh.hc_case)
                INTO data_header_hc_case
                FROM remi.padi_res_data_header prdh
                JOIN remi.padi_reservoirs pr ON pr.legacy_res_data_id = prdh.resdat_id
                JOIN remi.reservoir_submission_details rsd ON rsd.reservoir = pr.remi_reservoir_id
                WHERE rsd.id = discovery_rec.rsd_id;

                IF data_header_hc_case IN ('gas','condensate') THEN

                    UPDATE remi.reservoir_hydrocarbons
                    SET hydrocarbons_discovered = 'GAS_ONLY'
                    WHERE reservoir_submission_detail_id = discovery_rec.rsd_id;

                ELSIF data_header_hc_case = 'oil' THEN

                    UPDATE remi.reservoir_hydrocarbons
                    SET hydrocarbons_discovered = 'OIL_ONLY'
                    WHERE reservoir_submission_detail_id = discovery_rec.rsd_id;

                ELSIF data_header_hc_case = 'mixed oil and gas' THEN

                    UPDATE remi.reservoir_hydrocarbons
                    SET hydrocarbons_discovered = 'MIXED_OIL_AND_GAS'
                    WHERE reservoir_submission_detail_id = discovery_rec.rsd_id;

                END IF;

            END IF;

        END IF;

    END LOOP;

END;

$$;