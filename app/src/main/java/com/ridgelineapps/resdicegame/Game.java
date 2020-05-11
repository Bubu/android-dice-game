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

import java.util.ArrayList;
import java.util.Collections;

//TODO: 
//  Confirm "Use knight resource(s)"
//  Center vertically
//  High score menu & toast when beating after 1st play

public class Game {
   Playsheet playsheet;
   Dice[] dice;
   int rolls;
   private ArrayList<Dice.Value> knightResources;
   private boolean builtResourceThisTurn;
   int turnsTaken;
   private boolean scored = false;
   
   private static Dice.Value[] RESOURCES_ROAD = new Dice.Value[]{ Dice.Value.Brick, Dice.Value.Lumber };
   private static Dice.Value[] RESOURCES_VILLAGE = new Dice.Value[]{ Dice.Value.Lumber, Dice.Value.Brick, Dice.Value.Wool, Dice.Value.Grain };
   private static Dice.Value[] RESOURCES_KNIGHT = new Dice.Value[]{ Dice.Value.Wool, Dice.Value.Grain, Dice.Value.Ore };
   private static Dice.Value[] RESOURCES_CITY = new Dice.Value[]{ Dice.Value.Grain, Dice.Value.Grain, Dice.Value.Ore, Dice.Value.Ore, Dice.Value.Ore };
   
   GameView gameView;
   
   public Game() {
      playsheet = new Playsheet();
      knightResources = new ArrayList<>();
      dice = new Dice[6];
      for(int i=0; i < dice.length; i++) {
         dice[i] = new Dice();
      }
      
      turnsTaken = 0;
      newTurn(true);
   }
   
   String getString(int id) {
      return gameView.activity.getResources().getString(id);
   }
   
   void setGameView(GameView gameView) {
      this.gameView = gameView;
   }
   
   void reset() {
      turnsTaken = 0;
      playsheet.reset();
      scored = false;
      newTurn(true);
      if(gameView != null)
         gameView.postInvalidate();      
   }
   
   private void newTurn(boolean firstTurn) {
       if(isGameDone()) {
           return;
       }
       
      rolls = 0;
      
      //Return unused knightResources
      for(int i=0; i < knightResources.size(); i++) {
          int index = getKnightResourceIndex(knightResources.get(i));
          if(index > 0) {
              playsheet.resourcesAvail[index] = true;
          }
      }
      
      knightResources.clear();
      for (Dice die : dice) {
         die.reset();
      }
      if(!firstTurn && !builtResourceThisTurn) {
         playsheet.turnsNothingBuilt++;
      }
      builtResourceThisTurn = false;
      
      if(!firstTurn) {
         turnsTaken++;
         playsheet.scoreTurn(turnsTaken);
         if(isGameDone()) {
             if(!scored) {
                 scored = true;
                 if(playsheet.getScore() > 0) {
                    HighScores.score(gameView.activity, playsheet.getScore());
                 }
             }
         }
      }
      if(gameView != null)
         gameView.postInvalidate();      
   }
   
   private boolean canUseKnightResource(int i) {
      return playsheet.canUseKnightResource(i);
   }

   void diceTouched(int i) {
      if(rolls == 0 || i < 0 || i >= dice.length) {
         return;
      }
      
      dice[i].hold(rolls, !dice[i].isHeld());
      if(gameView != null)
          gameView.postInvalidate();      
   }
   
   void roll() {
       if(isGameDone()) {
           reset();
           return;
       }
       
      if(!canRoll()) {
         newTurn(false);
         return;
      }
      
      int len = dice.length;
      Dice.Value[] old = new Dice.Value[len];
      for(int i=0; i < len; i++) {
         Dice die = dice[i];
         if(!die.isHeld()) {
            old[i] = die.getValue();
            die.roll();
         }         
      }
      
      // People think the dice are not random when they don't change, 
      // so keep the same dice, but switch order if possible to help
      // remove that disconcerting "I rolled, but some of the dice
      // didn't change" effect.
      for(int i=0; i < len; i++) {
         Dice die = dice[i];
         if(!die.isHeld()) {
            if(old[i] == die.getValue()) {
               for(int j=0; j < len; j++) {
                  Dice die2 = dice[j];
                  if(i == j || die2.isHeld()) {
                     continue;
                  }
                  if(old[j] != die.getValue() && old[i] != die2.getValue()) {
                     die.swap(die2);
                     break;
                  }
               }
            }
         }         
      }
      
      rolls++;
      if(gameView != null)
         gameView.postInvalidate();
   }
   
   boolean canRoll() {
      return (rolls < 3 && !builtResourceThisTurn && !isGameDone());
   }
   
   void buildVillage(int i) {
      if(!canBuildVillage(i)) {
         return;
      }
      
      useResources(RESOURCES_VILLAGE);
      playsheet.buildVillage(i);
      builtResourceThisTurn = true;
      if(gameView != null)
         gameView.postInvalidate();            
   }
   
   boolean canBuildVillage(int i) {
      if(!canBuildVillage()) {
         return false;
      }
      
      return playsheet.canBuildVillage(i);
   }
   
   private boolean canBuildVillage() {
      if(rolls == 0) {
         return false;
      }
      
      return canBuild(RESOURCES_VILLAGE);
   }
   
   void buildKnight(int i) {
      if(!canBuildKnight(i)) {
         return;
      }
      
      useResources(RESOURCES_KNIGHT);
      playsheet.buildKnight(i);
      builtResourceThisTurn = true;
      if(gameView != null)
         gameView.postInvalidate();            
   }
   
   boolean canBuildKnight(int i) {
      if(!canBuildKnight()) {
         return false;
      }
      
      return playsheet.canBuildKnight(i);
   }
   
   private boolean canBuildKnight() {
      if(rolls == 0) {
         return false;
      }
      
      return canBuild(RESOURCES_KNIGHT);
   }
   
   void buildRoad(int i) {
      if(!canBuildRoad(i)) {
         return;
      }
      
      useResources(RESOURCES_ROAD);
      playsheet.buildRoad(i);
      builtResourceThisTurn = true;
      if(gameView != null)
         gameView.postInvalidate();            
   }
   
   boolean canBuildRoad(int i) {
      if(!canBuildRoad()) {
         return false;
      }
      
      return playsheet.canBuildRoad(i);
   }   
   
   private boolean canBuildRoad() {
      if(rolls == 0) {
         return false;
      }

      return canBuild(RESOURCES_ROAD);
   }
   
   void buildCity(int i) {
      if(!canBuildCity(i)) {
         return;
      }
      
      useResources(RESOURCES_CITY);
      playsheet.buildCity(i);
      builtResourceThisTurn = true;
      if(gameView != null)
         gameView.postInvalidate();            
   }
   
   boolean canBuildCity(int i) {
      if(!canBuildCity()) {
         return false;
      }
      
      return playsheet.canBuildCity(i);
   }   
   
   private boolean canBuildCity() {
      if(rolls == 0) {
         return false;
      }

      return canBuild(RESOURCES_CITY);
   }
   
   private void useResources(Dice.Value[] resources) {
      // TODO: Cleanup how knightResources are tracked
      knightResources.clear();
      for(int i=1; i <= 6; i++) {
          if(canUseKnightResource(i)) {
              knightResources.add(playsheet.getKnightResource(i));
          }
      }
      
      int gold = 0;
      for(Dice die : dice) {
         if(die.isUsable() && die.getValue().equals(Dice.Value.Gold)) {
            gold++;
         }
      } 
      
      if(gold % 2 != 0) {
         gold--;
      }
      
      ArrayList<Dice.Value> resList = new ArrayList<>();
      Collections.addAll(resList, resources);
      
      for(Dice.Value val : resources) {
         for (Dice die : dice) {
            if (die.isUsable() && die.value.equals(val)) {
               die.use();
               resList.remove(val);
               break;
            }
         }
      }
      
      ArrayList<Dice.Value> resListFound = new ArrayList<>();

      for(Dice.Value val : resList) {
          for(int i = knightResources.size() - 1; i >=0 ; i--) {
             if(knightResources.get(i).equals(val)) {
                resListFound.add(val);
                break;
             }
          }
      }
      
      for(Dice.Value val : resListFound) {
         resList.remove(val);
      }
      
      int goldUsed = 0;
      boolean useWildcardResource = false;
      
      if(gold / 2 == resList.size()) {
         goldUsed = gold;
      }   
      else if(gold / 2 > resList.size()) {
         goldUsed = resList.size() * 2;
         for(int i=0; i < gold / 2 - resList.size() && resListFound.size() > 0; i++) {
            resListFound.remove(0);
            goldUsed += 2;
         }
      }
      else if(gold / 2 < resList.size()) {
         useWildcardResource = true;
         goldUsed = gold;
      }

      
      for(int i=0; i < dice.length && goldUsed > 0; i++) {
         if(dice[i].isUsable() && dice[i].value.equals(Dice.Value.Gold)) {
            dice[i].use();
            goldUsed--;
         }
      }
            
      for(Dice.Value val : resListFound) {
         for(int i = knightResources.size() - 1; i >=0 ; i--) {
            if(knightResources.get(i).equals(val)) {
               int index = getKnightResourceIndex(knightResources.get(i));
               playsheet.useKnightResource(index);
               knightResources.remove(i);
               break;
            }
         }            
      }
      if(useWildcardResource) {
          for(int i = knightResources.size() - 1; i >=0 ; i--) {
              if(knightResources.get(i).equals(Dice.Value.Any)) {
                  int index = getKnightResourceIndex(knightResources.get(i));
                  playsheet.useKnightResource(index);
                  knightResources.remove(i);
                  break;
              }
          }            
      }
   }

   private boolean canBuild(Dice.Value[] resourcesRequired) {
      boolean[] diceUsed = new boolean[dice.length];
      
      // TODO: Cleanup how knightResources are tracked
      knightResources.clear();
      for(int i=1; i <= 6; i++) {
          if(canUseKnightResource(i)) {
              knightResources.add(playsheet.getKnightResource(i));
          }
      }
      
      boolean[] knightResourcesUsed = new boolean[knightResources.size()];
      int gold = 0;
      for(Dice die : dice) {
         if(die.isUsable() && die.getValue().equals(Dice.Value.Gold)) {
            gold++;
         }
      }         
      int unfound = 0;
      for(Dice.Value val : resourcesRequired) {
         boolean found = false;
         for(int i=0; i < dice.length; i++) {
            if(dice[i].isUsable() && !diceUsed[i] && dice[i].value.equals(val)) {
               diceUsed[i] = true;
               found = true;
               break;
            }
         }
         if(!found) {
            for(int i=0; i < knightResources.size(); i++) {
               if(!knightResourcesUsed[i] && knightResources.get(i).equals(val)) {
                  knightResourcesUsed[i] = true;
                  found = true;
                  break;
               }
            }            
         }
         if(!found) {
             for(int i=0; i < knightResources.size(); i++) {
                if(!knightResourcesUsed[i] && knightResources.get(i).equals(Dice.Value.Any)) {
                   knightResourcesUsed[i] = true;
                   found = true;
                   break;
                }
             }            
          }
         
         if(!found) {
            unfound++;
         }
      }

      if(unfound > gold / 2) {
          return false;
      }
      return true;
   }
   
   boolean isGameDone() {
       return (turnsTaken == 15);
   }
   
   private int getKnightResourceIndex(Dice.Value val) {
       int index = 0;
       switch(val) {
           case Ore:
               index = 1;
               break;
           case Grain:
               index = 2;
               break;
           case Wool:
               index = 3;
               break;
           case Lumber:
               index = 4;
               break;
           case Brick:
               index = 5;
               break;
           case Any:
               index = 6;
               break;
        }
       return index;
   }
}
