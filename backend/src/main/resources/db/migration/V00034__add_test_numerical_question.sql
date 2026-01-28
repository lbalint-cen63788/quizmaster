ALTER TABLE question ADD COLUMN slug VARCHAR(255);

CREATE UNIQUE INDEX IF NOT EXISTS question_slug_unique ON question (slug) WHERE slug IS NOT NULL;

INSERT INTO question (
    question,
    answers,
    explanations,
    question_explanation,
    correct_answers,
    workspace_guid,
    easy_mode,
    edit_id,
    slug
)
SELECT
    'How many regions does Czechia have?',
    ARRAY['14', '48']::text[],
    ARRAY[]::text[],
    NULL,
    ARRAY[0]::integer[],
    NULL,
    FALSE,
    gen_random_uuid()::varchar(36),
    'test-numerical-question'
WHERE NOT EXISTS (
    SELECT 1 FROM question WHERE slug = 'test-numerical-question'
);
