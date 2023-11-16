@echo off

REM Create necessary directories
mkdir ".\lib"
mkdir ".\.vscode"

echo Downloading 'openjfx-21.0.1'...

REM Download the file
powershell -command "(New-Object System.Net.WebClient).DownloadFile('https://download2.gluonhq.com/openjfx/21.0.1/openjfx-21.0.1_windows-x64_bin-sdk.zip', 'openjfx-21.0.1_windows-x64_bin-sdk.zip')"

echo Installing 'openjfx-21.0.1'

REM Extract the contents of the downloaded file
powershell -command "Expand-Archive -Path '.\openjfx-21.0.1_windows-x64_bin-sdk.zip' -DestinationPath '.\lib'"

echo Creating .vscode files

REM Create and populate the launch.json file
echo { > ".\.vscode\launch.json"
echo  "configurations": [ >> ".\.vscode\launch.json"
echo    { >> ".\.vscode\launch.json"
echo      "type": "java", >> ".\.vscode\launch.json"
echo      "name": "Debug (Launch) - Current File", >> ".\.vscode\launch.json"
echo      "request": "launch", >> ".\.vscode\launch.json"
echo      "mainClass": "${file}", >> ".\.vscode\launch.json"
echo      "vmArgs": "--module-path ./lib/javafx-sdk-21.0.1/lib --add-modules javafx.controls,javafx.media,javafx.fxml" >> ".\.vscode\launch.json"
echo    } >> ".\.vscode\launch.json"
echo  ] >> ".\.vscode\launch.json"
echo } >> ".\.vscode\launch.json"

REM Create and populate the settings.json file
echo {^
  "java.project.referencedLibraries": ["lib/**/*.jar"]^
} > ".\.vscode\settings.json"

echo Cleaning up...

REM Clean up the downloaded zip file
del openjfx-21.0.1_windows-x64_bin-sdk.zip

echo Done.
