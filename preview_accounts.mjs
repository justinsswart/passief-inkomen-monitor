import { chromium } from "playwright";
(async () => {
  const browser = await chromium.launch();
  const ctx = await browser.newContext({ viewport: { width: 390, height: 844 } });
  const page = await ctx.newPage();
  await page.goto("file:///C:/Users/justi/passive-income-app/passive-income-app/www/index.html");
  await page.waitForTimeout(1500);
  const skipBtn = page.locator("#aSkip");
  if (await skipBtn.count() > 0) { await skipBtn.click(); await page.waitForTimeout(1000); }
  // Navigeer naar rekeningen tab
  await page.evaluate(() => { window.S && (window.S.tab='accounts') && window.render && window.render(); });
  await page.waitForTimeout(800);
  await page.screenshot({ path: "preview_accounts.png" });
  // Klik op eerste rekening om accordion te testen
  const firstRow = page.locator('.acc-row').first();
  if (await firstRow.count() > 0) {
    await firstRow.click();
    await page.waitForTimeout(400);
    await page.screenshot({ path: "preview_accounts_open.png" });
  }
  await browser.close();
  console.log("done");
})();
