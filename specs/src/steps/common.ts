import { expect, type Locator } from '@playwright/test'

export type TableOf<T> = { raw: () => T[] }

export const toText = (table: TableOf<string[]>) =>
    table
        .raw()
        .map(row => row[0])
        .join(' ')

export const expectTextToBe = async (locator: Locator, text: string) => await expect(locator).toHaveText(text)

export const expectTextToContain = async (locator: Locator, text: string) => await expect(locator).toContainText(text)

export const expectThatIsVisible = async (locator: Locator) => expect(locator).toBeVisible()

export const expectThatIsNotVisible = async (locator: Locator) => expect(locator).toBeHidden()

export const expectedNumberOfChildrenToBe = async (locator: Locator, count: number) =>
    await expect(locator).toHaveCount(count)
