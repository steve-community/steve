# Steve Docker Build Workflows

This repository contains GitHub Actions workflows for building and deploying the Steve OCPP Server Docker container.

## Workflows

### 1. `docker-build.yml` - Basic Build Pipeline
- **Triggers**: Push to main/master/develop branches, tags, and pull requests
- **Features**:
  - Builds Docker image using Dockerfile
  - Pushes to GitHub Container Registry (ghcr.io)
  - Generates Software Bill of Materials (SBOM)
  - Runs security scans with Trivy
  - Multi-platform builds (AMD64, ARM64)

### 2. `ci-cd.yml` - Advanced CI/CD Pipeline
- **Triggers**: Push to main/master/develop branches, tags, pull requests, and manual dispatch
- **Features**:
  - Runs comprehensive tests with multiple database versions
  - Builds and pushes Docker image
  - Security scanning
  - Environment-based deployments (staging/production)
  - Manual deployment triggers

### 3. `quick-build.yml` - Development Build
- **Triggers**: Push to develop branch and pull requests to develop
- **Features**:
  - Fast build for development
  - Pushes to develop branch with commit SHA tag

## Usage

### Automatic Builds
- Push to `main`/`master`: Full CI/CD pipeline with production deployment
- Push to `develop`: Quick build for development
- Create tags (`v*`): Full pipeline with production deployment
- Pull requests: Build and test only

### Manual Deployment
1. Go to Actions tab in GitHub
2. Select "Advanced Build Pipeline"
3. Click "Run workflow"
4. Choose environment (staging/production)

### Container Images
Images are pushed to GitHub Container Registry:
- `ghcr.io/your-org/steve:latest` (main branch)
- `ghcr.io/your-org/steve:develop-<sha>` (develop branch)
- `ghcr.io/your-org/steve:v1.0.0` (tagged releases)

## Configuration

### Environment Variables
- `REGISTRY`: Container registry (default: ghcr.io)
- `IMAGE_NAME`: Image name (default: github.repository)
- `JAVA_VERSION`: Java version (default: 21)

### Secrets Required
- `GITHUB_TOKEN`: Automatically provided by GitHub Actions

### Dockerfile Options
- `Dockerfile`: Standard build (current)
- `Dockerfile.optimized`: Multi-stage build with better caching and security

## Local Development

### Build locally:
```bash
docker build -t steve:local .
```

### Build with optimized Dockerfile:
```bash
docker build -f Dockerfile.optimized -t steve:optimized .
```

### Run with database:
```bash
docker run -p 8180:8180 -e DB_HOST=your-db-host steve:local
```

## Security Features
- Vulnerability scanning with Trivy
- SBOM generation for supply chain security
- Non-root user in optimized Dockerfile
- Multi-platform builds for better compatibility

## Troubleshooting

### Build Failures
1. Check Maven dependencies in pom.xml
2. Verify Java version compatibility
3. Check database connection settings

### Deployment Issues
1. Verify environment secrets are set
2. Check Kubernetes cluster connectivity
3. Validate image pull policies

### Performance
- Use `Dockerfile.optimized` for better build caching
- Enable GitHub Actions cache for faster builds
- Use multi-stage builds to reduce image size
