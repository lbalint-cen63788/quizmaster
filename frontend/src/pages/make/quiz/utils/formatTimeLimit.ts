export function formatTimeLimit(timeLimit: number) {
    if (timeLimit === 0) {
        return 'No time limit'
    }
    const hours = Math.floor(timeLimit / 3600)
    const minutes = Math.floor((timeLimit % 3600) / 60)
    const seconds = timeLimit % 60
    return `${hours}h ${minutes}m ${seconds}s`
}