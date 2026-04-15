import { Empleado, EmpleadosApiService } from "../../../core/api/empleados-api.service";

export class EmpleadoEditComponent {
  empleado: Empleado | null = null;
  etag: string | null = null;

  constructor(private readonly api: EmpleadosApiService) {}

  async load(clave: string): Promise<void> {
    const detail = await this.api.getByClave(clave);
    this.empleado = detail.empleado;
    this.etag = detail.etag;
  }

  async savePatch(data: Partial<Omit<Empleado, "clave">>): Promise<void> {
    if (!this.empleado || !this.etag) {
      throw new Error("Missing loaded empleado or ETag");
    }
    this.empleado = await this.api.updatePatch(this.empleado.clave, data, this.etag);
    const refreshed = await this.api.getByClave(this.empleado.clave);
    this.etag = refreshed.etag;
  }

  async remove(): Promise<void> {
    if (!this.empleado) {
      throw new Error("Missing loaded empleado");
    }
    await this.api.delete(this.empleado.clave);
    this.empleado = null;
    this.etag = null;
  }
}
