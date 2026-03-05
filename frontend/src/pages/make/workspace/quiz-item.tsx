import { LinkButton } from 'pages/components'
import type { QuizListItem } from 'model/quiz-list-item'

interface Props {
    readonly quiz: QuizListItem
    readonly workspaceguid: string
}

export const QuizItem = ({ quiz, workspaceguid }: Props) => (
    <div className="quiz-item question-item">
        <span className="question-text">{quiz.title}</span>
        <LinkButton to={`/quiz/${quiz.id}/edit?workspaceguid=${workspaceguid}`}>Edit</LinkButton>
        <LinkButton to={`/quiz/${quiz.id}`}>Take</LinkButton>
        <LinkButton to={`/quiz/${quiz.id}/stats`}>Statistics</LinkButton>
    </div>
)
