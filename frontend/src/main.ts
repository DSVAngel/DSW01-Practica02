import { bootstrapApplication } from "@angular/platform-browser";
import { Component } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { Empleado, EmpleadosApiService } from "./app/core/api/empleados-api.service";
import { Departamento, DepartamentosApiService } from "./app/core/api/departamentos-api.service";

@Component({
  selector: "app-root",
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <main class="shell">
      <h1>DSW01 Practica02 - Panel Operativo</h1>

      <section class="card">
        <h2>Autenticacion Basic</h2>
        <div class="row">
          <label>
            Usuario
            <input [(ngModel)]="auth.username" placeholder="admin" />
          </label>
          <label>
            Password
            <input [(ngModel)]="auth.password" type="password" placeholder="admin123" />
          </label>
          <button (click)="applyCredentials()">Conectar</button>
          <button class="secondary" (click)="clearCredentials()">Limpiar</button>
        </div>
      </section>

      <section class="grid-two">
        <article class="card">
          <h2>Empleados</h2>
          <div class="row-wrap">
            <input [(ngModel)]="empleadoForm.clave" placeholder="Clave" />
            <input [(ngModel)]="empleadoForm.nombre" placeholder="Nombre" />
            <input [(ngModel)]="empleadoForm.direccion" placeholder="Direccion" />
            <input [(ngModel)]="empleadoForm.telefono" placeholder="Telefono" />
            <button (click)="createEmpleado()">Crear</button>
            <button class="secondary" (click)="loadEmpleados()">Recargar</button>
          </div>

          <ul>
            <li *ngFor="let e of empleados">
              <strong>{{ e.clave }}</strong> - {{ e.nombre }}
              <span class="meta">{{ e.direccion }} / {{ e.telefono }}</span>
              <span class="meta" *ngIf="e.departamento">Depto: {{ e.departamento.nombre }} ({{ e.departamento.clave }})</span>

              <div class="row-wrap inline-controls">
                <input
                  [ngModel]="employeeEditDrafts[e.clave]?.nombre ?? e.nombre"
                  (ngModelChange)="setEmpleadoDraftField(e.clave, 'nombre', $event)"
                  placeholder="Nombre"
                />
                <input
                  [ngModel]="employeeEditDrafts[e.clave]?.direccion ?? e.direccion"
                  (ngModelChange)="setEmpleadoDraftField(e.clave, 'direccion', $event)"
                  placeholder="Direccion"
                />
                <input
                  [ngModel]="employeeEditDrafts[e.clave]?.telefono ?? e.telefono"
                  (ngModelChange)="setEmpleadoDraftField(e.clave, 'telefono', $event)"
                  placeholder="Telefono"
                />
                <button class="secondary" (click)="editEmpleado(e)">Guardar cambios</button>
              </div>

              <button class="danger" (click)="deleteEmpleado(e.clave)">Borrar</button>
            </li>
          </ul>
        </article>

        <article class="card">
          <h2>Departamentos</h2>
          <div class="row-wrap">
            <input [(ngModel)]="departamentoForm.clave" placeholder="Clave" />
            <input [(ngModel)]="departamentoForm.nombre" placeholder="Nombre" />
            <input [(ngModel)]="departamentoForm.descripcion" placeholder="Descripcion" />
            <input [(ngModel)]="departamentoForm.empleadosClavesText" placeholder="Empleados (claves separadas por coma)" />
            <button (click)="createDepartamento()">Crear</button>
            <button class="secondary" (click)="loadDepartamentos()">Recargar</button>
          </div>

          <ul>
            <li *ngFor="let d of departamentos">
              <strong>{{ d.clave }}</strong> - {{ d.nombre }}
              <span class="meta">{{ d.descripcion }}</span>
              <span class="meta">Empleados: {{ d.empleados.length }}</span>

              <div class="row-wrap inline-controls">
                <input
                  [ngModel]="assignmentInputs[d.clave] ?? ''"
                  (ngModelChange)="assignmentInputs[d.clave] = $event"
                  placeholder="Actualizar empleados por clave (coma)"
                />
                <button class="secondary" (click)="assignToDepartamento(d.clave)">Asignar/Reasignar</button>
                <button class="danger" (click)="deleteDepartamento(d.clave)">Borrar</button>
              </div>
            </li>
          </ul>
        </article>
      </section>

      <p *ngIf="message" class="message">{{ message }}</p>
    </main>
  `
})
class AppComponent {
  private readonly empleadosApi = new EmpleadosApiService();
  private readonly departamentosApi = new DepartamentosApiService();

  auth = {
    username: "admin",
    password: "admin123"
  };

  empleadoForm = {
    clave: "",
    nombre: "",
    direccion: "",
    telefono: ""
  };

  departamentoForm = {
    clave: "",
    nombre: "",
    descripcion: "",
    empleadosClavesText: ""
  };

  empleados: Empleado[] = [];
  departamentos: Departamento[] = [];
  assignmentInputs: Record<string, string | undefined> = {};
  employeeEditDrafts: Record<string, { nombre: string; direccion: string; telefono: string } | undefined> = {};
  message = "";

  async ngOnInit(): Promise<void> {
    this.applyCredentials();
    await this.refreshAll();
  }

  applyCredentials(): void {
    this.empleadosApi.setCredentials(this.auth.username, this.auth.password);
    this.departamentosApi.setCredentials(this.auth.username, this.auth.password);
    this.message = "Credenciales aplicadas";
  }

  clearCredentials(): void {
    this.empleadosApi.clearCredentials();
    this.departamentosApi.clearCredentials();
    this.message = "Credenciales limpiadas";
  }

  async createEmpleado(): Promise<void> {
    try {
      await this.empleadosApi.create({
        clave: this.empleadoForm.clave.trim(),
        nombre: this.empleadoForm.nombre.trim(),
        direccion: this.empleadoForm.direccion.trim(),
        telefono: this.empleadoForm.telefono.trim()
      });
      this.empleadoForm = { clave: "", nombre: "", direccion: "", telefono: "" };
      await this.refreshAll();
      this.message = "Empleado creado";
    } catch (error) {
      this.handleError(error, "No se pudo crear el empleado");
    }
  }

  async deleteEmpleado(clave: string): Promise<void> {
    try {
      await this.empleadosApi.delete(clave);
      await this.refreshAll();
      this.message = `Empleado ${clave} eliminado`;
      delete this.employeeEditDrafts[clave];
    } catch (error) {
      this.handleError(error, `No se pudo borrar empleado ${clave}`);
    }
  }

  async editEmpleado(empleado: Empleado): Promise<void> {
    try {
      const draft = this.employeeEditDrafts[empleado.clave] ?? {
        nombre: empleado.nombre,
        direccion: empleado.direccion,
        telefono: empleado.telefono
      };

      const detail = await this.empleadosApi.getByClave(empleado.clave);
      const etag = detail.etag;
      if (!etag) {
        throw new Error("No se encontro ETag para actualizar empleado");
      }

      await this.empleadosApi.updatePatch(
        empleado.clave,
        {
          nombre: draft.nombre.trim(),
          direccion: draft.direccion.trim(),
          telefono: draft.telefono.trim()
        },
        etag
      );

      await this.refreshAll();
      this.message = `Empleado ${empleado.clave} actualizado`;
    } catch (error) {
      this.handleError(error, `No se pudo actualizar empleado ${empleado.clave}`);
    }
  }

  async loadEmpleados(): Promise<void> {
    try {
      const page = await this.empleadosApi.list(0, 50);
      this.empleados = page.content;
      for (const empleado of this.empleados) {
        if (!this.employeeEditDrafts[empleado.clave]) {
          this.employeeEditDrafts[empleado.clave] = {
            nombre: empleado.nombre,
            direccion: empleado.direccion,
            telefono: empleado.telefono
          };
        }
      }
    } catch (error) {
      this.handleError(error, "No se pudo listar empleados");
    }
  }

  async createDepartamento(): Promise<void> {
    try {
      await this.departamentosApi.create({
        clave: this.departamentoForm.clave.trim(),
        nombre: this.departamentoForm.nombre.trim(),
        descripcion: this.departamentoForm.descripcion.trim(),
        empleadosClaves: this.parseClaves(this.departamentoForm.empleadosClavesText)
      });
      this.departamentoForm = { clave: "", nombre: "", descripcion: "", empleadosClavesText: "" };
      await this.refreshAll();
      this.message = "Departamento creado";
    } catch (error) {
      this.handleError(error, "No se pudo crear el departamento");
    }
  }

  async assignToDepartamento(clave: string): Promise<void> {
    try {
      await this.departamentosApi.updatePatch(clave, {
        empleadosClaves: this.parseClaves(this.assignmentInputs[clave] ?? "")
      });
      await this.refreshAll();
      this.message = `Asignaciones actualizadas para ${clave}`;
    } catch (error) {
      this.handleError(error, `No se pudieron actualizar asignaciones de ${clave}`);
    }
  }

  async deleteDepartamento(clave: string): Promise<void> {
    try {
      await this.departamentosApi.delete(clave);
      await this.refreshAll();
      this.message = `Departamento ${clave} eliminado`;
    } catch (error) {
      this.handleError(error, `No se pudo borrar departamento ${clave}`);
    }
  }

  async loadDepartamentos(): Promise<void> {
    try {
      const page = await this.departamentosApi.list(0, 50);
      this.departamentos = page.content;
      for (const departamento of this.departamentos) {
        if (this.assignmentInputs[departamento.clave] === undefined) {
          this.assignmentInputs[departamento.clave] = "";
        }
      }
    } catch (error) {
      this.handleError(error, "No se pudo listar departamentos");
    }
  }

  private async refreshAll(): Promise<void> {
    await Promise.all([this.loadEmpleados(), this.loadDepartamentos()]);
  }

  private parseClaves(value: string): string[] {
    return value
      .split(",")
      .map((it) => it.trim())
      .filter((it) => it.length > 0);
  }

  setEmpleadoDraftField(clave: string, field: "nombre" | "direccion" | "telefono", value: string): void {
    const current = this.employeeEditDrafts[clave] ?? { nombre: "", direccion: "", telefono: "" };
    this.employeeEditDrafts[clave] = {
      ...current,
      [field]: value
    };
  }

  private handleError(error: unknown, prefix: string): void {
    const detail = error instanceof Error ? error.message : "Error desconocido";
    this.message = `${prefix}: ${detail}`;
  }
}

bootstrapApplication(AppComponent).catch((err) => console.error(err));
