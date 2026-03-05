import { Button, LinkButton } from 'pages/components'
import type { QuestionListItem } from 'model/question-list-item'

interface Props {
    readonly question: QuestionListItem
    readonly index: number
    readonly onDeleteQuestion: () => void
}

export const QuestionItem = ({ question, index, onDeleteQuestion }: Props) => (
    <div className="question-item">
        {question.imageUrl && <img src={question.imageUrl} alt="" className="question-thumbnail" />}
        <span className="question-index">Q{index + 1}. </span>
        <span className="question-text">{question.question}</span>
        <LinkButton to={`/question/${question.editId}/edit`}>Edit</LinkButton>
        <LinkButton to={`/question/${question.id}`}>Take</LinkButton>
        {!question.isInAnyQuiz && (
            <Button className="link-button" onClick={onDeleteQuestion}>
                Delete
            </Button>
        )}
    </div>
)
