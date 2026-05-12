import { defineConfig } from "cypress";

export default defineConfig({
  e2e: {
    baseUrl: "http://localhost:4200",
    specPattern: "cypress/e2e/**/*.cy.{js,jsx,ts,tsx}",
    supportFile: false,
    video: false,
    requestTimeout: 5000,
    responseTimeout: 5000,
    defaultCommandTimeout: 5000
  }
});
