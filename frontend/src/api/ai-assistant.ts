import { postJson } from './helpers.ts'

interface AiAssistantRequest {
    readonly question: string
}

export interface AiAssistantResponse {
    readonly answer: string
}

export const postAiAssistant = async (question: string) =>
    await postJson<AiAssistantRequest, AiAssistantResponse>('/api/ai-assistant', { question })
