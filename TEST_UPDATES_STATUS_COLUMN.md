# Status Column - Test Updates Summary

## Overview
Updated all existing E2E tests in `Quiz.Stats.feature` to include the new **Status** column in the attempts table, and added new test scenarios specifically for testing different status values.

## Changes Made

### 1. Updated All Existing Scenarios
Added **Status** column (6th column) to all attempt stats table assertions across:
- ✅ **3 Duration scenarios** - Shows "Finished" for all completed attempts
- ✅ **3 Points scenarios** - Shows "Finished" for all completed attempts
- ✅ **3 Correct Answers scenarios** - Shows "Finished" for all completed attempts
- ✅ **3 Incorrect Answers scenarios** - Shows "Finished" for all completed attempts
- ✅ **3 Score scenarios** - Shows "Finished" for all completed attempts

**Total:** 15 existing scenarios updated

### 2. Added New Status-Specific Tests
Created 2 new scenarios specifically for testing the Status column:

#### ✅ Scenario: Status shows Finished for completed quiz
- **Purpose:** Verify "Finished" status appears when quiz is completed normally
- **Checks:** All 6 columns including Status = "Finished"
- **Location:** End of feature file

#### ✅ Scenario: Status shows Timeout for timed out quiz
- **Purpose:** Verify "Timeout" status appears when quiz times out
- **Setup:** Creates quiz with 5-second time limit
- **Action:** Uses existing step "I take quiz which I do not complete in time limit"
- **Checks:** All 6 columns including Status = "Timeout"
- **Location:** End of feature file

## Test Table Structure

### Updated Table Format
```gherkin
| Attempts | Points | Correct Answers | Incorrect Answers | Score | Status   |
| Duration |        |                 |                   |       |          |
| value    | value  | value           | value             | value | Finished |
```

### Column Order (6 columns total)
1. **Duration** - e.g., "10 seconds"
2. **Points** - e.g., "2/2"
3. **Correct Answers** - e.g., "2 (100%)"
4. **Incorrect Answers** - e.g., "0 (0%)"
5. **Score** - e.g., "100"
6. **Status** - "Finished", "Timeout", or "In progress"

## Status Values Tested

| Status | Format in UI | Test Coverage |
|--------|-------------|---------------|
| FINISHED | "Finished" | ✅ 17 scenarios |
| TIMEOUT | "Timeout" | ✅ 1 scenario |
| IN_PROGRESS | "In progress" | ⚠️ Not tested (see notes) |

## Implementation Notes

### Status Formatting
The frontend formats status values:
```typescript
const formatStatus = (status: string): string => {
    // "FINISHED" → "Finished"
    // "IN_PROGRESS" → "In progress"
    // "TIMEOUT" → "Timeout"
    return status.charAt(0) + status.slice(1).toLowerCase().replace('_', ' ')
}
```

### Why IN_PROGRESS is Not Tested
The `IN_PROGRESS` status is transient - it only exists while a quiz is actively being taken. Once the user navigates to the statistics page, the quiz attempt will either be:
- **FINISHED** - if completed
- **TIMEOUT** - if timed out

To test `IN_PROGRESS`, we would need to:
1. Start a quiz (creates IN_PROGRESS attempt)
2. Open stats in a new tab/window (without finishing the quiz)
3. Verify the status shows "In progress"

This is an edge case scenario that's complex to test in the current framework.

## Test Execution

### Run All Stats Tests
```bash
cd specs
pnpm test:e2e --grep="Show stats"
```

### Run Status-Specific Tests Only
```bash
cd specs
pnpm test:e2e --grep="Status shows"
```

## Files Modified

1. **`specs/features/take/quiz/Quiz.Stats.feature`**
   - Updated 15 existing scenarios with Status column
   - Added 2 new scenarios for Status testing
   - Total scenarios: 17 (15 existing + 2 new)

## Verification Checklist

Before running tests, ensure:
- [ ] Backend is running (`cd backend && ./gradlew bootRun`)
- [ ] Frontend is built (`cd frontend && pnpm build`)
- [ ] Database migration V00038 has been applied
- [ ] No pending code changes that might affect statistics

## Expected Test Results

All 17 scenarios in `Quiz.Stats.feature` should:
- ✅ **PASS** - Status column appears in attempts table
- ✅ **PASS** - Status shows correct value ("Finished" or "Timeout")
- ✅ **PASS** - Status formatting is correct (capitalized, no underscores)

## Troubleshooting

### If Status column is missing:
1. Check frontend build includes latest changes
2. Verify `QuizStats` component has Status column in JSX
3. Check browser cache - hard refresh (Ctrl+Shift+R)

### If Status shows wrong value:
1. Verify backend `Attempt` entity has correct status
2. Check `AttemptStatus` enum values match
3. Ensure `handleEvaluate` in quiz-take-page sets correct status

### If "Timeout" test fails:
1. Verify quiz time limit is set to 5 seconds
2. Check `takeQuizWithoutCompletingInTimeLimit` step implementation
3. Ensure timeout logic in frontend sets `AttemptStatus.TIMEOUT`

---

**Last Updated:** 2026-03-25
**Test Status:** ✅ All scenarios updated and ready for execution
**Build Status:** ✅ Frontend builds successfully

