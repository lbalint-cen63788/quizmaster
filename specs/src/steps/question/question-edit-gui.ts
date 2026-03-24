import type { DataTable } from '@cucumber/cucumber'
import { expect } from '@playwright/test'

import type { TableOf } from 'steps/common.ts'
import { Given, Then, When } from 'steps/fixture.ts'
import type { QuizmasterWorld } from 'steps/world'
import {
    addAnswers,
    type AnswerRaw,
    enterAnswer,
    enterAnswerExplanation,
    enterAnswerText,
    enterImageUrl,
    enterQuestion,
    enterQuestionExplanation,
    markQuestionAsPartiallyScored,
    markAnswerCorrectness,
    submitQuestion,
} from 'steps/question/ops.ts'
import {
    expectAnswer,
    expectDeleteButtonsState,
    expectEmptyAnswers,
    expectErrorCount,
    expectErrorMessages,
} from 'steps/question/expects.ts'
import { ensureWorkspace, navigateToWorkspace } from 'steps/workspace/ops.ts'
import { emptyQuestion } from 'steps/world'

const DEFAULT_AI_QUESTION = 'Které město je hlavní město České republiky?'
const DEFAULT_AI_CORRECT_ANSWER = 'Praha'
const DEFAULT_AI_INCORRECT_ANSWER = 'Brno'

Given('I start creating a question', async function () {
    await ensureWorkspace(this)
    await navigateToWorkspace(this)
    await this.workspacePage.createNewQuestion()
    this.questionWip = emptyQuestion()
})

Given('page {string}', async () => {
    // marker step used in some scenarios for readability
})

When('I start creating a new question', async function () {
    await this.workspacePage.createNewQuestion()
})

Given('I start editing question {string}', async function (bookmark: string) {
    await this.workspacePage.goto(this.workspaceGuid)
    await this.workspacePage.editQuestion(this.questionBookmarks[bookmark].question)
    this.activeQuestionBookmark = bookmark
})

When('I enable explanations', async function () {
    await this.questionEditPage.enableExplanations()
})

When('I disable explanations', async function () {
    await this.questionEditPage.disableExplanations()
})

// Title assertions

Then('I see question edit page', async function () {
    await this.questionEditPage.expectEditPageVisible()
})

Then('I see the question creation page', async function () {
    await this.questionEditPage.expectCreatePageVisible()
})

When('I go back to the workspace {string}', async function () {
    await this.questionEditPage.back()
})

Then('I see the workspace {string}', async function (name: string) {
    await this.workspacePage.expectWorkspaceName(name)
})

// Field assertions

Then('I see empty question text', async function () {
    await this.questionEditPage.expectQuestionValue('')
})

Then('I see question text {string}', async function (question: string) {
    await this.questionEditPage.expectQuestionValue(question)
})

Then(/I see explanations are (enabled|disabled)/, async function (value: string) {
    if (value === 'enabled') {
        await this.questionEditPage.expectExplanationsChecked()
    } else {
        await this.questionEditPage.expectExplanationsUnchecked()
    }
})

Then(/the question is (single|multiple|numerical) choice/, async function (value: string) {
    const expectedType = value === 'numerical' ? 'numerical' : value === 'multiple' ? 'multiple' : 'single'
    await this.questionEditPage.expectQuestionType(expectedType)
})

Then('I see numerical answer field', async function () {
    await this.questionEditPage.expectNumericalAnswerVisible()
})

Then('I do not see numerical answer field', async function () {
    await this.questionEditPage.expectNumericalAnswerNotVisible()
})

Then('I do not see answer fields', async function () {
    await this.questionEditPage.expectAnswerRowCount(0)
})

Then('I do not see Add Answer button', async function () {
    await this.questionEditPage.expectAddAnswerNotVisible()
})

Then('I see numerical correct answer {string}', async function (value: string) {
    await this.questionEditPage.expectNumericalCorrectAnswer(value)
})

Then(/easy mode is (on|off)/, async function (value: string) {
    if (value === 'on') {
        await this.questionEditPage.expectEasyModeChecked()
    } else {
        await this.questionEditPage.expectEasyModeUnchecked()
    }
})

Then(/easy mode is (available|not available)/, async function (value: string) {
    if (value === 'available') {
        await this.questionEditPage.expectEasyModeVisible()
    } else {
        await this.questionEditPage.expectEasyModeNotVisible()
    }
})

Then('I see explanation fields', async function () {
    await this.questionEditPage.expectExplanationFieldsExist()
})

Then('I do not see explanation fields', async function () {
    await this.questionEditPage.expectNoExplanationFields()
})

Then('I see 2 default empty answers', async function () {
    await this.questionEditPage.expectAnswerRowCount(2)

    await expectEmptyAnswers(this.questionEditPage, 0)
    await expectEmptyAnswers(this.questionEditPage, 1)

    await expectDeleteButtonsState(this.questionEditPage)
})

Then(/I see answer (\d+) as (correct|incorrect)/, async function (index: number, correctness: string) {
    if (correctness === 'correct') {
        await this.questionEditPage.expectAnswerCorrect(index - 1)
    } else {
        await this.questionEditPage.expectAnswerIncorrect(index - 1)
    }
})

Then(
    /I see answer (\d+) text "([^"]*)", (correct|incorrect), with explanation "([^"]*)"/,
    async function (index: number, answer: string, correctness: string, explanation: string) {
        await expectAnswer(this.questionEditPage, index - 1, answer, correctness === 'correct', explanation)
    },
)

Then('I see the answers fields', async function (data: TableOf<AnswerRaw>) {
    const answers = data.raw()

    await this.questionEditPage.expectAnswerRowCount(answers.length)

    let i = 0
    for (const [answer, star, explanation] of answers) {
        await expectAnswer(this.questionEditPage, i++, answer, star === '*', explanation)
    }
})

Then('I see empty question explanation', async function () {
    await this.questionEditPage.expectQuestionExplanation('')
})

Then('I see question explanation {string}', async function (explanation: string) {
    await this.questionEditPage.expectQuestionExplanation(explanation)
})

Then('I see prefilled valid AI question', async function () {
    await expect.poll(() => this.questionEditPage.questionValue().then(v => v.trim().length > 0)).toBe(true)

    const isNumerical = await this.questionEditPage.isNumericalChoice()
    if (isNumerical) {
        await expect
            .poll(() => this.questionEditPage.numericalCorrectAnswerValue().then(v => /^-?\d+$/.test(v.trim())))
            .toBe(true)
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

Given('AI assistant returns generated question {string}', async function (generatedQuestion: string) {
    this.aiAssistantGeneratedAnswer = generatedQuestion
    this.aiAssistantGeneratedCorrectAnswer = DEFAULT_AI_CORRECT_ANSWER
    this.aiAssistantGeneratedIncorrectAnswer = DEFAULT_AI_INCORRECT_ANSWER
    this.aiAssistantRequestQuestion = ''

    await this.page.route('**/api/ai-assistant', async route => {
        const requestBody = (await route.request().postDataJSON()) as { question?: string } | null
        this.aiAssistantRequestQuestion = requestBody?.question ?? ''

        await route.fulfill({
            status: 200,
            contentType: 'application/json',
            body: JSON.stringify({
                question: generatedQuestion,
                answers: [this.aiAssistantGeneratedCorrectAnswer, this.aiAssistantGeneratedIncorrectAnswer],
                correctAnswers: [0],
            }),
        })
    })
})

const mockDefaultAiAssistant = async (world: QuizmasterWorld) => {
    world.aiAssistantGeneratedAnswer = DEFAULT_AI_QUESTION
    world.aiAssistantGeneratedCorrectAnswer = DEFAULT_AI_CORRECT_ANSWER
    world.aiAssistantGeneratedIncorrectAnswer = DEFAULT_AI_INCORRECT_ANSWER
    world.aiAssistantRequestQuestion = ''

    await world.page.route('**/api/ai-assistant', async route => {
        const requestBody = (await route.request().postDataJSON()) as { question?: string } | null
        world.aiAssistantRequestQuestion = requestBody?.question ?? ''

        await route.fulfill({
            status: 200,
            contentType: 'application/json',
            body: JSON.stringify({
                question: world.aiAssistantGeneratedAnswer,
                answers: [world.aiAssistantGeneratedCorrectAnswer, world.aiAssistantGeneratedIncorrectAnswer],
                correctAnswers: [0],
            }),
        })
    })
}

Then('request to AI assistant contains question {string}', async function (expectedQuestion: string) {
    expect(this.aiAssistantRequestQuestion).toBe(expectedQuestion)
})

Then('question field is updated to {string}', async function (expectedQuestion: string) {
    await this.questionEditPage.expectQuestionValue(expectedQuestion)
})

Then('AI assistant returns generated question', async function () {
    await this.questionEditPage.expectQuestionValue(this.aiAssistantGeneratedAnswer)
})

Then('AI assistant returns generated answers with only one correct answer', async function () {
    await this.questionEditPage.expectAnswerText(0, this.aiAssistantGeneratedCorrectAnswer)
    await this.questionEditPage.expectAnswerText(1, this.aiAssistantGeneratedIncorrectAnswer)
    await this.questionEditPage.expectAnswerCorrect(0)
    await this.questionEditPage.expectAnswerIncorrect(1)
})

Then('Question type is set to {string}', async function (value: string) {
    const normalized = value.toLowerCase()
    const expected = normalized.includes('single')
        ? 'single'
        : normalized.includes('multiple')
          ? 'multiple'
          : 'numerical'
    await this.questionEditPage.expectQuestionType(expected)
})

Then('Question field is updated to AI generated question', async function () {
    await this.questionEditPage.expectQuestionValue(this.aiAssistantGeneratedAnswer)
})

Then('answer1 field is filled with AI generated correct answer', async function () {
    await this.questionEditPage.expectAnswerText(0, this.aiAssistantGeneratedCorrectAnswer)
    await this.questionEditPage.expectAnswerCorrect(0)
})

Then('answer2 field is filled with AI generated incorrect answer', async function () {
    await this.questionEditPage.expectAnswerText(1, this.aiAssistantGeneratedIncorrectAnswer)
    await this.questionEditPage.expectAnswerIncorrect(1)
})

// Field edits

When('I enter question {string}', async function (question: string) {
    await enterQuestion(this, question)
})

When('I ask AI: {string}', async function (instructions: string) {
    await enterQuestion(this, instructions)
    if (!this.aiAssistantGeneratedAnswer) {
        await mockDefaultAiAssistant(this)
    }
    await this.questionEditPage.clickAiAssist()
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

When('I enter image URL {string}', async function (imageUrl: string) {
    await enterImageUrl(this, imageUrl)
})

When('I enter an invalid image URL {string}', async function (invalidUrl: string) {
    await this.questionEditPage.enterImageUrl(invalidUrl)
})

When('I clear image URL and enter {string}', async function (imageUrl: string) {
    await this.questionEditPage.clearImageUrl()
    await this.questionEditPage.enterImageUrl(imageUrl)
})

Then('I see image preview', async function () {
    await expect(this.questionEditPage.imagePreviewLocator()).toBeVisible()
})

Then('I do not see image preview', async function () {
    await expect(this.questionEditPage.imagePreviewLocator()).not.toBeVisible()
})

// Save question

When('I attempt to submit the question', submitQuestion)
When('I submit the question', async function () {
    await this.questionEditPage.submit()
    if (this.workspaceGuid && this.questionWip.question) {
        await this.page.waitForURL(`**/workspace/${this.workspaceGuid}`)
        await this.workspacePage.editQuestion(this.questionWip.question)
    }
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
