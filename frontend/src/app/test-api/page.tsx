'use client'

import { useState } from 'react'
import { useAuth } from '@/contexts/AuthContext'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'

export default function TestAPI() {
  const { user, token } = useAuth()
  const [results, setResults] = useState<any>({})
  const [loading, setLoading] = useState('')

  const testEndpoint = async (name: string, url: string, options: any = {}) => {
    setLoading(name)
    try {
      const response = await fetch(url, {
        ...options,
        headers: {
          'Content-Type': 'application/json',
          ...(token && { 'Authorization': `Bearer ${token}` }),
          ...options.headers
        }
      })
      
      const data = response.ok ? await response.json() : await response.text()
      setResults(prev => ({
        ...prev,
        [name]: {
          status: response.status,
          ok: response.ok,
          data: data
        }
      }))
    } catch (error) {
      setResults(prev => ({
        ...prev,
        [name]: {
          status: 'Error',
          ok: false,
          data: error.message
        }
      }))
    } finally {
      setLoading('')
    }
  }

  const runAllTests = async () => {
    const API_URL = process.env.NEXT_PUBLIC_API_URL
    
    // Test API Gateway health
    await testEndpoint('API Gateway Health', `${API_URL}/actuator/health`)
    
    // Test User Service
    await testEndpoint('User Service Health', `${API_URL}/api/auth/validate`, {
      method: 'GET'
    })
    
    // Test Inventory Service
    if (user?.id) {
      await testEndpoint('Get User Inventory', `${API_URL}/api/inventory/users/${user.id}/items`)
      
      // Test create inventory item
      await testEndpoint('Create Inventory Item', `${API_URL}/api/inventory/items`, {
        method: 'POST',
        body: JSON.stringify({
          name: 'Test Item',
          quantity: '1',
          unit: 'piece',
          category: 'Other',
          expirationDate: '2025-12-31',
          userId: user.id
        })
      })
    }
  }

  return (
    <div className="container mx-auto p-8 max-w-4xl">
      <h1 className="text-3xl font-bold mb-6">API Test Dashboard</h1>
      
      <div className="mb-6">
        <p><strong>API URL:</strong> {process.env.NEXT_PUBLIC_API_URL}</p>
        <p><strong>User ID:</strong> {user?.id || 'Not authenticated'}</p>
        <p><strong>Token:</strong> {token ? 'Present' : 'Missing'}</p>
      </div>

      <div className="mb-6">
        <Button onClick={runAllTests} disabled={!!loading}>
          {loading ? `Testing ${loading}...` : 'Run All Tests'}
        </Button>
      </div>

      <div className="grid gap-4">
        {Object.entries(results).map(([name, result]: [string, any]) => (
          <Card key={name}>
            <CardHeader>
              <CardTitle className="flex items-center justify-between">
                {name}
                <span className={`px-2 py-1 rounded text-sm ${
                  result.ok 
                    ? 'bg-green-100 text-green-800' 
                    : 'bg-red-100 text-red-800'
                }`}>
                  {result.status}
                </span>
              </CardTitle>
            </CardHeader>
            <CardContent>
              <pre className="bg-gray-100 p-3 rounded text-sm overflow-auto max-h-40">
                {JSON.stringify(result.data, null, 2)}
              </pre>
            </CardContent>
          </Card>
        ))}
      </div>
    </div>
  )
}