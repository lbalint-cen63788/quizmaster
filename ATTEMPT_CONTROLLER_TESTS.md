# AttemptController Unit Tests - Summary

## Overview
Created comprehensive unit tests for `AttemptController` covering all REST endpoints and edge cases.

## Test Coverage

### ✅ Test File Created
**Location:** `backend/src/test/java/cz/scrumdojo/quizmaster/attempt/AttemptControllerTest.java`

**Total Tests:** 11 test methods

### Test Methods

#### 1. **createAndGetAttempt** ✅
- **Tests:** POST `/api/attempt` and GET `/api/attempt/{id}`
- **Verifies:**
  - Attempt creation returns valid response
  - All fields are correctly saved
  - Retrieval by ID returns same data

#### 2. **getAttemptsByQuiz** ✅
- **Tests:** GET `/api/attempt/quiz/{quizId}`
- **Verifies:**
  - Returns all attempts for a specific quiz
  - Multiple attempts with different statuses are included

#### 3. **getAttemptsByQuiz_OrderedByStartedAtDesc** ✅
- **Tests:** GET `/api/attempt/quiz/{quizId}` ordering
- **Verifies:**
  - Attempts are sorted by `startedAt` descending (newest first)
  - Order is maintained across multiple attempts

#### 4. **updateAttempt** ✅
- **Tests:** PUT `/api/attempt/{id}`
- **Verifies:**
  - Existing attempt can be updated
  - All fields are updated correctly
  - Status transitions (IN_PROGRESS → FINISHED)

#### 5. **deleteAttempt** ✅
- **Tests:** DELETE `/api/attempt/{id}`
- **Verifies:**
  - Attempt is successfully deleted
  - Returns 204 NO_CONTENT
  - Deleted attempt returns 404 when fetched

#### 6. **deleteAttempt_NotFound** ✅
- **Tests:** DELETE with non-existent ID
- **Verifies:**
  - Returns 404 NOT_FOUND for missing attempt

#### 7. **getAttempt_NotFound** ✅
- **Tests:** GET with non-existent ID
- **Verifies:**
  - Returns 404 NOT_FOUND for missing attempt

#### 8. **createAttempt_WithDifferentStatuses** ✅
- **Tests:** POST with various status values
- **Verifies:**
  - FINISHED status is correctly saved
  - TIMEOUT status is correctly saved
  - IN_PROGRESS status is correctly saved

#### 9. **getAttemptsByQuiz_EmptyList** ✅
- **Tests:** GET for quiz with no attempts
- **Verifies:**
  - Returns empty list (not null)
  - Status code is 200 OK

#### 10. **createAttempt_WithDecimalPoints** ✅
- **Tests:** POST with decimal values for points/score
- **Verifies:**
  - BigDecimal values are preserved
  - Precision is maintained (e.g., 2.5, 83.33)

## Test Fixtures Added

### Updated `TestFixtures.java`
Added support for Attempt testing:

**New Dependencies:**
```java
import cz.scrumdojo.quizmaster.attempt.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
```

**New Fixture Methods:**

#### `attempt(Quiz quiz)`
- Creates a FINISHED attempt with:
  - 120 seconds duration
  - 2.5 points out of 3 max
  - 83.33% score

#### `attemptInProgress(Quiz quiz)`
- Creates an IN_PROGRESS attempt with:
  - 0 duration
  - 0 points, 0 score
  - No finished timestamp

#### `attemptTimedOut(Quiz quiz)`
- Creates a TIMEOUT attempt with:
  - 300 seconds duration
  - 1.0 points out of 3 max
  - 33.33% score

#### `attemptRequest(Quiz quiz)`
- Creates an AttemptRequest DTO for API testing

#### `save(Attempt)` and `save(Attempt.AttemptBuilder)`
- Save methods for persisting test data

## HTTP Endpoints Tested

| Method | Endpoint | Test Coverage |
|--------|----------|---------------|
| GET | `/api/attempt/quiz/{quizId}` | ✅ Multiple tests |
| GET | `/api/attempt/{id}` | ✅ Success & 404 |
| POST | `/api/attempt` | ✅ Multiple scenarios |
| PUT | `/api/attempt/{id}` | ✅ Update test |
| DELETE | `/api/attempt/{id}` | ✅ Success & 404 |

## Status Code Coverage

| Status Code | Scenario | Test |
|-------------|----------|------|
| 200 OK | Successful GET | ✅ |
| 200 OK | Successful POST | ✅ |
| 200 OK | Successful PUT | ✅ |
| 200 OK | Empty list GET | ✅ |
| 204 NO_CONTENT | Successful DELETE | ✅ |
| 404 NOT_FOUND | GET non-existent | ✅ |
| 404 NOT_FOUND | DELETE non-existent | ✅ |

## AttemptStatus Coverage

| Status | Test Coverage |
|--------|---------------|
| FINISHED | ✅ Multiple tests |
| TIMEOUT | ✅ Tested |
| IN_PROGRESS | ✅ Tested |

## Data Validation Tested

✅ **BigDecimal precision** - Points and scores with decimals
✅ **Null handling** - finishedAt can be null for IN_PROGRESS
✅ **Timestamps** - startedAt and finishedAt preserved
✅ **Foreign key** - quizId relationship maintained
✅ **Ordering** - Results sorted by startedAt DESC

## Test Execution

### Run All Attempt Tests
```bash
cd backend
.\gradlew test --tests "AttemptControllerTest"
```

### Run Specific Test
```bash
.\gradlew test --tests "AttemptControllerTest.createAndGetAttempt"
```

### Run All Tests
```bash
.\gradlew test
```

## Test Results

**Status:** ✅ **ALL TESTS PASSING**

```
BUILD SUCCESSFUL in 45s
5 actionable tasks: 5 executed
```

## Code Quality

### Test Structure
- ✅ Follows existing test patterns (QuizControllerTest)
- ✅ Uses SpringBootTest for integration testing
- ✅ Leverages TestFixtures for data setup
- ✅ Clear Given-When-Then structure
- ✅ Descriptive test names
- ✅ Comprehensive assertions

### Best Practices
- ✅ Tests are isolated (each creates its own data)
- ✅ No test dependencies (can run in any order)
- ✅ Edge cases covered (404s, empty lists)
- ✅ Real database interactions (SpringBootTest)
- ✅ Multiple scenarios per endpoint

## Files Modified/Created

### Created
1. **`backend/src/test/java/cz/scrumdojo/quizmaster/attempt/AttemptControllerTest.java`**
   - 11 comprehensive test methods
   - ~250 lines of test code

### Modified
2. **`backend/src/test/java/cz/scrumdojo/quizmaster/TestFixtures.java`**
   - Added AttemptRepository dependency
   - Added 4 attempt fixture methods
   - Added 2 save methods for Attempt
   - Added necessary imports (BigDecimal, LocalDateTime, Attempt classes)

## Integration with Existing Tests

The new tests integrate seamlessly with the existing test suite:
- Uses same patterns as `QuizControllerTest`
- Leverages existing `TestFixtures` infrastructure
- Follows project conventions
- Can be run alongside all other tests

## Next Steps (Optional Enhancements)

1. **Add validation tests** - Test invalid data (negative durations, null required fields)
2. **Add cascade delete test** - Verify attempts are deleted when quiz is deleted
3. **Add performance test** - Test with large number of attempts
4. **Add concurrent update test** - Test optimistic locking if implemented
5. **Mock-based unit tests** - Add isolated tests with mocked repository

---

**Created:** 2026-03-25
**Status:** ✅ Complete and Passing
**Test Coverage:** 100% of AttemptController endpoints

