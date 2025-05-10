package main;

import entity.Entity;
import entity.InteractiveTile;
import entity.monster.MON_GreenSlime;
import entity.npc.NPC_Merchant;
import entity.npc.NPC_OldMan;
import entity.object.OBJ_Axe;
import entity.object.OBJ_Chest;
import entity.object.OBJ_Coin_Bronze;
import entity.object.OBJ_Door;
import entity.object.OBJ_Heart;
import entity.object.OBJ_Key;
import entity.object.OBJ_ManaCrystal;
import entity.object.OBJ_Potion_red;
import entity.object.OBJ_Shield_Blue;
import entity.tile_interactive.IT_DryTree;

public class AssetSetter {
	
	GamePanel gp;
	
	public MonsterData monsterData[][]; // 初始怪物數據(initialMonsterData) 的位置和類型	
	
	public AssetSetter(GamePanel gp) {
		this.gp = gp;
		monsterData = new MonsterData[gp.maxMap][gp.maxMonster];	// 依照 GamePanel 的 monsterMax 同時 MonsterData
	}
	
	// 初始化 OBJECT 位置
	public void setObject() {
	
		if (gp.obj == null) {
			gp.obj = new Entity[gp.maxMap][gp.maxobj];
		}else {			
			for (int map = 0; map < gp.maxMap; map++) {				
				if (gp.obj[map] == null) {
			        // 如果當前地圖的物件陣列還沒有初始化，則創建它
			        gp.obj[map] = new Entity[gp.maxobj];
			    } else {
			        // 如果當前地圖的物件陣列已經存在，您可以選擇清空它
			        for (int i = 0; i < gp.obj[map].length; i++) {
			            gp.obj[map][i] = null;
			        }
			    }
			}			
		}

		placeObject(0, 25, 23, new OBJ_Chest(gp, new OBJ_Key(gp)));
        placeObject(0, 21, 19, new OBJ_Potion_red(gp));
        placeObject(0, 26, 21, new OBJ_Potion_red(gp));
        placeObject(0, 33, 21, new OBJ_Axe(gp));
        placeObject(0, 35, 21, new OBJ_Shield_Blue(gp));
        placeObject(0, 14, 28, new OBJ_Potion_red(gp));
        placeObject(0, 22, 20, new OBJ_Potion_red(gp));
        
	}
	// 初始化 NPC 位置
	public void setNPC() {
	
		if (gp.npc == null) {
			gp.npc = new Entity[gp.maxMap][gp.maxNpc];
		} else {			
			for (int map = 0; map < gp.maxMap; map++) {				
				if (gp.npc[map] == null) {
			        // 如果當前地圖的物件陣列還沒有初始化，則創建它
			        gp.npc[map] = new Entity[gp.maxNpc];
			    } else {
			        // 如果當前地圖的物件陣列已經存在，您可以選擇清空它
			        for (int i = 0; i < gp.npc[map].length; i++) {
			            gp.npc[map][i] = null;
			        }
			    }				
			}			
		}
		
		placeNPC(0, 21, 21, new NPC_OldMan(gp));	
		placeNPC(1, 12, 7, new NPC_Merchant(gp));
		
	}
	// 初始化 MONSTER 位置
	public void setMonster() {
				
		setMonsterData();
        
		for (int i = 0; i < monsterData.length; i++) {
			if (monsterData[i] != null) {				
				for (int j = 0; j < monsterData[i].length ; j++) {
		        	if (monsterData[i][j] != null) {		        		
		        		placeMonster(i, j, monsterData[i][j].col, monsterData[i][j].row, monsterData[i][j].monster.get());		        		
					} else {
						continue;
					}
		        }				
			}
		} 
	}
	// 初始化 InteractiveTiles
	public void setInteractiveTile() {
		
		if (gp.iTile == null) {
			gp.iTile = new InteractiveTile[gp.maxMap][gp.maxiTle];
		} else {
			for (int map = 0; map < gp.maxMap; map++) {
				if (gp.iTile[gp.currentMap] == null) {
			        // 如果當前地圖的物件陣列還沒有初始化，則創建它
			        gp.iTile[gp.currentMap] = new InteractiveTile[gp.maxNpc];
			    } else {
			        // 如果當前地圖的物件陣列已經存在，您可以選擇清空它
			        for (int i = 0; i < gp.iTile[gp.currentMap].length; i++) {
			            gp.iTile[gp.currentMap][i] = null;
			        }
			    }
			}
		}

		
		for (int i = 0; i < 7; i++) {
			placeInteractiveTile(0, new IT_DryTree(gp, (27+i), 12));
			
		}
		placeInteractiveTile(0, new IT_DryTree(gp, 31, 21));
		
		for (int i = 0; i < 6; i++) {
			placeInteractiveTile(0, new IT_DryTree(gp, (13+i), 40));
		}
		
		placeInteractiveTile(0, new IT_DryTree(gp, 10, 40));
		for (int i = 0; i < 4; i++) {
			placeInteractiveTile(0, new IT_DryTree(gp, (10+i), 41));
			
		}
		
	}
	
	// 補充 死亡(null) 的 怪物 在 原本的位置
    public void replenishMonster(int index) {
    	// 檢查 index 是否有效，初始怪物數據是否存在於當前地圖的該索引
        if (monsterData != null && monsterData.length > gp.currentMap && monsterData[gp.currentMap] != null &&
            index >= 0 && index < monsterData[gp.currentMap].length && monsterData[gp.currentMap][index] != null) {

            MonsterData data = monsterData[gp.currentMap][index];

            // 檢查遊戲世界中當前地圖的該索引位置是否為空
            if (gp.monster != null && gp.monster.length > gp.currentMap && gp.monster[gp.currentMap] != null &&
                index >= 0 && index < gp.monster[gp.currentMap].length && gp.monster[gp.currentMap][index] == null) {

                placeMonster(gp.currentMap, index, data.col, data.row, data.monster.get());
                System.out.println("在 (" + data.col + ", " + data.row + ") 補充了一隻 " + data.monster.get().getClass().getSimpleName() + " 到地圖 " + gp.currentMap);
            }
        }
    }
	
	// 放置 OBJECT 方法
	private void placeObject(int map, int col, int row, Entity object) {
		for (int i = 0; i < gp.obj[map].length; i++) {
			if (gp.obj[map][i] == null) {
				gp.obj[map][i] = object;
				gp.obj[map][i].worldX = gp.tileSize * col;
				gp.obj[map][i].worldY = gp.tileSize * row;
				// System.out.printf("放置OBJECT 在gp.obj[%d][%d]%n", map, i);
				return; // 放置一個怪物後就返回
			}
		}
		System.err.println("警告：無法放置物件，obj 陣列已滿。");
	}
	// 放置 NPC 方法
	private void placeNPC(int map, int col, int row, Entity npc) {
		for (int i = 0; i < gp.npc[map].length; i++) {
			if (gp.npc[map][i] == null) {
				gp.npc[map][i] = npc;
				gp.npc[map][i].worldX = gp.tileSize * col;
				gp.npc[map][i].worldY = gp.tileSize * row;
				// System.out.printf("放置NPC 在gp.npc[%d][%d]%n", map, i);
				return; // 放置一個怪物後就返回
			}
		}
		System.err.println("警告：無法放置物件，npc 陣列已滿。");
	}
	
	// SET MONSTER DATA
	private void setMonsterData() {
		
		// () -> new MON_GreenSlime(gp) 是一個 Lambda 表達式，它沒有參數 (())，並且返回一個新的 MON_GreenSlime(gp) 物件。
		// 這個 Lambda 表達式實作了 Supplier<Entity> 介面的 get() 方法。
		monsterData[0][0] = new MonsterData(21, 38, () -> new MON_GreenSlime(gp));	
		monsterData[0][1] = new MonsterData(23, 42, () -> new MON_GreenSlime(gp));
		monsterData[0][2] = new MonsterData(24, 37, () -> new MON_GreenSlime(gp));
		monsterData[0][3] = new MonsterData(34, 42, () -> new MON_GreenSlime(gp));
		monsterData[0][4] = new MonsterData(38, 42, () -> new MON_GreenSlime(gp));
	}
	
	// 放置 MONSTER 方法
	private void placeMonster(int map, int index, int col, int row, Entity monster) {
        if (gp.monster != null && gp.monster.length > map && gp.monster[map] != null &&
        		index >= 0 && index < gp.monster[map].length) {
        	
            gp.monster[map][index] = monster;
            gp.monster[map][index].worldX = gp.tileSize * col;
            gp.monster[map][index].worldY = gp.tileSize * row;
            // System.out.printf("放置MONSTER在gp.monster[%d][%d]%n", map, index);
            
        } else {
        	System.err.println("錯誤：無效的怪物索引 " + index + " 或無效的地圖索引 " + map);
        }
    }
	
	// 放置 iTile 方法
	private void placeInteractiveTile(int map, InteractiveTile iTile) {
		for (int i = 0; i < gp.iTile[map].length; i++) {
			if (gp.iTile[map][i] == null) {
				gp.iTile[map][i] = iTile;
				// System.out.printf("放置iTile 在gp.iTile[%d][%d]%n", map, i);
				return;
			}
		}
		System.err.println("警告：無法放置物件，iTile 陣列已滿。");
	}
}
