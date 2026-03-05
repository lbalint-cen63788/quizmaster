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
import { ROUTES } from 'urls.ts'

export const App = () => (
    <BrowserRouter>
        <Routes>
            <Route path={ROUTES.home} element={<HomePage />} />

            {/* Legacy standalone question */}
            <Route path={ROUTES.questionNew} element={<CreateQuestionPage />} />
            <Route path={ROUTES.questionEditStandalone} element={<EditQuestionPage />} />
            <Route path={ROUTES.questionTake} element={<QuestionTakePage />} />

            {/* Workspace */}
            <Route path={ROUTES.workspaceNew} element={<WorkspaceCreatePage />} />
            <Route path={ROUTES.workspace} element={<WorkspacePage />} />
            <Route path={ROUTES.workspaceQuestionNew} element={<CreateQuestionPage />} />
            <Route path={ROUTES.workspaceQuestionEdit} element={<EditQuestionPage />} />

            {/* Quiz management (workspace-scoped) */}
            <Route path={ROUTES.workspaceQuizNew} element={<QuizCreatePage />} />
            <Route path={ROUTES.workspaceQuizEdit} element={<QuizEditPage />} />
            <Route path={ROUTES.workspaceQuizStats} element={<QuizStatsPage />} />

            {/* Quiz taking (public) */}
            <Route path={ROUTES.quizWelcome} element={<QuizWelcomePage />} />
            <Route path={ROUTES.quizTake} element={<QuizTakePage />} />
        </Routes>
    </BrowserRouter>
)
