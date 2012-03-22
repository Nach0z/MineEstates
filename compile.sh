#!/bin/bash
shopt -s globstar
javac -d target -classpath /home/nach0z/Bukkit/target/classes:.:/home/nach0z/minecraft/plugins/worldguard-5.5.2-SNAPSHOT.jar:/home/nach0z/minecraft/plugins/worldedit-5.3-SNAPSHOT.jar:/home/nach0z/minecraft/plugins/Vault.jar src/main/java/**/*.java
cp src/main/resources/* target/
./jarify
#cp ~/Hardcore-Minecraft/target/Hardcore.jar ~/minecraft/plugins/
