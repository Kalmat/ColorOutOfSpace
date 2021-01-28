package dev.alef.coloroutofspace.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.piglin.AbstractPiglinEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.GroundPathHelper;
import net.minecraft.world.World;

public abstract class AbstractColorEntity extends AbstractPiglinEntity {
   protected static final DataParameter<Boolean> field_242333_b = EntityDataManager.createKey(AbstractColorEntity.class, DataSerializers.BOOLEAN);
   protected int field_242334_c = 0;

   public AbstractColorEntity(EntityType<? extends AbstractColorEntity> p_i241915_1_, World p_i241915_2_) {
      super(p_i241915_1_, p_i241915_2_);
      this.setCanPickUpLoot(true);
      this.func_242339_eS();
      this.setPathPriority(PathNodeType.DANGER_FIRE, 16.0F);
      this.setPathPriority(PathNodeType.DAMAGE_FIRE, -1.0F);
   }

   private void func_242339_eS() {
      if (GroundPathHelper.isGroundNavigator(this)) {
         ((GroundPathNavigator)this.getNavigator()).setBreakDoors(true);
      }
   }

   protected void updateAITasks() {
      super.updateAITasks();
   }

   public boolean func_242336_eL() {
      return false;
   }
}