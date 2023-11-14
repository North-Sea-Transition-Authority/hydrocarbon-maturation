DO $$
DECLARE

    single_reservoir_rec RECORD;
    oil_in_place_rec RECORD;
    oil_recoverable_resources_rec RECORD;
    associated_gas_rec RECORD;
    gas_in_place_rec RECORD;
    gas_recoverable_resources_rec RECORD;
    oil_equivalent_rec RECORD;
    associated_condensate_rec RECORD;

BEGIN

    FOR single_reservoir_rec IN (
        SELECT
            sd.id sd_id,
            rsd.id rsd_id,
            pd.id pd_id,
            gco.has_associated_condensate_recoverable_resources gas_associated_condensate,
            mco.has_associated_condensate_recoverable_resources combined_associated_condensate
        FROM remi.resources r
        JOIN remi.submissions s ON s.resource_id = r.id
        JOIN remi.submission_details sd ON sd.submission_id = s.id
        JOIN remi.reservoir_submission_details rsd ON rsd.submission_detail_id = sd.id
        JOIN remi.probability_distributions pd ON pd.reservoir_submission_detail_id = rsd.id
        LEFT JOIN remi.gas_case_outputs gco ON gco.reservoir_submission_detail_id = rsd.id
        LEFT JOIN remi.mixed_case_outputs mco ON mco.reservoir_submission_detail_id = rsd.id
        WHERE (
            SELECT COUNT(*) FROM remi.reservoir_submission_details rsd
            WHERE rsd.submission_detail_id = sd.id
        ) = 1
        AND r.type = 'DISCOVERY'
    )
    LOOP

        IF single_reservoir_rec.gas_associated_condensate IS NOT NULL THEN

            INSERT INTO remi.gas_case_volumetric_totals (
                id,
                submission_detail_id,
                has_associated_condensate_recoverable_resources
            ) VALUES (
                gen_random_uuid(),
                single_reservoir_rec.sd_id,
                single_reservoir_rec.gas_associated_condensate
            );

        END IF;

        IF single_reservoir_rec.combined_associated_condensate IS NOT NULL THEN

            INSERT INTO remi.combined_case_volumetric_totals (
                id,
                submission_detail_id,
                has_associated_condensate_recoverable_resources
            ) VALUES (
                gen_random_uuid(),
                single_reservoir_rec.sd_id,
                single_reservoir_rec.combined_associated_condensate
            );

        END IF;

        FOR oil_in_place_rec IN (
            SELECT *
            FROM remi.probability_distributions pd
            WHERE pd.id = single_reservoir_rec.pd_id
            AND pd.instance = 'OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY'
        )
        LOOP
            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
                instance,
                min_value,
                p99,
                p90,
                mode_value,
                p50,
                mean_value,
                p10,
                p1,
                max_value,
                constant_value,
                most_likely_value,
                comments,
                three_c,
                two_c,
                one_c,
                other_distribution_type,
                reservoir_submission_detail_id
            ) VALUES (
                gen_random_uuid(),
                single_reservoir_rec.sd_id,
                oil_in_place_rec.distribution_type,
                'TOTAL_OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY',
                oil_in_place_rec.min_value,
                oil_in_place_rec.p99,
                oil_in_place_rec.p90,
                oil_in_place_rec.mode_value,
                oil_in_place_rec.p50,
                oil_in_place_rec.mean_value,
                oil_in_place_rec.p10,
                oil_in_place_rec.p1,
                oil_in_place_rec.max_value,
                oil_in_place_rec.constant_value,
                oil_in_place_rec.most_likely_value,
                oil_in_place_rec.comments,
                oil_in_place_rec.three_c,
                oil_in_place_rec.two_c,
                oil_in_place_rec.one_c,
                oil_in_place_rec.other_distribution_type,
                NULL
            );
        END LOOP;

        FOR oil_recoverable_resources_rec IN (
            SELECT *
            FROM remi.probability_distributions pd
            WHERE pd.id = single_reservoir_rec.pd_id
            AND pd.instance = 'OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY'
        )
        LOOP
            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
                instance,
                min_value,
                p99,
                p90,
                mode_value,
                p50,
                mean_value,
                p10,
                p1,
                max_value,
                constant_value,
                most_likely_value,
                comments,
                three_c,
                two_c,
                one_c,
                other_distribution_type,
                reservoir_submission_detail_id
            ) VALUES (
                gen_random_uuid(),
                single_reservoir_rec.sd_id,
                oil_recoverable_resources_rec.distribution_type,
                'TOTAL_OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY',
                oil_recoverable_resources_rec.min_value,
                oil_recoverable_resources_rec.p99,
                oil_recoverable_resources_rec.p90,
                oil_recoverable_resources_rec.mode_value,
                oil_recoverable_resources_rec.p50,
                oil_recoverable_resources_rec.mean_value,
                oil_recoverable_resources_rec.p10,
                oil_recoverable_resources_rec.p1,
                oil_recoverable_resources_rec.max_value,
                oil_recoverable_resources_rec.constant_value,
                oil_recoverable_resources_rec.most_likely_value,
                oil_recoverable_resources_rec.comments,
                oil_recoverable_resources_rec.three_c,
                oil_recoverable_resources_rec.two_c,
                oil_recoverable_resources_rec.one_c,
                oil_recoverable_resources_rec.other_distribution_type,
                NULL
            );
        END LOOP;

        FOR associated_gas_rec IN (
            SELECT *
            FROM remi.probability_distributions pd
            WHERE pd.id = single_reservoir_rec.pd_id
                    AND pd.instance = 'OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY'
        )
        LOOP
            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
                instance,
                min_value,
                p99,
                p90,
                mode_value,
                p50,
                mean_value,
                p10,
                p1,
                max_value,
                constant_value,
                most_likely_value,
                comments,
                three_c,
                two_c,
                one_c,
                other_distribution_type,
                reservoir_submission_detail_id
            ) VALUES (
                gen_random_uuid(),
                single_reservoir_rec.sd_id,
                associated_gas_rec.distribution_type,
                'TOTAL_OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY',
                associated_gas_rec.min_value,
                associated_gas_rec.p99,
                associated_gas_rec.p90,
                associated_gas_rec.mode_value,
                associated_gas_rec.p50,
                associated_gas_rec.mean_value,
                associated_gas_rec.p10,
                associated_gas_rec.p1,
                associated_gas_rec.max_value,
                associated_gas_rec.constant_value,
                associated_gas_rec.most_likely_value,
                associated_gas_rec.comments,
                associated_gas_rec.three_c,
                associated_gas_rec.two_c,
                associated_gas_rec.one_c,
                associated_gas_rec.other_distribution_type,
                NULL
            );
        END LOOP;

        FOR gas_in_place_rec IN (
            SELECT *
            FROM remi.probability_distributions pd
            WHERE pd.id = single_reservoir_rec.pd_id
            AND pd.instance = 'GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY'
        )
        LOOP
            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
                instance,
                min_value,
                p99,
                p90,
                mode_value,
                p50,
                mean_value,
                p10,
                p1,
                max_value,
                constant_value,
                most_likely_value,
                comments,
                three_c,
                two_c,
                one_c,
                other_distribution_type,
                reservoir_submission_detail_id
            ) VALUES (
                gen_random_uuid(),
                single_reservoir_rec.sd_id,
                gas_in_place_rec.distribution_type,
                'TOTAL_GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY',
                gas_in_place_rec.min_value,
                gas_in_place_rec.p99,
                gas_in_place_rec.p90,
                gas_in_place_rec.mode_value,
                gas_in_place_rec.p50,
                gas_in_place_rec.mean_value,
                gas_in_place_rec.p10,
                gas_in_place_rec.p1,
                gas_in_place_rec.max_value,
                gas_in_place_rec.constant_value,
                gas_in_place_rec.most_likely_value,
                gas_in_place_rec.comments,
                gas_in_place_rec.three_c,
                gas_in_place_rec.two_c,
                gas_in_place_rec.one_c,
                gas_in_place_rec.other_distribution_type,
                NULL
            );
        END LOOP;

        FOR gas_recoverable_resources_rec IN (
            SELECT *
            FROM remi.probability_distributions pd
            WHERE pd.id = single_reservoir_rec.pd_id
            AND pd.instance = 'GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY'
        )
        LOOP
            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
                instance,
                min_value,
                p99,
                p90,
                mode_value,
                p50,
                mean_value,
                p10,
                p1,
                max_value,
                constant_value,
                most_likely_value,
                comments,
                three_c,
                two_c,
                one_c,
                other_distribution_type,
                reservoir_submission_detail_id
            ) VALUES (
                gen_random_uuid(),
                single_reservoir_rec.sd_id,
                gas_recoverable_resources_rec.distribution_type,
                'TOTAL_GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY',
                gas_recoverable_resources_rec.min_value,
                gas_recoverable_resources_rec.p99,
                gas_recoverable_resources_rec.p90,
                gas_recoverable_resources_rec.mode_value,
                gas_recoverable_resources_rec.p50,
                gas_recoverable_resources_rec.mean_value,
                gas_recoverable_resources_rec.p10,
                gas_recoverable_resources_rec.p1,
                gas_recoverable_resources_rec.max_value,
                gas_recoverable_resources_rec.constant_value,
                gas_recoverable_resources_rec.most_likely_value,
                gas_recoverable_resources_rec.comments,
                gas_recoverable_resources_rec.three_c,
                gas_recoverable_resources_rec.two_c,
                gas_recoverable_resources_rec.one_c,
                gas_recoverable_resources_rec.other_distribution_type,
                NULL
            );
        END LOOP;

        FOR oil_equivalent_rec IN (
            SELECT *
            FROM remi.probability_distributions pd
            WHERE pd.id = single_reservoir_rec.pd_id
            AND pd.instance = 'OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY'
        )
        LOOP
            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
                instance,
                min_value,
                p99,
                p90,
                mode_value,
                p50,
                mean_value,
                p10,
                p1,
                max_value,
                constant_value,
                most_likely_value,
                comments,
                three_c,
                two_c,
                one_c,
                other_distribution_type,
                reservoir_submission_detail_id
            ) VALUES (
                gen_random_uuid(),
                single_reservoir_rec.sd_id,
                oil_equivalent_rec.distribution_type,
                'TOTAL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY',
                oil_equivalent_rec.min_value,
                oil_equivalent_rec.p99,
                oil_equivalent_rec.p90,
                oil_equivalent_rec.mode_value,
                oil_equivalent_rec.p50,
                oil_equivalent_rec.mean_value,
                oil_equivalent_rec.p10,
                oil_equivalent_rec.p1,
                oil_equivalent_rec.max_value,
                oil_equivalent_rec.constant_value,
                oil_equivalent_rec.most_likely_value,
                oil_equivalent_rec.comments,
                oil_equivalent_rec.three_c,
                oil_equivalent_rec.two_c,
                oil_equivalent_rec.one_c,
                oil_equivalent_rec.other_distribution_type,
                NULL
            );
        END LOOP;

        FOR associated_condensate_rec IN (
            SELECT *
            FROM remi.probability_distributions pd
            WHERE pd.id = single_reservoir_rec.pd_id
            AND pd.instance = 'OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY'
        )
        LOOP
            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
                instance,
                min_value,
                p99,
                p90,
                mode_value,
                p50,
                mean_value,
                p10,
                p1,
                max_value,
                constant_value,
                most_likely_value,
                comments,
                three_c,
                two_c,
                one_c,
                other_distribution_type,
                reservoir_submission_detail_id
            ) VALUES (
                gen_random_uuid(),
                single_reservoir_rec.sd_id,
                associated_condensate_rec.distribution_type,
                'TOTAL_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY',
                associated_condensate_rec.min_value,
                associated_condensate_rec.p99,
                associated_condensate_rec.p90,
                associated_condensate_rec.mode_value,
                associated_condensate_rec.p50,
                associated_condensate_rec.mean_value,
                associated_condensate_rec.p10,
                associated_condensate_rec.p1,
                associated_condensate_rec.max_value,
                associated_condensate_rec.constant_value,
                associated_condensate_rec.most_likely_value,
                associated_condensate_rec.comments,
                associated_condensate_rec.three_c,
                associated_condensate_rec.two_c,
                associated_condensate_rec.one_c,
                associated_condensate_rec.other_distribution_type,
                NULL
            );
        END LOOP;

    END LOOP;

END;
$$;