import { EmpleadoListComponent } from "./empleado-list.component";
import { EmpleadosApiService } from "../../../core/api/empleados-api.service";

describe("EmpleadoListComponent", () => {
  it("debe iniciar con lista vacia", () => {
    const api = {} as EmpleadosApiService;
    const component = new EmpleadoListComponent(api);

    expect(component.empleados.length).toBe(0);
    expect(component.selected).toBeNull();
  });
});
