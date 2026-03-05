import type { Quiz } from 'model/quiz.ts'

import { useState, useEffect } from 'react'
import { useNavigate, useParams } from 'react-router-dom'

import { useApi } from 'api/hooks'
import { fetchWorkspaceQuestions } from 'api/workspace'
import { urls, useWorkspaceId } from 'urls.ts'

import type { QuestionListItem } from 'model/question-list-item.ts'
import { QuizEditForm } from './quiz-edit-form'
import { fetchQuiz, putQuiz, type QuizCreateRequest } from 'api/quiz'
import { Alert, Page } from 'pages/components'

export const QuizEditPage = () => {
    const workspaceId = useWorkspaceId()
    const navigate = useNavigate()
    const { id: quizId } = useParams()

    const [workspaceQuestions, setWorkspaceQuestions] = useState<readonly QuestionListItem[]>([])
    const [quiz, setQuiz] = useState<Quiz | undefined>(undefined)
    const [loading, setLoading] = useState(true)
    const [errorMessage, setErrorMessage] = useState<string>('')

    useApi(workspaceId, fetchWorkspaceQuestions, setWorkspaceQuestions)

    useEffect(() => {
        if (!quizId) {
            setErrorMessage('Quiz ID is missing in URL.')
            setLoading(false)
            return
        }
        setLoading(true)
        fetchQuiz(quizId)
            .then(setQuiz)
            .catch(() => setErrorMessage('Failed to load quiz.'))
            .finally(() => setLoading(false))
    }, [quizId])

    const handleSubmit = (quizData: QuizCreateRequest) => {
        if (!quizId) {
            setErrorMessage('Quiz ID is missing in URL.')
            return
        }
        putQuiz(quizData, quizId).then(() => {
            navigate(urls.workspace(workspaceId))
        })
    }

    return (
        <Page title="Edit Quiz" id="edit-quiz-page">
            {loading ? (
                <div>Loading quiz...</div>
            ) : quiz ? (
                <QuizEditForm
                    questions={workspaceQuestions}
                    quiz={quiz}
                    workspaceId={workspaceId}
                    onSubmit={handleSubmit}
                />
            ) : null}
            {errorMessage && <Alert type="error">{errorMessage}</Alert>}
        </Page>
    )
}
