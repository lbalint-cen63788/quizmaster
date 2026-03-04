import type { Page } from '@playwright/test'

export class WorkspaceCreatePage {
    constructor(private page: Page) {}

    gotoNew = () => this.page.goto('/workspace/new')
    isCreatePage = () => this.page.locator('#create-workspace-page').isVisible()

    private workspaceTitleLocator = () => this.page.locator('#workspace-title')
    private workspaceGuidValue = ''
    enterWorkspaceName = (title: string) => this.workspaceTitleLocator().fill(title)
    workspaceTitleValue = () => this.workspaceTitleLocator().inputValue()
    workspaceGuid = () => this.workspaceGuidValue

    submit = async () => {
        await this.page.locator('button[type="submit"]').click()
        await this.page.waitForURL(/\/workspace\/[a-f0-9-]+/)

        this.workspaceGuidValue = this.page.url().split('/').pop() ?? ''
    }

    back = () => this.page.locator('button#back').click()

    errorMessage = () => this.page.textContent('#error-message')
}
