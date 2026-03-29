DROP INDEX IF EXISTS idx_question_edit_id;
ALTER TABLE question DROP COLUMN IF EXISTS edit_id;
