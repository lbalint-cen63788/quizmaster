import type { TableOf } from 'steps/common.ts'
import {
    addAnswers,
    type AnswerRaw,
    enterImageUrl,
    enterQuestion,
    enterQuestionExplanation,
} from 'steps/question/ops.ts'
import { emptyQuestion, type QuizmasterWorld } from 'steps/world'

export const openCreateWorkspacePage = async (world: QuizmasterWorld) => {
    world.workspaceCreatePage.gotoNew()
}

export const createWorkspace = async (world: QuizmasterWorld, name: string) => {
    await openCreateWorkspacePage(world)
    await world.workspaceCreatePage.enterWorkspaceName(name)
    await world.workspaceCreatePage.submit()
}

export const createQuestionInWorkspace = async (
    world: QuizmasterWorld,
    bookmark: string,
    question: string,
    answerRawTable: TableOf<AnswerRaw>,
    isEasy?: boolean,
    explanation?: string,
    imageUrl?: string,
) => {
    await world.workspacePage.createNewQuestion()
    world.questionWip = emptyQuestion()
    await enterQuestion(world, question)
    await addAnswers(world, answerRawTable)
    if (isEasy) {
        await world.questionEditPage.setEasyMode()
    }
    if (explanation) {
        await enterQuestionExplanation(world, explanation)
    }
    if (imageUrl) {
        await enterImageUrl(world, imageUrl)
    }
    world.questionBookmarks[bookmark] = world.questionWip
    await world.questionEditPage.submit()
}
