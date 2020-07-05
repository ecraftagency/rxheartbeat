package com.transport.model;

import com.statics.FightData;
import com.statics.GameShowData;
import com.statics.RunShowData;
import com.statics.ShoppingData;

import java.util.List;

public class Fight {
  public FightData.FightLV  currentFightLV;
  public List<Integer>      usedIdols; //idolId - n restore
  public List<Integer>      restoreIdols;

  public GameShowData.GameShow  currentGameShow;
  public List<Integer>          gameShowUsedIdols;
  public List<Integer>          gameShowRestoreIdols;
  public int                    gameShowOpenCountDown;

  public RunShowData.RunShow    currentRunShow;
  public ShoppingData.Shopping  currentShopping;
}