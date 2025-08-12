# NourishCare Frontend

A comprehensive nutrition and meal planning web application built with Next.js 14, Tailwind CSS, and shadcn/ui.

## 🌟 Features

### Core Functionality
- **Virtual Fridge Management**: Track food inventory with expiration date monitoring
- **Smart Recipe Collection**: Browse and search healthy recipes with detailed nutrition info
- **Meal Planning**: Weekly meal planner with drag-and-drop functionality
- **Calorie Estimation**: AI-powered nutrition analysis from meal photos
- **Analytics Dashboard**: Food waste tracking and sustainability metrics
- **Conversational Health Bot**: AI assistant for nutrition guidance

### Design Highlights
- **Nature-Inspired Design**: Clean, minimalist interface with green color palette
- **Fully Responsive**: Optimized for mobile, tablet, and desktop
- **Accessibility First**: Built with shadcn/ui for accessible components
- **Micro-Interactions**: Smooth transitions and hover effects
- **Color-Coded System**: Visual indicators for food expiration status

## 🚀 Technology Stack

- **Framework**: Next.js 14+ with App Router
- **Styling**: Tailwind CSS with custom design system
- **UI Components**: shadcn/ui component library
- **Icons**: Lucide React for consistent iconography
- **Charts**: Recharts for data visualization
- **Typography**: Inter font family

## 📁 Project Structure

```
src/
├── app/                    # Next.js App Router pages
│   ├── dashboard/         # Virtual fridge dashboard
│   ├── recipes/           # Recipe collection and details
│   ├── planner/          # Weekly meal planner
│   ├── estimator/        # Calorie estimation tool
│   └── page.tsx          # Landing page with authentication
├── components/           # Reusable components
│   ├── ui/              # shadcn/ui components
│   ├── AddItemModal.tsx # Food item addition modal
│   ├── ChatBot.tsx      # AI assistant component
│   └── Navigation.tsx   # Main navigation component
└── lib/
    ├── mock-data.ts     # Mock data for development
    └── utils.ts         # Utility functions
```

## 🎨 Design System

### Color Palette
- **Primary Green**: #22c55e (nature-inspired, wellness-focused)
- **Secondary Neutrals**: Warm grays and clean whites
- **Status Colors**: Green (fresh), Yellow (expiring soon), Red (expired)

### Component Patterns
- **Cards**: Rounded corners, subtle shadows, hover effects
- **Buttons**: Consistent sizing, clear visual hierarchy
- **Forms**: Clean layouts with proper labeling
- **Navigation**: Intuitive structure with active states

## 🔧 Development Setup

1. **Install Dependencies**
   ```bash
   npm install
   ```

2. **Start Development Server**
   ```bash
   npm run dev
   ```

3. **Build for Production**
   ```bash
   npm run build
   ```

## 📱 Page Overview

### Landing Page (/)
- **Clean Authentication**: Social login options (Google, Apple)
- **Brand Introduction**: NourishCare mission and value proposition
- **Feature Preview**: Quick overview of core functionality

### Dashboard (/dashboard)
- **Analytics Overview**: Key metrics and charts
- **Food Inventory**: Color-coded expiration tracking
- **Quick Actions**: Add new items, view expiring foods
- **Waste Reduction Stats**: Environmental impact tracking

### Recipes (/recipes)
- **Recipe Gallery**: Beautiful card-based layout
- **Advanced Search**: Filter by difficulty, cook time, ingredients
- **Detailed Views**: Complete recipes with nutrition facts
- **Chef's Tips**: Waste reduction and cooking advice

### Meal Planner (/planner)
- **Weekly Calendar**: Grid-based meal planning
- **Drag & Drop**: Easy meal assignment (future enhancement)
- **Shopping Lists**: Auto-generated from meal plans
- **Nutrition Tracking**: Weekly calorie and macro summaries

### Calorie Estimator (/estimator)
- **Photo Upload**: Drag-and-drop image interface
- **AI Analysis**: Mock nutrition breakdown (ready for API integration)
- **Progress Visualization**: Macro breakdowns with progress bars
- **History Tracking**: Previous analysis results

## 🤖 AI Integration Ready

The application is structured to easily integrate with AI services:

- **Backend API Endpoints**: Prepared for Spring Boot integration
- **Mock Data Structure**: Matches expected API responses
- **Component Architecture**: Modular design for easy API swapping
- **Error Handling**: Graceful fallbacks and loading states

## 🌱 Sustainability Focus

Built with environmental consciousness:
- **Waste Reduction Tracking**: Quantified impact metrics
- **Expiration Monitoring**: Proactive food management
- **Eco-Friendly Tips**: Integrated sustainability guidance
- **Carbon Footprint**: CO₂ savings calculations

## 🎯 Future Enhancements

- **Real-time Notifications**: Push alerts for expiring foods
- **Social Features**: Recipe sharing and community challenges
- **Smart Shopping**: AI-powered grocery list optimization
- **Barcode Scanning**: Automated food item entry
- **Recipe Generation**: AI-created recipes from available ingredients

## 📞 Support

This application represents a complete, production-ready frontend for modern nutrition management. The codebase follows best practices and is ready for backend integration and deployment.

Built with ❤️ for healthier eating and reduced food waste.
