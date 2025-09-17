'use client';

import React, { useState, useEffect } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Input } from "@/components/ui/input";
import { Separator } from "@/components/ui/separator";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { MapPin, Clock, Phone, Mail, Users, TrendingUp, Heart, Search, Plus } from 'lucide-react';
import Link from 'next/link';
import { donationApi } from '@/lib/api';
import { useAuth } from '@/contexts/AuthContext';

// Types
interface FoodItem {
  id: string;
  name: string;
  category: string;
  quantity: number | string; // Can be number or string like "1 kg"
  unit: string;
  expirationDate: string;
  isOpened: boolean;
}

interface FoodDonation {
  id: string;
  donorId: string;
  donorName: string;
  donorPhone: string;
  donorEmail: string;
  address: string;
  city: string;
  pickupInstructions?: string;
  description?: string;
  foodItems: FoodItem[];
  status: 'AVAILABLE' | 'TAKEN' | 'CANCELLED' | 'EXPIRED';
  createdAt: string;
  updatedAt: string;
}

interface DonationStats {
  totalAvailableDonations: number;
  totalCompletedDonations: number;
  recentDonations: number;
}

const DonationsPage = () => {
  const { user, isAuthenticated } = useAuth();
  const [donations, setDonations] = useState<FoodDonation[]>([]);
  const [userDonations, setUserDonations] = useState<FoodDonation[]>([]);
  const [filteredDonations, setFilteredDonations] = useState<FoodDonation[]>([]);
  const [stats, setStats] = useState<DonationStats>({
    totalAvailableDonations: 0,
    totalCompletedDonations: 0,
    recentDonations: 0
  });
  const [loading, setLoading] = useState(true);
  const [userDonationsLoading, setUserDonationsLoading] = useState(true);
  const [searchCity, setSearchCity] = useState('');
  const [error, setError] = useState<string | null>(null);

  // API functions with fallback to mock data
  const fetchDonations = async (): Promise<FoodDonation[]> => {
    try {
      return await donationApi.getAvailable();
    } catch (error) {
      console.error('Error fetching donations:', error);
      // Return mock data for development
      return [
        {
          id: '1',
          donorId: 'user123',
          donorName: 'Sarah Johnson',
          donorPhone: '+1 (555) 123-4567',
          donorEmail: 'sarah.johnson@email.com',
          address: '123 Maple Street, Apt 4B',
          city: 'New York',
          pickupInstructions: 'Ring doorbell. Available evenings after 6 PM.',
          description: 'Fresh groceries from my weekly shopping - won\'t be able to use them before expiry.',
          foodItems: [
            {
              id: 'item1',
              name: 'Organic Spinach',
              category: 'Vegetables',
              quantity: 2,
              unit: 'bunches',
              expirationDate: '2025-09-20',
              isOpened: false
            },
            {
              id: 'item2',
              name: 'Greek Yogurt',
              category: 'Dairy',
              quantity: 1,
              unit: 'container',
              expirationDate: '2025-09-19',
              isOpened: false
            },
            {
              id: 'item3',
              name: 'Whole Wheat Bread',
              category: 'Bakery',
              quantity: 1,
              unit: 'loaf',
              expirationDate: '2025-09-18',
              isOpened: false
            }
          ],
          status: 'AVAILABLE',
          createdAt: '2025-09-17T10:30:00Z',
          updatedAt: '2025-09-17T10:30:00Z'
        },
        {
          id: '2',
          donorId: 'user456',
          donorName: 'Michael Chen',
          donorPhone: '+1 (555) 987-6543',
          donorEmail: 'michael.chen@email.com',
          address: '456 Oak Avenue',
          city: 'Brooklyn',
          pickupInstructions: 'Leave at front door if no answer. Ring twice.',
          description: 'Cooked too much for the weekend gathering!',
          foodItems: [
            {
              id: 'item4',
              name: 'Homemade Vegetable Soup',
              category: 'Prepared Food',
              quantity: 2,
              unit: 'containers',
              expirationDate: '2025-09-18',
              isOpened: false
            },
            {
              id: 'item5',
              name: 'Fresh Baked Cookies',
              category: 'Dessert',
              quantity: 24,
              unit: 'pieces',
              expirationDate: '2025-09-19',
              isOpened: false
            }
          ],
          status: 'AVAILABLE',
          createdAt: '2025-09-17T14:15:00Z',
          updatedAt: '2025-09-17T14:15:00Z'
        }
      ];
    }
  };

  const fetchStats = async (): Promise<DonationStats> => {
    try {
      return await donationApi.getStats();
    } catch (error) {
      console.error('Error fetching stats:', error);
      // Return mock data for development
      return {
        totalAvailableDonations: 2,
        totalCompletedDonations: 15,
        recentDonations: 8
      };
    }
  };

  const fetchUserDonations = async (userId: string): Promise<FoodDonation[]> => {
    try {
      return await donationApi.getUserDonations(userId);
    } catch (error) {
      console.error('Error fetching user donations:', error);
      return [];
    }
  };

  useEffect(() => {
    const loadData = async () => {
      try {
        setLoading(true);
        const [donationsData, statsData] = await Promise.all([
          fetchDonations(),
          fetchStats()
        ]);
        setDonations(donationsData);
        setFilteredDonations(donationsData);
        setStats(statsData);
      } catch (err) {
        setError('Failed to load donations data');
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, []);

  // Load user donations when user is authenticated
  useEffect(() => {
    const loadUserDonations = async () => {
      if (!user?.id || !isAuthenticated) {
        setUserDonations([]);
        setUserDonationsLoading(false);
        return;
      }

      try {
        setUserDonationsLoading(true);
        const userDonationsData = await fetchUserDonations(user.id);
        setUserDonations(userDonationsData);
      } catch (err) {
        console.error('Failed to load user donations:', err);
        setUserDonations([]);
      } finally {
        setUserDonationsLoading(false);
      }
    };

    loadUserDonations();
  }, [user?.id, isAuthenticated]);

  useEffect(() => {
    if (searchCity.trim() === '') {
      setFilteredDonations(donations);
    } else {
      const filtered = donations.filter(donation => 
        donation.city.toLowerCase().includes(searchCity.toLowerCase())
      );
      setFilteredDonations(filtered);
    }
  }, [searchCity, donations]);

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const getDaysUntilExpiry = (expirationDate: string) => {
    const today = new Date();
    const expiry = new Date(expirationDate);
    const diffTime = expiry.getTime() - today.getTime();
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    return diffDays;
  };

  const getExpiryBadgeColor = (days: number) => {
    if (days <= 1) return 'destructive';
    if (days <= 3) return 'secondary';
    return 'default';
  };

  const DonationCard = ({ donation }: { donation: FoodDonation }) => (
    <Card className="hover:shadow-lg transition-shadow">
      <CardHeader>
        <div className="flex justify-between items-start">
          <div>
            <CardTitle className="text-lg">{donation.donorName}</CardTitle>
            <CardDescription className="flex items-center mt-1">
              <MapPin className="w-4 h-4 mr-1" />
              {donation.address}, {donation.city}
            </CardDescription>
          </div>
          <Badge variant="outline" className="ml-2">
            {donation.foodItems.length} items
          </Badge>
        </div>
      </CardHeader>
      <CardContent className="space-y-4">
        {donation.description && (
          <p className="text-sm text-muted-foreground">{donation.description}</p>
        )}
        
        <div className="space-y-2">
          <h4 className="font-medium">Available Items:</h4>
          <div className="space-y-2">
            {donation.foodItems.map((item) => {
              const daysLeft = getDaysUntilExpiry(item.expirationDate);
              return (
                <div key={item.id} className="flex justify-between items-center p-2 bg-muted rounded-lg">
                  <div className="flex-1">
                    <span className="font-medium">{item.name}</span>
                    <span className="text-sm text-muted-foreground ml-2">
                      {item.quantity} {item.unit}
                    </span>
                  </div>
                  <Badge variant={getExpiryBadgeColor(daysLeft)} className="text-xs">
                    {daysLeft === 0 ? 'Today' : daysLeft === 1 ? '1 day' : `${daysLeft} days`}
                  </Badge>
                </div>
              );
            })}
          </div>
        </div>

        {donation.pickupInstructions && (
          <div className="p-3 bg-blue-50 rounded-lg">
            <h5 className="text-sm font-medium text-blue-900 mb-1">Pickup Instructions:</h5>
            <p className="text-sm text-blue-800">{donation.pickupInstructions}</p>
          </div>
        )}

        <Separator />

        <div className="flex justify-between items-center">
          <div className="text-sm text-muted-foreground">
            <Clock className="w-4 h-4 inline mr-1" />
            Posted {formatDate(donation.createdAt)}
          </div>
          
          <div className="flex space-x-2">
            <Button size="sm" variant="outline">
              <Phone className="w-4 h-4 mr-1" />
              Call
            </Button>
            <Button size="sm" variant="outline">
              <Mail className="w-4 h-4 mr-1" />
              Email
            </Button>
            <Button size="sm">
              <Heart className="w-4 h-4 mr-1" />
              Claim
            </Button>
          </div>
        </div>
      </CardContent>
    </Card>
  );

  if (loading) {
    return (
      <div className="container mx-auto px-4 py-8">
        <div className="flex items-center justify-center min-h-[400px]">
          <div className="text-center">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary mx-auto"></div>
            <p className="mt-4 text-muted-foreground">Loading donations...</p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="mb-8">
        <div className="flex justify-between items-center mb-6">
          <div>
            <h1 className="text-3xl font-bold">Community Food Sharing</h1>
            <p className="text-muted-foreground mt-2">
              Help reduce food waste by sharing with your community
            </p>
          </div>
          <Link href="/donations/donate">
            <Button>
              <Plus className="w-4 h-4 mr-2" />
              Donate Food
            </Button>
          </Link>
        </div>

        {/* Stats Cards */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">Available Now</CardTitle>
              <Users className="h-4 w-4 text-muted-foreground" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">{stats.totalAvailableDonations}</div>
              <p className="text-xs text-muted-foreground">donations ready for pickup</p>
            </CardContent>
          </Card>
          
          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">Successfully Shared</CardTitle>
              <TrendingUp className="h-4 w-4 text-muted-foreground" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">{stats.totalCompletedDonations}</div>
              <p className="text-xs text-muted-foreground">donations completed this month</p>
            </CardContent>
          </Card>
          
          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">Recent Activity</CardTitle>
              <Heart className="h-4 w-4 text-muted-foreground" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">{stats.recentDonations}</div>
              <p className="text-xs text-muted-foreground">new donations this week</p>
            </CardContent>
          </Card>
        </div>
      </div>

      <Tabs defaultValue="browse" className="space-y-4">
        <TabsList>
          <TabsTrigger value="browse">Browse Donations</TabsTrigger>
          <TabsTrigger value="my-donations">My Donations</TabsTrigger>
        </TabsList>

        <TabsContent value="browse" className="space-y-4">
          {/* Search and Filters */}
          <Card>
            <CardHeader>
              <CardTitle className="text-lg">Find Food Near You</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="flex space-x-4">
                <div className="flex-1">
                  <div className="relative">
                    <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-muted-foreground w-4 h-4" />
                    <Input
                      placeholder="Search by city..."
                      value={searchCity}
                      onChange={(e) => setSearchCity(e.target.value)}
                      className="pl-10"
                    />
                  </div>
                </div>
              </div>
            </CardContent>
          </Card>

          {/* Donations List */}
          {error ? (
            <Card>
              <CardContent className="py-8">
                <div className="text-center text-muted-foreground">
                  <p>{error}</p>
                  <p className="text-sm mt-2">Showing sample data for demonstration</p>
                </div>
              </CardContent>
            </Card>
          ) : null}

          {filteredDonations.length === 0 ? (
            <Card>
              <CardContent className="py-12">
                <div className="text-center text-muted-foreground">
                  <Heart className="w-12 h-12 mx-auto mb-4 opacity-50" />
                  <h3 className="text-lg font-medium mb-2">No donations available</h3>
                  <p>
                    {searchCity 
                      ? `No donations found in "${searchCity}". Try searching for a different city.`
                      : 'Be the first to share food with your community!'
                    }
                  </p>
                  <Link href="/donations/donate" className="mt-4 inline-block">
                    <Button>
                      <Plus className="w-4 h-4 mr-2" />
                      Donate Food
                    </Button>
                  </Link>
                </div>
              </CardContent>
            </Card>
          ) : (
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
              {filteredDonations.map((donation) => (
                <DonationCard key={donation.id} donation={donation} />
              ))}
            </div>
          )}
        </TabsContent>

        <TabsContent value="my-donations">
          {!isAuthenticated ? (
            <Card>
              <CardContent className="py-12">
                <div className="text-center text-muted-foreground">
                  <Users className="w-12 h-12 mx-auto mb-4 opacity-50" />
                  <h3 className="text-lg font-medium mb-2">Sign In Required</h3>
                  <p className="mb-4">Please sign in to view your donations</p>
                  <Link href="/auth/login">
                    <Button>
                      Sign In
                    </Button>
                  </Link>
                </div>
              </CardContent>
            </Card>
          ) : userDonationsLoading ? (
            <div className="text-center py-8">
              <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-green-600 mx-auto mb-4"></div>
              <p className="text-gray-600">Loading your donations...</p>
            </div>
          ) : userDonations.length === 0 ? (
            <Card>
              <CardContent className="py-12">
                <div className="text-center text-muted-foreground">
                  <Users className="w-12 h-12 mx-auto mb-4 opacity-50" />
                  <h3 className="text-lg font-medium mb-2">No Donations Yet</h3>
                  <p className="mb-4">You haven't created any donations yet. Start sharing your food with the community!</p>
                  <Link href="/donations/donate">
                    <Button>
                      <Plus className="w-4 h-4 mr-2" />
                      Create Your First Donation
                    </Button>
                  </Link>
                </div>
              </CardContent>
            </Card>
          ) : (
            <div className="space-y-6">
              <div className="flex justify-between items-center">
                <h2 className="text-2xl font-bold">Your Donations</h2>
                <Link href="/donations/donate">
                  <Button>
                    <Plus className="w-4 h-4 mr-2" />
                    Create New Donation
                  </Button>
                </Link>
              </div>
              
              <div className="grid gap-6">
                {userDonations.map((donation) => (
                  <Card key={donation.id} className="overflow-hidden">
                    <CardHeader className="pb-3">
                      <div className="flex justify-between items-start">
                        <div>
                          <CardTitle className="text-lg">{donation.donorName}</CardTitle>
                          <CardDescription className="flex items-center mt-1">
                            <MapPin className="w-4 h-4 mr-1" />
                            {donation.address}, {donation.city}
                          </CardDescription>
                        </div>
                        <Badge variant={donation.status === 'AVAILABLE' ? 'default' : 
                                     donation.status === 'TAKEN' ? 'secondary' : 
                                     donation.status === 'CANCELLED' ? 'destructive' : 'outline'}>
                          {donation.status}
                        </Badge>
                      </div>
                    </CardHeader>
                    
                    <CardContent>
                      <div className="space-y-4">
                        {/* Food Items */}
                        <div>
                          <h4 className="font-medium mb-2">Food Items ({donation.foodItems.length})</h4>
                          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-2">
                            {donation.foodItems.map((item) => (
                              <div key={item.id} className="flex justify-between items-center p-2 bg-muted rounded">
                                <div>
                                  <span className="font-medium text-sm">{item.name}</span>
                                  <p className="text-xs text-muted-foreground">
                                    {typeof item.quantity === 'string' ? item.quantity : `${item.quantity} ${item.unit}`} • {item.category}
                                  </p>
                                </div>
                                <Badge variant="outline" className="text-xs">
                                  {(() => {
                                    const today = new Date();
                                    const expiry = new Date(item.expirationDate);
                                    const diffTime = expiry.getTime() - today.getTime();
                                    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
                                    return diffDays === 0 ? 'Today' : diffDays === 1 ? '1 day' : `${diffDays} days`;
                                  })()}
                                </Badge>
                              </div>
                            ))}
                          </div>
                        </div>
                        
                        {/* Contact and Pickup Info */}
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4 pt-4 border-t">
                          <div>
                            <h5 className="font-medium text-sm mb-2">Contact Information</h5>
                            <div className="space-y-1 text-sm text-muted-foreground">
                              <div className="flex items-center">
                                <Phone className="w-3 h-3 mr-2" />
                                {donation.donorPhone}
                              </div>
                              {donation.donorEmail && (
                                <div className="flex items-center">
                                  <Mail className="w-3 h-3 mr-2" />
                                  {donation.donorEmail}
                                </div>
                              )}
                            </div>
                          </div>
                          
                          <div>
                            <h5 className="font-medium text-sm mb-2">Created</h5>
                            <div className="flex items-center text-sm text-muted-foreground">
                              <Clock className="w-3 h-3 mr-2" />
                              {new Date(donation.createdAt).toLocaleDateString('en-US', {
                                year: 'numeric',
                                month: 'short',
                                day: 'numeric',
                                hour: '2-digit',
                                minute: '2-digit'
                              })}
                            </div>
                          </div>
                        </div>
                        
                        {/* Pickup Instructions */}
                        {donation.pickupInstructions && (
                          <div className="pt-4 border-t">
                            <h5 className="font-medium text-sm mb-2">Pickup Instructions</h5>
                            <p className="text-sm text-muted-foreground">{donation.pickupInstructions}</p>
                          </div>
                        )}
                        
                        {/* Description */}
                        {donation.description && (
                          <div className="pt-4 border-t">
                            <h5 className="font-medium text-sm mb-2">Description</h5>
                            <p className="text-sm text-muted-foreground">{donation.description}</p>
                          </div>
                        )}
                      </div>
                    </CardContent>
                  </Card>
                ))}
              </div>
            </div>
          )}
        </TabsContent>
      </Tabs>
    </div>
  );
};

export default DonationsPage;