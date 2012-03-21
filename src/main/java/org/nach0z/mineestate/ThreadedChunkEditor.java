package org.nach0z.mineestate;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.*;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

import com.sk89q.worldedit.regions.*;
import com.sk89q.worldedit.*;

public class ThreadedChunkEditor extends Thread implements Runnable{
	private CuboidRegion cuboid;
	private Chunk chunk;
	private String newOwnerName;

	public ThreadedChunkEditor(CuboidRegion cuboid, Chunk chunk, String newOwnerName) {
		this.chunk = chunk;
		this.newOwnerName = newOwnerName;
		this.cuboid = cuboid;
	}

	public void run() {
		 for(int i = 0; i < 16; i++) {
                        for(int j = 0; j < 128; j++) {
                                for(int k = 0; k < 16; k++) {
                                        BlockState block = chunk.getBlock(i,j,k).getState();
                                        org.bukkit.Location loc = block.getLocation();
                                        com.sk89q.worldedit.Vector blockVec = new com.sk89q.worldedit.Vector(block.getX(), block.getY(), block.getZ());
                                        if(cuboid.contains(blockVec)) {
						if(!(block.getTypeId() == 68))
							continue;
						else {
							System.out.println("Found a sign n00b!");
							Sign sg = (Sign)block;
							String[] str = sg.getLines();
							if(str[0].equalsIgnoreCase("[private]")) {
								sg.setLine(1,newOwnerName);
								sg.setLine(2,"");
								sg.setLine(3,"");
								sg.update();
							}
						}
					}
                                }
                        }
                }

	}

}
