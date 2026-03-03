import type { DataTable } from '@cucumber/cucumber'
import { expect } from '@playwright/test'

import { expectTextToBe } from 'steps/common.ts'
import { Then, When } from 'steps/fixture.ts'
import { expectColorFeedback, expectQuestion } from 'steps/question/expects.ts'
import { answerQuestion } from 'steps/question/ops.ts'

When('I take question {string}', async function (bookmark: string) {
    await this.page.goto(this.questionBookmarks[bookmark].url)
    this.activeQuestionBookmark = bookmark
})

Then('I see the question and the answers', async function () {
    await expectQuestion(this.takeQuestionPage, this.activeQuestion)
})

When('I answer {string}', async function (answerList: string) {
    await answerQuestion(this, answerList)
})

When('I press the key {int}', async function (num: number) {
    if (num < 0 || num > 9) {
        throw new Error(`Invalid numpad key: ${num}`)
    }

    await this.page.click('body')

    this.page.keyboard.press(`Numpad${num}`)
})

When('I uncheck answer {string}', async function (answerList: string) {
    const answers = this.parseAnswers(answerList)
    for (const answer of answers) {
        await this.takeQuestionPage.unselectAnswer(answer)
    }
})

When('I check answer {string}', async function (answerList: string) {
    const answers = this.parseAnswers(answerList)
    for (const answer of answers) {
        await this.takeQuestionPage.selectAnswer(answer)
    }
})

When('I submit question', async function () {
    await this.takeQuestionPage.submit()
})

Then('I see feedback {string}', async function (feedback: string) {
    await expectTextToBe(this.takeQuestionPage.questionFeedbackLocator(), feedback)
})

Then('I see score {string}', async function (score: string) {
    await expectTextToBe(this.takeQuestionPage.questionScoreLocator(), score)
})

Then('no answer is selected', async function () {
    expect(await this.takeQuestionPage.selectedAnswersLocator().count()).toBe(0)
})

Then('I see the question explanation', async function () {
    await expectTextToBe(this.takeQuestionPage.questionExplanationLocator(), this.activeQuestion.explanation)
})

Then('I see individual explanations per answer:', async function (dataTable: DataTable) {
    const rows = dataTable.hashes()
    for (const row of rows) {
        const { answer, explanation } = row
        await expect(this.takeQuestionPage.answerExplanationLocator(answer)).toHaveText(explanation)
    }
})

Then('I see the {string} question for the quiz', async function (questionName: string) {
    expect(await this.takeQuestionPage.questionText()).toBe(questionName)
})

Then('I see individual color feedback per answer:', async function (dataTable: DataTable) {
    await expectColorFeedback(this.takeQuestionPage, dataTable.hashes())
})

Then('I see that question has number of correct answers displayed', async function () {
    const correctAnswersNumberElement = this.takeQuestionPage.correctAnswersCountLocator()
    await expect(correctAnswersNumberElement).toBeAttached()
})

Then('I see that the question has {int} correct answers', async function (count: number) {
    const correctAnswersNumberText = await this.takeQuestionPage.correctAnswersCountNumber()
    expect(correctAnswersNumberText).toBe(count)
})

Then('I do not see correct answers count', async function () {
    const correctAnswersNumberElement = this.takeQuestionPage.correctAnswersCountLocator()
    await expect(this.takeQuestionPage.submitButtonLocator()).toBeVisible()
    await expect(correctAnswersNumberElement).not.toBeAttached()
})

Then('I see the image {string}', async function (filename: string) {
    const questionImageLocator = this.takeQuestionPage.questionImageLocator(filename)
    await expect(questionImageLocator).toBeVisible()
})

Then('I do not see the image {string}', async function (filename: string) {
    const questionImageLocator = this.takeQuestionPage.questionImageLocator(filename)
    await expect(questionImageLocator).not.toBeVisible()
})
