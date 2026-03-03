import type { QuizmasterWorld } from 'steps/world'

export const openQuiz = async (world: QuizmasterWorld, quizId: string) => {
    const quizUrl = world.quizBookmarks[quizId]?.url || `/quiz/${quizId}`
    await world.page.goto(quizUrl)
}

export const startQuiz = async (world: QuizmasterWorld, quizId: string) => {
    await openQuiz(world, quizId)
    await world.quizWelcomePage.start()
}

export const answerNth = async (world: QuizmasterWorld, n: number) => {
    await world.takeQuestionPage.selectAnswerNth(n)
    await world.takeQuestionPage.submit()
}

export const answerCorrectly = async (world: QuizmasterWorld) => answerNth(world, 0)
export const answerIncorrectly = async (world: QuizmasterWorld) => answerNth(world, 1)

export const repeatAsync = async (n: number, fn: () => Promise<void>) => {
    for (let i = 0; i < n; i++) await fn()
}
