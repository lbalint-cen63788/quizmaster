import { LinkButton } from 'pages/components'
import type { QuizListItem } from 'model/quiz-list-item'
import { urls, useWorkspaceId } from 'urls.ts'

interface Props {
    readonly quiz: QuizListItem
}

export const QuizItem = ({ quiz }: Props) => {
    const workspaceId = useWorkspaceId()
    return (
        <div className="quiz-item question-item">
            <span className="question-text">{quiz.title}</span>
            <LinkButton label="Edit" to={urls.workspaceQuizEdit(workspaceId, quiz.id)} />
            <LinkButton label="Take" to={urls.quizWelcome(quiz.id)} />
            <LinkButton label="Statistics" to={urls.workspaceQuizStats(workspaceId, quiz.id)} />
        </div>
    )
}
