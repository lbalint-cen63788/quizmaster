import './create-question.scss'
import { useNavigate } from 'react-router-dom'
import { type QuestionApiData, saveQuestion } from 'api/question.ts'

import { Page } from 'pages/components/page.tsx'
import { QuestionEditForm } from './form/question-form.tsx'
import { urls, useWorkspaceId } from 'urls.ts'

export function CreateQuestionPage() {
    const workspaceId = useWorkspaceId()
    const navigate = useNavigate()

    const handleSubmit = (questionData: QuestionApiData) => {
        const apiData = { ...questionData, workspaceGuid: workspaceId || null }
        saveQuestion(apiData).then(response => {
            const url = workspaceId ? urls.workspace(workspaceId) : urls.questionEditStandalone(response.editId)
            navigate(url)
        })
    }

    const handleBack = () => {
        navigate(workspaceId ? urls.workspace(workspaceId) : urls.home())
    }

    return (
        <Page title="Create Question" id="create-question-page">
            <QuestionEditForm onSubmit={handleSubmit} onBack={handleBack} />
        </Page>
    )
}
