import { BrowserRouter, Route, Routes } from 'react-router-dom'

import { HomePage } from 'pages/make/home'
import { QuestionTakePage } from 'pages/take/question-take'
import { QuizTakePage } from 'pages/take/quiz-take/quiz-take-page.tsx'

import { QuizWelcomePage } from 'pages/take/quiz-take/quiz-welcome/quiz-welcome-page'

import { WorkspaceCreatePage } from 'pages/make/create-workspace/workspace-create-page'
import { CreateQuestionPage } from 'pages/make/create-question/create-question-page'
import { EditQuestionPage } from 'pages/make/create-question/edit-question-page'
import { WorkspacePage } from 'pages/make/workspace/workspace'
import { QuizCreatePage } from 'pages/make/quiz-create/quiz-create-page.tsx'
import { QuizStatsPage } from 'pages/make/quiz-stats/quiz-stats-page'
import { QuizEditPage } from 'pages/make/quiz-create/quiz-edit-page'

export const App = () => (
    <BrowserRouter>
        <Routes>
            <Route path="/question/new" element={<CreateQuestionPage />} />
            <Route path="/workspace/new" element={<WorkspaceCreatePage />} />
            <Route path="/workspace/:id" element={<WorkspacePage />} />
            <Route path="/quiz/:id" element={<QuizWelcomePage />} />
            <Route path="/quiz/:id/questions/:questionId?" element={<QuizTakePage />} />
            <Route path="/quiz-create/new" element={<QuizCreatePage />} />
            <Route path="/quiz/:id/edit" element={<QuizEditPage />} />
            <Route path="/question/:id/edit" element={<EditQuestionPage />} />
            <Route path="/question/:id" element={<QuestionTakePage />} />
            <Route path="/" element={<HomePage />} />
            <Route path="/quiz/:id/stats" element={<QuizStatsPage />} />
        </Routes>
    </BrowserRouter>
)
