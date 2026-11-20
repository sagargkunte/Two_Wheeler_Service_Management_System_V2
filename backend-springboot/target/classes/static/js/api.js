/**
 * API Client & Global Helpers for Two-Wheeler Service Management System
 */

// Dynamically determine backend URL: If running from port 8080 (served by Spring Boot), use relative "";
// If opened directly from file:// or a dev server, use http://localhost:8080.
const API_BASE_URL = (window.location.port === "8080") ? "" : "http://localhost:8080";

const api = {
  baseUrl: API_BASE_URL,

  async request(endpoint, options = {}) {
    const url = `${this.baseUrl}${endpoint}`;
    const defaultHeaders = {
      "Content-Type": "application/json",
    };

    const config = {
      ...options,
      headers: {
        ...defaultHeaders,
        ...options.headers,
      },
      credentials: "include", // essential for session cookies
    };

    try {
      const response = await fetch(url, config);
      const isJson = response.headers.get("content-type")?.includes("application/json");
      const data = isJson ? await response.json() : await response.text();

      if (!response.ok) {
        const errorMsg = (typeof data === "object" && data.message) ? data.message : "Request failed";
        throw new Error(errorMsg);
      }

      return data;
    } catch (error) {
      console.error(`API Error on [${options.method || "GET"} ${endpoint}]:`, error);
      throw error;
    }
  },

  get(endpoint) {
    return this.request(endpoint, { method: "GET" });
  },

  post(endpoint, body) {
    return this.request(endpoint, {
      method: "POST",
      body: JSON.stringify(body),
    });
  },

  put(endpoint, body) {
    return this.request(endpoint, {
      method: "PUT",
      body: JSON.stringify(body),
    });
  },

  // Auth helper
  async checkAuth() {
    try {
      const user = await this.get("/api/me");
      return user;
    } catch (err) {
      return null;
    }
  },

  async logout() {
    try {
      await this.get("/auth/logout");
    } catch (e) {}
    sessionStorage.clear();
    window.location.href = "login.html";
  },

  // URL query parameter helper
  getQueryParam(param) {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get(param);
  }
};
