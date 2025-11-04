// import { useState } from 'react'
// import { Question } from 'model/question.ts'

// export interface Answer {
//     readonly answer: string
//     readonly explanation: string
//     readonly isCorrect: boolean
// }

// export const useQuestionFormState = (question: Question) => {
//     const [questionText, setQuestionText] = useState<string>(question.question)
//     const [answers, setAnswers] = useState<readonly string[]>(question.answers)
//     const [explanations, setExplanations] = useState<readonly string[]>(question.explanations)
//     const [correctAnswers, setCorrectAnswers] = useState<readonly number[]>(question.correctAnswers)
//     const [questionExplanation, setQuestionExplanation] = useState(question.questionExplanation)
//     const [isMultipleChoice, setIsMultipleChoice] = useState(question.correctAnswers.length > 1)
//     const [workspaceGuid, setWorkspaceGuid] = useState(question.workspaceGuid)
//     const [isEasyModeChoice, setIsEasyModeChoice] = useState(question.easyMode)

//     const answer = (index: number): Answer => ({
//         answer: answers[index],
//         explanation: explanations[index],
//         isCorrect: correctAnswers.includes(index),
//     })
// }
