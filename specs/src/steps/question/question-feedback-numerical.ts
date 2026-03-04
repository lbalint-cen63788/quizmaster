import { expect } from '@playwright/test'

import { Given, Then, When } from 'steps/fixture.ts'
import { emptyQuestion } from 'steps/world'

// Given(
//     'numerical question {string} with correct answer {string}',
//     async function (question: string, correctAnswer: string) {
//         const params = new URLSearchParams({ question, correct: correctAnswer })
//         const url = `/test-numerical-question?${params.toString()}`

//         const bookmark = question
//         this.questionBookmarks[bookmark] = {
//             ...emptyQuestion(),
//             question,
//             url,
//             answers: [{ answer: correctAnswer, isCorrect: true, explanation: '' }],
//         }
//         this.activeQuestionBookmark = bookmark
//     },
// )

Then('I see a number input', async function () {
    await expect(this.takeQuestionPage.numericalInputLocator()).toBeVisible()
})

Then('number input is focused', async function () {
    const input = this.takeQuestionPage.numericalInputLocator()
    await expect(input).toBeFocused()
})

When('I enter {string}', async function (answer: string) {
    await this.takeQuestionPage.fillNumericalAnswer(answer)
})
