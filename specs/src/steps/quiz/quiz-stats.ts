import type { DataTable } from '@cucumber/cucumber'
import { expect } from '@playwright/test'

import { expectTextToBe } from 'steps/common.ts'
import { Given, Then, When } from 'steps/fixture.ts'
import { takeQuizWithAnswers, takeQuizWithAnswersTimed } from 'steps/quiz/ops.ts'

Given('I take quiz {string} with answer(s)', async function (quizName: string, data: DataTable) {
    await takeQuizWithAnswers(this, quizName, data)
})

When(
    'I take quiz {string} with answers in {int} seconds',
    async function (quizName: string, timer: number, data: DataTable) {
        await takeQuizWithAnswersTimed(this, quizName, timer, data)
    },
)

Then('I see stats page for quiz {string}', async function (quizName: string) {
    const statsPageHeaderElemenLocator = this.quizStatsPage.pageHeadingLocator()

    await expectTextToBe(statsPageHeaderElemenLocator, `Statistics for quiz: ${quizName}`)
})

Then('I see stats table', async function (data: DataTable) {
    const statsTableBodyRowsLocator = this.quizStatsPage.statsTableBodyRowsLocator()
    const actualRowCount = await statsTableBodyRowsLocator.count()

    const expectedRows = data.rows().filter(row => row.some(cell => cell.trim() !== ''))
    const expectedRowCount = expectedRows.length

    await expect(actualRowCount).toBe(expectedRowCount)

    for (let i = 0; i < expectedRows.length; i++) {
        const expectedRow = expectedRows[i]
        const actualRowLocator = statsTableBodyRowsLocator.nth(i)

        for (let j = 0; j < expectedRow.length; j++) {
            const expectedCell = expectedRow[j].trim()
            if (expectedCell !== '') {
                await expectTextToBe(actualRowLocator.locator('td').nth(j), expectedCell)
            }
        }
    }
})
