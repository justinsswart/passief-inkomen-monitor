const { chromium } = require("playwright");
(async () => {
  const browser = await chromium.launch();
  const ctx = await browser.newContext({ viewport: { width: 390, height: 844 } });
  const page = await ctx.newPage();
  await page.goto("file:///C:/Users/justi/passive-income-app/passive-income-app/www/index.html");
  await page.waitForTimeout(1500);
  const skipBtn = page.locator("#aSkip");
  if (await skipBtn.count() > 0) { await skipBtn.click(); await page.waitForTimeout(1000); }
  await page.screenshot({ path: "preview_live.png" });
  await browser.close();
  console.log("done");
})();
