# Build and push Docker image to GitHub Container Registry
$IMAGE = "ghcr.io/lazarvuksanovic/pravda-article-service:latest"

Write-Host "Building Docker image: $IMAGE" -ForegroundColor Cyan
docker build -t $IMAGE .

if ($LASTEXITCODE -ne 0) {
    Write-Host "Build failed!" -ForegroundColor Red
    exit 1
}

Write-Host "Pushing Docker image: $IMAGE" -ForegroundColor Cyan
docker push $IMAGE

if ($LASTEXITCODE -ne 0) {
    Write-Host "Push failed!" -ForegroundColor Red
    exit 1
}

Write-Host "Successfully built and pushed $IMAGE" -ForegroundColor Green
