/*
 * Copyright (C) 2012 Resource Dice Game (http://code.google.com/p/android-dice-game)
 * 
 * This program is free software: you can redistribute it and/or 
 * modify it under the terms of the GNU General Public License as published 
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *   
 * This source code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.ridgelineapps.resdicegame;

public class Dice {
   enum Value {
      None,
      Wool,
      Grain,
      Brick,
      Ore,
      Lumber,
      Gold,
      Any
   }
   
   Value value;
   private boolean held;
   private boolean used;
   
   public Dice() {
      reset();
   }
   
   void roll() {
      int i = (int) (Math.random() * 6 + 1);
      switch(i) {
         case 1:
            value = Value.Wool;
            break;
         case 2:
            value = Value.Grain;
            break;
         case 3:
            value = Value.Brick;
            break;
         case 4:
            value = Value.Ore;
            break;
         case 5:
            value = Value.Lumber;
            break;
         case 6:
            value = Value.Gold;
            break;
      }
   }
   
   void swap(Dice die) {
      Dice.Value temp = die.getValue();
      die.setValue(value);
      value = temp;      
   }
   
   boolean isHeld() {
      return held;
   }
   
   void hold(boolean b) {
      held = b;
   }
   
   boolean isUsable() {
      return !used;
   }
   
   void use() {
      used = true;
   }
   
   Value getValue() {
      return value;
   }
   
   private void setValue(Value value) {
      this.value = value;
   }
   
   void reset() {
      held = false;
      used = false;
      value = Value.None;
   }
}
