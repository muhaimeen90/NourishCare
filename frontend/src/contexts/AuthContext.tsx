'use client'

import React, { createContext, useContext, useState, useEffect } from 'react'
import { useSession, signIn, signOut } from 'next-auth/react'

interface User {
  id: string
  username: string
  email: string
  firstName: string
  lastName: string
  roles: string[]
  image?: string
}

interface AuthContextType {
  user: User | null
  token: string | null
  login: (token: string, userData: User) => void
  logout: () => void
  signInWithGoogle: () => void
  isAuthenticated: boolean
  isLoading: boolean
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const { data: session, status } = useSession()
  const [user, setUser] = useState<User | null>(null)
  const [token, setToken] = useState<string | null>(null)
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    if (status === 'loading') return

    if (session?.user) {
      // Handle NextAuth session (Google OAuth)
      const sessionUser: User = {
        id: session.user.id || '',
        username: session.user.email?.split('@')[0] || '',
        email: session.user.email || '',
        firstName: session.user.name?.split(' ')[0] || '',
        lastName: session.user.name?.split(' ').slice(1).join(' ') || '',
        roles: ['USER'],
        image: session.user.image || undefined
      }
      setUser(sessionUser)
      setToken(session.accessToken as string || 'oauth-session')
    } else {
      // Check for stored authentication data (regular login)
      const storedToken = localStorage.getItem('token')
      const storedUser = localStorage.getItem('user')
      
      if (storedToken && storedUser) {
        try {
          const userData = JSON.parse(storedUser)
          setToken(storedToken)
          setUser(userData)
        } catch (error) {
          console.error('Error parsing stored user data:', error)
          localStorage.removeItem('token')
          localStorage.removeItem('user')
        }
      }
    }
    
    setIsLoading(false)
  }, [session, status])

  const login = (authToken: string, userData: User) => {
    setToken(authToken)
    setUser(userData)
    localStorage.setItem('token', authToken)
    localStorage.setItem('user', JSON.stringify(userData))
  }

  const logout = async () => {
    if (session) {
      // NextAuth logout
      await signOut({ redirect: false })
    }
    // Regular logout
    setToken(null)
    setUser(null)
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  const signInWithGoogle = () => {
    signIn('google', { callbackUrl: '/dashboard' })
  }

  const value = {
    user,
    token,
    login,
    logout,
    signInWithGoogle,
    isAuthenticated: !!user && (!!token || !!session),
    isLoading: isLoading || status === 'loading',
  }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider')
  }
  return context
}