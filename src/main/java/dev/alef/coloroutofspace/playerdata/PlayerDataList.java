package dev.alef.coloroutofspace.playerdata;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import dev.alef.coloroutofspace.Refs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PlayerDataList {

    private final static Logger LOGGER = LogManager.getLogger();
	
	private List<PlayerData> playerDataList = new ArrayList<PlayerData>();
	private List<World> created = new ArrayList<World>();

	public PlayerDataList() {
	}
	
	public PlayerData add(World worldIn, PlayerEntity player) {
		PlayerData playerData = new PlayerData(worldIn, player);
		this.playerDataList.add(playerData);
		return playerData;
	}
	
	public PlayerData get(World worldIn, PlayerEntity player) {
		
		for (int i = 0; i < this.playerDataList.size(); ++i) {

			PlayerData playerData = this.playerDataList.get(i);

			if (worldIn.toString().equals(playerData.getWorld().toString()) && player.getUniqueID().equals(playerData.getPlayerUUID())) {
				return playerData;
			}
		}
		return null;
	}
	
	public List<PlayerData> getList() {
		return this.playerDataList;
	}
	
	public int getSize() {
		return this.playerDataList.size();
	}
	
	public boolean delete(World worldIn, PlayerEntity player) {
		
		boolean deleted = false;
		
		for (int i = 0; i < this.playerDataList.size(); ++i) {

			PlayerData playerData = this.playerDataList.get(i);

			if (worldIn.toString().equals(playerData.getWorld().toString()) && player.getUniqueID().equals(playerData.getPlayerUUID())) {
				this.playerDataList.remove(i);
				for (int j = 0; j < this.created.size(); ++j) {
					if (worldIn.toString().equals(this.created.get(j).toString())) {
						this.created.remove(j);
					}
				}
				deleted = true;
			}
		}
		return deleted;
	}
	
	public PlayerData create(World worldIn, PlayerEntity player) {
		this.created.add(worldIn);
		return this.add(worldIn, player);
	}
	
	public boolean isCreated(World worldIn) {
		
		for (World world : this.created) {
			if (worldIn.toString().equals(world.toString())) {
				return true;
			}
		}
		return false;
	}
	
	public PlayerData read(World worldIn, PlayerEntity player) throws FileNotFoundException, CommandSyntaxException {

		PlayerData playerData = null;
		if (this.isCreated(worldIn)) {
			return this.get(worldIn, player);
		}
		playerData = this.create(worldIn, player);

		try {
			String fileName = "mods\\"+Refs.MODID+"\\"+player.getUniqueID().toString();
			File myObj = new File(fileName);
			Scanner myReader = new Scanner(myObj);
			String reg = "";
			String prefix = "";
			String data = "";
			
			while (myReader.hasNextLine()) {
				
				reg = myReader.nextLine();
				prefix = reg.substring(0, 2);
				data = reg.substring(2, reg.length());
				
				if (prefix.equals("MP")) {
					playerData.setMetPos(BlockPos.fromLong(Long.parseLong(data)));
				} 
				else if (prefix.equals("BP")) {
					playerData.setBedPos(BlockPos.fromLong(Long.parseLong(data)));
				} 
				else if (prefix.equals("FP")) {
					playerData.setFallPos(BlockPos.fromLong(Long.parseLong(data)));
				}
				else if (prefix.equals("FJ")) {
					playerData.setFirstJoin(Long.parseLong(data));
				}
				else if (prefix.equals("FD")) {
					playerData.setFallDay(Integer.parseInt(data));
				}
				else if (prefix.equals("MF")) {
					playerData.setMetFallen(data.equals("true"));
				}
				else if (prefix.equals("PI")) {
					playerData.setPlayerInfected(data.equals("true"));
				}
				else if (prefix.equals("PC")) {
					playerData.setPlayerCured(data.equals("true"));
				}
				else if (prefix.equals("MA")) {
					playerData.setMetActive(data.equals("true"));
				}
				else if (prefix.equals("PR")) {
					playerData.setPrevRadius(Integer.parseInt(data));
				}
				else if (prefix.equals("SC")) {
					playerData.setCureLevel(Integer.parseInt(data));
				}
			}
			myReader.close();
		}
		catch (FileNotFoundException e) {
			LOGGER.info("No previous Structure/ClipBoard found");	
		}
		return playerData;
	}
	
	public boolean write(World worldIn, PlayerEntity player, boolean createNew) throws IOException {
		
		try {
		    PlayerData playerData = this.get(worldIn, player);
		    
			if (playerData != null && player.getUniqueID().equals(playerData.getPlayerUUID()) && this.isCreated(playerData.getWorld())) {
				
				String fileName = "mods\\"+Refs.MODID;
				File myObj = new File(fileName);
			    myObj.mkdirs();
				fileName += "\\"+player.getUniqueID().toString();
			    myObj = new File(fileName);
			    if (createNew) {
			    	if (!myObj.createNewFile()) {
			    		myObj.delete();
			    		myObj.createNewFile();
			    	}
			    }
				FileWriter myWriter = new FileWriter(fileName, !createNew);
				
				if (playerData.getMetPos() != null) {
					myWriter.write("MP"+playerData.getMetPos().toLong()+System.lineSeparator());
				}
				if (playerData.getBedPos() != null) {
					myWriter.write("BP"+playerData.getBedPos().toLong()+System.lineSeparator());
				}
				if (playerData.getFallPos() != null) {
					myWriter.write("FP"+playerData.getFallPos().toLong()+System.lineSeparator());
				}
				myWriter.write("FJ"+playerData.getFirstJoin()+System.lineSeparator());
				myWriter.write("FD"+playerData.getFallDay()+System.lineSeparator());
				myWriter.write("MF"+playerData.isMetFallen()+System.lineSeparator());
				myWriter.write("PI"+playerData.isPlayerInfected()+System.lineSeparator());
				myWriter.write("PC"+playerData.isPlayerCured()+System.lineSeparator());
				myWriter.write("MA"+playerData.isMetActive()+System.lineSeparator());
				myWriter.write("PR"+playerData.getPrevRadius()+System.lineSeparator());
				myWriter.write("SC"+playerData.getCureLevel()+System.lineSeparator());
				
				myWriter.close();
				return true;
			}
		}
		catch (IOException e) {
			LOGGER.warn("Couldn't create savefile. No Structure/ClipBoard will be saved");
		}
		return false;
	}
	
	@SuppressWarnings("unused")
	private String cypher(String text) {
        return Base64.getEncoder().encodeToString(text.getBytes());
	}
	
	@SuppressWarnings("unused")
	private String decypher(String text) {
		byte[] decBytes = Base64.getDecoder().decode(text);
		return new String(decBytes);
	}
}

