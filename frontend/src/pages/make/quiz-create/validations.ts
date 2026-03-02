export const errorMessage = {
    'empty-title': 'Quiz title is required.',
    'negative-time-limit': 'Time limit cannot be negative.',
    'time-limit-above-max': 'Time limit cannot exceed 21600 seconds (6 hours).',
    'score-above-max': 'Pass score cannot exceed 100%.',
    'few-questions': 'At least two questions must be selected.',
    'too-many-randomized-questions': 'Final question count cannot exceed the number of selected questions.',
}

type ErrorCode = keyof typeof errorMessage

export interface QuizFormState {
    readonly title: string
    readonly description: string
    readonly timeLimit: number
    readonly passScore: number
    readonly selectedIds: ReadonlySet<number>
    readonly finalCount: number
}

export function validateQuizForm(state: QuizFormState): Set<ErrorCode> {
    const errors = new Set<ErrorCode>()

    if (!state.title) errors.add('empty-title')
    if (state.timeLimit < 0) errors.add('negative-time-limit')
    if (state.timeLimit > 21600) errors.add('time-limit-above-max')
    if (state.passScore > 100) errors.add('score-above-max')
    if (state.selectedIds.size < 2) errors.add('few-questions')
    if (state.finalCount > 0 && state.finalCount > state.selectedIds.size) errors.add('too-many-randomized-questions')

    return errors
}
