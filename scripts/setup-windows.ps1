<#
  Prepara un PC con Windows para compilar Panbu SIN Android Studio.

  Instala:
    - JDK 21 (Eclipse Temurin) con winget
    - Android SDK command-line tools en %LOCALAPPDATA%\Android\Sdk (verifica el checksum oficial)
    - platform-tools (adb), Android 36, build-tools 35 y el driver USB de Google
  Configura para tu usuario: JAVA_HOME, ANDROID_HOME y adb en el PATH.

  Uso (PowerShell, desde la carpeta del repositorio):
      powershell -ExecutionPolicy Bypass -File scripts\setup-windows.ps1

  Después cierra y vuelve a abrir VS Code (o la terminal) para que tome las variables nuevas.
#>
$ErrorActionPreference = 'Stop'
$ProgressPreference = 'SilentlyContinue'   # Invoke-WebRequest es mucho más rápido sin barra de progreso

# --- 1. JDK 21 ---------------------------------------------------------------
$jdkRoot = 'C:\Program Files\Eclipse Adoptium'
$jdk = Get-ChildItem $jdkRoot -Directory -Filter 'jdk-21*' -ErrorAction SilentlyContinue |
    Sort-Object Name | Select-Object -Last 1
if (-not $jdk) {
    Write-Host '== Instalando JDK 21 (Temurin) con winget =='
    winget install --id EclipseAdoptium.Temurin.21.JDK -e --silent --accept-source-agreements --accept-package-agreements
    $jdk = Get-ChildItem $jdkRoot -Directory -Filter 'jdk-21*' -ErrorAction SilentlyContinue |
        Sort-Object Name | Select-Object -Last 1
}
if (-not $jdk) { throw 'No se encontró el JDK 21. Instálalo a mano desde https://adoptium.net (versión 21, Windows x64).' }
$env:JAVA_HOME = $jdk.FullName
[Environment]::SetEnvironmentVariable('JAVA_HOME', $jdk.FullName, 'User')
Write-Host "JAVA_HOME = $($jdk.FullName)"

# --- 2. Android command-line tools -------------------------------------------
$sdk = Join-Path $env:LOCALAPPDATA 'Android\Sdk'
$sdkManager = Join-Path $sdk 'cmdline-tools\latest\bin\sdkmanager.bat'
if (-not (Test-Path $sdkManager)) {
    Write-Host '== Descargando Android command-line tools =='
    $repo = [xml](Invoke-WebRequest 'https://dl.google.com/android/repository/repository2-3.xml' -UseBasicParsing).Content
    $pkg = $repo.GetElementsByTagName('remotePackage') |
        Where-Object { $_.path -eq 'cmdline-tools;latest' } | Select-Object -First 1
    $archive = $pkg.GetElementsByTagName('archive') |
        Where-Object { $_.'host-os' -eq 'windows' } | Select-Object -First 1
    $file = $archive.complete.url
    $sha1 = $archive.complete.checksum
    if ($sha1 -is [System.Xml.XmlElement]) { $sha1 = $sha1.'#text' }

    $tmp = Join-Path $env:TEMP 'panbu-cmdline-tools'
    Remove-Item $tmp -Recurse -Force -ErrorAction SilentlyContinue
    New-Item $tmp -ItemType Directory | Out-Null
    $zip = Join-Path $tmp 'tools.zip'
    Invoke-WebRequest "https://dl.google.com/android/repository/$file" -OutFile $zip -UseBasicParsing

    $hash = (Get-FileHash $zip -Algorithm SHA1).Hash.ToLower()
    if ($hash -ne $sha1.ToLower()) { throw "Checksum incorrecto: $hash (se esperaba $sha1)" }

    Expand-Archive $zip -DestinationPath $tmp
    New-Item (Join-Path $sdk 'cmdline-tools') -ItemType Directory -Force | Out-Null
    Move-Item (Join-Path $tmp 'cmdline-tools') (Join-Path $sdk 'cmdline-tools\latest')
}
$env:ANDROID_HOME = $sdk
[Environment]::SetEnvironmentVariable('ANDROID_HOME', $sdk, 'User')
Write-Host "ANDROID_HOME = $sdk"

# --- 3. Paquetes del SDK -----------------------------------------------------
Write-Host '== Instalando paquetes del SDK =='
$packages = @('platform-tools', 'platforms;android-36', 'build-tools;35.0.0', 'extras;google;usb_driver')
('y' + [Environment]::NewLine) * 20 | & $sdkManager "--sdk_root=$sdk" @packages
if ($LASTEXITCODE -ne 0) { throw 'sdkmanager falló; revisa el mensaje de arriba.' }

# --- 4. adb en el PATH -------------------------------------------------------
$platformTools = Join-Path $sdk 'platform-tools'
$userPath = [Environment]::GetEnvironmentVariable('Path', 'User')
if (-not $userPath) {
    [Environment]::SetEnvironmentVariable('Path', $platformTools, 'User')
} elseif ($userPath -notlike "*$platformTools*") {
    [Environment]::SetEnvironmentVariable('Path', "$userPath;$platformTools", 'User')
}

Write-Host ''
Write-Host 'Listo. Cierra y vuelve a abrir VS Code, y luego prueba:'
Write-Host '    .\gradlew.bat assembleDebug'
