@echo off
echo Compilation du projet TODO List...
javac -source 8 -target 8 -cp "src/main/java" src/main/java/com/main/model/User.java
javac -source 8 -target 8 -cp "src/main/java" src/main/java/com/main/model/Task.java
javac -source 8 -target 8 -cp "src/main/java" src/main/java/com/main/model/DatedTask.java
javac -source 8 -target 8 -cp "src/main/java" src/main/java/com/main/model/Student.java
javac -source 8 -target 8 -cp "src/main/java" src/main/java/com/main/exception/ElementNotFoundException.java
javac -source 8 -target 8 -cp "src/main/java" src/main/java/com/main/database/DatabaseAccess.java
javac -source 8 -target 8 -cp "src/main/java" src/main/java/com/main/builder/TaskBuilder.java
javac -source 8 -target 8 -cp "src/main/java" src/main/java/com/main/service/UserService.java
javac -source 8 -target 8 -cp "src/main/java" src/main/java/com/main/service/TaskService.java
javac -source 8 -target 8 -cp "src/main/java" src/main/java/com/main/ui/TodoListUI.java
javac -source 8 -target 8 -cp "src/main/java" src/main/java/com/main/server/TodoServer.java
javac -source 8 -target 8 -cp "src/main/java" src/main/java/com/main/Main.java
javac -source 8 -target 8 -cp "src/main/java" src/main/java/com/main/StudentMain.java
echo Compilation terminee !
pause