import { expect } from '@playwright/test'

import type { QuizScorePage } from 'pages'
import type { Answer } from 'steps/world'

export const expectQuizResult = async (
    page: QuizScorePage,
    expectedCorrectAnswers: string,
    expectedTotalQuestions: number,
    expectedPercentage: number,
    expectedTextResult: string,
    expectedPassScore: number,
) => {
    expect(await page.correctAnswers()).toBe(expectedCorrectAnswers)
    expect(await page.totalQuestions()).toBe(expectedTotalQuestions)
    expect(await page.percentageResult()).toBe(expectedPercentage)
    expect(await page.textResult()).toBe(expectedTextResult)
    expect(await page.passScore()).toBe(expectedPassScore)
}

export const expectOriginalResult = async (
    page: QuizScorePage,
    expectedCorrectAnswers: number,
    expectedPercentage: number,
    expectedTextResult: string,
) => {
    expect(await page.firstCorrectAnswers()).toBe(expectedCorrectAnswers)
    expect(await page.firstPercentageResult()).toBe(expectedPercentage)
    expect(await page.firstTextResult()).toBe(expectedTextResult)
}

export const expectOriginalResultNotVisible = async (page: QuizScorePage) => {
    expect(await page.firstCorrectAnswersPresent()).toBe(false)
    expect(await page.firstPercentageResultPresent()).toBe(false)
    expect(await page.firstTextResultPresent()).toBe(false)
}

export const expectAllOptionsForQuestion = async (page: QuizScorePage, question: string, expectedAnswers: Answer[]) => {
    const answers = await page.answers(question)
    expect(answers.length).toBe(expectedAnswers.length)
    for (const answer of expectedAnswers) {
        expect(answers).toContain(answer.answer)
    }
}
