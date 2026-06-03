export interface EmpleadoResumen {
  clave: string;
  nombre: string;
}

export interface Departamento {
  clave: string;
  nombre: string;
  descripcion: string;
  empleados: EmpleadoResumen[];
  version?: number;
}

export interface DepartamentoPageResponse {
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  content: Departamento[];
}

export class DepartamentosApiService {
  private readonly baseUrl = "/api/v1/departamentos";
  private authHeaderValue: string | null = null;

  setCredentials(username: string, password: string): void {
    this.authHeaderValue = "Basic " + btoa(`${username}:${password}`);
  }

  clearCredentials(): void {
    this.authHeaderValue = null;
  }

  async create(payload: {
    clave: string;
    nombre: string;
    descripcion: string;
    empleadosClaves: string[];
  }): Promise<Departamento> {
    return this.request<Departamento>(this.baseUrl, {
      method: "POST",
      body: JSON.stringify(payload)
    });
  }

  async list(page: number, size: number): Promise<DepartamentoPageResponse> {
    return this.request<DepartamentoPageResponse>(`${this.baseUrl}?page=${page}&size=${size}`, {
      method: "GET"
    });
  }

  async updatePatch(clave: string, payload: {
    nombre?: string;
    descripcion?: string;
    empleadosClaves?: string[];
  }): Promise<Departamento> {
    return this.request<Departamento>(`${this.baseUrl}/${encodeURIComponent(clave)}`, {
      method: "PATCH",
      body: JSON.stringify(payload)
    });
  }

  async delete(clave: string): Promise<void> {
    await this.request<void>(`${this.baseUrl}/${encodeURIComponent(clave)}`, {
      method: "DELETE"
    });
  }

  private async request<T>(url: string, init: RequestInit): Promise<T> {
    const response = await this.rawRequest(url, init);
    if (response.status === 204) {
      return undefined as T;
    }
    return (await response.json()) as T;
  }

  private async rawRequest(url: string, init: RequestInit): Promise<Response> {
    const headers: Record<string, string> = {
      "Content-Type": "application/json",
      ...(init.headers as Record<string, string> | undefined)
    };

    if (this.authHeaderValue) {
      headers["Authorization"] = this.authHeaderValue;
    }

    const response = await fetch(url, {
      ...init,
      headers
    });

    if (!response.ok) {
      throw new Error(`HTTP ${response.status}`);
    }

    return response;
  }
}
