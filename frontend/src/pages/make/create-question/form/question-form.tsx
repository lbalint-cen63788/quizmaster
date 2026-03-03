import { useRef, useState } from 'react'
import { Button, SubmitButton, Form, Field, TextArea, CheckField, Row } from 'pages/components'
import { AnswersEdit, stateToQuestionApiData } from 'pages/make/create-question/form'
import { useQuestionFormState } from './question-form-state'
import { validateQuestionFormState, errorMessage } from './validators.ts'
import type { QuestionApiData } from 'api/question.ts'
import type { Question } from 'model/question.ts'
import { ErrorMessage, createValidator } from 'pages/components/forms/validations.tsx'

interface QuestionEditProps {
    readonly question?: Question
    readonly onSubmit: (questionData: QuestionApiData) => void
}

export const QuestionEditForm = ({ question, onSubmit }: QuestionEditProps) => {
    const state = useQuestionFormState(question)
    const aiAssistDialogRef = useRef<HTMLDialogElement>(null)
    const [aiPrompt, setAiPrompt] = useState('')

    const validator = createValidator(() => validateQuestionFormState(state), errorMessage)

    const handleSubmit = () => onSubmit(stateToQuestionApiData(state))
    const handleAiAssist = () => aiAssistDialogRef.current?.showModal()

    const handleAiGenerate = () => {
        // TODO: Call AI API with aiPrompt to generate question and answers
        aiAssistDialogRef.current?.close()
        setAiPrompt('')
    }

    return (
        <Form id="question-create-form" validator={validator} onSubmit={handleSubmit}>
            <Field label="Question" required>
                <TextArea id="question-text" value={state.questionText} onChange={state.setQuestionText} />
                <Button id="ai-assistent" className="secondary button" onClick={handleAiAssist}>
                    AI assist
                </Button>
                <ErrorMessage errorCode="empty-question" />
            </Field>
            <Row>
                <CheckField
                    id="is-multiple-choice"
                    label="Multiple choice"
                    checked={state.isMultipleChoice}
                    onToggle={state.toggleMultipleChoice}
                />
                {state.isMultipleChoice && (
                    <CheckField
                        id="easy-mode"
                        label="Easy mode"
                        checked={state.easyMode}
                        onToggle={state.setEasyMode}
                    />
                )}
            </Row>
            <AnswersEdit
                setShowExplanations={state.setShowExplanations}
                showExplanations={state.showExplanations}
                answerStates={state.answerStates}
                isMultipleChoice={state.isMultipleChoice}
                addAnswer={state.addAnswer}
                removeAnswer={state.removeAnswer}
            />
            <Field label="Question explanation">
                <TextArea
                    id="question-explanation"
                    value={state.questionExplanation}
                    onChange={state.setQuestionExplanation}
                />
            </Field>
            <Row>
                <SubmitButton />
            </Row>

            <dialog ref={aiAssistDialogRef}>
                <h3>AI assist</h3>
                <Field label="Prompt">
                    <TextArea id="ai-assist-prompt" value={aiPrompt} onChange={setAiPrompt} />
                </Field>
                <Row>
                    <Button id="ai-assist-generate" className="primary button" onClick={handleAiGenerate}>
                        Vygenerovat
                    </Button>
                </Row>
            </dialog>
        </Form>
    )
}
