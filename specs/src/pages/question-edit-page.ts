import { expect, type Page } from '@playwright/test'

export class QuestionEditPage {
    constructor(private page: Page) {}

    gotoNew = () => this.page.goto('/question/new', { waitUntil: 'networkidle' })
    gotoEdit = (url: string) => this.page.goto(url, { waitUntil: 'networkidle' })

    private editPageLocator = () => this.page.locator('#edit-question-page')
    private createPageLocator = () => this.page.locator('#create-question-page')
    isEditPage = () => this.editPageLocator().isVisible()
    isCreatePage = () => this.createPageLocator().isVisible()

    private questionLocator = () => this.page.locator('#question-text')
    enterQuestion = (question: string) => this.questionLocator().fill(question)
    questionValue = () => this.questionLocator().inputValue()

    private aiAssistButtonLocator = () => this.page.getByRole('button', { name: /AI assist(ant)?/i })
    clickAiAssist = () => this.aiAssistButtonLocator().click()

    private showExplanationLocator = () => this.page.locator('#show-explanation')
    explanationsEnabled = () => this.showExplanationLocator().isChecked()
    enableExplanations = () => this.showExplanationLocator().check()
    disableExplanations = () => this.showExplanationLocator().uncheck()

    private questionTypeLocator = () => this.page.locator('#question-type')
    questionType = () => this.questionTypeLocator().inputValue()
    isMultipleChoice = async () => (await this.questionType()) === 'multiple'
    isNumericalChoice = async () => (await this.questionType()) === 'numerical'
    setMultipleChoice = () => this.questionTypeLocator().selectOption('multiple')
    setSingleChoice = () => this.questionTypeLocator().selectOption('single')
    setNumericalChoice = () => this.questionTypeLocator().selectOption('numerical')

    private numericalCorrectAnswerLocator = () => this.page.locator('#numerical-correct-answer')
    enterNumericalCorrectAnswer = (value: string) => this.numericalCorrectAnswerLocator().fill(value)
    numericalCorrectAnswerValue = () => this.numericalCorrectAnswerLocator().inputValue()

    private easyModeLocator = () => this.page.locator('#easy-mode')
    isEasyMode = () => this.easyModeLocator().isChecked()
    setEasyMode = () => this.easyModeLocator().check()

    private explanationFieldsLocator = () => this.page.locator('input.explanation')

    private answerRowsLocator = () => this.page.locator('.answer-row')
    answerRowCount = () => this.answerRowsLocator().count()

    private answerRowLocator = (index: number) => this.page.locator('.answer-row').nth(index)
    private answerTextLocator = (index: number) => this.answerRowLocator(index).locator('input.text')
    enterAnswerText = (index: number, value: string) => this.answerTextLocator(index).fill(value)
    answerText = (index: number) => this.answerTextLocator(index).inputValue()

    private answerIsCorrectLocator = (index: number) =>
        this.answerRowLocator(index).locator('input[type="checkbox"], input[type="radio"]')
    isAnswerCorrect = (index: number) => this.answerIsCorrectLocator(index).isChecked()
    setAnswerCorrectness = (index: number, isCorrect: boolean) =>
        this.answerIsCorrectLocator(index).setChecked(isCorrect)

    private answerExplanationLocator = (index: number) => this.answerRowLocator(index).locator('input.explanation')
    answerExplanation = (index: number) => this.answerExplanationLocator(index).inputValue()
    enterAnswerExplanation = (index: number, explanation: string) =>
        this.answerExplanationLocator(index).fill(explanation)

    enterAnswer = async (index: number, value: string, correct: boolean, explanation: string | undefined) => {
        await this.enterAnswerText(index, value)
        await this.setAnswerCorrectness(index, correct)
        if (explanation !== undefined) await this.enterAnswerExplanation(index, explanation)
    }

    private addAnswerButtonLocator = () => this.page.locator('button#add-answer')
    addAdditionalAnswer = async () => {
        const idx = await this.answerRowCount()
        await this.addAnswerButtonLocator().click()
        await this.answerRowLocator(idx).waitFor({ state: 'visible' })
    }

    private imageUrlLocator = () => this.page.locator('#image-url')
    enterImageUrl = (url: string) => this.imageUrlLocator().fill(url)

    imagePreviewLocator = () => this.page.locator('img.image-preview')

    private questionExplanationLocator = () => this.page.locator('#question-explanation')
    enterQuestionExplanation = (question: string) => this.questionExplanationLocator().fill(question)
    questionExplanation = () => this.questionExplanationLocator().inputValue()

    submit = () => this.page.locator('button[type="submit"]').click()

    private backButtonLocator = () => this.page.locator('button#back')
    back = () => this.backButtonLocator().click()

    private questionUrlLocator = () => this.page.locator('#question-link')
    questionUrl = () => this.questionUrlLocator().textContent()

    private questionEditUrlLocator = () => this.page.locator('#question-edit-link')
    questionEditUrl = () => this.questionEditUrlLocator().textContent()

    errorsLocator = () => this.page.locator('.alert.error')
    hasError = (error: string) => this.page.getByTestId(error).waitFor({ state: 'visible' })

    answerDeleteButtonsLocator = () => this.page.locator('.trash-button')
    private answerDeleteButtonLocator = (idx: number) => this.answerDeleteButtonsLocator().nth(idx)
    deleteAnswer = (index: number) => this.answerDeleteButtonLocator(index).click()

    // Retrying assertions
    expectEditPageVisible = () => expect(this.editPageLocator()).toBeVisible()
    expectCreatePageVisible = () => expect(this.createPageLocator()).toBeVisible()
    expectQuestionValue = (value: string) => expect(this.questionLocator()).toHaveValue(value)
    expectQuestionType = (type: string) => expect(this.questionTypeLocator()).toHaveValue(type)
    expectExplanationsChecked = () => expect(this.showExplanationLocator()).toBeChecked()
    expectExplanationsUnchecked = () => expect(this.showExplanationLocator()).not.toBeChecked()
    expectNumericalAnswerVisible = () => expect(this.numericalCorrectAnswerLocator()).toBeVisible()
    expectNumericalAnswerNotVisible = () => expect(this.numericalCorrectAnswerLocator()).not.toBeVisible()
    expectNumericalCorrectAnswer = (value: string) => expect(this.numericalCorrectAnswerLocator()).toHaveValue(value)
    expectEasyModeChecked = () => expect(this.easyModeLocator()).toBeChecked()
    expectEasyModeUnchecked = () => expect(this.easyModeLocator()).not.toBeChecked()
    expectEasyModeVisible = () => expect(this.easyModeLocator()).toBeVisible()
    expectEasyModeNotVisible = () => expect(this.easyModeLocator()).not.toBeVisible()
    expectExplanationFieldsExist = () => expect(this.explanationFieldsLocator().first()).toBeVisible()
    expectNoExplanationFields = () => expect(this.explanationFieldsLocator()).toHaveCount(0)
    expectAnswerRowCount = (count: number) => expect(this.answerRowsLocator()).toHaveCount(count)
    expectAnswerText = (index: number, value: string) => expect(this.answerTextLocator(index)).toHaveValue(value)
    expectAnswerCorrect = (index: number) => expect(this.answerIsCorrectLocator(index)).toBeChecked()
    expectAnswerIncorrect = (index: number) => expect(this.answerIsCorrectLocator(index)).not.toBeChecked()
    expectAnswerExplanation = (index: number, value: string) =>
        expect(this.answerExplanationLocator(index)).toHaveValue(value)
    expectQuestionExplanation = (value: string) => expect(this.questionExplanationLocator()).toHaveValue(value)
    expectAddAnswerNotVisible = () => expect(this.addAnswerButtonLocator()).not.toBeVisible()
    expectErrorCount = (n: number) => expect(this.errorsLocator()).toHaveCount(n)
}
