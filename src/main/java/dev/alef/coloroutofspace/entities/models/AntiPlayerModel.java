package dev.alef.coloroutofspace.entities.models;

import net.minecraft.client.renderer.entity.model.AbstractZombieModel;
import net.minecraft.entity.monster.ZombieEntity;

public class AntiPlayerModel<T extends ZombieEntity> extends AbstractZombieModel<T> {

   public AntiPlayerModel(float modelSize, boolean p_i1168_2_) {
      super(modelSize, 0.0F, 64, p_i1168_2_ ? 32 : 64);
   }

   public boolean isAggressive(T entityIn) {
      return entityIn.isAggressive();
   }
}
