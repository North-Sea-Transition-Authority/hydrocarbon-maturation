# Audit revisions and the undeveloped discovery tables

**Audience:** anyone reading ReMI data from outside the development team — analysts, reporting
developers, and data consumers who have been given access to the ReMI database or an extract of it.

**Scope:** what the audit revision tables are, what they do and do not record, how they relate to the
two undeveloped discovery tables, and how to query the combination without drawing the wrong
conclusion. Everything below lives in the `remi` schema.

---

## 1. Two different kinds of "history"

ReMI records history in two distinct ways, and mixing them up is the single most common source of
wrong numbers.

| | **Business history** | **System history (audit)** |
|---|---|---|
| Question it answers | "What did the regulator record as true *as of* a given date?" | "What did the database row look like *after* a given change, and who made it?" |
| Where it lives | Ordinary tables — for undeveloped discoveries, one row per reporting snapshot in `undeveloped_discovery_information`, keyed by `information_date` | Shadow `*_aud` tables plus the `audit_revisions` table |
| Timeline it uses | The date the information refers to (`information_date`) | The wall-clock time the change was saved (`audit_revisions.updated_at`) |
| Typical use | Trend reporting, resource totals by year | Provenance, correction investigation, "who changed this and when" |

A single row in `undeveloped_discovery_information` is itself a point in a business time series. The
audit tables sit *underneath* that and track edits to each of those rows. So a 2C volume can move for
two quite different reasons: a new reporting snapshot was added (business history), or an existing
snapshot was corrected (system history). Only the audit tables can tell you which happened.

---

## 2. The audit revision model

ReMI uses Hibernate Envers. The model is the standard Envers one, with a customised revision entity.

### 2.1 `audit_revisions` — one row per saved transaction

| Column | Type | Meaning |
|---|---|---|
| `rev` | `SERIAL` (integer), primary key | Revision number. Monotonically increasing, so it also orders changes reliably. |
| `updated_at` | `TIMESTAMPTZ` | When the change was committed. |
| `updated_by_user_id` | `VARCHAR(255)` | The Energy Portal web user account (WUA) ID of the signed-in user. Numeric in practice, but stored as text. |
| `updated_by_proxy_user_id` | `VARCHAR(255)` | Populated when the change was made by someone acting on another user's behalf (proxy sign-in). Usually null. |

Important properties:

- **One `rev` per database transaction, not per row and not per field.** Saving a single screen can
  touch several audited tables; all of the resulting audit rows share one `rev`. Conversely, two
  changes made minutes apart are two `rev` values even if they touch the same column.
- **Both user columns are nullable.** They are filled from the authenticated user in the current
  request. Changes made by background or system processes with no signed-in user (for example, a
  change arriving from another Energy Portal service over the message queue) can have no user
  recorded, unless the process explicitly supplies a fallback audit user. Treat `NULL` as "system
  change", not as "data missing".
- **No names or email addresses are stored.** Only the WUA ID. Resolving it to a person requires the
  Energy Portal Accounts Service; it is deliberately not duplicated into ReMI.
- **The revision number is not a version number** of any particular record. Do not present `rev` to
  end users as "version 5 of this discovery".

### 2.2 `*_aud` tables — one row per audited record per revision

Every audited table `x` has a shadow table `x_aud` containing:

- `rev` — foreign key to `audit_revisions`,
- `revtype` — what happened to *this* row in *that* revision,
- the record's primary key, and
- a **full copy of every audited column** as it stood at the end of that revision.

The primary key is `(rev, id)`.

`revtype` values:

| Value | Meaning |
|---|---|
| `0` | ADD — the row was inserted |
| `1` | MOD — the row was updated |
| `2` | DEL — the row was physically deleted |

Two consequences worth stating plainly:

1. **Audit rows are snapshots, not deltas.** A `revtype = 1` row does not tell you which column
   changed. To find that, compare the row against the previous revision of the same `id`.
2. **Deletions retain their data.** ReMI enables Envers' `store_data_at_delete`, so a `revtype = 2`
   row carries the final column values rather than nulls. In practice you will rarely see
   `revtype = 2` for undeveloped discovery data — see section 3.2 on soft deletion.

### 2.3 What is and is not audited

17 tables are audited. The ones a data consumer is most likely to meet:

`resources_aud`, `resource_groups_aud`, `resource_group_links_aud`, `fields_aud`, `reservoirs_aud`,
`regulator_managed_information_aud`, `regulator_reporting_information_aud`, `projects_aud`,
`project_stage_history_aud`, `project_capex_aud`, `project_submission_estimates_aud`,
`actual_project_dates_aud`, `environmental_activity_aud`, `teams_aud`, `team_roles_aud`,
**`undeveloped_discoveries_aud`**, **`undeveloped_discovery_information_aud`**.

Not audited, and the usual traps:

- **The submission pipeline is not Envers-audited.** Submissions and submission details carry their
  own version numbers and statuses (`version_number`, `status`) — that *is* their history mechanism.
  Do not look for `submission_details_aud`; it does not exist.
- **Related entities marked as not-audited store only the foreign key.** In
  `undeveloped_discoveries_aud` you get `resource_id`, not a snapshot of the resource itself. To see
  how that resource looked at the time you must join to `resources_aud` and use its own revision
  history.
- **Audit history starts when a table was first audited**, not when ReMI went live. Rows that predate
  the relevant migration have no `revtype = 0` row.

---

## 3. The undeveloped discovery tables

Undeveloped discovery data is regulator-maintained: it is entered and maintained in ReMI by NSTA
staff holding the Reserves and Resources Advisor role, not by operators through a submission. That is
why it is Envers-audited rather than versioned through the submission pipeline.

### 3.1 `undeveloped_discoveries` — the tracking flag

| Column | Type | Meaning |
|---|---|---|
| `id` | `UUID` PK | Surrogate key |
| `resource_id` | `UUID` → `resources.id` | The discovery resource this record belongs to |
| `is_undeveloped_discovery` | `BOOLEAN` | Whether this resource is currently tracked as an undeveloped discovery |

There is at most one row per resource. The row is created the first time the flag is set, so
**absence of a row means "never assessed", while `is_undeveloped_discovery = false` means "assessed
and excluded"** — a distinction that matters when building denominators.

`undeveloped_discoveries_aud` therefore gives you the history of inclusion and exclusion: when a
discovery entered the tracked population, and when it left it.

### 3.2 `undeveloped_discovery_information` — the reported snapshots

One row per *information date* per tracked discovery. This is the table that carries the numbers.

**Identity and lifecycle**

| Column | Meaning |
|---|---|
| `id` | `UUID` PK |
| `undeveloped_discovery_id` | → `undeveloped_discoveries.id` |
| `information_date` | The date the information relates to. This is the business timeline; ordering and "latest" are determined by this column, not by `id` or by revision order. |
| `status` | `EXTANT` or `DELETED` |

**`status` is a soft delete.** Removing a snapshot through the UI sets `status = 'DELETED'` and leaves
the row in place. Consequences for anyone querying directly:

- **Every query must filter `status = 'EXTANT'`.** Omitting it silently re-includes withdrawn
  snapshots and inflates totals.
- A deletion appears in the audit tables as `revtype = 1` (MOD) with `status` changing to `DELETED` —
  *not* as `revtype = 2`.

**Values captured from the user**

| Column | Unit | Notes |
|---|---|---|
| `oil_in_place_p50` | mmbbl | Oil initially in place, P50 |
| `oil_recoverable_one_c` / `_two_c` / `_three_c` | mmbbl | Oil recoverable resources, 1C/2C/3C |
| `gas_in_place_p50` | bcf | Gas initially in place, P50 |
| `gas_recoverable_one_c` / `_two_c` / `_three_c` | bcf | Gas recoverable resources, 1C/2C/3C |
| `difference_reason` | text | Free text explaining a difference in the volumetrics |
| `provided_discovery_complexity` | boolean | Whether complexity inputs were supplied |
| `viscosity_range`, `vertical_heterogeneity_range`, `areal_density_range`, `structural_complexity_range` | enum text | Banded complexity inputs, stored as enum names |

All volumetric columns are nullable — a snapshot may report oil only, gas only, or neither.

**Values derived by the application at save time**

| Column | Unit | Derivation |
|---|---|---|
| `oil_equivalent_recoverable_one_c` / `_two_c` / `_three_c` | mmboe | `oil_recoverable_xC + (gas_recoverable_xC / 5.8)`, nulls treated as zero, rounded to 2 dp (half-even) |
| `percentage_gas` | % | `(1 − oil_recoverable_two_c / oil_equivalent_recoverable_two_c) × 100`, 2 dp. Special cases: `0` when 2C oil equivalent is zero (nothing reported); `100` when 2C oil is zero but gas is not |
| `calculated_complexity` | score 6–30 | `(2 × viscosity) + vertical_heterogeneity + areal_density + (2 × structural_complexity)`, where each band scores 1–5. `NULL` if any of the four inputs is missing |

Three things to know about the derived columns:

1. **They are stored, not computed on read.** They reflect the formula in force when the row was last
   saved. If a formula ever changes, older rows keep their original values until re-saved.
2. **Because nulls are treated as zero in the mmboe calculation, an mmboe value of `0.00` does not
   mean "no resources reported"** — it can equally mean nothing was entered. Check the underlying oil
   and gas columns before interpreting zeros.
3. **Areal density scores inversely.** Lower areal density bands score *higher* (5), the reverse of
   the other three inputs. Do not assume a monotonic mapping from band order to score.

---

## 4. How the two fit together

```
  resources
     |  (resource_id — relation itself is not audited)
     v
  undeveloped_discoveries  --------------->  undeveloped_discoveries_aud
     |   id, is_undeveloped_discovery              (rev, revtype, + snapshot)
     |                                                    |
     |  (undeveloped_discovery_id)                        |
     v                                                    v
  undeveloped_discovery_information  -->  undeveloped_discovery_information_aud
         id, information_date, status,           (rev, revtype, + snapshot)
         volumetrics, derived values                      |
                                                          v
                                                   audit_revisions
                                              rev, updated_at, updated_by_user_id
```

Read it as three layers:

1. **Population** — `undeveloped_discoveries` says *which* resources count as undeveloped discoveries
   right now; its audit table says when that changed.
2. **Content** — `undeveloped_discovery_information` says *what was reported and for when*; each row
   is a business-time snapshot.
3. **Provenance** — the `*_aud` tables plus `audit_revisions` say *who touched either of the above,
   and when in real time*.

Layer 2 already gives you a time series. Reach for layer 3 only when you need provenance, corrections
or deletions — not to build the series itself.

---

## 5. Worked example: one discovery, five changes

This is the shape the data takes for a single discovery that is tracked, has information added, then
edited, then deleted, and then has new information added. Identifiers are abbreviated for
readability; in the database they are full UUIDs. Only the columns needed to make the point are
shown.

### 5.1 What happened

| `rev` | `updated_at` | `updated_by_user_id` | Event |
|---|---|---|---|
| 4821 | 2026-01-12 09:41 | 41277 | Resource flagged as a tracked undeveloped discovery |
| 4903 | 2026-01-19 14:02 | 41277 | First information snapshot added, for information date 2025-12-31 |
| 5140 | 2026-02-03 11:27 | 63104 | That snapshot corrected — 2C gas recoverable revised from 58.00 to 46.40 bcf |
| 5388 | 2026-03-11 16:08 | 41277 | That snapshot withdrawn (deleted through the UI) |
| 5402 | 2026-03-12 10:15 | 41277 | New snapshot added, for information date 2026-02-28 |

Note the `rev` values are not consecutive. The counter is global across ReMI, so unrelated changes
made by other users to other resources sit in the gaps.

### 5.2 What the live tables hold afterwards

`undeveloped_discoveries` — one row, created at rev 4821 and never changed since:

| `id` | `resource_id` | `is_undeveloped_discovery` |
|---|---|---|
| `6d2b…4c2` | `a41f…c07` | `true` |

`undeveloped_discovery_information` — **two** rows, because the deletion was a soft delete:

| `id` | `information_date` | `status` | `oil_recoverable_two_c` | `gas_recoverable_two_c` | `oil_equivalent_recoverable_two_c` | `percentage_gas` | `calculated_complexity` |
|---|---|---|---|---|---|---|---|
| `9e77…8b1` | 2025-12-31 | `DELETED` | 12.40 | 46.40 | 20.40 | 39.22 | 13 |
| `b35c…e55` | 2026-02-28 | `EXTANT` | 14.00 | 52.20 | 23.00 | 39.13 | 15 |

The live tables show only the *end state*. The correction at rev 5140 has left no trace here — the
row simply holds its corrected values. The withdrawn snapshot is still physically present and will
be returned by any query that forgets `status = 'EXTANT'`.

### 5.3 The audit trail

`undeveloped_discoveries_aud` — a single row, since the tracking flag was set once and never changed:

| `rev` | `revtype` | `id` | `resource_id` | `is_undeveloped_discovery` |
|---|---|---|---|---|
| 4821 | 0 | `6d2b…4c2` | `a41f…c07` | `true` |

`undeveloped_discovery_information_aud` — four rows across the two information records:

| `rev` | `revtype` | `id` | `information_date` | `status` | `oil_rec_2c` | `gas_rec_2c` | `mmboe_2c` | `pct_gas` | `complexity` |
|---|---|---|---|---|---|---|---|---|---|
| 4903 | 0 | `9e77…8b1` | 2025-12-31 | `EXTANT` | 12.40 | 58.00 | 22.40 | 44.64 | 13 |
| 5140 | 1 | `9e77…8b1` | 2025-12-31 | `EXTANT` | 12.40 | 46.40 | 20.40 | 39.22 | 13 |
| 5388 | 1 | `9e77…8b1` | 2025-12-31 | `DELETED` | 12.40 | 46.40 | 20.40 | 39.22 | 13 |
| 5402 | 0 | `b35c…e55` | 2026-02-28 | `EXTANT` | 14.00 | 52.20 | 23.00 | 39.13 | 15 |

Seven things this trace demonstrates:

1. **There is no `revtype = 2` anywhere.** The deletion is rev 5388: `revtype = 1` with `status`
   flipping to `DELETED`. Searching the audit tables for physical deletes would miss it entirely.
2. **Every audit row repeats every audited column.** Rev 5140 is a full snapshot, not a record of
   "gas 2C changed". To see *what* the correction did you diff rev 5140 against rev 4903.
3. **The derived columns moved on their own.** Correcting 2C gas from 58.00 to 46.40 recalculated
   `mmboe_2c` (22.40 → 20.40) and `percentage_gas` (44.64 → 39.22). Nobody typed those values, and
   they were recalculated at save time — see section 3.2.
4. **`calculated_complexity` did not move**, because the correction touched no complexity input. The
   new snapshot scores 15 rather than 13 because its structural complexity band was reported one
   step higher.
5. **Rev 5388 changed exactly one column** and still copied the whole row. Audit row count tells you
   nothing about the size of a change.
6. **The new snapshot is a new record**, not an edit. It gets a new `id` and `revtype = 0`, and the
   withdrawn record coexists with it indefinitely.
7. **The correction was made by a different user** (63104) from the one who entered and later
   withdrew the data (41277). Provenance is per revision, not per record.

### 5.4 How the values move over time

Two different traces, because there are two timelines (section 1).

**Trace 1 — system time.** Every audited column of snapshot `9e77…8b1`, at each of its three
revisions. Changed cells are bold.

| Column | rev 4903 (added) | rev 5140 (corrected) | rev 5388 (withdrawn) |
|---|---|---|---|
| `status` | `EXTANT` | `EXTANT` | **`DELETED`** |
| `information_date` | 2025-12-31 | 2025-12-31 | 2025-12-31 |
| `oil_in_place_p50` | 31.00 | 31.00 | 31.00 |
| `oil_recoverable_one_c` | 8.00 | 8.00 | 8.00 |
| `oil_recoverable_two_c` | 12.40 | 12.40 | 12.40 |
| `oil_recoverable_three_c` | 18.00 | 18.00 | 18.00 |
| `gas_in_place_p50` | 140.00 | 140.00 | 140.00 |
| `gas_recoverable_one_c` | 40.60 | 40.60 | 40.60 |
| `gas_recoverable_two_c` | 58.00 | **46.40** | 46.40 |
| `gas_recoverable_three_c` | 81.20 | 81.20 | 81.20 |
| `oil_equivalent_recoverable_one_c` | 15.00 | 15.00 | 15.00 |
| `oil_equivalent_recoverable_two_c` | 22.40 | **20.40** | 20.40 |
| `oil_equivalent_recoverable_three_c` | 32.00 | 32.00 | 32.00 |
| `percentage_gas` | 44.64 | **39.22** | 39.22 |
| `calculated_complexity` | 13 | 13 | 13 |

One user-entered cell changed across the whole life of this snapshot. It dragged two derived cells
with it, because 2C gas feeds both the 2C oil equivalent and the percentage gas; the 1C and 3C
figures were untouched, and so were their derived values. The withdrawal moved `status` and nothing
else. Section 6 has a query that produces this trace as a change log, listing only the columns that
actually moved at each revision.

**Trace 2 — business time.** The reported position by information date, which is what a trend report
is actually built from:

| Column | 2025-12-31 (withdrawn) | 2026-02-28 (current) |
|---|---|---|
| `oil_recoverable_two_c` | 12.40 | 14.00 |
| `gas_recoverable_two_c` | 46.40 | 52.20 |
| `oil_equivalent_recoverable_two_c` | 20.40 | 23.00 |
| `percentage_gas` | 39.22 | 39.13 |
| `calculated_complexity` | 13 | 15 |

As things stand the series has exactly **one** usable point, not two: the 2025-12-31 column is
withdrawn data and a correct query filters it out. There is no "movement from 20.40 to 23.00" to
report — that would be comparing a withdrawn figure against a live one.

**Trace 3 — what an extract would have said, depending on when it ran.** Same discovery, same
queries, run on different days:

| Extract run | Snapshots returned | 2C mmboe |
|---|---|---|
| 2026-01-15 | none — flagged as tracked, but no information yet | — |
| 2026-01-20 | 2025-12-31 | 22.40 |
| 2026-02-15 | 2025-12-31 (corrected) | 20.40 |
| 2026-03-12 09:00 | none — withdrawn, replacement not yet entered | — |
| 2026-03-15 | 2026-02-28 | 23.00 |

The row that catches people out is the fourth: for roughly eighteen hours this discovery was tracked
and reported nothing at all. A monthly extract that happened to land in that window would show the
discovery with no volumetrics, which is a true statement about the system and a misleading one about
the resource. If an extract feeds a published figure, record the extraction timestamp alongside it —
it is the only way to explain a number that later becomes irreproducible.

### 5.5 What the queries in section 6 return for this discovery

| Query | Result |
|---|---|
| Latest reported snapshot | One row: `b35c…e55`, 2026-02-28, 23.00 mmboe (2C) |
| The same query with the `status = 'EXTANT'` filter left out | Still one row, and still the correct one, because 2026-02-28 is the later information date regardless. A query written without the filter looks fine when tested this way |
| **Sum of 2C mmboe** with the `status = 'EXTANT'` filter left out | **43.40 mmboe for one discovery** — the withdrawn snapshot is added to the live one. The same omission that was harmless above is now a wrong number, with nothing to signal it |
| As-at reconstruction at 2026-01-25 | `9e77…8b1` as at rev 4903: 22.40 mmboe — the pre-correction figure |
| As-at reconstruction at 2026-02-15 | `9e77…8b1` as at rev 5140: 20.40 mmboe, still `EXTANT` — what the system held before the withdrawal, and what a report published that week would legitimately have said |
| As-at reconstruction today | `b35c…e55` only: 23.00 mmboe |

The three different "correct" answers for the same discovery — 22.40, 20.40 and 23.00 mmboe — are the
reason to be explicit about which timeline a published figure is built on.

---

## 6. Query recipes

All examples assume the `remi` schema.

**Current tracked population with its latest reported snapshot**

```sql
WITH ranked_information AS (
    SELECT udi.*,
           ROW_NUMBER() OVER (
               PARTITION BY udi.undeveloped_discovery_id
               ORDER BY udi.information_date DESC, udi.id DESC
           ) AS rn
    FROM remi.undeveloped_discovery_information udi
    WHERE udi.status = 'EXTANT'
)
SELECT ud.resource_id,
       i.information_date,
       i.oil_equivalent_recoverable_two_c AS mmboe_2c,
       i.percentage_gas,
       i.calculated_complexity
FROM remi.undeveloped_discoveries ud
JOIN ranked_information i
  ON i.undeveloped_discovery_id = ud.id
 AND i.rn = 1
WHERE ud.is_undeveloped_discovery IS TRUE;
```

The `id` in the `ORDER BY` is only a tie-break, so that two snapshots sharing one `information_date`
resolve the same way every time you run the query. It carries no meaning of its own — if you find
ties in practice, that is worth querying with the data owner rather than silently picking one.

**Full reported time series for one discovery** (business history — no audit tables needed)

```sql
SELECT information_date,
       oil_recoverable_two_c,
       gas_recoverable_two_c,
       oil_equivalent_recoverable_two_c
FROM remi.undeveloped_discovery_information
WHERE undeveloped_discovery_id = :discovery_id
  AND status = 'EXTANT'
ORDER BY information_date;
```

**Who changed a snapshot, and when** (system history)

```sql
SELECT a.rev,
       ar.updated_at,
       ar.updated_by_user_id,
       ar.updated_by_proxy_user_id,
       a.revtype,
       a.status,
       a.information_date,
       a.oil_equivalent_recoverable_two_c
FROM remi.undeveloped_discovery_information_aud a
JOIN remi.audit_revisions ar ON ar.rev = a.rev
WHERE a.id = :information_id
ORDER BY a.rev;
```

Read consecutive rows as before/after pairs: `revtype = 0` is the original entry, each `revtype = 1`
is an edit, and a `revtype = 1` row where `status` becomes `DELETED` is a withdrawal.

**Change log for one snapshot — what actually moved at each revision.** This is the query behind
trace 1 in section 5.4. `LAG` pulls the previous revision's value alongside the current one, so you
can compare without self-joining.

```sql
WITH history AS (
    SELECT a.rev,
           ar.updated_at,
           ar.updated_by_user_id,
           a.status,
           a.oil_recoverable_two_c,
           a.gas_recoverable_two_c,
           a.oil_equivalent_recoverable_two_c,
           LAG(a.status)                            OVER (ORDER BY a.rev) AS prev_status,
           LAG(a.oil_recoverable_two_c)             OVER (ORDER BY a.rev) AS prev_oil_two_c,
           LAG(a.gas_recoverable_two_c)             OVER (ORDER BY a.rev) AS prev_gas_two_c,
           LAG(a.oil_equivalent_recoverable_two_c)  OVER (ORDER BY a.rev) AS prev_mmboe_two_c
    FROM remi.undeveloped_discovery_information_aud a
    JOIN remi.audit_revisions ar ON ar.rev = a.rev
    WHERE a.id = :information_id
)
SELECT rev,
       updated_at,
       updated_by_user_id,
       prev_status       AS status_from,       status       AS status_to,
       prev_oil_two_c    AS oil_2c_from,       oil_recoverable_two_c            AS oil_2c_to,
       prev_gas_two_c    AS gas_2c_from,       gas_recoverable_two_c            AS gas_2c_to,
       prev_mmboe_two_c  AS mmboe_2c_from,     oil_equivalent_recoverable_two_c AS mmboe_2c_to
FROM history
WHERE prev_status IS NULL                                        -- the original entry
   OR status <> prev_status
   OR COALESCE(oil_recoverable_two_c, -1)            <> COALESCE(prev_oil_two_c, -1)
   OR COALESCE(gas_recoverable_two_c, -1)            <> COALESCE(prev_gas_two_c, -1)
   OR COALESCE(oil_equivalent_recoverable_two_c, -1) <> COALESCE(prev_mmboe_two_c, -1)
ORDER BY rev;
```

Extend the pattern to whichever columns you care about — the 1C and 3C figures, the in-place volumes,
the complexity bands. The `COALESCE(…, -1)` is there because these columns are nullable and `NULL <>
NULL` is unknown, not true, so a value appearing or disappearing would otherwise be missed. `-1` is
safe as a sentinel because volumetrics are never negative. (Postgres offers `IS DISTINCT FROM` for
this, which reads better but does not port to older SQL Server versions.)

**State of the data as at a point in real time** — audit reconstruction. Use this only when you
genuinely need "what did the system hold last March", for example to reproduce a published figure.

```sql
WITH cutoff AS (
    SELECT MAX(rev) AS rev
    FROM remi.audit_revisions
    WHERE updated_at <= :as_at_timestamp
),
ranked_revisions AS (
    SELECT a.*,
           ROW_NUMBER() OVER (PARTITION BY a.id ORDER BY a.rev DESC) AS rn
    FROM remi.undeveloped_discovery_information_aud a
    WHERE a.rev <= (SELECT rev FROM cutoff)
)
SELECT *
FROM ranked_revisions
WHERE rn = 1                -- the last revision of each row at or before the cutoff
  AND revtype <> 2          -- exclude rows physically deleted by then
  AND status = 'EXTANT';    -- exclude rows soft-deleted by then
```

`rev` is safe to rank on here because it is globally sequential, so the highest `rev` at or below the
cutoff is always the most recent change to that row. No tie-break is needed — `(rev, id)` is the
primary key of the audit table.

**Discoveries added to or removed from the tracked population in a period**

```sql
SELECT a.id,
       a.resource_id,
       a.is_undeveloped_discovery,
       ar.updated_at,
       ar.updated_by_user_id
FROM remi.undeveloped_discoveries_aud a
JOIN remi.audit_revisions ar ON ar.rev = a.rev
WHERE ar.updated_at >= :from AND ar.updated_at < :to
ORDER BY ar.updated_at;
```

---

## 7. Checklist before you publish a number

- [ ] Filtered `undeveloped_discovery_information.status = 'EXTANT'`.
- [ ] Filtered `undeveloped_discoveries.is_undeveloped_discovery IS TRUE` where the tracked
      population is what you mean, and decided how to treat resources with no row at all.
- [ ] Used `information_date` — not `rev`, `updated_at` or insertion order — to pick "latest" or to
      bucket by year.
- [ ] Checked whether `0.00` mmboe values are genuine zeros or unreported inputs.
- [ ] Not summed across snapshots of the same discovery (there is one row per discovery per
      information date — taking the latest per discovery is almost always what you want).
- [ ] Treated `NULL` audit user IDs as system-originated changes rather than data quality problems.
- [ ] Remembered that `rev` numbers are global across ReMI, so consecutive revisions of one discovery
      are almost never consecutive integers.

---

## 8. Glossary

| Term | Meaning |
|---|---|
| **Revision (`rev`)** | One committed transaction across the whole application, numbered sequentially |
| **`revtype`** | 0 = inserted, 1 = updated, 2 = physically deleted |
| **Envers** | The Hibernate module that writes the `*_aud` tables automatically |
| **Soft delete** | Marking a row `DELETED` rather than removing it; used for discovery information |
| **Information date** | The date reported information relates to — the business timeline |
| **WUA ID** | Energy Portal web user account identifier; how ReMI records "who" |
| **1C / 2C / 3C** | Low / best / high estimates of contingent recoverable resources |
| **mmbbl / bcf / mmboe** | Million barrels (oil) / billion cubic feet (gas) / million barrels of oil equivalent |
