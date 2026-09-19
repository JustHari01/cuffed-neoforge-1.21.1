$ErrorActionPreference = 'Stop'

$sourceJar = 'C:\Users\ndinh\curseforge\minecraft\Instances\Tiktok 1.21.1\mods\capsule-machine-1.0.1.jar'
$outputPath = Join-Path $PSScriptRoot '..\src\main\resources\assets\capsulemachine\models\generated\capsule_machine.json'
$outputPath = [System.IO.Path]::GetFullPath($outputPath)

if (-not (Test-Path -LiteralPath $sourceJar)) {
    throw "Capsule Machine JAR not found: $sourceJar"
}

[System.Reflection.Assembly]::LoadWithPartialName('System.IO.Compression.FileSystem') | Out-Null
$zip = [System.IO.Compression.ZipFile]::OpenRead($sourceJar)
try {
    $entry = $zip.GetEntry('assets/capsulemachine/models/generated/capsule_machine.json')
    if ($null -eq $entry) {
        throw 'Capsule Machine model entry not found in the source JAR.'
    }

    $reader = New-Object System.IO.StreamReader($entry.Open())
    try {
        $model = $reader.ReadToEnd()
    } finally {
        $reader.Dispose()
    }
} finally {
    $zip.Dispose()
}

$allowed = @(-45.0, -22.5, 0.0, 22.5, 45.0)
$model = [regex]::Replace($model, '"angle"\s*:\s*(-?\d+(?:\.\d+)?)', {
    param($match)
    $angle = [double]$match.Groups[1].Value
    $replacement = $allowed | Sort-Object { [math]::Abs($_ - $angle) } | Select-Object -First 1
    '"angle": ' + $replacement.ToString('0.0', [Globalization.CultureInfo]::InvariantCulture)
})

$parent = Split-Path -Parent $outputPath
New-Item -ItemType Directory -Force -Path $parent | Out-Null
[System.IO.File]::WriteAllText($outputPath, $model, [System.Text.UTF8Encoding]::new($false))

$parsed = Get-Content -LiteralPath $outputPath -Raw | ConvertFrom-Json
$invalid = @($parsed.elements | ForEach-Object { $_.rotation } | Where-Object { $null -ne $_ -and ([double]$_.angle -notin $allowed) })
if ($invalid.Count -ne 0) {
    throw "Generated model still has $($invalid.Count) unsupported element rotations."
}

Write-Output "Generated $outputPath"
