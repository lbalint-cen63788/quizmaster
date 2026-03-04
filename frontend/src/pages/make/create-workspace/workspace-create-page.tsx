import { useState } from 'react'
import { useNavigate } from 'react-router-dom'

import { tryCatch } from 'helpers.ts'
import { postWorkspace } from 'api/workspace.ts'

import { Alert, Page } from 'pages/components'
import { WorkspaceCreateForm, type WorkspaceFormData } from './workspace-create-form.tsx'

export function WorkspaceCreatePage() {
    const [errorMessage, setErrorMessage] = useState<string>('')

    const navigate = useNavigate()

    const onSubmit = async (data: WorkspaceFormData) =>
        await tryCatch(setErrorMessage, async () => {
            const response = await postWorkspace(data)
            navigate(`/workspace/${response.guid}`)
        })

    const onBack = () => {
        navigate('/')
    }

    return (
        <Page title="Create Workspace" id="create-workspace-page">
            <WorkspaceCreateForm onSubmit={onSubmit} onBack={onBack} />
            {errorMessage && <Alert type="error">{errorMessage}</Alert>}
        </Page>
    )
}
