#!/usr/bin/env python3

import requests
import json
import base64
from pathlib import Path

def test_vision_api():
    # API endpoint
    url = "http://localhost:8080/api/vision/detect-food"
    
    # Test with a food image (you should replace this with the path to your test image)
    # For this test, I'll create a small test payload
    test_image_path = input("Enter the path to your food image: ").strip()
    
    if not Path(test_image_path).exists():
        print(f"Error: Image file '{test_image_path}' not found!")
        return
    
    try:
        # Read and encode the image
        with open(test_image_path, 'rb') as image_file:
            image_data = base64.b64encode(image_file.read()).decode('utf-8')
        
        # Prepare the request payload
        payload = {
            "imageData": image_data
        }
        
        headers = {
            'Content-Type': 'application/json'
        }
        
        print("Sending image to vision service...")
        print(f"Image size: {len(image_data)} characters (base64)")
        
        # Make the request
        response = requests.post(url, json=payload, headers=headers, timeout=60)
        
        print(f"\nResponse Status: {response.status_code}")
        
        if response.status_code == 200:
            result = response.json()
            print("\n=== VISION SERVICE RESULTS ===")
            print(json.dumps(result, indent=2))
            
            if result.get('detectedFoods'):
                print(f"\n=== SUMMARY ===")
                print(f"Total food items detected: {len(result['detectedFoods'])}")
                
                total_calories = 0
                for i, food in enumerate(result['detectedFoods'], 1):
                    print(f"\n{i}. {food.get('label', 'Unknown')}")
                    print(f"   USDA ID: {food.get('usdaFdcId', 'N/A')}")
                    print(f"   Estimated Weight: {food.get('estimatedGrams', 0):.1f}g")
                    print(f"   Estimated Calories: {food.get('estimatedCalories', 0):.1f} kcal")
                    print(f"   Confidence: {food.get('confidence', 0):.2f}")
                    print(f"   Estimation Method: {food.get('estimationMethod', 'N/A')}")
                    
                    if food.get('referenceObject'):
                        ref = food['referenceObject']
                        print(f"   Reference Object: {ref.get('type', 'N/A')} ({ref.get('confidence', 0):.2f} confidence)")
                    
                    total_calories += food.get('estimatedCalories', 0)
                
                print(f"\nTotal Estimated Calories: {total_calories:.1f} kcal")
            else:
                print("No food items detected.")
        else:
            print(f"Error: {response.status_code}")
            print(response.text)
            
    except requests.exceptions.Timeout:
        print("Request timed out. The vision service might be processing a large image.")
    except requests.exceptions.ConnectionError:
        print("Connection error. Make sure the API Gateway is running on localhost:8080")
    except Exception as e:
        print(f"Error: {e}")

if __name__ == "__main__":
    test_vision_api()