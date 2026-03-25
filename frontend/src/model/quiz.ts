import type { Question } from './question.ts'

export type QuizMode = 'learn' | 'exam'
export type Difficulty = 'easy' | 'hard' | 'keep-question'
export type TimeLimitType = 'quiz' | 'question'

export interface Quiz {
    readonly id: number
    readonly title: string
    readonly description: string
    readonly questions: readonly Question[]
    readonly mode: QuizMode
    readonly difficulty?: Difficulty
    readonly passScore: number
    readonly timeLimit: number
    readonly timeLimitType: TimeLimitType
    readonly randomQuestionCount?: number
}
