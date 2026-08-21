import type { ApiResponse } from '../types'

const API_PREFIX = '/api/v1'

interface RequestOptions extends Omit<RequestInit, 'body'> {
  params?: Record<string, unknown>
  body?: unknown
}

function buildUrl(path: string, params?: RequestOptions['params']): string {
  const url = new URL(`${API_PREFIX}${path}`, window.location.origin)

  if (params) {
    Object.entries(params).forEach(([key, value]) => {
      if (value === undefined || value === '' || value === null) {
        return
      }
      if (Array.isArray(value)) {
        value.forEach((item) => url.searchParams.append(key, String(item)))
        return
      }
      url.searchParams.set(key, String(value))
    })
  }

  return url.toString()
}

export async function request<T>(
  path: string,
  options: RequestOptions = {},
): Promise<T> {
  const { params, headers, body, ...requestOptions } = options
  const response = await fetch(buildUrl(path, params), {
    ...requestOptions,
    headers: {
      'Content-Type': 'application/json',
      ...headers,
    },
    body: body ? JSON.stringify(body) : undefined,
  })

  const result = (await response.json()) as ApiResponse<T>
  if (!response.ok || (result.code !== 0 && result.code !== 200)) {
    throw new Error(result.message || '请求处理失败')
  }

  return result.data
}
