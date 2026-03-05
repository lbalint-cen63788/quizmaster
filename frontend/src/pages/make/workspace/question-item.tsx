import { Button, LinkButton } from 'pages/components'
import type { QuestionListItem } from 'model/question-list-item'
import { urls, useWorkspaceId } from 'urls.ts'

interface Props {
    readonly question: QuestionListItem
    readonly index: number
    readonly onDeleteQuestion: () => void
}

export const QuestionItem = ({ question, index, onDeleteQuestion }: Props) => {
    const workspaceId = useWorkspaceId()
    return (
        <div className="question-item">
            {question.imageUrl && <img src={question.imageUrl} alt="" className="question-thumbnail" />}
            <span className="question-index">Q{index + 1}. </span>
            <span className="question-text">{question.question}</span>
            <LinkButton label="Edit" to={urls.workspaceQuestionEdit(workspaceId, question.id)} />
            <LinkButton label="Take" to={urls.questionTake(question.id)} />
            {!question.isInAnyQuiz && (
                <Button className="link-button" onClick={onDeleteQuestion}>
                    Delete
                </Button>
            )}
        </div>
    )
}
