-- Corrects probability distributions migrated from PAD-I which were recorded with distribution_type = 'CUSTOM' against
-- the discovery fluid contact instances. Those instances offer preset distribution types, so there is no custom
-- behaviour to resolve and validating the section threw:
--   java.lang.IllegalStateException: Cannot create instance of a ProbabilityDistributionTypeBehaviour that has custom
--   behaviour
--
-- Introduced by 00085_discovery_volumetric_parameter_migration.sql, which hard-coded 'CUSTOM' for the depth to water
-- contact block instead of deriving the type from padi_res_data_param.depth_to_water_dist the way every other block in
-- that migration does. This patch applies the same mapping retrospectively: PAD-I codes N, L, B, T, R and C become the
-- matching distribution type, Z means PAD-I held no distribution type, and any other code is kept verbatim in
-- other_distribution_type.

-- 1. How many rows are affected, and for which resources?
SELECT pd.instance,
       count(*)                         affected_rows,
       count(DISTINCT sd.submission_id) affected_submissions
FROM remi.probability_distributions pd
JOIN remi.submission_details sd ON sd.id = pd.submission_detail_id
WHERE pd.distribution_type = 'CUSTOM'
  AND pd.instance IN (
      'DEPTH_TO_OIL_WATER_CONTACT_DISCOVERY',
      'DEPTH_TO_GAS_WATER_CONTACT_DISCOVERY',
      'DEPTH_TO_GAS_OIL_CONTACT_DISCOVERY'
  )
GROUP BY pd.instance;

-- 2. What the affected rows will be set to.
SELECT COALESCE(prdp.depth_to_water_dist, '<null>') padi_distribution_code,
       CASE prdp.depth_to_water_dist
           WHEN 'N' THEN 'NORMAL'
           WHEN 'L' THEN 'LOGNORMAL'
           WHEN 'B' THEN 'BETA'
           WHEN 'T' THEN 'TRIANGULAR'
           WHEN 'R' THEN 'RECTANGULAR'
           WHEN 'C' THEN 'CONSTANT'
           END                                      distribution_type,
       CASE
           WHEN prdp.depth_to_water_dist IS NOT NULL
               AND prdp.depth_to_water_dist NOT IN ('N', 'L', 'B', 'T', 'R', 'C', 'Z')
               THEN prdp.depth_to_water_dist
           END                                      other_distribution_type,
       count(*)                                     affected_rows
FROM remi.probability_distributions pd
JOIN remi.reservoir_submission_details rsd ON rsd.id = pd.reservoir_submission_detail_id
JOIN remi.padi_reservoirs pr ON pr.remi_reservoir_id = rsd.reservoir
JOIN remi.padi_res_data_header prdh ON prdh.resdat_id = pr.legacy_res_data_id
JOIN remi.padi_res_data_param prdp ON prdp.resdat_id = prdh.resdat_id
WHERE pd.distribution_type = 'CUSTOM'
  AND pd.instance IN (
      'DEPTH_TO_OIL_WATER_CONTACT_DISCOVERY',
      'DEPTH_TO_GAS_WATER_CONTACT_DISCOVERY',
      'DEPTH_TO_GAS_OIL_CONTACT_DISCOVERY'
  )
GROUP BY prdp.depth_to_water_dist;

-- 3. Set the distribution type from PAD-I.
--    Because the original migration treated every row as CUSTOM it populated the probability columns and left
--    constant_value empty, so rows which turn out to be CONSTANT also need reshaping into the columns the rest of
--    00085 would have used for them: the value sits in mean_value and belongs in constant_value.
UPDATE remi.probability_distributions pd
SET distribution_type = padi.padi_distribution_type,
    other_distribution_type = padi.padi_other_distribution_type,
    constant_value = CASE WHEN padi.padi_distribution_type = 'CONSTANT' THEN pd.mean_value ELSE pd.constant_value END,
    min_value = CASE WHEN padi.padi_distribution_type = 'CONSTANT' THEN NULL ELSE pd.min_value END,
    p99 = CASE WHEN padi.padi_distribution_type = 'CONSTANT' THEN NULL ELSE pd.p99 END,
    p90 = CASE WHEN padi.padi_distribution_type = 'CONSTANT' THEN NULL ELSE pd.p90 END,
    mode_value = CASE WHEN padi.padi_distribution_type = 'CONSTANT' THEN NULL ELSE pd.mode_value END,
    p50 = CASE WHEN padi.padi_distribution_type = 'CONSTANT' THEN NULL ELSE pd.p50 END,
    mean_value = CASE WHEN padi.padi_distribution_type = 'CONSTANT' THEN NULL ELSE pd.mean_value END,
    p10 = CASE WHEN padi.padi_distribution_type = 'CONSTANT' THEN NULL ELSE pd.p10 END,
    p1 = CASE WHEN padi.padi_distribution_type = 'CONSTANT' THEN NULL ELSE pd.p1 END,
    max_value = CASE WHEN padi.padi_distribution_type = 'CONSTANT' THEN NULL ELSE pd.max_value END,
    most_likely_value = CASE WHEN padi.padi_distribution_type = 'CONSTANT' THEN NULL ELSE pd.most_likely_value END
FROM (
    SELECT pd.id,
           CASE prdp.depth_to_water_dist
               WHEN 'N' THEN 'NORMAL'
               WHEN 'L' THEN 'LOGNORMAL'
               WHEN 'B' THEN 'BETA'
               WHEN 'T' THEN 'TRIANGULAR'
               WHEN 'R' THEN 'RECTANGULAR'
               WHEN 'C' THEN 'CONSTANT'
               END                          padi_distribution_type,
           CASE
               WHEN prdp.depth_to_water_dist IS NOT NULL
                   AND prdp.depth_to_water_dist NOT IN ('N', 'L', 'B', 'T', 'R', 'C', 'Z')
                   THEN prdp.depth_to_water_dist
               END                          padi_other_distribution_type
    FROM remi.probability_distributions pd
    JOIN remi.reservoir_submission_details rsd ON rsd.id = pd.reservoir_submission_detail_id
    JOIN remi.padi_reservoirs pr ON pr.remi_reservoir_id = rsd.reservoir
    JOIN remi.padi_res_data_header prdh ON prdh.resdat_id = pr.legacy_res_data_id
    JOIN remi.padi_res_data_param prdp ON prdp.resdat_id = prdh.resdat_id
    WHERE pd.distribution_type = 'CUSTOM'
      AND pd.instance IN (
          'DEPTH_TO_OIL_WATER_CONTACT_DISCOVERY',
          'DEPTH_TO_GAS_WATER_CONTACT_DISCOVERY',
          'DEPTH_TO_GAS_OIL_CONTACT_DISCOVERY'
      )
) padi
WHERE pd.id = padi.id;

-- 4. Clear anything step 3 could not resolve, so no unusable 'CUSTOM' rows are left behind (PAD-I held no distribution
--    code, or the row has no PAD-I record to derive one from). These behave like any other legacy distribution with no
--    type: the section shows as incomplete and the operator is asked to select a distribution.
UPDATE remi.probability_distributions
SET distribution_type = NULL
WHERE distribution_type = 'CUSTOM'
  AND instance IN (
      'DEPTH_TO_OIL_WATER_CONTACT_DISCOVERY',
      'DEPTH_TO_GAS_WATER_CONTACT_DISCOVERY',
      'DEPTH_TO_GAS_OIL_CONTACT_DISCOVERY'
  );

-- 5. Verify nothing unusable remains.
SELECT count(*) remaining_custom_rows
FROM remi.probability_distributions
WHERE distribution_type = 'CUSTOM'
  AND instance IN (
      'DEPTH_TO_OIL_WATER_CONTACT_DISCOVERY',
      'DEPTH_TO_GAS_WATER_CONTACT_DISCOVERY',
      'DEPTH_TO_GAS_OIL_CONTACT_DISCOVERY'
  );
