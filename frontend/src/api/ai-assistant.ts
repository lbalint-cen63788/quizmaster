import { postJson } from './helpers.ts'

interface AiAssistantRequest {
    readonly question: string
}

export interface AiAssistantResponse {
    readonly question: string
    readonly answers: readonly string[]
    readonly correctAnswers: readonly number[]
}

export const postAiAssistant = async (question: string) =>
    await postJson<AiAssistantRequest, AiAssistantResponse>('/api/ai-assistant', { question })
