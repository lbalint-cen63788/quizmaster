import type { Question } from 'model/question.ts'
import { fetchJson, postJson, patchJson, callDelete } from './helpers.ts'

export const fetchQuestion = async (questionId: string) => await fetchJson<Question>(`/api/question/${questionId}`)

export interface TestNumericalQuestion {
    readonly question: string
    readonly correctAnswer: string
}

export const fetchTestNumericalQuestion = async () =>
    await fetchJson<TestNumericalQuestion>('/api/test-numerical-question')

export const deleteQuestion = async (questionId: string) => await callDelete(`/api/question/${questionId}`)

export const fetchQuestionByEditId = async (editId: string) => await fetchJson<Question>(`/api/question/${editId}/edit`)

export type QuestionApiData = Omit<Question, 'id'>

export interface QuestionWriteResponse {
    readonly id: number
    readonly editId: string
}

export const saveQuestion = async (question: QuestionApiData) =>
    await postJson<QuestionApiData, QuestionWriteResponse>('/api/question', question)

export const updateQuestion = async (question: QuestionApiData, editId: string) =>
    await patchJson<QuestionApiData, QuestionWriteResponse>(`/api/question/${editId}`, question)
