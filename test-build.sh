#!/bin/bash

# Steve Docker Build Test Script
# This script helps test the Docker build process locally

set -e

echo "🚀 Steve Docker Build Test Script"
echo "================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}✓${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}⚠${NC} $1"
}

print_error() {
    echo -e "${RED}✗${NC} $1"
}

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    print_error "Docker is not running. Please start Docker and try again."
    exit 1
fi
print_status "Docker is running"

# Check if we're in the right directory
if [ ! -f "Dockerfile" ]; then
    print_error "Dockerfile not found. Please run this script from the steve directory."
    exit 1
fi
print_status "Found Dockerfile"

# Check if pom.xml exists
if [ ! -f "pom.xml" ]; then
    print_error "pom.xml not found. Please run this script from the steve directory."
    exit 1
fi
print_status "Found pom.xml"

# Build the Docker image
echo ""
echo "🔨 Building Docker image..."
echo "============================"

# Build with different options
echo "Building standard image..."
docker build -t steve:test .

echo ""
echo "Building optimized image..."
if [ -f "Dockerfile.optimized" ]; then
    docker build -f Dockerfile.optimized -t steve:optimized .
    print_status "Optimized image built successfully"
else
    print_warning "Dockerfile.optimized not found, skipping optimized build"
fi

print_status "Standard image built successfully"

# Test the image
echo ""
echo "🧪 Testing the image..."
echo "======================"

# Check if image was created
if docker images | grep -q "steve.*test"; then
    print_status "Image 'steve:test' created successfully"
else
    print_error "Image 'steve:test' not found"
    exit 1
fi

# Show image details
echo ""
echo "📊 Image Information:"
echo "===================="
docker images | grep steve

# Test image startup (without database)
echo ""
echo "🔍 Testing image startup..."
echo "=========================="

# Start container in background
CONTAINER_ID=$(docker run -d -p 8180:8180 -e DB_HOST=localhost steve:test)

# Wait a bit for startup
sleep 5

# Check if container is running
if docker ps | grep -q $CONTAINER_ID; then
    print_status "Container started successfully"
    
    # Check logs
    echo ""
    echo "📋 Container logs (last 10 lines):"
    echo "=================================="
    docker logs --tail 10 $CONTAINER_ID
    
    # Clean up
    docker stop $CONTAINER_ID > /dev/null 2>&1
    docker rm $CONTAINER_ID > /dev/null 2>&1
    print_status "Container cleaned up"
else
    print_error "Container failed to start"
    docker logs $CONTAINER_ID
    docker rm $CONTAINER_ID > /dev/null 2>&1
    exit 1
fi

echo ""
echo "🎉 All tests passed!"
echo "==================="
echo ""
echo "Next steps:"
echo "1. Push your changes to trigger GitHub Actions"
echo "2. Check the Actions tab in GitHub for build status"
echo "3. Your image will be available at: ghcr.io/your-org/steve"
echo ""
echo "To run locally with database:"
echo "docker run -p 8180:8180 -e DB_HOST=your-db-host steve:test"
