@echo off
chcp 65001
color 9b
mode con:cols=40 lines=15
echo Aktualizator launchera [NAME] v1.0
echo Nie zamykaj tego okna.
echo Oczekiwanie na zamkniecie launchera...
timeout 3 > NUL
echo Aktualizacja...
move "[DATADIR]\update.jar" "[LOCATION]"
echo Czyszczenie...
start /b javaw -jar "[LOCATION]"
del "%~f0" & exit