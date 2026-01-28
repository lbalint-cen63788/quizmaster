import type React from 'react'
import type { QuestionListItem } from 'model/question-list-item'
import { QuizCreateForm, type QuizCreateFormData } from './quiz-create-form'

import type { Quiz } from 'model/quiz.ts'

interface QuizEditFormProps {
    questions: readonly QuestionListItem[]
    onSubmit: (data: QuizCreateFormData) => void
    quiz?: Quiz
}

export const QuizEditForm: React.FC<QuizEditFormProps> = ({ questions, onSubmit, quiz }) => {
    return <QuizCreateForm questions={questions} onSubmit={onSubmit} quiz={quiz} />
}
