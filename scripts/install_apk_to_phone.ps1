param(
    [string]$Port,
    [string]$PhoneIp = "192.168.178.23",
    [string]$ApkPath = "app\build\outputs\apk\debug\app-debug.apk",
    [switch]$UseLastPort,
    [switch]$SkipBuild
)

$ErrorActionPreference = "Stop"

$RepoRoot = Split-Path -Parent $PSScriptRoot
$LastPortFile = Join-Path $PSScriptRoot ".last_adb_port"

Set-Location $RepoRoot

function Test-PortValue {
    param([string]$Value)

    return -not [string]::IsNullOrWhiteSpace($Value) -and $Value -match '^\d+$'
}

function Get-LocalProperty {
    param([string]$Name)

    $LocalProperties = Join-Path $RepoRoot "local.properties"
    if (-not (Test-Path $LocalProperties)) {
        return $null
    }

    $Line = Get-Content $LocalProperties |
        Where-Object { $_ -match "^\s*$([regex]::Escape($Name))\s*=" } |
        Select-Object -First 1

    if ($null -eq $Line) {
        return $null
    }

    return ($Line -replace "^\s*$([regex]::Escape($Name))\s*=\s*", "").Trim()
}

function Find-Adb {
    $AdbFromPath = Get-Command adb -ErrorAction SilentlyContinue
    if ($null -ne $AdbFromPath) {
        return $AdbFromPath.Source
    }

    $SdkDir = Get-LocalProperty -Name "sdk.dir"
    if (-not [string]::IsNullOrWhiteSpace($SdkDir)) {
        $Candidate = Join-Path $SdkDir "platform-tools\adb.exe"
        if (Test-Path $Candidate) {
            return $Candidate
        }
    }

    Write-Error "adb wurde nicht gefunden. Stelle sicher, dass Android platform-tools im PATH sind oder local.properties sdk.dir setzt."
}

function Ensure-JavaHome {
    if (-not [string]::IsNullOrWhiteSpace($env:JAVA_HOME)) {
        return
    }

    $AndroidStudioJbr = "C:\Program Files\Android\Android Studio\jbr"
    if (Test-Path $AndroidStudioJbr) {
        $env:JAVA_HOME = $AndroidStudioJbr
        $env:Path = "$env:JAVA_HOME\bin;$env:Path"
    }
}

function Build-DebugApk {
    if ($SkipBuild) {
        Write-Host "Build wird uebersprungen (-SkipBuild)."
        return
    }

    Ensure-JavaHome

    $Gradle = Join-Path $RepoRoot "gradlew.bat"
    if (-not (Test-Path $Gradle)) {
        Write-Error "gradlew.bat wurde nicht gefunden: $Gradle"
    }

    Write-Host "Baue Debug-APK ..."
    & $Gradle assembleDebug
    if ($LASTEXITCODE -ne 0) {
        Write-Error "Gradle-Build fehlgeschlagen."
    }
}

function Install-With-Port {
    param(
        [string]$PortToUse,
        [string]$ResolvedApkPath,
        [string]$AdbPath
    )

    $Device = "${PhoneIp}:$PortToUse"

    Write-Host "Verbinde mit $Device ..."
    $ConnectOutput = & $AdbPath connect $Device 2>&1
    $ConnectOutput | ForEach-Object { Write-Host $_ }

    $Connected = $LASTEXITCODE -eq 0 -and ($ConnectOutput -match "connected to|already connected to")
    if (-not $Connected) {
        return $false
    }

    Write-Host "Pruefe Geraeteverbindung ..."
    $State = & $AdbPath -s $Device get-state 2>$null
    if ($LASTEXITCODE -ne 0 -or $State -ne "device") {
        Write-Host "Geraet ist nicht bereit: $Device"
        return $false
    }

    Write-Host "Installiere $ResolvedApkPath ..."
    & $AdbPath -s $Device install -r $ResolvedApkPath
    if ($LASTEXITCODE -ne 0) {
        return $false
    }

    Write-Host "Fertig. APK wurde auf $Device installiert."
    return $true
}

$Adb = Find-Adb

Build-DebugApk

$ResolvedApk = Resolve-Path -Path $ApkPath -ErrorAction SilentlyContinue
if ($null -eq $ResolvedApk) {
    Write-Error "APK nicht gefunden: $ApkPath. Erwarteter Build-Befehl: .\gradlew.bat assembleDebug"
}

$PortsToTry = @()

if ($UseLastPort -and (Test-Path $LastPortFile)) {
    $LastPort = (Get-Content -Raw -Path $LastPortFile).Trim()
    if (Test-PortValue $LastPort) {
        $PortsToTry += $LastPort
    }
}

if (Test-PortValue $Port -and -not ($PortsToTry -contains $Port)) {
    $PortsToTry += $Port
}

foreach ($CandidatePort in $PortsToTry) {
    Write-Host "Versuche gespeicherten Port $CandidatePort ..."
    if (Install-With-Port -PortToUse $CandidatePort -ResolvedApkPath $ResolvedApk.Path -AdbPath $Adb) {
        Set-Content -Path $LastPortFile -Value $CandidatePort -NoNewline
        exit 0
    }

    Write-Host "Port $CandidatePort hat nicht funktioniert."
}

while ($true) {
    $NewPort = Read-Host "Neuen ADB-Port fuer $PhoneIp eingeben"

    if (-not (Test-PortValue $NewPort)) {
        Write-Host "Ungueltiger Port: $NewPort"
        continue
    }

    if (Install-With-Port -PortToUse $NewPort -ResolvedApkPath $ResolvedApk.Path -AdbPath $Adb) {
        Set-Content -Path $LastPortFile -Value $NewPort -NoNewline
        exit 0
    }

    Write-Host "Port $NewPort hat nicht funktioniert. Bitte neuen Port eingeben."
}
