export type QuizMode = 'learn' | 'exam'
export type Difficulty = 'easy' | 'hard' | 'keep-question'
export type TimeLimitType = 'quiz' | 'question'

export interface Quiz {
    title: string
    description: string
    questionIds: number[]
    mode: QuizMode
    passScore: number
    timeLimit: number
    timeLimitType?: TimeLimitType
    randomQuestionCount?: number
    difficulty?: Difficulty
}

export interface QuizBookmark extends Quiz {
    url: string
}

export const emptyQuiz = (): Quiz => ({
    title: '',
    description: '',
    questionIds: [],
    mode: 'exam',
    passScore: 0,
    timeLimit: 120,
    timeLimitType: 'quiz',
})
export const emptyQuizBookmark = (): QuizBookmark => ({
    url: '',
    title: '',
    description: '',
    questionIds: [],
    mode: 'exam',
    passScore: 0,
    timeLimit: 120,
    timeLimitType: 'quiz',
})
