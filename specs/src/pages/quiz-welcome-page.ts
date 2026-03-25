import { expect, type Page } from '@playwright/test'

export class QuizWelcomePage {
    constructor(private page: Page) {}

    private headerLocator = () => this.page.locator('h1')
    private nameLocator = () => this.page.locator('h2#quiz-name')
    private descriptionLocator = () => this.page.locator('p#quiz-description')
    private questionCountLocator = () => this.page.locator('span#question-count')
    private feedbackLocator = () => this.page.locator('span#question-feedback')
    private passScoreLocator = () => this.page.locator('span#pass-score')
    private timeLimitLocator = () => this.page.locator('span#time-limit')

    expectHeader = (text: string) => expect(this.headerLocator()).toHaveText(text)
    expectName = (name: string) => expect(this.nameLocator()).toHaveText(name)
    expectDescription = (description: string) => expect(this.descriptionLocator()).toHaveText(description)
    expectQuestionCount = (count: number) => expect(this.questionCountLocator()).toHaveText(String(count))
    expectFeedback = (feedback: string) => expect(this.feedbackLocator()).toHaveText(feedback)
    expectPassScore = (score: number) => expect(this.passScoreLocator()).toContainText(String(score))
    timeLimit = async () => Number.parseInt((await this.timeLimitLocator().textContent()) ?? '')
    expectTimeLimit = (seconds: string) => expect(this.timeLimitLocator()).toContainText(seconds)

    startButton = () => this.page.locator('button#start')
    start = () => this.startButton().click()
}
