import { Empleado, EmpleadosApiService } from "../../../core/api/empleados-api.service";

export class EmpleadoListComponent {
  empleados: Empleado[] = [];
  page = 0;
  size = 10;
  selected: Empleado | null = null;

  constructor(private readonly api: EmpleadosApiService) {}

  async load(): Promise<void> {
    const response = await this.api.list(this.page, this.size);
    this.empleados = response.content;
  }

  async loadDetail(clave: string): Promise<void> {
    const detail = await this.api.getByClave(clave);
    this.selected = detail.empleado;
  }
}
