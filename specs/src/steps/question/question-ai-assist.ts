import { expect } from '@playwright/test'

import { Given, When, Then } from 'steps/fixture.ts'
import { emptyAnswer, emptyQuestion } from 'steps/world/question.ts'

Given('I open question create page for workspace {string}', async function (workspaceGuid: string) {
    await this.page.goto(`/question/new?workspaceguid=${workspaceGuid}`, { waitUntil: 'networkidle' })
    this.questionWip = emptyQuestion()
})

Then('I see AI assist button', async function () {
    await expect(this.questionEditPage.aiAssistButtonLocator()).toBeVisible()
})

When('I click AI assist button', async function () {
    await this.questionEditPage.clickAiAssistButton()
})

Then('I see AI assist popup', async function () {
    await expect(this.questionEditPage.aiAssistDialogLocator()).toBeVisible()
})

When('I fill AI prompt {string}', async function (prompt: string) {
    await this.questionEditPage.fillAiAssistPrompt(prompt)
})

When('I click AI generate button', async function () {
    await this.questionEditPage.clickAiAssistGenerateButton()
})

Then('AI assist popup is closed', async function () {
    await expect(this.questionEditPage.aiAssistDialogLocator()).toBeHidden()
})

Then('I see the form prefilled with generated question and answers', async function () {
    const questionText = await this.questionEditPage.questionValue()
    expect(questionText).not.toBe('')

    const answerCount = await this.questionEditPage.answerRowCount()
    expect(answerCount).toBe(4)

    let correctCount = 0
    const answers = []
    for (let i = 0; i < answerCount; i++) {
        const answerText = await this.questionEditPage.answerText(i)
        expect(answerText).not.toBe('')

        const isCorrect = await this.questionEditPage.isAnswerCorrect(i)
        if (isCorrect) correctCount++

        answers.push({
            ...emptyAnswer(),
            answer: answerText,
            isCorrect,
        })
    }

    expect(correctCount).toBe(1)

    this.questionWip.question = questionText
    this.questionWip.answers = answers
})

Then('I see the generated question in the workspace list', async function () {
    await this.page.waitForURL(/\/workspace\//)
    await expect(this.page.getByText(this.questionWip.question)).toBeVisible()
})
