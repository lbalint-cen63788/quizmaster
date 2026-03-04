# Controller Refactoring Notes

Issues found during backend review. Workspace is done. Question and Quiz remain.

## QuestionController

### DTOs
- `GET /api/question/{id}` and `GET /api/question/{editId}/edit` return raw `Question` JPA entity — need a `QuestionResponse` record.
- `QuestionRequest` exists and is clean. Just needs `@Valid` + validation annotations added.
- `QuestionWriteResponse` is fine (used for both create and update).

### Bugs
- **PATCH update loses `workspaceGuid`**: `updateQuestion()` creates a new entity from the request, copies only `id` and `editId` from the existing entity. Any field not in the request body (like `workspaceGuid`) gets nulled. Fix: carry over all existing fields, then overlay the request fields.

### Access Control Inconsistency
- Edit/update uses `editId` (UUID) for security-through-obscurity.
- Delete uses numeric `id` — anyone who can guess a sequential ID can delete. Consider switching delete to `editId` too.

### Annotations
- `@Autowired` on constructor — remove.
- `@Transactional` on single-repo calls — remove (keep on `updateQuestion` which does find + save).

### URL
- Currently `/api/question` (singular) — change to `/api/questions` (plural).
- Frontend references in `frontend/src/api/question.ts`.

### Missing Tests
- No test for `GET /api/question/{editId}/edit` with non-existent editId (404 case).
- Rewrite all tests to MockMvc + `content().json()` style.

### CLAUDE.md Discrepancy
- CLAUDE.md mentions a `deletable` flag on `GET /api/question/{id}` — this doesn't exist in the code. Update docs.
- CLAUDE.md refers to `QuestionCreateResponse` but the actual class is `QuestionWriteResponse`. Update docs.

---

## QuizController

### DTOs
- `QuizRequest` exists and is clean. Needs `@Valid` + validation annotations.
- `QuizResponse` is a Lombok class (`@Getter @Setter @Builder`) — convert to a record for consistency.
- `QuizResponse.questions` is `Question[]` (raw entity) — should use a response DTO.
- `QuizCreateResponse` is fine.

### Layering
- Controller injects both `QuizService` and `QuizRepository`. GET goes through service, POST and PUT bypass it and write directly to the repository. Route all operations through the service.

### Missing Behavior
- `PUT /api/quiz/{id}` doesn't verify the quiz exists — blindly sets ID and saves. Should return 404 if not found.
- `PUT /api/quiz/{id}` returns `QuizCreateResponse` — semantic mismatch. Use a shared `QuizWriteResponse` or similar.

### Annotations
- `@Autowired` on constructor — remove.
- `@Transactional` on single-repo calls — remove.

### URL
- Currently `/api/quiz` (singular) — change to `/api/quizzes` (plural).
- Frontend references in `frontend/src/api/quiz.ts`.

### Missing Tests
- No test for quiz update (`PUT`).
- No test for `GET /api/quiz/{id}` with non-existent id (404 case).
- Rewrite all tests to MockMvc + `content().json()` style.

---

## Dead Code

- `QuizRepository.existsQuizWithQuestionId()` — defined but never called anywhere. Was likely the pre-optimization per-question check, superseded by `findQuestionIdsInQuizzesByWorkspaceGuid`. Remove it.

## Other

- `TestNumericalQuestionController` — hardcoded test endpoint in production source. Move behind feature flag or to a test profile.
- `ResponseHelper` — utility class without private constructor (minor).
