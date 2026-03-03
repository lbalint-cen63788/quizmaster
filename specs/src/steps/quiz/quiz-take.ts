import type { DataTable } from '@cucumber/cucumber'
import { expect } from '@playwright/test'

import { When, Then } from 'steps/fixture.ts'
import { answerCorrectly, answerIncorrectly, repeatAsync } from 'steps/quiz/ops.ts'

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
    // Build reverse lookup: question text -> bookmark
    const textToBookmark: Record<string, string> = {}
    for (const [bookmark, question] of Object.entries(this.questionBookmarks)) {
        textToBookmark[question.question] = bookmark
    }

    const questionCount = Object.keys(textToBookmark).length

    // Progress through each question
    for (let i = 0; i < questionCount; i++) {
        await this.takeQuestionPage.waitForLoaded()

        const questionText = (await this.takeQuestionPage.questionText()) || ''
        const bookmark = textToBookmark[questionText] || questionText

        // Check if correct answers count is visible and record it
        const countLocator = this.takeQuestionPage.correctAnswersCountLocator()
        const isVisible = await countLocator.isVisible()

        if (isVisible) {
            const count = await countLocator.textContent()
            this.correctAnswersCounts[bookmark] = count || '-'
        } else {
            this.correctAnswersCounts[bookmark] = '-'
        }

        // Answer and submit to progress
        await this.takeQuestionPage.selectAnswerNth(0)
        await this.takeQuestionPage.submit()
    }
})

Then('I see the correct answers count', async function (dataTable: DataTable) {
    const rows = dataTable.raw()
    for (const [bookmark, expected] of rows) {
        const actual = this.correctAnswersCounts[bookmark]
        expect(actual).toBe(expected)
    }
})
