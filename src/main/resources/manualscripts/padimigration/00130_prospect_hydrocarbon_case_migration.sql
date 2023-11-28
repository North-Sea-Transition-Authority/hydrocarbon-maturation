DO $$
DECLARE

    case_rec RECORD;
    has_gas_properties BOOLEAN := FALSE;
    has_oil_properties BOOLEAN := FALSE;

BEGIN

    FOR case_rec IN (
        SELECT prdh.*, sd.id sd_id
        FROM remi.padi_res_data_header prdh
        JOIN remi.submission_details sd ON sd.legacy_res_data_id = prdh.resdat_id
        JOIN remi.submissions s ON s.id = sd.submission_id
        JOIN remi.resources r ON r.id = s.resource_id
        WHERE r.type = 'PROSPECT'
        AND sd.submission_type != 'POST_DRILL_DRY_HOLE'
    )
    LOOP

        SELECT TRUE
        INTO has_gas_properties
        WHERE EXISTS (
            SELECT 1
            FROM remi.probability_distributions pd
            WHERE pd.instance IN (
                'GAS_HYDROCARBON_SATURATION_PROSPECT',
                'GAS_EXPANSION_FACTOR',
                'GAS_RECOVERY_FACTOR',
                'GAS_FORMATION_VOLUME_FACTOR_PROSPECT',
                'CONDENSATE_GAS_RATIO_LIFE_OF_FIELD',
                'GAS_OUTPUTS_GAS_IN_PLACE_PROSPECT',
                'GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_PROSPECT',
                'GAS_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT',
                'GAS_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT'
            )
            AND pd.submission_detail_id = case_rec.sd_id
            AND pd.distribution_type != 'NOT_CALCULATED'
        );

        SELECT TRUE
        INTO has_oil_properties
        WHERE EXISTS (
            SELECT 1
            FROM remi.probability_distributions pd
            WHERE pd.instance IN (
                'OIL_RECOVERY_FACTOR_PROSPECT',
                'OIL_FORMATION_VOLUME_FACTOR_PROSPECT',
                'OIL_HYDROCARBON_SATURATION',
                'ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD',
                'OIL_OUTPUTS_OIL_IN_PLACE_PROSPECT',
                'OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_PROSPECT',
                'OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT',
                'OIL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT'
            )
            AND pd.submission_detail_id = case_rec.sd_id
            AND pd.distribution_type != 'NOT_CALCULATED'
        );

        INSERT INTO remi.hydrocarbon_case_details (
            id,
            submission_detail_id,
            oil_only,
            oil_only_comments,
            gas_only,
            gas_only_comments,
            oil_and_gas,
            oil_and_gas_comments
        ) VALUES (
            gen_random_uuid(),
            case_rec.sd_id,
            CASE
                WHEN has_oil_properties THEN TRUE
            END,
            CASE
                WHEN has_oil_properties THEN case_rec.hc_case_comment
            END,
            CASE
                WHEN has_gas_properties THEN TRUE
            END,
            CASE
                WHEN has_gas_properties THEN case_rec.hc_case_comment
            END,
            CASE
                WHEN LOWER(case_rec.hc_case) = 'mixed oil and gas' THEN TRUE
            END,
            CASE
                WHEN LOWER(case_rec.hc_case) = 'mixed oil and gas' THEN case_rec.hc_case_comment
            END
        ); -- discuss with NSTA what to do with the resources listed as having 'Other' as the hydrocarbon case

        has_gas_properties := FALSE;
        has_oil_properties := FALSE;

    END LOOP;

END;

$$;