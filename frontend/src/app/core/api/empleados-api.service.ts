export interface Empleado {
  clave: string;
  nombre: string;
  direccion: string;
  telefono: string;
  departamento?: {
    clave: string;
    nombre: string;
  } | null;
  version?: number;
}

export interface EmpleadoPageResponse {
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  content: Empleado[];
}

export class EmpleadosApiService {
  private readonly baseUrl = "/api/empleados";
  private authHeaderValue: string | null = null;

  setCredentials(username: string, password: string): void {
    this.authHeaderValue = "Basic " + btoa(`${username}:${password}`);
  }

  clearCredentials(): void {
    this.authHeaderValue = null;
  }

  async create(payload: Empleado): Promise<Empleado> {
    return this.request<Empleado>(this.baseUrl, {
      method: "POST",
      body: JSON.stringify(payload)
    });
  }

  async getByClave(clave: string): Promise<{ empleado: Empleado; etag: string | null }> {
    const response = await this.rawRequest(`${this.baseUrl}/${encodeURIComponent(clave)}`, {
      method: "GET"
    });

    const empleado = (await response.json()) as Empleado;
    return { empleado, etag: response.headers.get("ETag") };
  }

  async list(page: number, size: number): Promise<EmpleadoPageResponse> {
    return this.request<EmpleadoPageResponse>(`${this.baseUrl}?page=${page}&size=${size}`, {
      method: "GET"
    });
  }

  async updatePut(clave: string, payload: Omit<Empleado, "clave">, etag: string): Promise<Empleado> {
    return this.request<Empleado>(`${this.baseUrl}/${encodeURIComponent(clave)}`, {
      method: "PUT",
      headers: { "If-Match": etag },
      body: JSON.stringify(payload)
    });
  }

  async updatePatch(clave: string, payload: Partial<Omit<Empleado, "clave">>, etag: string): Promise<Empleado> {
    return this.request<Empleado>(`${this.baseUrl}/${encodeURIComponent(clave)}`, {
      method: "PATCH",
      headers: { "If-Match": etag },
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
