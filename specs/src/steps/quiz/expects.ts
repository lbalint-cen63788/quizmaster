import { expect } from '@playwright/test'

import type { QuestionPage, QuizScorePage, TakeQuestionPage } from 'pages'
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

export const expectNavigationButtons = async (questionPage: QuestionPage, expectedButtons: string[]) => {
    const buttonLocatorMap: Record<string, () => ReturnType<typeof questionPage.backButtonLocator>> = {
        Back: () => questionPage.backButtonLocator(),
        Next: () => questionPage.nextButtonLocator(),
        Evaluate: () => questionPage.evaluateButtonLocator(),
    }

    for (const name of expectedButtons) {
        const locator = buttonLocatorMap[name]
        if (!locator) throw new Error(`Unknown button: "${name}"`)
        await expect(locator()).toBeVisible()
    }

    await expect(questionPage.navigationButtonsLocator()).toHaveCount(expectedButtons.length)
}

export const expectAnswersChecked = async (takeQuestionPage: TakeQuestionPage, answers: string[], checked: boolean) => {
    for (const answer of answers) {
        if (checked) {
            await expect(takeQuestionPage.answerCheckLocator(answer)).toBeChecked()
        } else {
            await expect(takeQuestionPage.answerCheckLocator(answer)).not.toBeChecked()
        }
    }
}
