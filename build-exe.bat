@echo off
setlocal

rem ── Script pour developpeur : reconstruit le jeu pre-empaquete dans Jeu\ ──
rem ── apres une modification du code. Necessite Maven + le JDK installes.  ──

set JDK_BIN=C:\Program Files\Java\jdk-26.0.1\bin

echo === Compilation du jar (Maven) ===
call mvn package
if errorlevel 1 (
    echo Echec de la compilation Maven.
    pause
    exit /b 1
)

echo.
echo === Creation de l'exe (jpackage) ===
rmdir /s /q Jeu 2>nul
"%JDK_BIN%\jpackage.exe" ^
  --type app-image ^
  --input target ^
  --main-jar jeux-rpg-1.1-jar-with-dependencies.jar ^
  --main-class lancement.gui.Launcher ^
  --name "Fairy Tail RPG" ^
  --app-version 1.1 ^
  --java-options "--enable-native-access=ALL-UNNAMED" ^
  --dest Jeu

if errorlevel 1 (
    echo Echec de jpackage.
    pause
    exit /b 1
)

echo.
echo === Termine ! ===
echo   Ouvrez Jeu\Fairy Tail RPG\ et double-cliquez sur Fairy Tail RPG.exe
pause
