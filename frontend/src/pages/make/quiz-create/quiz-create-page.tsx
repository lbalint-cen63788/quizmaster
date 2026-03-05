import { useState } from 'react'
import { useNavigate } from 'react-router-dom'

import { useApi } from 'api/hooks'
import { fetchWorkspaceQuestions } from 'api/workspace'
import { urls, useWorkspaceId } from 'urls.ts'

import type { QuestionListItem } from 'model/question-list-item.ts'
import { postQuiz } from 'api/quiz'
import { QuizEditForm, type QuizEditFormData } from './quiz-edit-form'
import { tryCatch } from 'helpers'
import { Alert, Page } from 'pages/components'
import { QuizUrl } from './components/quiz-url'

export const QuizCreatePage = () => {
    const workspaceId = useWorkspaceId()
    const navigate = useNavigate()

    const [workspaceQuestions, setWorkspaceQuestions] = useState<readonly QuestionListItem[]>([])
    const [quizId, setQuizId] = useState<string | undefined>(undefined)
    const [errorMessage, setErrorMessage] = useState<string>('')

    useApi(workspaceId, fetchWorkspaceQuestions, setWorkspaceQuestions)

    const onSubmit = (data: QuizEditFormData) =>
        tryCatch(setErrorMessage, async () => {
            const quizId = await postQuiz(data)
            setQuizId(quizId)
            navigate(urls.workspace(workspaceId))
        })

    return (
        <Page title="Create Quiz" id="create-quiz-page">
            <QuizEditForm questions={workspaceQuestions} workspaceId={workspaceId} onSubmit={onSubmit} />

            {errorMessage && <Alert type="error">{errorMessage}</Alert>}
            {quizId && <QuizUrl quizId={quizId} />}
        </Page>
    )
}
