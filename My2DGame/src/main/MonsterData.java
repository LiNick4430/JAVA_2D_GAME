package main;

import entity.Entity;

//紀錄 Monster 的基本資料
public class MonsterData {
     int col;
     int row;
     // 宣告了一個名為 monsterSupplier 的變數，這個變數將會持有一個函數（或者更準確地說，一個實作了 Supplier<Entity> 介面的物件
     // ，這個函數在被調用時（通過 get() 方法）將會提供（產生並返回）一個 Entity 類型的物件。
     java.util.function.Supplier<Entity> monster;

     // java.util.function  	// Java 8 引入的一個函數式介面（Functional Interface）的套件。函數式介面是只包含一個抽象方法的介面，可以用於 Lambda 表達式或方法引用。
     // Supplier<T> 介面			// Supplier<T> 是一個函數式介面，它代表一個提供者，可以產生一個 T 類型的結果。
  								// 只包含一個抽象方法：T get()。這個方法不接受任何參數，並返回一個 T 類型的實例。
     
     public MonsterData(int col, int row, java.util.function.Supplier<Entity> monster) {
         this.col = col;
         this.row = row;
         this.monster = monster;
     }
 }
