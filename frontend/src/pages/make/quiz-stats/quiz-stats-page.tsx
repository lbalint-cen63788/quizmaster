import { useState } from 'react'
import { useParams } from 'react-router-dom'

import type { Quiz } from 'model/quiz.ts'
import { useApi } from 'api/hooks.ts'
import { fetchQuiz } from 'api/quiz.ts'
import { QuizStats } from './quiz-stats-component.tsx'

export const QuizStatsPage = () => {
    const params = useParams()
    const [quiz, setQuiz] = useState<Quiz>()

    useApi(params.id, fetchQuiz, setQuiz)

    return quiz && <QuizStats quiz={quiz} />
}
