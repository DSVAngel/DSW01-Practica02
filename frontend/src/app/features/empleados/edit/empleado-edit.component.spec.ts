import { EmpleadoEditComponent } from "./empleado-edit.component";
import { EmpleadosApiService } from "../../../core/api/empleados-api.service";

describe("EmpleadoEditComponent", () => {
  it("debe iniciar sin empleado seleccionado", () => {
    const api = {} as EmpleadosApiService;
    const component = new EmpleadoEditComponent(api);

    expect(component.empleado).toBeNull();
    expect(component.etag).toBeNull();
  });
});
