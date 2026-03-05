import './workspace.scss'
import { useState } from 'react'
import { useParams } from 'react-router-dom'

import type { QuestionListItem } from 'model/question-list-item'
import type { QuizListItem } from 'model/quiz-list-item'
import type { Workspace } from 'model/workspace'

import { useApi } from 'api/hooks'
import { fetchWorkspace, fetchWorkspaceQuestions, fetchWorkspaceQuizzes } from 'api/workspace'
import { deleteQuestion } from 'api/question'

import { ItemList, LinkButton } from 'pages/components'
import { QuestionItem } from './question-item'
import { QuizItem } from './quiz-item'

export function WorkspacePage() {
    const params = useParams()

    const [workspace, setWorkspace] = useState<Workspace>({ guid: params.id || '', title: '' })
    const [questions, setQuestions] = useState<readonly QuestionListItem[]>([])
    const [quizzes, setQuizzes] = useState<readonly QuizListItem[]>([])

    useApi(params.id, fetchWorkspace, setWorkspace)
    useApi(params.id, fetchWorkspaceQuestions, setQuestions)
    useApi(params.id, fetchWorkspaceQuizzes, setQuizzes)

    const onDeleteQuestion = async (id: number) => {
        await deleteQuestion(`${id}`)
        if (params.id) {
            setQuestions(await fetchWorkspaceQuestions(params.id))
        }
    }

    return (
        <div className="workspace-page">
            {workspace.title && <h1 data-testid="workspace-title">{workspace.title}</h1>}
            <div className="create-buttons">
                <LinkButton id="create-question" to={`/question/new?workspaceguid=${workspace.guid}`}>
                    Create Question
                </LinkButton>
                <LinkButton id="create-quiz" to={`/quiz-create/new?workspaceguid=${workspace.guid}`}>
                    Create Quiz
                </LinkButton>
            </div>
            <ItemList title="My Questions">
                {questions.map((q, index) => (
                    <QuestionItem
                        key={q.id || index}
                        question={q}
                        index={index}
                        onDeleteQuestion={() => onDeleteQuestion(q.id)}
                    />
                ))}
            </ItemList>
            <ItemList title="My quizzes">
                {quizzes.map(quiz => (
                    <QuizItem key={quiz.id} quiz={quiz} workspaceguid={workspace.guid} />
                ))}
            </ItemList>
        </div>
    )
}
