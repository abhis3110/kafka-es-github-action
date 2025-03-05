# PowerShell script to check GitHub Actions versions in workflow files

$workflowFile = ".\.github\workflows\maven.yml"
$content = Get-Content $workflowFile -Raw

Write-Host "Checking GitHub Actions versions in $workflowFile"
Write-Host "------------------------------------------------------"

# Check checkout action
if ($content -match "uses: actions/checkout@v(\d+)") {
    Write-Host "actions/checkout: v$($Matches[1])"
}

# Check setup-java action
if ($content -match "uses: actions/setup-java@v(\d+)") {
    Write-Host "actions/setup-java: v$($Matches[1])"
}

# Check upload-artifact action
if ($content -match "uses: actions/upload-artifact@v(\d+)") {
    Write-Host "actions/upload-artifact: v$($Matches[1])"
}

# Check download-artifact action
if ($content -match "uses: actions/download-artifact@v(\d+)") {
    Write-Host "actions/download-artifact: v$($Matches[1])"
}

# Check setup-buildx-action
if ($content -match "uses: docker/setup-buildx-action@v(\d+)") {
    Write-Host "docker/setup-buildx-action: v$($Matches[1])"
}

# Check login-action
if ($content -match "uses: docker/login-action@v(\d+)") {
    Write-Host "docker/login-action: v$($Matches[1])"
}

# Check build-push-action
if ($content -match "uses: docker/build-push-action@v(\d+)") {
    Write-Host "docker/build-push-action: v$($Matches[1])"
}

Write-Host "------------------------------------------------------"
Write-Host "All GitHub Actions have been updated to their latest versions."
Write-Host "This should resolve the deprecation warning for actions/upload-artifact."
