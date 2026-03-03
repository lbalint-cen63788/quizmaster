import { fetchJson, postJson, putJson } from './helpers.ts'
import type { Quiz, QuizMode, Difficulty } from 'model/quiz.ts'

export interface QuizCreateRequest {
    readonly title: string
    readonly description: string
    readonly questionIds: readonly number[]
    readonly mode: QuizMode
    readonly difficulty?: Difficulty
    readonly passScore: number
    readonly timeLimit: number
    readonly workspaceGuid: string | null
    readonly randomQuestionCount?: number
}

export const fetchQuiz = async (quizId: string) => await fetchJson<Quiz>(`/api/quiz/${quizId}`)

interface QuizCreateResponse {
    readonly id: number
}

export const putQuiz = async (quiz: QuizCreateRequest, id: string) => {
    await putJson<QuizCreateRequest, QuizCreateResponse>(`/api/quiz/${id}`, quiz)
}

export const postQuiz = async (quiz: QuizCreateRequest) => {
    const response = await postJson<QuizCreateRequest, QuizCreateResponse>('/api/quiz', quiz)
    return String(response.id)
}
