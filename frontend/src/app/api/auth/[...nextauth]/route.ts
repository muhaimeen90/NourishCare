import NextAuth from 'next-auth'
import GoogleProvider from 'next-auth/providers/google'

// Type extensions
declare module 'next-auth' {
  interface Session {
    accessToken?: string
    user: {
      id: string
      name?: string | null
      email?: string | null
      image?: string | null
    }
  }
  
  interface User {
    backendUserId?: string
  }
}

declare module 'next-auth/jwt' {
  interface JWT {
    accessToken?: string
    userId?: string
    backendUserId?: string
    googleId?: string
  }
}

// Debug logging
console.log('NextAuth Environment Check:', {
  GOOGLE_CLIENT_ID: process.env.GOOGLE_CLIENT_ID ? 'Set' : 'Not set',
  GOOGLE_CLIENT_SECRET: process.env.GOOGLE_CLIENT_SECRET ? 'Set' : 'Not set',
  NEXTAUTH_URL: process.env.NEXTAUTH_URL,
  NEXTAUTH_SECRET: process.env.NEXTAUTH_SECRET ? 'Set' : 'Not set',
})

const handler = NextAuth({
  providers: [
    GoogleProvider({
      clientId: process.env.GOOGLE_CLIENT_ID!,
      clientSecret: process.env.GOOGLE_CLIENT_SECRET!,
    }),
  ],
  callbacks: {
    async jwt({ token, account, user }) {
      if (account && user) {
        console.log('JWT callback - account:', account.provider, 'user:', user.email)
        token.accessToken = account.access_token
        token.googleId = user.id
        token.email = user.email
        
        // Use the backend user ID that was set during signIn callback
        token.backendUserId = (user as any).backendUserId
        token.userId = token.backendUserId || user.id
        
        console.log('JWT callback - setting userId to:', token.userId)
      }
      return token
    },
    async session({ session, token }) {
      console.log('Session callback - session:', session.user?.email)
      if (session.user) {
        session.user.id = token.userId as string
        session.accessToken = token.accessToken as string
      }
      return session
    },
    async signIn({ user, account, profile }) {
      console.log('SignIn callback - provider:', account?.provider, 'user:', user.email)
      if (account?.provider === 'google') {
        try {
          // Create user in our backend system
          console.log('Creating user in backend for:', user.email)
          const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/api/auth/google-signin`, {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json',
            },
            body: JSON.stringify({
              googleId: user.id,
              email: user.email,
              name: user.name,
              image: user.image,
              firstName: (profile as any)?.given_name || user.name?.split(' ')[0] || '',
              lastName: (profile as any)?.family_name || user.name?.split(' ').slice(1).join(' ') || '',
            }),
          })
          
          if (response.ok) {
            const userData = await response.json()
            console.log('Backend user created successfully:', userData)
            // Store backend user ID (extend user object)
            ;(user as any).backendUserId = userData.id
            console.log('Backend user ID set to:', userData.id)
            return true
          } else {
            console.error('Backend user creation failed:', response.status, await response.text())
            // Still allow OAuth login even if backend fails - use Google ID as fallback
            ;(user as any).backendUserId = user.id
            return true
          }
        } catch (error) {
          console.error('Error creating user in backend:', error)
          // Use Google ID as fallback if backend fails
          ;(user as any).backendUserId = user.id
          return true // Still allow OAuth login even if backend fails
        }
      }
      return true
    },
  },
  pages: {
    signIn: '/auth/login',
    error: '/auth/error',
  },
  session: {
    strategy: 'jwt',
  },
  debug: true, // Enable debug mode
})

export { handler as GET, handler as POST }