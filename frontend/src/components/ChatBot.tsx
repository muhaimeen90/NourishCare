'use client';

import { useState } from 'react';
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Card, CardContent } from '@/components/ui/card';
import { MessageCircle, Send, Bot, User } from 'lucide-react';

interface Message {
  id: string;
  text: string;
  isBot: boolean;
  timestamp: Date;
}

const mockResponses = [
  "Based on your ingredients, I recommend making a delicious spinach and salmon salad! This will help you use items that are expiring soon.",
  "Great choice! Here's a quick recipe: Sauté the spinach with garlic, season the salmon with herbs, and serve together with a lemon dressing.",
  "I notice you have chicken breast expiring soon. Would you like some recipe suggestions to use it up?",
  "Remember to check your expiration dates regularly. I can send you notifications when items are about to expire!",
  "For meal planning, I suggest using your vegetables first as they tend to expire faster than other items.",
];

export function ChatBot() {
  const [isOpen, setIsOpen] = useState(false);
  const [messages, setMessages] = useState<Message[]>([
    {
      id: '1',
      text: 'Hi! I\'m your NourishCare assistant. I can help you with recipes, meal planning, and reducing food waste. How can I help you today?',
      isBot: true,
      timestamp: new Date('2025-08-11T09:00:00Z'),
    },
  ]);
  const [inputValue, setInputValue] = useState('');
  const [messageIdCounter, setMessageIdCounter] = useState(2);

  const handleSendMessage = () => {
    if (!inputValue.trim()) return;

    // Add user message
    const userMessage: Message = {
      id: messageIdCounter.toString(),
      text: inputValue,
      isBot: false,
      timestamp: new Date(),
    };

    setMessages(prev => [...prev, userMessage]);
    setInputValue('');
    setMessageIdCounter(prev => prev + 1);

    // Simulate bot response
    setTimeout(() => {
      const botMessage: Message = {
        id: (messageIdCounter + 1).toString(),
        text: mockResponses[Math.floor(Math.random() * mockResponses.length)],
        isBot: true,
        timestamp: new Date(),
      };
      setMessages(prev => [...prev, botMessage]);
      setMessageIdCounter(prev => prev + 1);
    }, 1000);
  };

  const handleKeyPress = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter') {
      handleSendMessage();
    }
  };

  return (
    <>
      {/* Floating Chat Button */}
      <Button
        onClick={() => setIsOpen(true)}
        className="fixed bottom-6 right-6 h-14 w-14 rounded-full bg-green-600 hover:bg-green-700 shadow-lg z-50"
        size="icon"
      >
        <MessageCircle className="h-6 w-6" />
      </Button>

      {/* Chat Dialog */}
      <Dialog open={isOpen} onOpenChange={setIsOpen}>
        <DialogContent className="sm:max-w-md h-[600px] flex flex-col">
          <DialogHeader>
            <DialogTitle className="flex items-center gap-2">
              <div className="bg-green-100 p-2 rounded-full">
                <Bot className="h-5 w-5 text-green-600" />
              </div>
              NourishCare Assistant
            </DialogTitle>
          </DialogHeader>

          {/* Messages Container */}
          <div className="flex-1 overflow-y-auto space-y-4 py-4">
            {messages.map((message) => (
              <div
                key={message.id}
                className={`flex gap-3 ${message.isBot ? 'justify-start' : 'justify-end'}`}
              >
                {message.isBot && (
                  <div className="bg-green-100 p-2 rounded-full h-8 w-8 flex items-center justify-center flex-shrink-0">
                    <Bot className="h-4 w-4 text-green-600" />
                  </div>
                )}
                
                <Card className={`max-w-[80%] ${message.isBot ? 'bg-gray-50' : 'bg-green-600'}`}>
                  <CardContent className="p-3">
                    <p className={`text-sm ${message.isBot ? 'text-gray-900' : 'text-white'}`}>
                      {message.text}
                    </p>
                    <p className={`text-xs mt-1 ${message.isBot ? 'text-gray-500' : 'text-green-100'}`}>
                      {message.timestamp.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                    </p>
                  </CardContent>
                </Card>

                {!message.isBot && (
                  <div className="bg-green-600 p-2 rounded-full h-8 w-8 flex items-center justify-center flex-shrink-0">
                    <User className="h-4 w-4 text-white" />
                  </div>
                )}
              </div>
            ))}
          </div>

          {/* Input Section */}
          <div className="flex gap-2 pt-4 border-t">
            <Input
              placeholder="Ask me anything about your food..."
              value={inputValue}
              onChange={(e) => setInputValue(e.target.value)}
              onKeyPress={handleKeyPress}
              className="flex-1"
            />
            <Button 
              onClick={handleSendMessage}
              disabled={!inputValue.trim()}
              className="bg-green-600 hover:bg-green-700"
            >
              <Send className="h-4 w-4" />
            </Button>
          </div>

          {/* Quick Actions */}
          <div className="flex flex-wrap gap-2 pt-2">
            {['Recipe suggestions', 'Expiring items', 'Meal plan help'].map((action) => (
              <Button
                key={action}
                variant="outline"
                size="sm"
                onClick={() => {
                  setInputValue(action);
                  handleSendMessage();
                }}
                className="text-xs"
              >
                {action}
              </Button>
            ))}
          </div>
        </DialogContent>
      </Dialog>
    </>
  );
}
