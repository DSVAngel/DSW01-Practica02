describe("Login page", () => {
  it("shows login screen", () => {
    cy.visit("/login");
    cy.contains("h1", "Iniciar sesión").should("be.visible");
    cy.contains("button", "Entrar").should("be.visible");
  });
});
