import { Empleado, EmpleadosApiService } from "../../../core/api/empleados-api.service";

export class EmpleadoCreateComponent {
  model: Empleado = {
    clave: "",
    nombre: "",
    direccion: "",
    telefono: ""
  };

  lastError: string | null = null;

  constructor(private readonly api: EmpleadosApiService) {}

  async submit(): Promise<Empleado | null> {
    this.lastError = null;
    try {
      return await this.api.create(this.model);
    } catch (error) {
      this.lastError = (error as Error).message;
      return null;
    }
  }
}
