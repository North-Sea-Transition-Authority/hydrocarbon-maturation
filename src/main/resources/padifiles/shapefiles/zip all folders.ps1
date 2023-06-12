# Get the current path
$currentPath = Get-Location

# Get all directories in the current path
$directories = Get-ChildItem -Path $currentPath -Directory

# Iterate over each directory
foreach($directory in $directories)
{
    # Define the name of the zip file
    $zipFileName = "$($directory.FullName).zip"

    # Get all items in the directory
    $items = Get-ChildItem -Path $directory.FullName | ForEach-Object { $_.FullName }

    # Create the zip file
    Compress-Archive -Path $items -DestinationPath $zipFileName

    Write-Host "Compressed $directory into $zipFileName"
}
