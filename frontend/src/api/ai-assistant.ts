import { postJson } from './helpers.ts'

interface AiAssistantRequest {
    readonly question: string
}

export interface AiAssistantResponse {
    readonly question: string
    readonly correctAnswerText: string
    readonly incorrectAnswerText: string
    readonly correctAnswer?: 'answer1' | 'answer2'
}

export const postAiAssistant = async (question: string) =>
    await postJson<AiAssistantRequest, AiAssistantResponse>('/api/ai-assistant', { question })
