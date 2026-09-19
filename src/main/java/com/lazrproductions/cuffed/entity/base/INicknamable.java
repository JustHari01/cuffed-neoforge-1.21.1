package com.lazrproductions.cuffed.entity.base;

import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

public interface INicknamable {
   Component getNickname();

   void setNickname(@Nullable Component var1);

   String serializeNickname();

   void deserializeNickname(CompoundTag var1);

   void deserializeNickname(String var1);
}
