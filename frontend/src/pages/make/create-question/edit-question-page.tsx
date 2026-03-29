import { useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { useApi } from 'api/hooks'
import { fetchWorkspaceQuestion, type QuestionApiData, updateQuestion } from 'api/question.ts'

import { Page } from 'pages/components/page.tsx'
import { QuestionEditForm } from './form/question-form.tsx'
import { LoadedIndicator } from './components.tsx'
import type { Question } from 'model/question.ts'
import { urls, useWorkspaceId } from 'urls.ts'

export function EditQuestionPage() {
    const workspaceId = useWorkspaceId()
    const params = useParams()
    const questionId = params.id || ''

    const [question, setQuestion] = useState<Question | undefined>(undefined)
    const [isLoaded, setIsLoaded] = useState<boolean>(false)

    useApi(
        questionId,
        id => fetchWorkspaceQuestion(workspaceId, id),
        question => {
            setQuestion(question)
            setIsLoaded(true)
        },
    )
    const navigate = useNavigate()

    const handleSubmit = (questionData: QuestionApiData) => {
        updateQuestion(workspaceId, question?.id ?? 0, questionData).then(() => {
            navigate(urls.workspace(workspaceId))
        })
    }

    return (
        <Page title="Edit Question" id="edit-question-page">
            <QuestionEditForm key={question?.id ?? ''} question={question} onSubmit={handleSubmit} />
            <LoadedIndicator isLoaded={isLoaded} />
        </Page>
    )
}
