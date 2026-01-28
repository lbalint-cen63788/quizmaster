import './test-numerical-question.scss'

import type { FormEvent } from 'react'
import { useEffect, useState } from 'react'

import { fetchTestNumericalQuestion, type TestNumericalQuestion } from 'api/question.ts'

export const TestNumericalQuestionPage = () => {
    const [questionData, setQuestionData] = useState<TestNumericalQuestion | null>(null)
    const [answer, setAnswer] = useState('')
    const [feedback, setFeedback] = useState<string | null>(null)
    const [loadError, setLoadError] = useState<string | null>(null)

    useEffect(() => {
        fetchTestNumericalQuestion()
            .then(data => {
                setQuestionData(data)
                setFeedback(null)
                setAnswer('')
            })
            .catch(() => setLoadError('Unable to load the numerical question.'))
    }, [])

    const onSubmit = (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault()
        if (!questionData) return
        const trimmed = answer.trim()
        if (!trimmed) return

        setFeedback(trimmed === questionData.correctAnswer ? 'Correct!' : 'Incorrect!')
    }

    if (loadError) {
        return (
            <main className="test-numerical-question">
                <p>{loadError}</p>
            </main>
        )
    }

    if (!questionData) {
        return (
            <main className="test-numerical-question">
                <p>Loading question...</p>
            </main>
        )
    }

    return (
        <main className="test-numerical-question">
            <h1 data-testid="question-title">{questionData.question}</h1>
            <form onSubmit={onSubmit}>
                <label htmlFor="answer">Your answer</label>
                <input id="answer" type="number" value={answer} onChange={event => setAnswer(event.target.value)} />
                <button id="submit-answer" type="submit">
                    Submit
                </button>
            </form>

            {feedback && <p className="question-feedback">{feedback}</p>}
        </main>
    )
}
