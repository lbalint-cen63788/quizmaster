import { useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { useApi } from 'api/hooks'
import { fetchQuestion, fetchQuestionByEditId, type QuestionApiData, updateQuestion } from 'api/question.ts'

import { Page } from 'pages/components/page.tsx'
import { QuestionEditForm } from './form/question-form.tsx'
import { LoadedIndicator, QuestionEditLink, QuestionLink } from './components.tsx'
import type { Question } from 'model/question.ts'
import { urls } from 'urls.ts'

export function EditQuestionPage() {
    const params = useParams()
    const workspaceId = params.workspaceId || ''
    const paramId = params.id || params.editId || ''

    const [question, setQuestion] = useState<Question | undefined>(undefined)
    const [editId, setEditId] = useState<string>('')

    const [isLoaded, setIsLoaded] = useState<boolean>(false)
    const [linkToQuestion, setLinkToQuestion] = useState<string>('')
    const [linkToEditQuestion, setLinkToEditQuestion] = useState<string>('')

    const fetchFn = workspaceId ? fetchQuestion : fetchQuestionByEditId

    useApi(paramId, fetchFn, question => {
        setQuestion(question)
        setEditId(question.editId)
        if (!workspaceId) {
            setLinkToQuestion(`${location.origin}/question/${question.id}`)
            setLinkToEditQuestion(`${location.origin}/question/${paramId}/edit`)
        }
        setIsLoaded(true)
    })
    const navigate = useNavigate()

    const handleSubmit = (questionData: QuestionApiData) => {
        updateQuestion(questionData, editId || paramId).then(() => {
            navigate(workspaceId ? urls.workspace(workspaceId) : urls.workspace(questionData.workspaceGuid || ''))
        })
    }

    return (
        <Page title="Edit Question" id="edit-question-page">
            <QuestionEditForm key={question?.editId || ''} question={question} onSubmit={handleSubmit} />
            {!workspaceId && <QuestionLink url={linkToQuestion} />}
            {!workspaceId && <QuestionEditLink editUrl={linkToEditQuestion} />}
            <LoadedIndicator isLoaded={isLoaded} />
        </Page>
    )
}
