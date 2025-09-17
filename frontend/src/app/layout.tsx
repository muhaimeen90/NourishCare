import type { Metadata } from "next";
import { Geist, Geist_Mono } from "next/font/google";
import "./globals.css";
import { Navigation } from "@/components/Navigation";
import { AuthProvider } from "@/contexts/AuthContext";
import { NextAuthProvider } from "@/components/NextAuthProvider";

const geistSans = Geist({
  variable: "--font-geist-sans",
  subsets: ["latin"],
});

const geistMono = Geist_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin"],
});

export const metadata: Metadata = {
  title: "NourishCare - Smart Food Management",
  description: "Track your food inventory, reduce waste, and share with your community",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body
        className={`${geistSans.variable} ${geistMono.variable} antialiased`}
      >
        <NextAuthProvider>
          <AuthProvider>
            <div className="min-h-screen bg-gray-50">
              <Navigation />
              <main className="pb-4">
                {children}
              </main>
            </div>
          </AuthProvider>
        </NextAuthProvider>
      </body>
    </html>
  );
}
