import './quiz-create-form.scss'
import { useEffect, useState } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'

import { useStateSet } from 'helpers'
import type { QuestionListItem } from 'model/question-list-item.ts'
import type { QuizCreateRequest } from 'api/quiz.ts'

import { Field, Form, NumberInput, RadioSet, Row, SubmitButton, TextArea, TextInput } from 'pages/components'
import { QuestionSelect } from './components/question-select.tsx'
import { ErrorMessage, createValidator } from 'pages/components/forms/validations.tsx'
import { validateQuizForm, errorMessage } from './validations.ts'
import type { QuizMode, Difficulty } from 'model/quiz.ts'

export type QuizCreateFormData = QuizCreateRequest

import type { Quiz } from 'model/quiz.ts'

interface QuizCreateProps {
    readonly questions: readonly QuestionListItem[]
    readonly onSubmit: (data: QuizCreateFormData) => void
    readonly quiz?: Quiz
}

export const QuizCreateForm = ({ questions, onSubmit, quiz }: QuizCreateProps) => {
    const navigate = useNavigate()
    const [title, setTitle] = useState<string>('')
    const [description, setDescription] = useState<string>('')
    const [selectedIds, toggleSelectedId, addSelectedId] = useStateSet<number>()
    const [timeLimit, setTimeLimit] = useState<number>(600)
    const [randomQuestionCount, setRandomQuestionCount] = useState<number>(0)
    const [passScore, setPassScore] = useState<number>(80)
    const [filter, setFilter] = useState<string>('')
    const [searchParams] = useSearchParams()
    const [checkRandomize, setCheckRandomize] = useState(false)
    const [filteredQuestions, setFilteredQuestions] = useState<readonly QuestionListItem[]>(questions)
    const [feedbackMode, setFeedbackMode] = useState<QuizMode>('exam')
    const [difficulty, setDifficulty] = useState<Difficulty>('keep-question')
    const [isInitialized, setIsInitialized] = useState(false)
    useEffect(() => {
        if (quiz && !isInitialized) {
            setTitle(quiz.title || '')
            setDescription(quiz.description || '')
            setTimeLimit(quiz.timeLimit ?? 600)
            setRandomQuestionCount(quiz.randomQuestionCount ?? 0)
            setPassScore(quiz.passScore ?? 80)
            setFeedbackMode(quiz.mode || 'exam')
            setDifficulty(quiz.difficulty || 'keep-question')
            setCheckRandomize(!!quiz.randomQuestionCount)
            if (quiz.questions) {
                for (const q of quiz.questions) {
                    addSelectedId(q.id)
                }
            }
            setIsInitialized(true)
        }
    }, [quiz, addSelectedId, isInitialized])

    const validator = createValidator(
        () => validateQuizForm({ title, description, timeLimit, passScore, selectedIds, randomQuestionCount }),
        errorMessage,
    )

    const toFormData = (): QuizCreateFormData => ({
        title,
        description,
        questionIds: Array.from(selectedIds),
        mode: feedbackMode,
        difficulty,
        passScore,
        timeLimit,
        workspaceGuid: searchParams.get('workspaceguid') || null,
        randomQuestionCount,
    })

    const onBack = () => {
        const workspaceGuid = searchParams.get('workspaceguid')
        if (workspaceGuid) {
            navigate(`/workspace/${workspaceGuid}`)
            return
        }
        navigate('/')
    }

    useEffect(() => {
        if (filter === '') {
            setFilteredQuestions(questions)
        } else {
            const filtered = questions.filter(q => q.question.toLowerCase().includes(filter.toLowerCase()))
            setFilteredQuestions(filtered)
        }
    }, [filter, questions])

    return (
        <Form id="create-quiz" validator={validator} onSubmit={() => onSubmit(toFormData())}>
            <Field label="Quiz title" required>
                <TextInput id="quiz-title" value={title} onChange={setTitle} />
                <ErrorMessage errorCode="empty-title" />
            </Field>
            <Field label="Quiz description">
                <TextArea id="quiz-description" value={description} onChange={setDescription} />
            </Field>
            <Row>
                <Field label="Pass score (in %)">
                    <NumberInput id="pass-score" value={passScore} onChange={setPassScore} />
                    <ErrorMessage errorCode="score-above-max" />
                </Field>
                <Field label="Time limit (in sec)">
                    <NumberInput id="time-limit" value={timeLimit} onChange={setTimeLimit} />
                    <ErrorMessage errorCode="negative-time-limit" />
                    <ErrorMessage errorCode="time-limit-above-max" />
                </Field>
            </Row>
            <Field label="Feedback mode">
                <RadioSet
                    name="mode"
                    value={feedbackMode}
                    onChange={setFeedbackMode}
                    options={{ exam: 'Exam', learn: 'Learning' }}
                />
            </Field>
            <Field label="Difficulty">
                <RadioSet
                    name="difficulty"
                    value={difficulty}
                    onChange={setDifficulty}
                    options={{ easy: 'Easy', hard: 'Hard', 'keep-question': 'Keep Question' }}
                />
            </Field>
            <div className="label">Select quiz questions</div>
            <Field label="Filter">
                <TextInput id="question-filter" value={filter} onChange={setFilter} />
            </Field>
            <QuestionSelect questions={filteredQuestions} selectedIds={selectedIds} onSelect={toggleSelectedId} />
            <ErrorMessage errorCode="few-questions" />

            <div className="question-count-info">
                <span className="inline-label">
                    <div className="bold-count" id="selected-question-count-for-quiz">
                        {selectedIds.size}
                    </div>
                    selected question(s)
                </span>
            </div>
            <div className="question-count-info">
                <span className="inline-label">
                    <div className="bold-count" id="total-question-count-for-quiz">
                        {questions.length}
                    </div>
                    total questions available
                </span>
            </div>

            <span className="inline-label">
                <input
                    type="checkbox"
                    id="isRandomized"
                    onChange={e => setCheckRandomize(e.target.checked)}
                    checked={checkRandomize}
                />
                <label htmlFor="isRandomized" className="randomize-label">
                    Randomize questions in the quiz
                </label>
            </span>
            {checkRandomize && (
                <span className="inline-label">
                    <div className="random-count-input">
                        <NumberInput
                            id="quiz-randomQuestionCount"
                            value={randomQuestionCount}
                            onChange={setRandomQuestionCount}
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
