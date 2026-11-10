<#
  Windows: instala el emulador de Android y crea el celular virtual "Panbu" (Android 15, tipo Pixel 6).
  Antes hay que haber ejecutado scripts\setup-windows.ps1 (JDK 21 + Android SDK).

  Uso (PowerShell, desde la carpeta del repositorio):
      powershell -ExecutionPolicy Bypass -File scripts\setup-emulator-windows.ps1
  Después: en VS Code, Terminal → Run Task… → "Panbu: abrir emulador".

  Requisito: la virtualización activada. Si el emulador dice que necesita aceleración por hardware
  (“requires hardware acceleration”), abre "Activar o desactivar las características de Windows", marca
  "Plataforma del hipervisor de Windows", acepta y reinicia. En algunos equipos también hay que activar la
  virtualización (VT-x / AMD-V / SVM) en la BIOS.
#>
$ErrorActionPreference = 'Stop'
$ProgressPreference = 'SilentlyContinue'

$sdk = $env:ANDROID_HOME
if (-not $sdk) { $sdk = Join-Path $env:LOCALAPPDATA 'Android\Sdk' }
$bin = Join-Path $sdk 'cmdline-tools\latest\bin'
if (-not (Test-Path (Join-Path $bin 'sdkmanager.bat'))) {
    throw 'No encontré el Android SDK. Ejecuta primero scripts\setup-windows.ps1 y reinicia VS Code.'
}
$image = 'system-images;android-35;google_apis;x86_64'

Write-Host '== Descargando el emulador y la imagen de Android 15 (unos 1,5 GB) =='
('y' + [Environment]::NewLine) * 20 | & (Join-Path $bin 'sdkmanager.bat') "--sdk_root=$sdk" 'emulator' 'platform-tools' $image
if ($LASTEXITCODE -ne 0) { throw 'sdkmanager falló; revisa el mensaje de arriba.' }

Write-Host '== Creando el celular virtual "Panbu" =='
'no' | & (Join-Path $bin 'avdmanager.bat') create avd -n Panbu -k $image -d pixel_6 --force
if ($LASTEXITCODE -ne 0) { throw 'avdmanager falló; revisa el mensaje de arriba.' }

# 2 GB de RAM y teclado de la PC habilitado dentro del emulador.
$cfg = Join-Path $env:USERPROFILE '.android\avd\Panbu.avd\config.ini'
$lines = Get-Content $cfg | Where-Object { $_ -notmatch '^(hw\.ramSize|hw\.keyboard)=' }
@($lines) + 'hw.ramSize=2048' + 'hw.keyboard=yes' | Set-Content $cfg -Encoding ascii

Write-Host ''
Write-Host 'Listo. En VS Code: Terminal → Run Task… → "Panbu: abrir emulador".'
