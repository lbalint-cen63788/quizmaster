import { useState } from 'react'
import { useNavigate } from 'react-router-dom'

import { useApi } from 'api/hooks'
import { fetchWorkspaceQuestions } from 'api/workspace'
import { useWorkspaceGuid } from 'urls.ts'

import type { QuestionListItem } from 'model/question-list-item.ts'
import { QuizEditForm } from './quiz-edit-form'
import { tryCatch } from 'helpers'
import { Alert, Page } from 'pages/components'

export const QuizEditPage = () => {
    const workspaceGuid = useWorkspaceGuid()
    const navigate = useNavigate()

    const [workspaceQuestions, setWorkspaceQuestions] = useState<readonly QuestionListItem[]>([])
    const [errorMessage, setErrorMessage] = useState<string>('')

    useApi(workspaceGuid, fetchWorkspaceQuestions, setWorkspaceQuestions)

    const onSubmit = () =>
        tryCatch(setErrorMessage, async () => {
            // zatím pouze navigace zpět
            if (workspaceGuid) {
                navigate(`/workspace/${workspaceGuid}`)
            }
        })

    const quiz = {
        id: 1,
        title: 'Sample Quiz',
        description: 'This is a sample quiz for editing.',
        questions: [],
        mode: 'exam' as const,
        passScore: 70,
        timeLimit: 900,
        size: 10,
    }

    return (
        <Page title="Edit Quiz" id="edit-quiz-page">
            <QuizEditForm questions={workspaceQuestions} quiz={quiz} onSubmit={onSubmit} />
            {errorMessage && <Alert type="error">{errorMessage}</Alert>}
        </Page>
    )
}
