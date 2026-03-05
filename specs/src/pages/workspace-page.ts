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

    takeQuestion = (question: string) => this.questionLocator(question).getByRole('link', { name: 'Take' }).click()
    editQuestion = (question: string) => this.questionLocator(question).getByRole('link', { name: 'Edit' }).click()

    private deleteButtonLocator = (question: string) =>
        this.questionLocator(question).getByRole('button', { name: 'Delete' })
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

    createNewQuestion = () => this.page.locator('#create-question').click()
    createNewQuiz = () => this.page.locator('#create-quiz').click()

    // ── Quiz list ────────────────────────────────────

    private quizLocator = (quiz: string) => this.page.locator('.quiz-item').filter({ hasText: quiz })

    takeQuiz = (quiz: string) => this.quizLocator(quiz).getByRole('link', { name: 'Take' }).click()
    editQuiz = (quiz: string) => this.quizLocator(quiz).getByRole('link', { name: 'Edit' }).click()
    statsQuiz = (quiz: string) => this.quizLocator(quiz).getByRole('link', { name: 'Statistics' }).click()

    expectQuizVisible = (quiz: string) => expect(this.quizLocator(quiz)).toBeVisible()
}
