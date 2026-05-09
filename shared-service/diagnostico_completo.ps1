# Script de diagnóstico completo para el sistema de almacenamiento de imágenes

Write-Host "`n╔══════════════════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║         DIAGNÓSTICO COMPLETO DE ALMACENAMIENTO          ║" -ForegroundColor Cyan
Write-Host "╚══════════════════════════════════════════════════════════╝`n" -ForegroundColor Cyan

# 1. Ubicación actual
Write-Host "📍 UBICACIÓN ACTUAL" -ForegroundColor Yellow
Write-Host "─" * 60 -ForegroundColor DarkGray
$currentPath = Get-Location
Write-Host "Ruta actual: $currentPath" -ForegroundColor White

# 2. Verificar carpeta uploads relativa
Write-Host "`n📁 VERIFICACIÓN DE CARPETA UPLOADS (RELATIVA)" -ForegroundColor Yellow
Write-Host "─" * 60 -ForegroundColor DarkGray
$uploadsRelative = ".\uploads"
if (Test-Path $uploadsRelative) {
    $uploadsAbsolute = (Resolve-Path $uploadsRelative).Path
    Write-Host "✅ Carpeta uploads EXISTE" -ForegroundColor Green
    Write-Host "   Ruta relativa: $uploadsRelative" -ForegroundColor Gray
    Write-Host "   Ruta absoluta: $uploadsAbsolute" -ForegroundColor Cyan

    # Abrir en el explorador
    Write-Host "`n🪟 ABRIR EN EXPLORADOR DE WINDOWS:" -ForegroundColor Magenta
    Write-Host "   Ejecuta este comando:" -ForegroundColor Gray
    Write-Host "   explorer.exe `"$uploadsAbsolute`"" -ForegroundColor White

    # Listar archivos
    Write-Host "`n📄 ARCHIVOS ENCONTRADOS:" -ForegroundColor Yellow
    $files = Get-ChildItem -Path $uploadsRelative -Recurse -File
    if ($files.Count -gt 0) {
        foreach ($file in $files) {
            $relativePath = $file.FullName.Replace($uploadsAbsolute, "").TrimStart("\")
            Write-Host "   📷 $relativePath" -ForegroundColor Green
            Write-Host "      Ruta completa: $($file.FullName)" -ForegroundColor Gray
            Write-Host "      Tamaño: $([math]::Round($file.Length / 1KB, 2)) KB" -ForegroundColor Gray
            Write-Host "      Modificado: $($file.LastWriteTime)" -ForegroundColor Gray
            Write-Host ""
        }
    } else {
        Write-Host "   ⚠️  No se encontraron archivos" -ForegroundColor Yellow
    }
} else {
    Write-Host "❌ Carpeta uploads NO EXISTE" -ForegroundColor Red
}

# 3. Buscar carpetas uploads en todo el proyecto
Write-Host "`n🔍 BÚSQUEDA EN TODO EL PROYECTO" -ForegroundColor Yellow
Write-Host "─" * 60 -ForegroundColor DarkGray
$projectRoot = (Get-Item $currentPath).Parent.Parent.Parent.FullName
Write-Host "Buscando carpetas 'uploads' desde: $projectRoot" -ForegroundColor Gray

$uploadsFound = Get-ChildItem -Path $projectRoot -Directory -Recurse -Filter "uploads" -ErrorAction SilentlyContinue |
                Where-Object { $_.FullName -notlike "*\node_modules\*" -and $_.FullName -notlike "*\build\*" }

if ($uploadsFound) {
    Write-Host "`nEncontradas $($uploadsFound.Count) carpeta(s) 'uploads':" -ForegroundColor Cyan
    foreach ($folder in $uploadsFound) {
        Write-Host "`n   📂 $($folder.FullName)" -ForegroundColor White
        $filesInFolder = Get-ChildItem -Path $folder.FullName -Recurse -File
        Write-Host "      Archivos: $($filesInFolder.Count)" -ForegroundColor Gray
        if ($filesInFolder.Count -gt 0) {
            foreach ($file in $filesInFolder) {
                Write-Host "      - $($file.Name) ($([math]::Round($file.Length / 1KB, 2)) KB)" -ForegroundColor Gray
            }
        }
    }
} else {
    Write-Host "No se encontraron carpetas 'uploads' en el proyecto" -ForegroundColor Yellow
}

# 4. Verificar configuración del servicio
Write-Host "`n⚙️  CONFIGURACIÓN DEL SERVICIO" -ForegroundColor Yellow
Write-Host "─" * 60 -ForegroundColor DarkGray
$applicationYml = Join-Path $currentPath "src\main\resources\application.yml"
if (Test-Path $applicationYml) {
    Write-Host "Archivo de configuración: $applicationYml" -ForegroundColor Gray
    $configContent = Get-Content $applicationYml | Select-String -Pattern "path:|base-url:" -Context 0,1
    if ($configContent) {
        Write-Host "`nConfiguración de storage:" -ForegroundColor Cyan
        $configContent | ForEach-Object { Write-Host "   $_" -ForegroundColor White }
    }
} else {
    Write-Host "⚠️  No se encontró application.yml" -ForegroundColor Yellow
}

# 5. Verificar atributos de archivo/carpeta
Write-Host "`n🔐 VERIFICAR ATRIBUTOS (carpeta uploads)" -ForegroundColor Yellow
Write-Host "─" * 60 -ForegroundColor DarkGray
if (Test-Path $uploadsRelative) {
    $uploadsDir = Get-Item $uploadsRelative -Force
    Write-Host "Atributos: $($uploadsDir.Attributes)" -ForegroundColor White

    if ($uploadsDir.Attributes -match "Hidden") {
        Write-Host "⚠️  La carpeta está OCULTA!" -ForegroundColor Red
        Write-Host "   Para ver archivos ocultos en Windows:" -ForegroundColor Yellow
        Write-Host "   1. Abre el Explorador de archivos" -ForegroundColor Gray
        Write-Host "   2. Ve a 'Ver' → 'Mostrar' → 'Elementos ocultos'" -ForegroundColor Gray
        Write-Host "`n   O ejecuta este comando para quitar el atributo oculto:" -ForegroundColor Yellow
        Write-Host "   attrib -h `"$($uploadsDir.FullName)`" /s /d" -ForegroundColor White
    }
}

# 6. Comandos útiles
Write-Host "`n💡 COMANDOS ÚTILES" -ForegroundColor Magenta
Write-Host "─" * 60 -ForegroundColor DarkGray
Write-Host "Para abrir la carpeta uploads en el explorador:" -ForegroundColor Cyan
Write-Host "   explorer.exe `"$(if (Test-Path $uploadsRelative) { (Resolve-Path $uploadsRelative).Path } else { '.' })`"" -ForegroundColor White

Write-Host "`nPara listar archivos detalladamente:" -ForegroundColor Cyan
Write-Host "   Get-ChildItem -Path .\uploads -Recurse -Force | Format-Table Name,Length,LastWriteTime" -ForegroundColor White

Write-Host "`nPara verificar la imagen subida:" -ForegroundColor Cyan
Write-Host "   Abre en navegador: http://localhost:8084/api/files/users/1/20260508_225406_83a365bc.jpg" -ForegroundColor White

Write-Host "`n"

