UPDATE question
SET answers = ARRAY['14']::text[]
WHERE slug = 'test-numerical-question';
