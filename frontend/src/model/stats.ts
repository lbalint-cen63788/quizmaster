export interface StatsRecord {
    readonly id: number
    readonly started: string
    readonly finished: string
    readonly score: number
    readonly timedOut?: boolean
    readonly maxScore: number
}

export type Stats = readonly StatsRecord[]
