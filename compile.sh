#!/bin/bash
shopt -s globstar
javac -d target -classpath /home/nach0z/Bukkit/target/classes:.:/home/nach0z/worldguard/target/worldguard-5.5.2-SNAPSHOT.jar src/main/java/**/*.java
cp src/main/resources/plugin.yml target/
./jarify
#cp ~/Hardcore-Minecraft/target/Hardcore.jar ~/minecraft/plugins/
