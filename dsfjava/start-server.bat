@echo off
echo Démarrage du serveur TODO List...
echo 2 | java -cp "src/main/java" com.main.Main
start http://localhost:8080
pause 