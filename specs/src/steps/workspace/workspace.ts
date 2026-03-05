import { Given, Then, When } from 'steps/fixture.ts'
import type { QuizmasterWorld } from 'steps/world'

const createWorkspace = async (world: QuizmasterWorld, title: string) => {
    await world.workspaceCreatePage.gotoNew()
    await world.workspaceCreatePage.enterWorkspaceName(title)
    await world.workspaceCreatePage.submit()
}

Given('I saved the workspace {string}', async function (workspaceTitle: string) {
    await createWorkspace(this, workspaceTitle)
})

Then('I see the {string} workspace page', async function (name: string) {
    await this.workspacePage.expectWorkspaceName(name)
})

Then('I see an empty workspace', async function () {
    await this.workspacePage.expectQuestionCount(0)
})

Then('I see question in list {string}', async function (question: string) {
    await this.workspacePage.expectQuestionVisible(question)
})

Then('I see workspace title {string}', async function (title: string) {
    await this.workspacePage.expectWorkspaceName(title)
})

When('I take question {string} from the list', async function (question: string) {
    this.activeQuestionBookmark = question
    await this.workspacePage.takeQuestion(question)
})

When('I delete question {string} from the list', async function (question: string) {
    this.activeQuestionBookmark = question
    await this.workspacePage.deleteQuestion(question)
})

Then('I cannot delete question {string}', async function (question: string) {
    await this.workspacePage.expectDeleteButtonNotVisible(question)
})

When('I edit question {string} from the list', async function (question: string) {
    this.activeQuestionBookmark = question
    await this.workspacePage.editQuestion(question)
})

Then(/I copy the (take|edit) URL for question "(.+)"/, async function (page: string, question: string) {
    this.activeQuestionBookmark = question

    if (page === 'take') await this.workspacePage.copyTakeQuestion(question)
    else if (page === 'edit') await this.workspacePage.copyEditQuestion(question)
})

Then('I see image thumbnail for question {string}', async function (question: string) {
    await this.workspacePage.expectQuestionThumbnailVisible(question)
})

Then('I do not see image thumbnail for question {string}', async function (question: string) {
    await this.workspacePage.expectQuestionThumbnailNotVisible(question)
})

Then('I see the quiz {string} in the workspace', async function (quizName: string) {
    await this.workspacePage.expectQuizVisible(quizName)
})

Then('I take quiz {string}', async function (quiz: string) {
    await this.workspacePage.takeQuiz(quiz)
})

When('I open stats for quiz {string}', async function (quizName: string) {
    await this.workspacePage.statsQuiz(quizName)
})
