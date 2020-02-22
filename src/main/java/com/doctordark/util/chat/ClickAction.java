package com.doctordark.util.chat;

import net.minecraft.server.v1_7_R4.*;

public enum ClickAction
{
  OPEN_URL(EnumClickAction.OPEN_URL), 
  OPEN_FILE(EnumClickAction.OPEN_FILE), 
  RUN_COMMAND(EnumClickAction.RUN_COMMAND), 
  SUGGEST_COMMAND(EnumClickAction.SUGGEST_COMMAND);
  
  private final EnumClickAction clickAction;
  
  private ClickAction(final EnumClickAction action) {
      this.clickAction = action;
  }
  
  public EnumClickAction getNMS() {
      return this.clickAction;
  }
}