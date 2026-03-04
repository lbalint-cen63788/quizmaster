import { useState } from 'react'

import { postAiAssistant } from 'api/ai-assistant.ts'
import { SubmitButton, Form, Field, TextArea, TextInput, CheckField, Row, Button, Alert } from 'pages/components'
import { AnswersEdit, stateToQuestionApiData } from 'pages/make/create-question/form'
import { useQuestionFormState } from './question-form-state'
import { validateQuestionFormState, errorMessage } from './validators.ts'
import type { QuestionApiData } from 'api/question.ts'
import type { Question } from 'model/question.ts'
import { ErrorMessage, createValidator } from 'pages/components/forms/validations.tsx'

interface QuestionEditProps {
    readonly question?: Question
    readonly onSubmit: (questionData: QuestionApiData) => void
    readonly onBack?: () => void
    readonly onAiAssistantClick?: (instructions: string) => void
}

export const QuestionEditForm = ({ question, onSubmit, onBack, onAiAssistantClick }: QuestionEditProps) => {
    const state = useQuestionFormState(question)
    const [aiLoading, setAiLoading] = useState(false)
    const [aiError, setAiError] = useState('')
    const [aiGenerated, setAiGenerated] = useState(false)

    const validator = createValidator(() => validateQuestionFormState(state), errorMessage)

    const handleSubmit = () =>
        onSubmit({
            ...stateToQuestionApiData(state),
            aiGenerated,
            questionType: state.questionType,
        })

    const handleAiAssistantClick = async () => {
        setAiError('')
        setAiLoading(true)
        setAiGenerated(false)

        try {
            if (onAiAssistantClick) {
                onAiAssistantClick(state.questionText)
                return
            }
            const response = await postAiAssistant(state.questionText)
            const correctFirst = response.correctAnswer !== 'answer2'
            state.applyAiSingleChoice(
                response.question,
                correctFirst ? response.correctAnswerText : response.incorrectAnswerText,
                correctFirst ? response.incorrectAnswerText : response.correctAnswerText,
            )
            setAiGenerated(true)
        } catch (error) {
            const message = error instanceof Error ? error.message : 'AI assistant request failed.'
            setAiError(message || 'AI assistant request failed.')
        } finally {
            setAiLoading(false)
        }
    }

    return (
        <Form id="question-create-form" validator={validator} onSubmit={handleSubmit}>
            <Field label="Question" required>
                <div className="question-input-with-action">
                    <TextArea
                        id="question-text"
                        className="question-textarea-with-action"
                        value={state.questionText}
                        onChange={state.setQuestionText}
                    />
                    <Button
                        id="question-ai-assistant-button"
                        className="secondary button question-ai-assistant-button"
                        onClick={handleAiAssistantClick}
                        disabled={aiLoading}
                    >
                        {aiLoading ? 'Loading...' : 'AI Assistant'}
                    </Button>
                </div>
                <ErrorMessage errorCode="empty-question" />
                {aiError && <Alert type="error">{aiError}</Alert>}
            </Field>
            <Field label="Image URL">
                <TextInput id="image-url" value={state.imageUrl} onChange={state.setImageUrl} />
                {state.imageUrl && (
                    <img
                        src={state.imageUrl}
                        alt="preview"
                        className="image-preview"
                        style={{ maxWidth: '300px', display: 'block', marginTop: '0.5rem' }}
                    />
                )}
            </Field>
            <Row>
                <Field label="Question type" required>
                    <select
                        id="question-type"
                        value={state.questionType}
                        onChange={e => state.selectQuestionType(e.target.value as 'single' | 'multiple' | 'numerical')}
                    >
                        <option value="single">Single choice</option>
                        <option value="multiple">Multiple choice</option>
                        <option value="numerical">Numerical question</option>
                    </select>
                </Field>
                {state.isMultipleChoice && (
                    <CheckField
                        id="easy-mode"
                        label="Easy mode"
                        checked={state.easyMode}
                        onToggle={state.setEasyMode}
                    />
                )}
            </Row>
            {state.isNumerical ? (
                <Field label="Correct numerical answer" required>
                    <input
                        type="number"
                        id="numerical-correct-answer"
                        value={state.numericalAnswer}
                        onChange={e => state.setNumericalAnswer(e.target.value)}
                    />
                    <ErrorMessage errorCode="empty-numerical-answer" />
                    <ErrorMessage errorCode="invalid-numerical-answer" />
                </Field>
            ) : (
                <AnswersEdit
                    setShowExplanations={state.setShowExplanations}
                    showExplanations={state.showExplanations}
                    answerStates={state.answerStates}
                    isMultipleChoice={state.isMultipleChoice}
                    addAnswer={state.addAnswer}
                    removeAnswer={state.removeAnswer}
                />
            )}
            <Field label="Question explanation">
                <TextArea
                    id="question-explanation"
                    value={state.questionExplanation}
                    onChange={state.setQuestionExplanation}
                />
            </Field>
            <Row>
                {onBack && (
                    <Button id="back" className="primary button" onClick={onBack}>
                        Back
                    </Button>
                )}
                <SubmitButton />
            </Row>
        </Form>
    )
}
