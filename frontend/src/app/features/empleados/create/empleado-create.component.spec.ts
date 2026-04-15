import { EmpleadoCreateComponent } from "./empleado-create.component";
import { EmpleadosApiService } from "../../../core/api/empleados-api.service";

describe("EmpleadoCreateComponent", () => {
  it("debe inicializar un modelo vacio", () => {
    const api = { create: async () => ({}) } as unknown as EmpleadosApiService;
    const component = new EmpleadoCreateComponent(api);

    expect(component.model.clave).toBe("");
    expect(component.model.nombre).toBe("");
  });
});
