import { useState, useEffect } from 'react'

import { useQuizApi } from './hooks.ts'

import { QuizScorePage } from './quiz-score-page.tsx'
import { QuestionForm } from './quiz.tsx'
import type { QuizAnswers } from './quiz-answers-state.ts'
import { useNavigate } from 'react-router-dom'
import { updateAttempt } from 'api/stats.ts'
import { getQuizRunId } from 'helpers.ts'
import { evaluate } from './quiz-score.ts'
import { AttemptStatus } from 'model/stats.ts'

export const QuizTakePage = () => {
    const quiz = useQuizApi()
    const [quizAnswers, setQuizAnswers] = useState<QuizAnswers | null>(null)
    const [timedOut, setTimedOut] = useState(false)
    const navigate = useNavigate()

    useEffect(() => {
        const stored = sessionStorage.getItem('quizAnswers')
        if (stored) {
            setQuizAnswers(JSON.parse(stored))
        }
    }, [])

    function updateSessionStorage(answers: QuizAnswers | null) {
        if (answers !== null) {
            sessionStorage.setItem('quizAnswers', JSON.stringify(answers))
        } else {
            sessionStorage.removeItem('quizAnswers')
        }
    }

    async function handleEvaluate(answers: QuizAnswers | null, isTimedOut = false) {
        navigate(`/quiz/${quiz?.id}/questions`)
        updateSessionStorage(answers)
        setQuizAnswers(answers)
        setTimedOut(isTimedOut)

        if (!quiz || !answers) return

        const evaluation = evaluate(quiz, answers)
        const score = Math.round((evaluation.score / evaluation.total) * 100)
        const points = evaluation.score
        const maxScore = evaluation.total
        const attemptId = getQuizRunId()

        // Get start time from sessionStorage to avoid timezone issues
        const startTimeMs = sessionStorage.getItem('quizStartTime')
        const endTime = new Date()

        if (startTimeMs) {
            const durationSeconds = Math.round((endTime.getTime() - Number.parseInt(startTimeMs)) / 1000)

            // Determine status based on timeout and pass score
            let status: AttemptStatus
            if (isTimedOut) {
                status = AttemptStatus.TIMEOUT
            } else if (score >= quiz.passScore) {
                status = AttemptStatus.SUCCESS
            } else {
                status = AttemptStatus.FAILED
            }

            await updateAttempt(attemptId, {
                quizId: quiz.id,
                durationSeconds,
                points,
                score,
                status,
                maxScore,
                startedAt: new Date(Number.parseInt(startTimeMs)).toISOString(),
                finishedAt: endTime.toISOString(),
            })

            // Clean up
            sessionStorage.removeItem('quizStartTime')
        }
    }

    if (quiz) {
        return quizAnswers ? (
            <QuizScorePage quiz={quiz} quizAnswers={quizAnswers} timedOut={timedOut} />
        ) : (
            <QuestionForm quiz={quiz} onEvaluate={handleEvaluate} />
        )
    }
}
