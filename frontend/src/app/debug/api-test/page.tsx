'use client'

import { useState } from 'react'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { useAuth } from '@/contexts/AuthContext'

export default function ApiTestPage() {
  const { user, token } = useAuth()
  const [results, setResults] = useState<Record<string, any>>({})
  const [loading, setLoading] = useState<string | null>(null)

  const testEndpoint = async (name: string, url: string, options: RequestInit = {}) => {
    setLoading(name)
    try {
      const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL}${url}`, {
        ...options,
        headers: {
          'Content-Type': 'application/json',
          ...(token ? { 'Authorization': `Bearer ${token}` } : {}),
          ...options.headers,
        },
      })

      const result = {
        status: response.status,
        statusText: response.statusText,
        ok: response.ok,
        data: null as any,
        error: null as string | null,
      }

      if (response.ok) {
        try {
          result.data = await response.json()
        } catch (e) {
          result.data = 'No JSON response'
        }
      } else {
        try {
          result.error = await response.text()
        } catch (e) {
          result.error = `HTTP ${response.status} ${response.statusText}`
        }
      }

      setResults(prev => ({ ...prev, [name]: result }))
    } catch (error) {
      setResults(prev => ({ 
        ...prev, 
        [name]: { 
          status: 0, 
          statusText: 'Network Error', 
          ok: false, 
          data: null, 
          error: error instanceof Error ? error.message : 'Unknown error' 
        }
      }))
    } finally {
      setLoading(null)
    }
  }

  const endpoints = [
    { name: 'Health Check', url: '/actuator/health' },
    { name: 'API Gateway Health', url: '/' },
    { name: 'Inventory Items (All)', url: '/api/inventory/items' },
    { name: 'User Items', url: user?.id ? `/api/inventory/users/${user.id}/items` : null },
    { name: 'Community Donations', url: '/api/community/donations/available' },
    { name: 'User Service Health', url: '/api/auth/validate' },
    { name: 'Recipes', url: '/api/recipes' },
  ].filter(endpoint => endpoint.url !== null) as Array<{ name: string; url: string }>

  const testCreateItem = async () => {
    const testItem = {
      name: 'Test Item',
      quantity: '1',
      unit: 'piece',
      category: 'Test',
      expirationDate: '2025-12-31',
      userId: user?.id || 'test-user'
    }

    await testEndpoint('Create Item (POST)', '/api/inventory/items', {
      method: 'POST',
      body: JSON.stringify(testItem)
    })
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="max-w-4xl mx-auto">
        <h1 className="text-3xl font-bold mb-6">API Endpoint Testing</h1>
        
        {/* User Info */}
        <Card className="mb-6">
          <CardHeader>
            <CardTitle>Authentication Status</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-2">
              <p><strong>User:</strong> {user ? `${user.email} (${user.id})` : 'Not authenticated'}</p>
              <p><strong>Token:</strong> {token ? 'Present' : 'Missing'}</p>
              <p><strong>API URL:</strong> {process.env.NEXT_PUBLIC_API_URL}</p>
            </div>
          </CardContent>
        </Card>

        {/* Test Buttons */}
        <Card className="mb-6">
          <CardHeader>
            <CardTitle>API Tests</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="grid grid-cols-2 md:grid-cols-3 gap-3 mb-4">
              {endpoints.map(endpoint => (
                <Button
                  key={endpoint.name}
                  variant="outline"
                  size="sm"
                  disabled={loading === endpoint.name}
                  onClick={() => testEndpoint(endpoint.name, endpoint.url)}
                >
                  {loading === endpoint.name ? 'Testing...' : endpoint.name}
                </Button>
              ))}
            </div>
            
            <div className="flex gap-2">
              <Button
                variant="default"
                size="sm"
                disabled={loading === 'Create Item (POST)' || !user}
                onClick={testCreateItem}
              >
                {loading === 'Create Item (POST)' ? 'Creating...' : 'Test Create Item (POST)'}
              </Button>
              
              <Button
                variant="secondary"
                size="sm"
                onClick={() => setResults({})}
              >
                Clear Results
              </Button>
            </div>
          </CardContent>
        </Card>

        {/* Results */}
        <div className="space-y-4">
          {Object.entries(results).map(([name, result]) => (
            <Card key={name}>
              <CardHeader>
                <div className="flex justify-between items-center">
                  <CardTitle className="text-lg">{name}</CardTitle>
                  <Badge variant={result.ok ? 'default' : 'destructive'}>
                    {result.status} {result.statusText}
                  </Badge>
                </div>
              </CardHeader>
              <CardContent>
                {result.ok ? (
                  <div className="space-y-2">
                    <p className="text-green-600 font-medium">✅ Success</p>
                    {result.data && (
                      <div className="bg-green-50 p-3 rounded border">
                        <p className="text-sm font-medium mb-1">Response Data:</p>
                        <pre className="text-xs overflow-auto max-h-40">
                          {JSON.stringify(result.data, null, 2)}
                        </pre>
                      </div>
                    )}
                  </div>
                ) : (
                  <div className="space-y-2">
                    <p className="text-red-600 font-medium">❌ Failed</p>
                    {result.error && (
                      <div className="bg-red-50 p-3 rounded border">
                        <p className="text-sm font-medium mb-1">Error:</p>
                        <p className="text-xs text-red-700">{result.error}</p>
                      </div>
                    )}
                  </div>
                )}
              </CardContent>
            </Card>
          ))}
        </div>
      </div>
    </div>
  )
}