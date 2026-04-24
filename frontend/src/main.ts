import { bootstrapApplication } from "@angular/platform-browser";
import { Component, Injectable, OnInit, inject } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { CanActivateFn, Router, RouterOutlet, Routes, provideRouter } from "@angular/router";
import { Empleado, EmpleadosApiService } from "./app/core/api/empleados-api.service";
import { Departamento, DepartamentosApiService } from "./app/core/api/departamentos-api.service";

@Injectable({ providedIn: "root" })
class AuthStateService {
  username = "admin";
  password = "admin123";
  isAuthenticated = false;

  setCredentials(username: string, password: string): void {
    this.username = username;
    this.password = password;
  }

  setAuthenticated(value: boolean): void {
    this.isAuthenticated = value;
  }

  logout(): void {
    this.isAuthenticated = false;
  }
}

const panelGuard: CanActivateFn = () => {
  const auth = inject(AuthStateService);
  const router = inject(Router);
  return auth.isAuthenticated ? true : router.createUrlTree(["/login"]);
};

@Component({
  selector: "app-login-page",
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <main class="shell login-shell">
      <header class="hero">
        <p class="eyebrow">DSW01 Practica02</p>
        <h1>Iniciar sesión</h1>
        <p class="hero-copy">Accede al panel con credenciales Basic válidas.</p>
      </header>

      <section class="card login-card">
        <h2>Acceso</h2>
        <div class="row">
          <label>
            Usuario
            <input [(ngModel)]="username" placeholder="admin" />
          </label>
          <label>
            Password
            <input [(ngModel)]="password" type="password" placeholder="admin123" />
          </label>
          <button (click)="login()">Entrar</button>
        </div>
        <p class="message" *ngIf="message">{{ message }}</p>
      </section>
    </main>
  `
})
class LoginPageComponent {
  private readonly authState = inject(AuthStateService);
  private readonly router = inject(Router);

  username = this.authState.username;
  password = this.authState.password;
  message = "Usa tus credenciales para continuar.";

  async login(): Promise<void> {
    const empleadosApi = new EmpleadosApiService();
    const departamentosApi = new DepartamentosApiService();

    empleadosApi.setCredentials(this.username, this.password);
    departamentosApi.setCredentials(this.username, this.password);

    try {
      await Promise.all([empleadosApi.list(0, 1), departamentosApi.list(0, 1)]);
      this.authState.setCredentials(this.username, this.password);
      this.authState.setAuthenticated(true);
      this.message = "Sesión iniciada correctamente";
      await this.router.navigate(["/panel"]);
    } catch (error) {
      this.authState.logout();
      const detail = error instanceof Error ? error.message : "Error desconocido";
      this.message = `No se pudo iniciar sesión: ${detail}`;
    }
  }
}

@Component({
  selector: "app-dashboard-page",
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <main class="shell">
      <header class="hero">
        <p class="eyebrow">DSW01 Practica02</p>
        <h1>Panel Operativo</h1>
        <p class="hero-copy">Gestiona empleados y departamentos.</p>
      </header>

      <section class="card session-bar">
        <div>
          <h2>Sesión activa</h2>
          <p class="meta">Usuario: {{ authState.username || 'sin usuario' }}</p>
        </div>
        <button class="secondary" (click)="logout()">Cerrar sesión</button>
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
              <span class="meta">Versión: {{ e.version ?? 0 }}</span>
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
              <span class="meta">Versión: {{ d.version ?? 0 }}</span>
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
class DashboardPageComponent implements OnInit {
  readonly authState = inject(AuthStateService);
  private readonly router = inject(Router);

  private readonly empleadosApi = new EmpleadosApiService();
  private readonly departamentosApi = new DepartamentosApiService();

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

  ngOnInit(): void {
    if (!this.authState.isAuthenticated) {
      this.router.navigate(["/login"]);
      return;
    }

    this.empleadosApi.setCredentials(this.authState.username, this.authState.password);
    this.departamentosApi.setCredentials(this.authState.username, this.authState.password);
    this.refreshAll();
  }

  async logout(): Promise<void> {
    this.authState.logout();
    this.empleadosApi.clearCredentials();
    this.departamentosApi.clearCredentials();
    this.empleados = [];
    this.departamentos = [];
    this.assignmentInputs = {};
    this.employeeEditDrafts = {};
    this.empleadoForm = { clave: "", nombre: "", direccion: "", telefono: "" };
    this.departamentoForm = { clave: "", nombre: "", descripcion: "", empleadosClavesText: "" };
    this.message = "Sesión cerrada";
    await this.router.navigate(["/login"]);
  }

  async createEmpleado(): Promise<void> {
    this.requireAuthenticated();
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
    this.requireAuthenticated();
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
    this.requireAuthenticated();
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
    this.requireAuthenticated();
    try {
      const page = await this.empleadosApi.list(0, 50);
      this.empleados = page.content;
      for (const empleado of this.empleados) {
        this.employeeEditDrafts[empleado.clave] ??= {
          nombre: empleado.nombre,
          direccion: empleado.direccion,
          telefono: empleado.telefono
        };
      }
    } catch (error) {
      this.handleError(error, "No se pudo listar empleados");
    }
  }

  async createDepartamento(): Promise<void> {
    this.requireAuthenticated();
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
    this.requireAuthenticated();
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
    this.requireAuthenticated();
    try {
      await this.departamentosApi.delete(clave);
      await this.refreshAll();
      this.message = `Departamento ${clave} eliminado`;
    } catch (error) {
      this.handleError(error, `No se pudo borrar departamento ${clave}`);
    }
  }

  async loadDepartamentos(): Promise<void> {
    this.requireAuthenticated();
    try {
      const page = await this.departamentosApi.list(0, 50);
      this.departamentos = page.content;
      for (const departamento of this.departamentos) {
        this.assignmentInputs[departamento.clave] ??= "";
      }
    } catch (error) {
      this.handleError(error, "No se pudo listar departamentos");
    }
  }

  private async refreshAll(): Promise<void> {
    await Promise.all([this.loadEmpleados(), this.loadDepartamentos()]);
  }

  private requireAuthenticated(): void {
    if (!this.authState.isAuthenticated) {
      throw new Error("Debes iniciar sesión para acceder al panel");
    }
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

@Component({
  selector: "app-root",
  standalone: true,
  imports: [RouterOutlet],
  template: `<router-outlet></router-outlet>`
})
class AppComponent {}

const routes: Routes = [
  { path: "login", component: LoginPageComponent },
  { path: "panel", component: DashboardPageComponent, canActivate: [panelGuard] },
  { path: "", pathMatch: "full", redirectTo: "login" },
  { path: "**", redirectTo: "login" }
];

bootstrapApplication(AppComponent, {
  providers: [provideRouter(routes)]
}).catch((err) => console.error(err));
