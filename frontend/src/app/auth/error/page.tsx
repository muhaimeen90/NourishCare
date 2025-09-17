'use client'

import { useSearchParams } from 'next/navigation'
import Link from 'next/link'
import { Button } from '@/components/ui/button'

export default function AuthError() {
  const searchParams = useSearchParams()
  const error = searchParams.get('error')

  const getErrorMessage = (error: string | null) => {
    switch (error) {
      case 'Configuration':
        return 'There is a problem with the server configuration.'
      case 'AccessDenied':
        return 'You do not have permission to sign in.'
      case 'Verification':
        return 'The verification token has expired or has already been used.'
      case 'Callback':
        return 'There was an error with the OAuth callback. Please check your Google OAuth configuration.'
      default:
        return 'An unknown error occurred during authentication.'
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50">
      <div className="max-w-md w-full space-y-6 p-8 bg-white rounded-lg shadow-lg">
        <div className="text-center">
          <h1 className="text-2xl font-bold text-gray-900 mb-2">Authentication Error</h1>
          <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded mb-4">
            <p className="font-medium">Error: {error}</p>
            <p className="text-sm mt-1">{getErrorMessage(error)}</p>
          </div>
          
          {error === 'Callback' && (
            <div className="bg-blue-50 border border-blue-200 text-blue-700 px-4 py-3 rounded mb-4">
              <p className="font-medium">Google OAuth Callback Error</p>
              <p className="text-sm mt-1">
                Please ensure your Google Cloud Console has the correct redirect URI:
              </p>
              <code className="block text-xs mt-1 bg-white p-2 rounded border">
                http://localhost:3000/api/auth/callback/google
              </code>
            </div>
          )}
          
          <div className="space-y-3">
            <Button asChild className="w-full">
              <Link href="/auth/login">
                Try Again
              </Link>
            </Button>
            
            <Button asChild variant="outline" className="w-full">
              <Link href="/">
                Go Home
              </Link>
            </Button>
          </div>
          
          <div className="mt-6 text-xs text-gray-500">
            <p>Debug Information:</p>
            <p>Error Code: <code>{error}</code></p>
            <p>URL: <code>{window.location.href}</code></p>
          </div>
        </div>
      </div>
    </div>
  )
}