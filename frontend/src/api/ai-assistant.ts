import { postJson } from './helpers.ts'
import type { QuestionType } from '../pages/make/create-question/form'

interface AiAssistantRequest {
    readonly type: QuestionType
    readonly question: string
}

export interface AiAssistantResponse {
    readonly type: QuestionType
    readonly question: string
    readonly answers: readonly string[]
    readonly correctAnswers: readonly number[]
}

export const postAiAssistant = async (type: QuestionType, question: string) =>
    await postJson<AiAssistantRequest, AiAssistantResponse>('/api/ai-assistant', { type, question })
