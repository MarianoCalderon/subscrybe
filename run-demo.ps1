# =====================================================================
#  Subscrybe - Arranque para demo (app local + Postgres en Docker)
#  Uso: clic derecho > "Ejecutar con PowerShell"  o  .\run-demo.ps1
#  REQUISITO: Docker Desktop ABIERTO antes de correr esto.
#  NO ejecutes "mvn clean": borraria target\libs y este script dejaria de servir.
# =====================================================================
$ErrorActionPreference = "Stop"
Set-Location $PSScriptRoot

Write-Host "[1/3] Levantando Postgres en Docker..." -ForegroundColor Cyan
docker-compose up -d db

Write-Host "[2/3] Esperando a que la base de datos acepte conexiones (puerto 5434)..." -ForegroundColor Cyan
$ready = $false
for ($i = 0; $i -lt 30; $i++) {
    try {
        $c = New-Object System.Net.Sockets.TcpClient
        $c.Connect("localhost", 5434)
        if ($c.Connected) { $c.Close(); $ready = $true; break }
    } catch { Start-Sleep -Seconds 1 }
}
if (-not $ready) {
    Write-Host "ERROR: Postgres no respondio en el puerto 5434." -ForegroundColor Red
    Write-Host "       Abre Docker Desktop y espera a que diga 'Running', luego reintenta." -ForegroundColor Red
    Read-Host "Enter para salir"
    exit 1
}
Write-Host "      Base de datos lista." -ForegroundColor Green

Write-Host "[3/3] Iniciando Subscrybe en http://localhost:8081 ..." -ForegroundColor Green
Write-Host "      (Deja esta ventana abierta. Ctrl+C para detener la app.)" -ForegroundColor DarkGray
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.5.11-hotspot"
& "$env:JAVA_HOME\bin\java.exe" -cp "target\classes;target\libs\*" com.subscrybe.infrastructure.config.SubscrybeApplication
