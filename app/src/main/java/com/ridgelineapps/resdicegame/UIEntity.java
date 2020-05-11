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

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.view.MotionEvent;

public class UIEntity {
   enum Type {
      dice,
      road,
      village,
      city,
      knight,
      resource,
      roll,
   }
   
   private int x1;
   private int y1;
   private int x2;
   private int y2;
   
   Game game;
   
   private PolygonLite poly;
   Path path;
   
   private Type type;
   private int index;
   
   UIEntity(Game game, Type type, int index, int x1, int y1, int x2, int y2) {
      this.type = type;
      this.game = game;
      this.index = index;
      this.x1 = Math.min(x1, x2);
      this.y1 = Math.min(y1, y2);
      this.x2 = Math.max(x1, x2);
      this.y2 = Math.max(y1, y2);
   }
   
   UIEntity(Game game, Type type, int index, PolygonLite poly, Path path) {
      this.type = type;
      this.game = game;
      this.index = index;
      this.poly = poly;
      this.path = path;
   }
   
   boolean isWithin(int x, int y) {
      if(poly == null) {
          return x >= x1 && x <= x2 && y >= y1 && y <= y2;
      }
      else {
         return poly.contains(new PointLite(x, y));
      }
   }

    public void touch(int action) {
       if(action != MotionEvent.ACTION_DOWN && action != MotionEvent.ACTION_UP) {
           return;
       }
       
      switch (type) {
         case dice:
            if(action == MotionEvent.ACTION_DOWN) {
                game.diceTouched(index);
            }
            break;
         case road:
             if(action == MotionEvent.ACTION_UP) {
                 game.buildRoad(index);
             }
            break;
         case village:
             if(action == MotionEvent.ACTION_UP) {
                 game.buildVillage(index);
             }
            break;
         case city:
             if(action == MotionEvent.ACTION_UP) {
                 game.buildCity(index);
             }
            break;
         case knight:
             if(action == MotionEvent.ACTION_UP) {
                 game.buildKnight(index);
             }
            break;
         case resource:
            break;
      }
   }
   
   public void draw(Canvas canvas) {
      Paint p = new Paint();
      p.setStyle(Style.FILL);
      boolean draw = true;
      switch (type) {
      case road:
          if(game.canBuildRoad(index)) {
              highlight(p);
          }
          else if(index > 0 && game.playsheet.roads[index]) {
              darken(p);
          }
          else {
              draw = false;
          }
         break;
      case village:
          if(game.canBuildVillage(index)) {
              highlight(p);
          }
          else if(game.playsheet.villages[index]) {
              darken(p);
          }
          else {
              draw = false;
          }
         break;
      case city:
          if(game.canBuildCity(index)) {
              highlight(p);
          }
          else if(game.playsheet.cities[index]) {
              darken(p);
          }
          else {
              draw = false;
          }
         break;
      case knight:
          if(game.canBuildKnight(index)) {
              highlight(p);
          }
          else if(game.playsheet.knights >= index) {
              darken(p);
          }
          else {
              draw = false;
          }
         break;
      case resource:
          if(game.playsheet.canUseKnightResource(index)) {
              draw = false;
          }
          else if(game.playsheet.isKnightResourceUsed(index)) {
              p.setARGB(128, 220, 20, 20);
          }
          else {
              draw = false;
          }
         break;
     default:
         draw = false;
         break;
      }
      if(draw) {
          canvas.drawPath(path, p);
      }
   }
   
   private void highlight(Paint p) {
       p.setARGB(100, 35, 180, 15);
   }
   
   private void darken(Paint p) {
       p.setARGB(128, 100, 100, 0);
   }
}
