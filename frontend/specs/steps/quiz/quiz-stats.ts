import { Then } from '../fixture.ts'
import { expectTextToBe } from '../common.ts'


Then('I see stats page for quiz {string}', async function (quizName: string) {
    const statsPageHeaderElemenLocator = this.quizStatsPage.pageHeadingLocator()

    await expectTextToBe(statsPageHeaderElemenLocator, "Statistics for quiz: " + quizName)
})
