# NourishCare Frontend - Copilot Instructions

## Project Overview
NourishCare is a comprehensive nutrition and meal planning web application built with Next.js 14, Tailwind CSS, and shadcn/ui. The application focuses on reducing food waste through smart inventory management and promoting healthy eating habits.

## Technology Stack
- **Framework**: Next.js 14+ with App Router
- **Styling**: Tailwind CSS with custom green-focused design system
- **UI Components**: shadcn/ui for accessible, themeable components
- **Icons**: Lucide React for consistent iconography
- **Charts**: Recharts for data visualization
- **TypeScript**: Full type safety throughout the application

## Code Style Guidelines

### Component Structure
- Use functional components with hooks
- Prefer named exports for components
- Include proper TypeScript interfaces for props
- Use 'use client' directive for client-side components

### Styling Approach
- Utilize Tailwind CSS utility classes
- Follow the established green color palette (#22c55e primary)
- Use rounded corners (rounded-lg, rounded-full) for modern feel
- Implement hover states and transitions for interactivity

### File Organization
```
src/
├── app/                 # Next.js App Router pages
├── components/          # Reusable components
│   └── ui/             # shadcn/ui components
└── lib/                # Utilities and mock data
```

## Design Principles

### Color System
- **Primary**: Green shades for nature/wellness theme
- **Secondary**: Warm neutrals and clean whites
- **Status Colors**: 
  - Green: Fresh items (5+ days until expiration)
  - Yellow: Warning items (2-4 days until expiration) 
  - Red: Critical items (0-1 day until expiration)

### UI Patterns
- **Cards**: Primary container component with subtle shadows
- **Buttons**: Consistent sizing, green primary color
- **Forms**: Clean layouts with proper validation
- **Navigation**: Responsive with mobile-friendly hamburger menu

## Key Features Implementation

### Virtual Fridge (/dashboard)
- Color-coded food item cards based on expiration
- Analytics dashboard with charts
- Add item modal with multiple entry methods
- Real-time expiration tracking

### Recipe Collection (/recipes)
- Searchable recipe gallery
- Detailed recipe views with nutrition info
- Filter by difficulty, cook time, ingredients
- Recipe action buttons (save, share, add to meal plan)

### Meal Planner (/planner)
- Weekly calendar grid layout
- Meal assignment for breakfast/lunch/dinner
- Shopping list generation
- Nutrition summaries

### Calorie Estimator (/estimator)
- Photo upload interface
- Mock AI analysis simulation
- Nutrition breakdown visualization
- Analysis history tracking

## Mock Data Structure
All components use structured mock data from `src/lib/mock-data.ts`:
- `mockFoodItems`: Inventory items with expiration tracking
- `mockRecipes`: Recipe collection with detailed information
- `mockMealPlan`: Weekly meal assignments
- `mockNutritionData`: Nutrition analysis results

## Backend Integration Ready
The frontend is designed for easy Spring Boot backend integration:
- API-ready data structures
- Proper error handling patterns
- Loading state implementations
- Optimistic updates support

## Accessibility Features
- Semantic HTML structure
- Keyboard navigation support
- Screen reader compatibility
- High contrast color combinations
- Focus management in modals

## Development Best Practices
- Use TypeScript for type safety
- Implement proper error boundaries
- Include loading states for async operations
- Follow React hooks best practices
- Maintain consistent component patterns

## Testing Considerations
- Components are designed for easy unit testing
- Mock data enables predictable test scenarios
- Proper separation of concerns
- Clear component interfaces

When making changes to this codebase:
1. Maintain the established design system
2. Follow the component structure patterns
3. Use existing mock data when possible
4. Ensure responsive design principles
5. Keep accessibility in mind
6. Update TypeScript interfaces as needed
