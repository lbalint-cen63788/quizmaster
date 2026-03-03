import { expect } from '@playwright/test'

import { expectTextToBe, expectThatIsNotVisible, expectThatIsVisible } from 'steps/common.ts'
import { Given, When, Then } from 'steps/fixture.ts'
import { expectQuestion } from 'steps/question/expects.ts'
import type { QuizmasterWorld } from 'steps/world'

const openQuiz = async (world: QuizmasterWorld, quizId: string) => {
    const quizUrl = world.quizBookmarks[quizId]?.url || `/quiz/${quizId}`
    await world.page.goto(quizUrl)
}

export const startQuiz = async (world: QuizmasterWorld, quizId: string) => {
    await openQuiz(world, quizId)
    await world.quizWelcomePage.start()
}

Given('I open quiz {string}', async function (quizId: string) {
    await openQuiz(this, quizId)
})

Given('I start quiz {string}', async function (quizId: string) {
    await startQuiz(this, quizId)
})

Given('I start the quiz', async function () {
    await startQuiz(this, this.activeQuizBookmark)
})

Then('I see question {string}', async function (bookmark: string) {
    const question = this.questionBookmarks[bookmark]
    await expectQuestion(this.takeQuestionPage, question)
})

When('I proceed to the next question', async function () {
    await this.questionPage.next()
})

When('I skip the question', async function () {
    await this.questionPage.next()
})

When('I click the start button', async function () {
    await this.quizWelcomePage.start()
})

When('I go back to previous question', async function () {
    await this.questionPage.back()
})

When('I evaluate the quiz', async function () {
    await this.questionPage.evaluate()
})

Then('I proceed to the score page', async function () {
    await this.questionPage.evaluate()
})

Then('I see the "Game over" dialog', async function () {
    await expect(this.questionPage.evaluateModalButtonLocator()).toBeVisible()
})

When('I confirm the "Game over" dialog', async function () {
    await this.questionPage.evaluateModalButtonLocator().click()
})

Then('I see buttons {string}', async function (buttonList: string) {
    const expectedButtons = buttonList.split(',').map(b => b.trim())

    const buttonLocatorMap: Record<string, () => ReturnType<typeof this.questionPage.backButtonLocator>> = {
        Back: () => this.questionPage.backButtonLocator(),
        Next: () => this.questionPage.nextButtonLocator(),
        Evaluate: () => this.questionPage.evaluateButtonLocator(),
    }

    for (const name of expectedButtons) {
        const locator = buttonLocatorMap[name]
        if (!locator) throw new Error(`Unknown button: "${name}"`)
        await expect(locator()).toBeVisible()
    }

    await expect(this.questionPage.navigationButtonsLocator()).toHaveCount(expectedButtons.length)
})

Then('I should see answer {string} is checked', async function (answerList: string) {
    const answers = this.parseAnswers(answerList)
    for (const element of answers) {
        await expect(this.takeQuestionPage.answerCheckLocator(element)).toBeChecked()
    }
})

Then('I should see answer {string} is unchecked', async function (answerList: string) {
    const answers = this.parseAnswers(answerList)
    for (const element of answers) {
        await expect(this.takeQuestionPage.answerCheckLocator(element)).not.toBeChecked()
    }
})

Then('I should not see the answer', async function () {
    await expectThatIsNotVisible(this.takeQuestionPage.questionFeedbackLocator())
})

Then('I should see the answer', async function () {
    await expectThatIsVisible(this.takeQuestionPage.questionFeedbackLocator())
})

Then('progress shows {int} of {int}', async function (current: number, max: number) {
    expect(await this.questionPage.progressCurrent()).toBe(current)
    expect(await this.questionPage.progressMax()).toBe(max)
})

Then('I should see the text "Game over time"', async function () {
    await expectTextToBe(this.page.locator('dialog p'), 'Game over time')
})

Then('I should see the countdown timer {string}', async function (timer: string) {
    const timerDiv = this.page.getByTestId('timerID')
    await expectTextToBe(timerDiv, timer)
})

Then('I should see the countdown timer after delay is less then {string}', async function (timer: string) {
    await this.page.clock.install({ time: new Date() })
    await expectTextToBe(this.page.getByTestId('timerID'), timer)
    await this.page.clock.fastForward(timer)
    await expectTextToBe(this.page.getByTestId('timerID'), timer)
})

Then('I will wait for {string}', async function (timer: string) {
    await this.page.clock.install({ time: new Date() })
    await expectTextToBe(this.page.getByTestId('timerID'), timer)
    await this.page.clock.fastForward(timer)
})

Then('I should see the results table', async function () {
    const textResult = await this.quizScorePage.resultTableExists()
    expect(textResult).toBe(true)
})

Then('I see answer {string} checked', async function (answer: string) {
    expect(await this.takeQuestionPage.isAnswerSelected(answer)).toBe(true)
})

Then('I see the submit button as active', async function () {
    expect(await this.takeQuestionPage.submitButtonIsDisabled()).toBe(false)
})

Then('I see the submit button as inactive', async function () {
    expect(await this.takeQuestionPage.submitButtonIsDisabled()).toBe(true)
})
