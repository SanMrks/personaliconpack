$ErrorActionPreference = "Stop"

$toolsDir = "E:\Android\tools"

# 1. Download android.jar (~25 MB)
$androidJar = Join-Path $toolsDir "android.jar"
if (-not (Test-Path $androidJar)) {
    Write-Host "Downloading android.jar..."
    $androidJarUrl = "https://github.com/Sable/android-platforms/raw/master/android-34/android.jar"
    Invoke-WebRequest -Uri $androidJarUrl -OutFile $androidJar -UseBasicParsing
}

# 2. Download Uber APK Signer (~3 MB)
$signerJar = Join-Path $toolsDir "uber-apk-signer.jar"
if (-not (Test-Path $signerJar)) {
    Write-Host "Downloading Uber APK Signer..."
    $signerUrl = "https://github.com/patrickfav/uber-apk-signer/releases/download/v1.3.0/uber-apk-signer-1.3.0.jar"
    Invoke-WebRequest -Uri $signerUrl -OutFile $signerJar -UseBasicParsing
}

Write-Host "All tools ready!"
