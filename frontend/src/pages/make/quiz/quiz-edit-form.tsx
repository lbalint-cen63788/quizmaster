import './quiz-edit-form.scss'
import { useNavigate } from 'react-router-dom'

import { urls, useWorkspaceId } from 'urls.ts'
import { Field, Form, NumberInput, RadioSet, Row, SubmitButton, TextArea, TextInput } from 'pages/components'
import { QuestionCountInfo } from './components/question-count-info.tsx'
import { QuestionSelect } from './components/question-select.tsx'
import { ErrorMessage, createValidator } from 'pages/components/forms/validations.tsx'
import { validateQuizForm, errorMessage } from './validations.ts'
import { useQuizFormState, stateToQuizApiData, type QuizEditFormData } from './quiz-form-state.ts'

import type { QuestionListItem } from 'model/question-list-item.ts'
import type { Quiz } from 'model/quiz.ts'

interface QuizEditFormProps {
    readonly questions: readonly QuestionListItem[]
    readonly onSubmit: (data: QuizEditFormData) => void
    readonly quiz?: Quiz
}

export const QuizEditForm = ({ questions, onSubmit, quiz }: QuizEditFormProps) => {
    const workspaceId = useWorkspaceId()
    const navigate = useNavigate()
    const state = useQuizFormState(questions, quiz)

    const validator = createValidator(() => validateQuizForm(state), errorMessage)

    const onBack = () => {
        navigate(urls.workspace(workspaceId))
    }

    return (
        <Form id="create-quiz" validator={validator} onSubmit={() => onSubmit(stateToQuizApiData(state, workspaceId))}>
            <Field label="Quiz title" required>
                <TextInput id="quiz-title" value={state.title} onChange={state.setTitle} />
                <ErrorMessage errorCode="empty-title" />
            </Field>
            <Field label="Quiz description">
                <TextArea id="quiz-description" value={state.description} onChange={state.setDescription} />
            </Field>
            <Row>
                <Field label="Pass score (in %)">
                    <NumberInput id="pass-score" value={state.passScore} onChange={state.setPassScore} />
                    <ErrorMessage errorCode="score-above-max" />
                </Field>
                <Field label="Time limit (in sec)">
                    <NumberInput id="time-limit" value={state.timeLimit} onChange={state.setTimeLimit} />
                    <ErrorMessage errorCode="negative-time-limit" />
                    <ErrorMessage errorCode="time-limit-above-max" />
                </Field>
            </Row>
            <Field label="Feedback mode">
                <RadioSet
                    name="mode"
                    value={state.feedbackMode}
                    onChange={state.setFeedbackMode}
                    options={{ exam: 'Exam', learn: 'Learning' }}
                />
            </Field>
            <Field label="Difficulty">
                <RadioSet
                    name="difficulty"
                    value={state.difficulty}
                    onChange={state.setDifficulty}
                    options={{ easy: 'Easy', hard: 'Hard', 'keep-question': 'Keep Question' }}
                />
            </Field>
            <div className="label">Select quiz questions</div>
            <Field label="Filter">
                <TextInput id="question-filter" value={state.filter} onChange={state.setFilter} />
            </Field>
            <QuestionSelect
                questions={state.filteredQuestions}
                selectedIds={state.selectedIds}
                onSelect={state.toggleSelectedId}
            />
            <ErrorMessage errorCode="few-questions" />

            <QuestionCountInfo selectedCount={state.selectedIds.size} totalCount={questions.length} />

            <span className="inline-label">
                <input
                    type="checkbox"
                    id="isRandomized"
                    onChange={e => state.setCheckRandomize(e.target.checked)}
                    checked={state.checkRandomize}
                />
                <label htmlFor="isRandomized" className="randomize-label">
                    Randomize questions in the quiz
                </label>
            </span>
            {state.checkRandomize && (
                <span className="inline-label">
                    <div className="random-count-input">
                        <NumberInput
                            id="quiz-randomQuestionCount"
                            value={state.randomQuestionCount}
                            onChange={state.setRandomQuestionCount}
                        />
                    </div>
                    random questions
                </span>
            )}
            <ErrorMessage errorCode="too-many-randomized-questions" />
            <div className="flex-container">
                <button id="back" type="button" className="primary button" onClick={onBack}>
                    Back
                </button>
                <SubmitButton />
            </div>
        </Form>
    )
}
