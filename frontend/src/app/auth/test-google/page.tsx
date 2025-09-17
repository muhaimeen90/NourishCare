'use client'

import { signIn, signOut, useSession, getProviders } from 'next-auth/react'
import { useEffect, useState } from 'react'

export default function TestGoogleAuth() {
  const { data: session, status } = useSession()
  const [providers, setProviders] = useState<any>(null)
  const [error, setError] = useState<string>('')
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    async function fetchProviders() {
      try {
        const res = await getProviders()
        setProviders(res)
        console.log('Available providers:', res)
      } catch (err) {
        console.error('Error fetching providers:', err)
        setError('Failed to fetch providers')
      }
    }
    fetchProviders()
  }, [])

  const handleGoogleSignIn = async () => {
    try {
      setLoading(true)
      setError('')
      console.log('Attempting Google sign in...')
      
      // Test if NextAuth endpoints are working
      const providersTest = await fetch('/api/auth/providers')
      const providersData = await providersTest.json()
      console.log('Providers API response:', providersData)
      
      const result = await signIn('google', { 
        callbackUrl: '/dashboard',
        redirect: true  // Let it redirect normally
      })
      console.log('Sign in result:', result)
      
    } catch (err) {
      console.error('Google sign in error:', err)
      setError(`Exception: ${err}`)
      setLoading(false)
    }
  }

  const testDirectGoogleAuth = () => {
    // Direct test of Google OAuth flow
    window.location.href = '/api/auth/signin/google'
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50">
      <div className="max-w-md w-full space-y-6 p-8 bg-white rounded-lg shadow-lg">
        <h1 className="text-2xl font-bold text-center">Test Google OAuth</h1>
        
        {error && (
          <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded">
            {error}
          </div>
        )}
        
        <div className="space-y-4">
          <div>
            <strong>Session Status:</strong> {status}
          </div>
          
          <div>
            <strong>Environment Check:</strong>
            <div className="text-xs mt-2 bg-gray-100 p-2 rounded">
              <div>NEXTAUTH_URL: {process.env.NEXTAUTH_URL || 'Not set'}</div>
              <div>Google Client ID: {process.env.GOOGLE_CLIENT_ID ? 'Set' : 'Not set'}</div>
            </div>
          </div>
          
          <div>
            <strong>Available Providers:</strong>
            <pre className="text-xs mt-2 bg-gray-100 p-2 rounded overflow-auto max-h-32">
              {JSON.stringify(providers, null, 2)}
            </pre>
          </div>
          
          {session ? (
            <div>
              <p>Signed in as: {session.user?.email}</p>
              <button 
                onClick={() => signOut()}
                className="mt-2 px-4 py-2 bg-red-500 text-white rounded hover:bg-red-600"
              >
                Sign Out
              </button>
            </div>
          ) : (
            <div className="space-y-2">
              <button 
                onClick={handleGoogleSignIn}
                disabled={loading}
                className="w-full px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600 disabled:opacity-50"
              >
                {loading ? 'Loading...' : 'Test Google Sign In (via signIn)'}
              </button>
              
              <button 
                onClick={testDirectGoogleAuth}
                className="w-full px-4 py-2 bg-green-500 text-white rounded hover:bg-green-600"
              >
                Test Direct Google Auth
              </button>
            </div>
          )}
          
          <div>
            <h3 className="font-bold">Debug URLs:</h3>
            <ul className="text-sm space-y-1 mt-2">
              <li>
                <a 
                  href="/api/auth/providers" 
                  target="_blank"
                  className="text-blue-500 hover:underline"
                >
                  /api/auth/providers
                </a>
              </li>
              <li>
                <a 
                  href="/api/auth/signin" 
                  target="_blank"
                  className="text-blue-500 hover:underline"
                >
                  /api/auth/signin
                </a>
              </li>
              <li>
                <a 
                  href="/api/auth/signin/google" 
                  target="_blank"
                  className="text-blue-500 hover:underline"
                >
                  /api/auth/signin/google (Direct)
                </a>
              </li>
              <li>
                <a 
                  href="/api/auth/session" 
                  target="_blank"
                  className="text-blue-500 hover:underline"
                >
                  /api/auth/session
                </a>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  )
}