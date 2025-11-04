import './create-question.scss'
import { useState } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import { type QuestionApiData, saveQuestion } from 'api/question.ts'

import { emptyQuestionFormData, QuestionEditForm, toQuestionApiData } from './form'
import { ErrorMessages, type ErrorCodes } from './form/error-message'
import { validateQuestionFormData } from './validators'
import { LoadedIndicator, QuestionEditLink, QuestionLink } from './components.tsx'

export function CreateQuestionContainer() {
    const [searchParams] = useSearchParams()
    const workspaceGuid = searchParams.get('workspaceguid') ? searchParams.get('workspaceguid') : ''
    const navigate = useNavigate()

    const [questionData, setQuestionData] = useState(emptyQuestionFormData())
    const [linkToQuestion, setLinkToQuestion] = useState<string>('')
    const [linkToEditQuestion, setLinkToEditQuestion] = useState<string>('')

    const [errors, setErrors] = useState<ErrorCodes>(new Set())
    let editUrl = ''

    const postData = async (formData: QuestionApiData) => {
        await saveQuestion(formData)
            .then(response => {
                setLinkToQuestion(`${location.origin}/question/${response.id}`)
                setLinkToEditQuestion(`${location.origin}/question/${response.editId}/edit`)
                editUrl = `/question/${response.editId}/edit`
            })
            .catch(error => setLinkToQuestion(error.message))
    }

    const handleSubmit = () => {
        const errors = validateQuestionFormData(questionData)
        setErrors(errors)
        if (errors.size > 0) {
            return
        }
        const apiData = toQuestionApiData(questionData)

        if (workspaceGuid !== '') {
            apiData.workspaceGuid = workspaceGuid
        }

        postData(apiData).then(() => {
            if (workspaceGuid !== '') {
                //to be refactored we should not wait post data to finish
                navigate(`/workspace/${workspaceGuid}`)
            } else {
                navigate(editUrl)
            }
        })
    }

    const handleQuestionDelete = () => ({})

    return (
        <>
            <h1>Create Question</h1>
            <div className="question-page">
                <QuestionEditForm
                    questionData={questionData}
                    setQuestionData={setQuestionData}
                    onSubmit={handleSubmit}
                    isEdit={false}
                    handleQuestionDelete={handleQuestionDelete}
                />
                <ErrorMessages errorCodes={errors} />
                <QuestionLink url={linkToQuestion} />
                <QuestionEditLink editUrl={linkToEditQuestion} />
                <LoadedIndicator isLoaded={true} />
            </div>
        </>
    )
}
