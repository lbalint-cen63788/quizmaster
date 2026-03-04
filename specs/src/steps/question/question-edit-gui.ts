import type { DataTable } from '@cucumber/cucumber'
import { expect } from '@playwright/test'

import type { TableOf } from 'steps/common.ts'
import { Given, Then, When } from 'steps/fixture.ts'
import {
    addAnswers,
    type AnswerRaw,
    enterAnswer,
    enterAnswerExplanation,
    enterAnswerText,
    enterQuestion,
    enterQuestionExplanation,
    markQuestionAsPartiallyScored,
    markAnswerCorrectness,
    openCreatePage,
    openEditPage,
    saveQuestion,
    submitQuestion,
} from 'steps/question/ops.ts'
import {
    expectAnswer,
    expectDeleteButtonsState,
    expectEmptyAnswers,
    expectErrorCount,
    expectErrorMessages,
} from 'steps/question/expects.ts'

Given('I start creating a question', async function () {
    await openCreatePage(this)
})

Given('I start editing question {string}', async function (bookmark: string) {
    await openEditPage(this, bookmark)
})

When('I enable explanations', async function () {
    await this.questionEditPage.enableExplanations()
})

When('I disable explanations', async function () {
    await this.questionEditPage.disableExplanations()
})

// Title assertions

Then('I see question edit page', async function () {
    await this.questionEditPage.isEditPage()
})

// Field assertions

Then('I see empty question text', async function () {
    const question = await this.questionEditPage.questionValue()
    expect(question).toBe('')
})

Then('I see question text {string}', async function (question: string) {
    const questionValue = await this.questionEditPage.questionValue()
    expect(questionValue).toBe(question)
})

Then(/I see explanations are (enabled|disabled)/, async function (value: string) {
    const showExplanation = await this.questionEditPage.explanationsEnabled()
    expect(showExplanation).toBe(value === 'enabled')
})

Then(/the question is (single|multiple|numerical) choice/, async function (value: string) {
    const selectedType = await this.questionEditPage.questionType()
    const expectedType = value === 'numerical' ? 'numerical' : value === 'multiple' ? 'multiple' : 'single'
    expect(selectedType).toBe(expectedType)
})

Then('I see numerical answer field', async function () {
    const isVisible = await this.questionEditPage.isNumericalCorrectAnswerVisible()
    expect(isVisible).toBe(true)
})

Then('I do not see numerical answer field', async function () {
    const isVisible = await this.questionEditPage.isNumericalCorrectAnswerVisible()
    expect(isVisible).toBe(false)
})

Then('I do not see answer fields', async function () {
    const answerCount = await this.questionEditPage.answerRowCount()
    expect(answerCount).toBe(0)
})

Then('I do not see Add Answer button', async function () {
    const isVisible = await this.questionEditPage.isAddAnswerButtonVisible()
    expect(isVisible).toBe(false)
})

Then('I see numerical correct answer {string}', async function (value: string) {
    const current = await this.questionEditPage.numericalCorrectAnswerValue()
    expect(current).toBe(value)
})

Then(/easy mode is (on|off)/, async function (value: string) {
    const isEasyMode = await this.questionEditPage.isEasyMode()
    expect(isEasyMode).toBe(value === 'on')
})

Then(/easy mode is (available|not available)/, async function (value: string) {
    const isEasyModeVisible = await this.questionEditPage.isEasyModeVisible()
    expect(isEasyModeVisible).toBe(value === 'available')
})

Then('I see explanation fields', async function () {
    const explanationFieldsCount = await this.questionEditPage.countExplanationFields()
    expect(explanationFieldsCount).toBeGreaterThan(0)
})

Then('I do not see explanation fields', async function () {
    const explanationFieldsCount = await this.questionEditPage.countExplanationFields()
    expect(explanationFieldsCount).toBe(0)
})

Then('I see 2 default empty answers', async function () {
    const answerCount = await this.questionEditPage.answerRowCount()
    expect(answerCount).toBe(2)

    await expectEmptyAnswers(this.questionEditPage, 0)
    await expectEmptyAnswers(this.questionEditPage, 1)

    await expectDeleteButtonsState(this.questionEditPage)
})

Then(/I see answer (\d+) as (correct|incorrect)/, async function (index: number, correctness: string) {
    const isCorrect = correctness === 'correct'
    expect(await this.questionEditPage.isAnswerCorrect(index - 1)).toBe(isCorrect)
})

Then(
    /I see answer (\d+) text "([^"]*)", (correct|incorrect), with explanation "([^"]*)"/,
    async function (index: number, answer: string, correctness: string, explanation: string) {
        await expectAnswer(this.questionEditPage, index - 1, answer, correctness === 'correct', explanation)
    },
)

Then('I see the answers fields', async function (data: TableOf<AnswerRaw>) {
    const answers = data.raw()

    expect(await this.questionEditPage.answerRowCount()).toBe(answers.length)

    let i = 0
    for (const [answer, star, explanation] of answers) {
        await expectAnswer(this.questionEditPage, i++, answer, star === '*', explanation)
    }
})

Then('I see empty question explanation', async function () {
    const explanation = await this.questionEditPage.questionExplanation()
    expect(explanation).toBe('')
})

Then('I see question explanation {string}', async function (explanation: string) {
    const explanationValue = await this.questionEditPage.questionExplanation()
    expect(explanationValue).toBe(explanation)
})

Then('I see prefilled valid AI question', async function () {
    const questionValue = await this.questionEditPage.questionValue()
    expect(questionValue.trim().length).toBeGreaterThan(0)

    const isNumerical = await this.questionEditPage.isNumericalChoice()
    if (isNumerical) {
        const numericalAnswer = await this.questionEditPage.numericalCorrectAnswerValue()
        expect(numericalAnswer.trim()).toMatch(/^-?\d+$/)
        return
    }

    const answerCount = await this.questionEditPage.answerRowCount()
    expect(answerCount).toBeGreaterThanOrEqual(2)

    let correctAnswerCount = 0
    for (let i = 0; i < answerCount; i++) {
        const answerText = await this.questionEditPage.answerText(i)
        expect(answerText.trim().length).toBeGreaterThan(0)

        if (await this.questionEditPage.isAnswerCorrect(i)) {
            correctAnswerCount++
        }
    }

    expect(correctAnswerCount).toBeGreaterThanOrEqual(1)
})

// Field edits

When('I enter question {string}', async function (question: string) {
    await enterQuestion(this, question)
})

When('I enter AI instructions into field Question: {string}', async function (instructions: string) {
    await enterQuestion(this, instructions)
})

When('I click on button {string}', async function (buttonName: string) {
    if (buttonName === 'AI assist') {
        await this.questionEditPage.clickAiAssist()
        return
    }

    throw new Error(`Unsupported button name: ${buttonName}`)
})

When(/I mark the question as (single|multiple|numerical) choice/, async function (choice: string) {
    if (choice === 'single') {
        await this.questionEditPage.setSingleChoice()
    } else if (choice === 'multiple') {
        await this.questionEditPage.setMultipleChoice()
    } else {
        await this.questionEditPage.setNumericalChoice()
    }
})

When('I enter numerical correct answer {string}', async function (value: string) {
    await this.questionEditPage.enterNumericalCorrectAnswer(value)
})

When('I enter answer {int} text {string}', async function (index: number, answer: string) {
    await enterAnswerText(this, index - 1, answer)
})

When('I mark answer {int} as correct', async function (index: number) {
    await markAnswerCorrectness(this, index - 1, true)
})

When('I enter answer {int} explanation {string}', async function (index: number, explanation: string) {
    await enterAnswerExplanation(this, index - 1, explanation)
})

When('I enter answer {int} text {string} and mark it as correct', async function (index: number, answer: string) {
    await enterAnswer(this, index - 1, answer, true, undefined)
})

When(
    /I enter answer (\d+) text "([^"]*)", (correct|incorrect), with explanation "([^"]*)"/,
    async function (index: number, answer: string, correctness: string, explanation: string) {
        await enterAnswer(this, index - 1, answer, correctness === 'correct', explanation)
    },
)

Given('I enter answers', async function (answerRawTable: TableOf<AnswerRaw>) {
    await addAnswers(this, answerRawTable)
})

When('I add another answer', async function () {
    await this.questionEditPage.addAdditionalAnswer()
})

When('I enter question explanation {string}', async function (explanation: string) {
    await enterQuestionExplanation(this, explanation)
})

When('mark question as partially scored', async function () {
    await markQuestionAsPartiallyScored(this)
})

// Save question

When('I attempt to submit the question', submitQuestion)
When('I submit the question', submitQuestion)

When('I save the question', async function () {
    await saveQuestion(this, 'manual')
})

Then('I see question-take URL and question-edit URL', async function () {
    const takeUrl = await this.questionEditPage.questionUrl()
    const editUrl = await this.questionEditPage.questionEditUrl()

    expect(takeUrl).toBeDefined()
    expect(editUrl).toBeDefined()

    expect(editUrl).toContain('/edit')
    expect(editUrl).not.toContain('/undefined')
})

// Error messages assertions

Then('I see error messages', async function (table: DataTable) {
    const expectedErrors: string[] = table.raw().map(row => row[0])

    await expectErrorMessages(this.questionEditPage, expectedErrors)
})

Then('I see no error messages', async function () {
    await expectErrorCount(this.questionEditPage, 0)
})

Then('I delete answer {int}', async function (answerNumber: number) {
    await this.questionEditPage.deleteAnswer(answerNumber - 1)
})

Then('I can delete {int} answers', async function (buttonCount: number) {
    await expectDeleteButtonsState(this.questionEditPage, buttonCount, false)
})

Then('I see {int} delete buttons disabled', async function (buttonCount: number) {
    await expectDeleteButtonsState(this.questionEditPage, buttonCount, true)
})
