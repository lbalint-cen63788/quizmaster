import { useState, useEffect } from 'react'

import { useQuizApi } from './hooks.ts'

import { QuizScorePage } from './quiz-score-page.tsx'
import { QuestionForm } from './quiz.tsx'
import type { QuizAnswers } from './quiz-answers-state.ts'

export const QuizTakePage = () => {
    const quiz = useQuizApi()
    const [quizAnswers, setQuizAnswers] = useState<QuizAnswers | null>(null)

    useEffect(() => {
        const stored = sessionStorage.getItem('quizAnswers')
        if (stored) {
            setQuizAnswers(JSON.parse(stored))
        }
    }, [])

    function updateSessionStorage(answers: any) {
        if (answers !== null) {
            sessionStorage.setItem('quizAnswers', JSON.stringify(answers));
        } else {
            sessionStorage.removeItem('quizAnswers');
        }
    }
    function handleEvaluate(answers: any) {
        updateSessionStorage(answers);
        setQuizAnswers(answers);
    }

    if (quiz) {
        return quizAnswers ? (
            <QuizScorePage quiz={quiz} quizAnswers={quizAnswers} />
        ) : (
            <QuestionForm quiz={quiz} onEvaluate={handleEvaluate} />
        );
    }
}
