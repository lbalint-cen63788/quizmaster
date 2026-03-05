import { expect, type Page } from '@playwright/test'

export class WorkspacePage {
    constructor(private page: Page) {}

    // ── Navigation ───────────────────────────────────

    goto = (guid: string) => this.page.goto(`/workspace/${guid}`, { waitUntil: 'networkidle' })

    // ── Workspace name ───────────────────────────────

    private workspaceNameLocator = () => this.page.getByTestId('workspace-title')

    expectWorkspaceName = (name: string) => expect(this.workspaceNameLocator()).toHaveText(name)

    // ── Question list ────────────────────────────────

    private questionsLocator = () => this.page.locator('.question-item')
    private questionLocator = (question: string) => this.questionsLocator().filter({ hasText: question })

    expectQuestionCount = (count: number) => expect(this.questionsLocator()).toHaveCount(count)
    expectQuestionVisible = (question: string) => expect(this.questionLocator(question)).toBeVisible()

    // ── Question actions ─────────────────────────────

    private questionButton = (selector: string) => (question: string) => this.questionLocator(question).locator(`${selector} button`)
    private click = (locator: (question: string) => ReturnType<typeof this.questionLocator>) => (question: string) => locator(question).click()

    private takeButtonLocator = this.questionButton('.take-button')
    takeQuestion = this.click(this.takeButtonLocator)

    private editButtonLocator = this.questionButton('.edit-button')
    editQuestion = this.click(this.editButtonLocator)

    private copyTakeButtonLocator = this.questionButton('.copy-take-button')
    copyTakeQuestion = this.click(this.copyTakeButtonLocator)

    private copyEditButtonLocator = this.questionButton('.copy-edit-button')
    copyEditQuestion = this.click(this.copyEditButtonLocator)

    private deleteButtonLocator = this.questionButton('.delete-button')
    deleteQuestion = async (question: string) => {
        await this.deleteButtonLocator(question).click()
        await this.questionLocator(question).waitFor({ state: 'hidden' })
    }
    expectDeleteButtonNotVisible = (question: string) => expect(this.deleteButtonLocator(question)).not.toBeVisible()

    // ── Question thumbnail ───────────────────────────

    private questionThumbnailLocator = (question: string) =>
        this.questionLocator(question).locator('img.question-thumbnail')
    expectQuestionThumbnailVisible = (question: string) => expect(this.questionThumbnailLocator(question)).toBeVisible()
    expectQuestionThumbnailNotVisible = (question: string) =>
        expect(this.questionThumbnailLocator(question)).not.toBeVisible()

    // ── Create new question / quiz ───────────────────

    private createQuestionButtonLocator = () => this.page.locator('#create-question')
    createNewQuestion = () => this.createQuestionButtonLocator().click()

    private createQuizButtonLocator = () => this.page.locator('#create-quiz')
    createNewQuiz = () => this.createQuizButtonLocator().click()

    // ── Quiz list ────────────────────────────────────

    private quizLocator = (quiz: string) => this.page.locator('.quiz-item').filter({ hasText: quiz })

    private takeQuizButtonLocator = (quiz: string) => this.quizLocator(quiz).locator('.take-quiz-button button')
    takeQuiz = (quiz: string) => this.takeQuizButtonLocator(quiz).click()

    private editQuizButtonLocator = (quiz: string) => this.quizLocator(quiz).locator('.edit-quiz-button button')
    editQuiz = (quiz: string) => this.editQuizButtonLocator(quiz).click()

    private statsQuizButtonLocator = (quiz: string) => this.quizLocator(quiz).locator('.stats-quiz-button button')
    statsQuiz = (quiz: string) => this.statsQuizButtonLocator(quiz).click()

    expectQuizVisible = (quiz: string) => expect(this.quizLocator(quiz)).toBeVisible()
}
