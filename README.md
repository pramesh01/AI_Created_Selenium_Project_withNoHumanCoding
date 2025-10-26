# AIMCPAgent

Minimal Selenium-based test scaffold (Node.js)

What’s included
- package.json with selenium-webdriver and mocha
- example test in `src/tests/example.test.js`
- simple page object in `src/pages/examplePage.js`
- webdriver helper in `src/drivers/webdriver.js`
- VS Code debug/launch config in `.vscode/launch.json`

Quick start
1. From a terminal in `C:\Users\prame\AIMCPAgent` run:

```powershell
npm install
npm test
```

2. The example test opens https://qtpselenium.com using Chrome (make sure Chrome is installed and chromedriver is available via your system PATH or webdriver manager).

Notes
- This is a minimal scaffold to get you started. I can add Chromedriver downloads, CI config, or a test runner like WebdriverIO if you prefer.
