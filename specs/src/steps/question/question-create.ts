import type { DataTable } from '@cucumber/cucumber'

import type { TableOf } from 'steps/common.ts'
import { Given } from 'steps/fixture.ts'
import {
    addAnswers,
    createQuestion,
    enterImageUrl,
    enterQuestion,
    openCreatePage,
    saveQuestion,
    type AnswerRaw,
} from 'steps/question/ops.ts'

Given('a question {string}', async function (question: string) {
    await openCreatePage(this)
    await enterQuestion(this, question)
})

Given(
    'a question {string} bookmarked as {string}',
    async function (question: string, bookmark: string, answerRawTable: TableOf<AnswerRaw>) {
        await createQuestion(this, bookmark, question, false, answerRawTable)
    },
)

Given(
    'a numerical question {string} with correct answer {string} bookmarked as {string}',
    async function (question: string, correctAnswer: string, bookmark: string) {
        await openCreatePage(this)
        await enterQuestion(this, question)
        await this.questionEditPage.setNumericalChoice()
        await this.questionEditPage.enterNumericalCorrectAnswer(correctAnswer)
        await saveQuestion(this, bookmark)
    },
)

Given('questions', async function (data: DataTable) {
    for (const row of data.hashes()) {
        const { bookmark, question, answers, easy, explanation } = row
        const isEasy = easy === 'true'
        const answerRawTable = {
            raw: () =>
                answers.split(',').map(a => {
                    const [answer, correct] = a.trim().split(' ')
                    return [answer, correct === '(*)' ? '*' : '', undefined]
                }),
        } as TableOf<AnswerRaw>

        await createQuestion(this, bookmark, question, isEasy, answerRawTable, explanation)
    }
})

Given('with image {string}', async function (imageUrl: string) {
    await enterImageUrl(this, imageUrl)
})

Given('with answers:', async function (answerRawTable: TableOf<AnswerRaw>) {
    await this.questionEditPage.enableExplanations()
    await addAnswers(this, answerRawTable)
})

Given('with explanation {string}', async function (explanation: string) {
    await this.questionEditPage.enableExplanations()
    await this.questionEditPage.enterQuestionExplanation(explanation)
    this.questionWip.explanation = explanation
})

Given('marked as easy mode', async function () {
    await this.questionEditPage.setEasyMode()
})

Given('saved and bookmarked as {string}', async function (bookmark) {
    await saveQuestion(this, bookmark)
})
