export interface Answer {
    answer: string
    isCorrect: boolean
    explanation: string | undefined
}

export const emptyAnswer = (): Answer => ({ answer: '', isCorrect: false, explanation: undefined })

export interface Question {
    url: string
    editUrl: string
    question: string
    answers: Answer[]
    explanation: string
    imageUrl?: string
}

export const emptyQuestion = (): Question => ({ url: '', editUrl: '', question: '', answers: [], explanation: '' })
