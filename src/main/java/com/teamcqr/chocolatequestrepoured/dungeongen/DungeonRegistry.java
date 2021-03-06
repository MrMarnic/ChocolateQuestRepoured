package com.teamcqr.chocolatequestrepoured.dungeongen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import com.teamcqr.chocolatequestrepoured.CQRMain;
import com.teamcqr.chocolatequestrepoured.dungeongen.dungeons.AbandonedDungeon;
import com.teamcqr.chocolatequestrepoured.dungeongen.dungeons.CavernDungeon;
import com.teamcqr.chocolatequestrepoured.dungeongen.dungeons.ClassicNetherCity;
import com.teamcqr.chocolatequestrepoured.dungeongen.dungeons.DefaultSurfaceDungeon;
import com.teamcqr.chocolatequestrepoured.dungeongen.dungeons.DungeonOceanFloor;
import com.teamcqr.chocolatequestrepoured.dungeongen.dungeons.FloatingNetherCity;
import com.teamcqr.chocolatequestrepoured.dungeongen.dungeons.RuinDungeon;
import com.teamcqr.chocolatequestrepoured.dungeongen.dungeons.StrongholdDungeon;
import com.teamcqr.chocolatequestrepoured.dungeongen.dungeons.VillageDungeon;
import com.teamcqr.chocolatequestrepoured.dungeongen.dungeons.VolcanoDungeon;
import com.teamcqr.chocolatequestrepoured.init.base.ItemDungeonPlacer;
import com.teamcqr.chocolatequestrepoured.util.PropertyFileHelper;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;

public class DungeonRegistry {
	
	private int dungeonSpawnChance = 100;
	private int DungeonDistance = 20;
	private int DungeonSpawnDistance = 5;
	
	private HashMap<Biome, List<DungeonBase>> biomeDungeonMap = new HashMap<Biome, List<DungeonBase>>();;
	private HashMap<BlockPos, List<DungeonBase>> coordinateSpecificDungeons = new HashMap<BlockPos, List<DungeonBase>>();
	
	//TODO: Improve this method by splitting it into multiple smaller parts
	public void loadDungeonFiles() {
		System.out.println("Loading dungeon configs...");
		if(CQRMain.CQ_DUNGEON_FOLDER.exists() && CQRMain.CQ_DUNGEON_FOLDER.listFiles().length > 0) {
			System.out.println("Found " + CQRMain.CQ_DUNGEON_FOLDER.listFiles().length + " dungeon configs. Loading...");
			System.out.println("Searching dungeons in " + CQRMain.CQ_DUNGEON_FOLDER.getAbsolutePath() );
			for(File f : CQRMain.CQ_DUNGEON_FOLDER.listFiles()) {
				System.out.println("Loading dungeon configuration " + f.getName() + "...");
				Properties dungeonConfig = new Properties();
				FileInputStream stream = null;
				try {
					stream = new FileInputStream(f);
					
					dungeonConfig.load(stream);
					
					String dunType = dungeonConfig.getProperty("generator", "TEMPLATE_SURFACE");
					
					if(EDungeonGenerator.isValidDungeonGenerator(dunType)) {
						
						BlockPos lockedPos = new BlockPos(0,0,0);
						
						boolean posLocked = PropertyFileHelper.getBooleanProperty(dungeonConfig, "spawnAtCertainPosition", false);
						if(posLocked) {
							if(dungeonConfig.containsKey("spawnAt")) {
								String[] args = dungeonConfig.getProperty("spawnAt", "-;-;-").split(";");
								if(args.length == 3) {
									try {
										int x = Integer.parseInt(args[0]);
										int y = Integer.parseInt(args[1]);
										int z = Integer.parseInt(args[2]);
										
										lockedPos = new BlockPos(x, y, z);
										
									} catch(NumberFormatException ex) {
										posLocked = false;
									}
								} else {
									posLocked = false;
								}
							}  else {
								posLocked = false;
							}
						}
						
						DungeonBase dungeon = null;
						
						EDungeonGenerator generator = EDungeonGenerator.valueOf(dunType.toUpperCase());
						
						switch(generator) {
						case ABANDONED:
							dungeon = new AbandonedDungeon(f);
							break;
						case CAVERNS:
							dungeon = new CavernDungeon(f);
							break;
						case FLOATING_NETHER_CITY:
							dungeon = new FloatingNetherCity(f);
							break;
						case NETHER_CITY:
							dungeon = new ClassicNetherCity(f);
							break;
						case RUIN:
							dungeon = new RuinDungeon(f);
							break;
						case STRONGHOLD:
							dungeon = new StrongholdDungeon(f);
							break;
						case TEMPLATE_OCEAN_FLOOR:
							dungeon = new DungeonOceanFloor(f);
							break;
						case TEMPLATE_SURFACE:
							dungeon = new DefaultSurfaceDungeon(f);
							break;
						case VILLAGE:
							dungeon = new VillageDungeon(f);
							break;
						case VOLCANO:
							dungeon = new VolcanoDungeon(f);
							break;
						default:
							break;
						}
						
						if(dungeon != null) {
							//Position restriction stuff here
							if(posLocked) {
								System.out.println("Dungeon " + dungeon.getDungeonName() + " will spawn at X=" + lockedPos.getX() + " Y=" + lockedPos.getY() + " Z=" + lockedPos.getZ());
								dungeon.setLockPos(lockedPos, posLocked);
							}
							//DONE: do biome map filling
							//Biome map filling
							String[] biomes = PropertyFileHelper.getStringArrayProperty(dungeonConfig, "biomes", new String[]{"PLAINS"});
							System.out.println("Biomes where " + dungeon.getDungeonName() + " can spawn: ");
							for(String b : biomes) {
								System.out.println(" - " + b);
								//Add the biome to the map
								if(b.equalsIgnoreCase("*") || b.equalsIgnoreCase("ALL")) {
									addDungeonToAllBiomes(dungeon);
								} else {
									if(getBiomeByName(b) != null) {
										BiomeDictionary.Type biomeType = getBiomeByName(b);
										System.out.println("Dungeon " + dungeon.getDungeonName() + " may spawn in biomes:");
										for(Biome biome : BiomeDictionary.getBiomes(biomeType)) {
											if(this.biomeDungeonMap.containsKey(biome)) {
												addDungeonToBiome(dungeon, biome);
											}
										}
										
									}
								}
							}
							
							if(dungeon.isRegisteredSuccessful()) {
								System.out.println("Successfully registered dungeon " + dungeon.getDungeonName() + "!");
								new ItemDungeonPlacer(dungeon);
							} else {
								System.out.println("Cant load dungeon " + dungeon.getDungeonName() + "!");
							}
							System.out.println(" ");
							System.out.println(" ");
							
						}
						
					} else {
						System.out.println("Cant load dungeon configuration " + f.getName() + "!");
						System.out.println("Dungeon generator " + dunType + " is not a valid dungeon generator!");
					}
					
					try {
						stream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("Failed to load dungeon configuration " + f.getName() + "!");
				} finally {
					try {
						stream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
		} else {
			System.out.println("There are no dungeon configs :(");
		}
	}
	
	public List<DungeonBase> getDungeonsForBiome(Biome b) {
		if(b != null && biomeDungeonMap.containsKey(b) && !biomeDungeonMap.get(b).isEmpty()) {
			return biomeDungeonMap.get(b);
		}
		return new ArrayList<DungeonBase>();
	}
	
	public int getDungeonSpawnChance() {
		return dungeonSpawnChance;
	}
	
	public void addBiomeEntryToMap(Biome b) {
		if(biomeDungeonMap != null) {
			if(!biomeDungeonMap.containsKey(b)) {
				biomeDungeonMap.put(b, new ArrayList<DungeonBase>());
			}
		}
	}
	
	public int getDungeonDistance() {
		return DungeonDistance;
	}
	public int getDungeonSpawnDistance() {
		return DungeonSpawnDistance;
	}
	
	public HashMap<BlockPos, List<DungeonBase>> getCoordinateSpecificsMap() {
		return this.coordinateSpecificDungeons;
	}
	
	private void addDungeonToAllBiomes(DungeonBase dungeon) {
		System.out.println("Dungeon " + dungeon.getDungeonName() + " may spawn in biomes:");
		for(Biome biome : this.biomeDungeonMap.keySet()) {
			addDungeonToBiome(dungeon, biome);
		}
	}
	private void addDungeonToBiome(DungeonBase dungeon, Biome biome) {
		if(this.biomeDungeonMap.containsKey(biome)) {
			List<DungeonBase> dungs = this.biomeDungeonMap.get(biome);
			if(!dungs.contains(dungeon)) {
				this.biomeDungeonMap.get(biome).add(dungeon);
				System.out.println(" - " + biome.getBiomeName());
			}
		}
	}
	private BiomeDictionary.Type getBiomeByName(String biomeName) {
		for(BiomeDictionary.Type bType : BiomeDictionary.Type.getAll()) {
			if(biomeName.equalsIgnoreCase(bType.getName()) || biomeName.equalsIgnoreCase(bType.toString())) {
				return bType;
			}
		}
		return null;
	}

}
