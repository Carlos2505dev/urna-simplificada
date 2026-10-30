$ErrorActionPreference = 'Stop'
$projectRoot = $PSScriptRoot
$sourceRoot = Join-Path $projectRoot 'src\main\java'
$classesPath = Join-Path $projectRoot 'target\local-classes'

$sourceFiles = Get-ChildItem $sourceRoot -Recurse -Filter '*.java' |
    Where-Object {
        $_.Name -ne 'Main.java' -and $_.FullName -notlike '*\repository\firebase\*'
    } |
    ForEach-Object { $_.FullName }

New-Item -ItemType Directory -Path $classesPath -Force | Out-Null
Push-Location $projectRoot
try {
    & javac --release 17 -d $classesPath $sourceFiles
    if ($LASTEXITCODE -ne 0) {
        throw "A compilação falhou com código $LASTEXITCODE."
    }

    & java -cp $classesPath br.edu.urna.LocalMain
    if ($LASTEXITCODE -ne 0) {
        throw "A urna terminou com código $LASTEXITCODE."
    }
}
finally {
    Pop-Location
}