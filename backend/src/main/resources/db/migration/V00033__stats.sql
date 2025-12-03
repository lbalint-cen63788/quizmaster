CREATE TABLE stats (
    id SERIAL PRIMARY KEY,
    started_at TIMESTAMP WITH TIME ZONE NULL,
    quiz_id INTEGER NOT NULL REFERENCES quiz(id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX stats_quiz_id_idx ON stats(quiz_id);
