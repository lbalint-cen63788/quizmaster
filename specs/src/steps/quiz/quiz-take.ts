import type { DataTable } from '@cucumber/cucumber'
import { expect } from '@playwright/test'

import { When, Then } from 'steps/fixture.ts'
import { answerCorrectly, answerIncorrectly, progressThroughQuestions, repeatAsync } from 'steps/quiz/ops.ts'

When('I answer the question', async function () {
    await answerCorrectly(this)
})

When('I answer correctly', async function () {
    await answerCorrectly(this)
})

When('I answer incorrectly', async function () {
    await answerIncorrectly(this)
})

When('I answer {int} questions correctly', async function (correct: number) {
    await repeatAsync(correct, () => answerCorrectly(this))
})

When('I answer {int} questions incorrectly', async function (incorrect: number) {
    await repeatAsync(incorrect, () => answerIncorrectly(this))
})

When('I progress through the questions', async function () {
    await progressThroughQuestions(this)
})

Then('I see the correct answers count', async function (dataTable: DataTable) {
    const rows = dataTable.raw()
    for (const [bookmark, expected] of rows) {
        const actual = this.correctAnswersCounts[bookmark]
        expect(actual).toBe(expected)
    }
})
