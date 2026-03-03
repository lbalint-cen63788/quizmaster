import type { Quiz } from 'model/quiz.ts'
import type { Stats } from 'model/stats.ts'
import './quiz-stats-component.scss'

export interface QuizStatsProps {
    readonly quiz: Quiz
    readonly stats: Stats
}

interface SummaryStats {
    readonly started: number
    readonly finished: number
}

const formatDuration = (started: string, finished: string): string => {
    const start = new Date(started)
    const end = new Date(finished)
    const diffMs = end.getTime() - start.getTime()
    const diffSeconds = Math.round(diffMs / 1000)

    if (diffSeconds < 60) {
        return `${diffSeconds} second${diffSeconds !== 1 ? 's' : ''}`
    }

    const minutes = Math.floor(diffSeconds / 60)
    const seconds = diffSeconds % 60

    if (minutes < 60) {
        if (seconds === 0) {
            return `${minutes} minute${minutes !== 1 ? 's' : ''}`
        }
        return `${minutes} minute${minutes !== 1 ? 's' : ''} ${seconds} second${seconds !== 1 ? 's' : ''}`
    }

    const hours = Math.floor(minutes / 60)
    const remainingMinutes = minutes % 60

    if (remainingMinutes === 0 && seconds === 0) {
        return `${hours} hour${hours !== 1 ? 's' : ''}`
    }
    if (seconds === 0) {
        return `${hours} hour${hours !== 1 ? 's' : ''} ${remainingMinutes} minute${remainingMinutes !== 1 ? 's' : ''}`
    }
    if (remainingMinutes === 0) {
        return `${hours} hour${hours !== 1 ? 's' : ''} ${seconds} second${seconds !== 1 ? 's' : ''}`
    }
    return `${hours} hour${hours !== 1 ? 's' : ''} ${remainingMinutes} minute${remainingMinutes !== 1 ? 's' : ''} ${seconds} second${seconds !== 1 ? 's' : ''}`
}

export const QuizStats = ({ quiz, stats }: QuizStatsProps) => {
    const summary: SummaryStats = {
        started: stats.length,
        finished: stats.filter(stat => Boolean(stat.finished)).length,
    }

    return (
        <div className="quiz-stats">
            <h2>Statistics for quiz: {quiz.title}</h2>
            <table data-testid="summary-stats-table">
                <caption>Summary</caption>
                <thead>
                    <tr>
                        <th>Started</th>
                        <th>Finished</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td>{summary.started}</td>
                        <td>{summary.finished}</td>
                    </tr>
                </tbody>
            </table>
            <table data-testid="attempt-stats-table">
                <caption>Attempts</caption>
                <thead>
                    <tr>
                        <th>Duration</th>
                        <th>Score</th>
                    </tr>
                </thead>
                <tbody>
                    {stats.map(stat => (
                        <tr key={stat.id}>
                            <td>{formatDuration(stat.started, stat.finished)}</td>
                            <td>{stat.score}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    )
}
