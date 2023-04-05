DO $$

DECLARE

    discovery_rec RECORD;
    has_mixed_case_properties BOOLEAN;
    has_oil_case_params BOOLEAN;
    has_oil_case_fluid_properties BOOLEAN;
    has_gas_case_params BOOLEAN;
    has_gas_case_fluid_properties BOOLEAN;

BEGIN

    FOR discovery_rec IN (
        SELECT sd.id sd_id
        FROM remi.submission_details sd
        JOIN remi.resources r ON sd.resource_id = r.id
        JOIN remi.discovery_well_details dwd ON dwd.submission_detail_id = sd.id
        WHERE r.type = 'DISCOVERY'
    )
    LOOP

        has_mixed_case_properties := FALSE;
        has_oil_case_params := FALSE;
        has_oil_case_fluid_properties := FALSE;
        has_gas_case_params := FALSE;
        has_gas_case_fluid_properties := FALSE;

        SELECT TRUE
        INTO has_mixed_case_properties
        WHERE EXISTS (
            SELECT 1
            FROM remi.probability_distributions pd
            WHERE pd.instance IN (
                'MIXED_FORMATION_VOLUME_FACTOR_PROSPECT_DISCOVERY',
                'MIXED_OIL_HYDROCARBON_SATURATION',
                'MIXED_GAS_HYDROCARBON_SATURATION',
                'MIXED_ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD',
                'MIXED_GAS_EXPANSION_FACTOR',
                'MIXED_CONDENSATE_GAS_RATIO_LIFE_OF_FIELD',
                'MIXED_GAS_RECOVERY_FACTOR',
                'MIXED_OIL_RECOVERY_FACTOR',
                'MIXED_CASE_OIL_IN_PLACE',
                'MIXED_CASE_OIL_RECOVERABLE_RESOURCES',
                'MIXED_CASE_ASSOCIATED_GAS_RECOVERABLE_RESOURCES',
                'MIXED_CASE_GAS_IN_PLACE',
                'MIXED_CASE_GAS_RECOVERABLE_RESOURCES',
                'MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES',
                'MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES'
            )
            AND pd.submission_detail_id = discovery_rec.sd_id
            AND pd.distribution_type != 'NOT_CALCULATED'
        );

        IF has_mixed_case_properties THEN

            -- discovery well details created already in discovery and dry well details migration script
            UPDATE remi.discovery_well_details
            SET hydrocarbons_discovered = 'MIXED_OIL_AND_GAS'
            WHERE submission_detail_id = discovery_rec.sd_id;

        ELSE

            SELECT TRUE
            INTO has_oil_case_params
            WHERE EXISTS (
                SELECT 1
                FROM remi.probability_distributions pd
                WHERE pd.instance IN (
                    'OIL_RECOVERY_FACTOR',
                    'FORMATION_VOLUME_FACTOR_PROSPECT_DISCOVERY',
                    'OIL_HYDROCARBON_SATURATION',
                    'ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD',
                    'OIL_OUTPUTS_OIL_IN_PLACE',
                    'OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES',
                    'OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES',
                    'OIL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES'
                )
                AND pd.submission_detail_id = discovery_rec.sd_id
                AND pd.distribution_type != 'NOT_CALCULATED'
            );

            has_oil_case_params := COALESCE(has_oil_case_params, FALSE);

            SELECT TRUE
            INTO has_gas_case_params
            WHERE EXISTS (
                SELECT 1
                FROM remi.probability_distributions pd
                WHERE pd.instance IN (
                    'GAS_HYDROCARBON_SATURATION',
                    'GAS_EXPANSION_FACTOR',
                    'GAS_RECOVERY_FACTOR',
                    'CONDENSATE_GAS_RATIO_LIFE_OF_FIELD',
                    'GAS_OUTPUTS_GAS_IN_PLACE',
                    'GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES',
                    'GAS_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES',
                    'GAS_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES'
                )
                AND pd.submission_detail_id = discovery_rec.sd_id
                AND pd.distribution_type != 'NOT_CALCULATED'
            );

            has_gas_case_params := COALESCE(has_gas_case_params, FALSE);

            SELECT TRUE
            INTO has_oil_case_fluid_properties
            WHERE EXISTS(
                SELECT 1
                FROM remi.oil_fluid_properties ofp
                WHERE ofp.submission_detail_id = discovery_rec.sd_id
            );

            has_oil_case_fluid_properties := COALESCE(has_oil_case_fluid_properties, FALSE);

            SELECT TRUE
            INTO has_gas_case_fluid_properties
            WHERE EXISTS(
                SELECT 1
                FROM remi.gas_fluid_properties gfp
                WHERE gfp.submission_detail_id = discovery_rec.sd_id
            );

            has_gas_case_fluid_properties := COALESCE(has_gas_case_fluid_properties, FALSE);

            -- if both oil properties and gas properties exists then it is a mixed case
            IF (has_oil_case_params OR has_oil_case_fluid_properties) AND (has_gas_case_params OR has_gas_case_fluid_properties) THEN

                UPDATE remi.discovery_well_details
                SET hydrocarbons_discovered = 'MIXED_OIL_AND_GAS'
                WHERE submission_detail_id = discovery_rec.sd_id;

            -- if any oil properties exist and no gas properties exist then it is an oil only case
            ELSIF (has_oil_case_params OR has_oil_case_fluid_properties) AND NOT(has_gas_case_params) AND NOT(has_gas_case_fluid_properties) THEN

                UPDATE remi.discovery_well_details
                SET hydrocarbons_discovered = 'OIL_ONLY'
                WHERE submission_detail_id = discovery_rec.sd_id;

            -- if any gas properties exist and no oil properties exist then it is an gas only case
            ELSIF (has_gas_case_params OR has_gas_case_fluid_properties) AND NOT(has_oil_case_params) AND NOT(has_oil_case_fluid_properties) THEN

                UPDATE remi.discovery_well_details
                SET hydrocarbons_discovered = 'GAS_ONLY'
                WHERE submission_detail_id = discovery_rec.sd_id;

            END IF;

        END IF;

    END LOOP;

END;

$$;