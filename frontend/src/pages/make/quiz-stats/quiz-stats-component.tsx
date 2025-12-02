import type { Quiz } from 'model/quiz.ts'

export interface QuizStatsProps {
    readonly quiz: Quiz
}

export const QuizStats = ({ quiz }: QuizStatsProps) => (
    <h2>Statistics for quiz: {quiz.title}</h2>
)
