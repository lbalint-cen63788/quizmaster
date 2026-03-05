import { Alert } from 'pages/components'
import { urls } from 'urls.ts'

interface QuizUrlProps {
    readonly quizId: string
}

export const QuizUrl = ({ quizId }: QuizUrlProps) => {
    const url = `${location.origin}${urls.quizWelcome(quizId)}`

    return (
        <Alert type="success">
            Quiz url: <a href={url}>{url}</a>
        </Alert>
    )
}
