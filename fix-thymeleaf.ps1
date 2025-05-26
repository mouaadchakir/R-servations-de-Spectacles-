# Script PowerShell pour corriger la syntaxe du00e9pru00e9ciu00e9e de Thymeleaf

$templateDir = "$PSScriptRoot\src\main\resources\templates"
$filePattern = "*.html"

# Recherche ru00e9cursive de tous les fichiers HTML
$files = Get-ChildItem -Path $templateDir -Filter $filePattern -Recurse

$modifiedCount = 0

foreach ($file in $files) {
    $content = Get-Content -Path $file.FullName -Raw
    
    # Vu00e9rifie si le fichier contient la syntaxe du00e9pru00e9ciu00e9e
    if ($content -match 'th:replace="layout/main :: html\(~\{::title\}, ~\{::section\}\)"') {
        Write-Host "Correction du fichier: $($file.FullName)"
        
        # Remplace la syntaxe du00e9pru00e9ciu00e9e par la syntaxe recommandu00e9e
        $newContent = $content -replace 'th:replace="layout/main :: html\(~\{::title\}, ~\{::section\}\)"', 'th:replace="~{layout/main :: html(~{::title}, ~{::section})}"'
        
        # u00c9crit le nouveau contenu dans le fichier
        Set-Content -Path $file.FullName -Value $newContent
        $modifiedCount++
    }
}

Write-Host "Terminu00e9. $modifiedCount fichiers ont u00e9tu00e9 modifiu00e9s."
