import type { DataTable } from '@cucumber/cucumber'
import { expect } from '@playwright/test'

import { expectedNumberOfChildrenToBe } from 'steps/common.ts'
import { Then, When } from 'steps/fixture.ts'
import { expectQuizFormErrors } from 'steps/quiz/expects.ts'
import type { QuizMode, Difficulty } from 'steps/world/quiz.ts'

When('I start creating a new quiz', async function () {
    await this.workspacePage.createNewQuiz()
})

Then('I see the quiz creation page', async function () {
    await this.page.waitForSelector('#create-quiz-page')
    const isVisible = await this.page.locator('#create-quiz-page').isVisible()
    expect(isVisible).toBe(true)
})

When('I enter quiz name {string}', async function (title: string) {
    await this.quizCreatePage.enterQuizName(title)
})

When('I set randomized question count to {int}', async function (finalCount: string) {
    await this.quizCreatePage.enterQuizFinalCount(finalCount)
})

Then('I see empty quiz title', async function () {
    const value = await this.quizCreatePage.getQuizTitleValue()
    expect(value).toBe('')
})

Then('I see empty quiz description', async function () {
    const value = await this.quizCreatePage.getQuizDescriptionValue()
    expect(value).toBe('')
})

Then('I see time limit {string} seconds', async function (timeLimit: string) {
    const value = await this.quizCreatePage.timeLimitInput().inputValue()
    expect(value).toBe(timeLimit)
})

Then('I see pass score {string}', async function (score: string) {
    const value = await this.quizCreatePage.passScoreInput().inputValue()
    expect(value).toBe(score)
})

When('I see quiz question {string}', async function (title: string) {
    await expect(this.quizCreatePage.getQuestion(title).first()).toBeVisible()
})

When('I start editing a quiz {string}', async function (quiz: string) {
    await expect(this.quizCreatePage.getQuestion(quiz).first()).toBeVisible()
})

When('questions belonging to the quiz are marked', async function (quiz: string) {
    await expect(this.quizCreatePage.getQuestion(quiz).first()).toBeVisible()
})

When("I don't see quiz questions {string}", async function (title: string) {
    await expect(this.quizCreatePage.getQuestion(title).first()).toBeHidden()
})

When('I enter quiz description {string}', async function (title: string) {
    await this.quizCreatePage.enterDescription(title)
})

When('I select question {string}', async function (question: string) {
    await this.quizCreatePage.selectQuestion(question)
})

When('I enable question randomization', async function () {
    await this.quizCreatePage.selectRandomizedFunction()
})

When(/I select (exam|learn) mode/, async function (mode: QuizMode) {
    await this.quizCreatePage.selectFeedbackMode(mode)
})

When('I select difficulty {string}', async function (inputDifficulty: string) {
    const difficulty = inputDifficulty.toLowerCase().replace('_', '-') as Difficulty
    await this.quizCreatePage.selectDifficulty(difficulty)
})

When('I submit the quiz', async function () {
    await this.quizCreatePage.submit()
})

When('I enter pass score {string}', async function (score: string) {
    await this.quizCreatePage.passScoreInput().fill(score)
})

When('I enter time limit {string}', async function (limit: string) {
    await this.quizCreatePage.timeLimitInput().fill(limit)
})

When('I filter questions by {string}', async function (s: string) {
    await this.quizCreatePage.enterFilterString(s)
})

Then('I see workspace with {int} available questions', async function (count: number) {
    await expectedNumberOfChildrenToBe(this.quizCreatePage.questionsInList(), count)
})

Then('I clear time limit', async function () {
    await this.quizCreatePage.clearTimeLimit()
})

Then('I clear score', async function () {
    await this.quizCreatePage.clearScore()
})

Then('I see error messages in quiz form', async function (table: DataTable) {
    await expectQuizFormErrors(
        this.quizCreatePage,
        table.raw().map(row => row[0]),
    )
})

Then('I see no error messages in quiz form', async function () {
    const hasError = await this.quizCreatePage.hasAnyError()
    expect(hasError).toBe(false)
})

Then('I see question is marked {string}', async function (question: string) {
    await expect(this.page.getByLabel(question)).toBeChecked()
})

Then('I see question is not marked {string}', async function (question: string) {
    await expect(this.page.getByLabel(question)).not.toBeChecked()
})

Then('I set quiz time limit to {string}', async function (timeLimit: string) {
    await this.quizCreatePage.timeLimitInput().fill(timeLimit)
})

Then('I see formatted quiz time limit {string}', async function (formattedTimeLimit: string) {
    await expect(this.quizCreatePage.formattedTimeLimitLabel()).toHaveText(formattedTimeLimit)
})
