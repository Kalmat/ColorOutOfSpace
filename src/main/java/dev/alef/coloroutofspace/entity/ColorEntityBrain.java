package dev.alef.coloroutofspace.entity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.task.AttackTargetTask;
import net.minecraft.entity.ai.brain.task.DummyTask;
import net.minecraft.entity.ai.brain.task.FindInteractionAndLookTargetTask;
import net.minecraft.entity.ai.brain.task.FindNewAttackTargetTask;
import net.minecraft.entity.ai.brain.task.FirstShuffledTask;
import net.minecraft.entity.ai.brain.task.ForgetAttackTargetTask;
import net.minecraft.entity.ai.brain.task.GetAngryTask;
import net.minecraft.entity.ai.brain.task.InteractWithDoorTask;
import net.minecraft.entity.ai.brain.task.InteractWithEntityTask;
import net.minecraft.entity.ai.brain.task.LookAtEntityTask;
import net.minecraft.entity.ai.brain.task.LookTask;
import net.minecraft.entity.ai.brain.task.MoveToTargetTask;
import net.minecraft.entity.ai.brain.task.WalkRandomlyTask;
import net.minecraft.entity.ai.brain.task.WalkToTargetTask;
import net.minecraft.entity.ai.brain.task.WalkTowardsPosTask;
import net.minecraft.entity.ai.brain.task.WorkTask;
import net.minecraft.entity.monster.piglin.AbstractPiglinEntity;
import net.minecraft.entity.monster.piglin.PiglinBruteBrain;
import net.minecraft.entity.monster.piglin.PiglinTasks;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.GlobalPos;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class ColorEntityBrain extends PiglinBruteBrain {
	
   private static final Method method = ObfuscationReflectionHelper.findMethod(PiglinTasks.class, "func_234509_e_", AbstractPiglinEntity.class, LivingEntity.class);
	
   protected static Brain<?> func_242354_a(ColorEntity p_242354_0_, Brain<ColorEntity> p_242354_1_) {
      func_242359_b(p_242354_0_, p_242354_1_);
      func_242362_c(p_242354_0_, p_242354_1_);
      func_242364_d(p_242354_0_, p_242354_1_);
      p_242354_1_.setDefaultActivities(ImmutableSet.of(Activity.CORE));
      p_242354_1_.setFallbackActivity(Activity.IDLE);
      p_242354_1_.switchToFallbackActivity();
      return p_242354_1_;
   }

   protected static void func_242352_a(ColorEntity p_242352_0_) {
      GlobalPos globalpos = GlobalPos.getPosition(p_242352_0_.world.getDimensionKey(), p_242352_0_.getPosition());
      p_242352_0_.getBrain().setMemory(MemoryModuleType.HOME, globalpos);
   }

   private static void func_242359_b(ColorEntity p_242354_0_, Brain<ColorEntity> p_242359_1_) {
      p_242359_1_.registerActivity(Activity.CORE, 0, ImmutableList.of(new LookTask(45, 90), new WalkToTargetTask(), new InteractWithDoorTask(), new GetAngryTask<>()));
   }

   private static void func_242362_c(ColorEntity p_242354_0_, Brain<ColorEntity> p_242362_1_) {
      p_242362_1_.registerActivity(Activity.IDLE, 10, ImmutableList.of(new ForgetAttackTargetTask<>(ColorEntityBrain::func_242349_a), func_242346_a(), func_242356_b(), new FindInteractionAndLookTargetTask(EntityType.PLAYER, 4)));
   }

   private static void func_242364_d(ColorEntity p_242354_0_, Brain<ColorEntity> p_242364_1_) {
      p_242364_1_.registerActivity(Activity.FIGHT, 10, ImmutableList.of(new FindNewAttackTargetTask<>((p_242361_1_) -> {
         return !func_242350_a(p_242354_0_, p_242361_1_);
      }), new MoveToTargetTask(1.0F), new AttackTargetTask(20)), MemoryModuleType.ATTACK_TARGET);
   }

   private static FirstShuffledTask<ColorEntity> func_242346_a() {
      return new FirstShuffledTask<>(ImmutableList.of(Pair.of(new LookAtEntityTask(EntityType.PLAYER, 8.0F), 1), Pair.of(new LookAtEntityTask(EntityType.PIGLIN, 8.0F), 1), Pair.of(new LookAtEntityTask(EntityType.field_242287_aj, 8.0F), 1), Pair.of(new LookAtEntityTask(8.0F), 1), Pair.of(new DummyTask(30, 60), 1)));
   }

   private static FirstShuffledTask<ColorEntity> func_242356_b() {
      return new FirstShuffledTask<>(ImmutableList.of(Pair.of(new WalkRandomlyTask(0.6F), 2), Pair.of(InteractWithEntityTask.func_220445_a(EntityType.PIGLIN, 8, MemoryModuleType.INTERACTION_TARGET, 0.6F, 2), 2), Pair.of(InteractWithEntityTask.func_220445_a(EntityType.field_242287_aj, 8, MemoryModuleType.INTERACTION_TARGET, 0.6F, 2), 2), Pair.of(new WalkTowardsPosTask(MemoryModuleType.HOME, 0.6F, 2, 100), 2), Pair.of(new WorkTask(MemoryModuleType.HOME, 0.6F, 5), 2), Pair.of(new DummyTask(30, 60), 1)));
   }

   protected static void func_242358_b(ColorEntity p_242358_0_) {
      Brain<ColorEntity> brain = p_242358_0_.getBrain();
      Activity activity = brain.getTemporaryActivity().orElse((Activity)null);
      brain.switchActivities(ImmutableList.of(Activity.FIGHT, Activity.IDLE));
      Activity activity1 = brain.getTemporaryActivity().orElse((Activity)null);
      if (activity != activity1) {
         func_242363_d(p_242358_0_);
      }

      p_242358_0_.setAggroed(brain.hasMemory(MemoryModuleType.ATTACK_TARGET));
   }

   private static boolean func_242350_a(AbstractColorEntity p_242354_0_, LivingEntity p_242350_1_) {
      return func_242349_a(p_242354_0_).filter((p_242348_1_) -> {
         return p_242348_1_ == p_242350_1_;
      }).isPresent();
   }

   private static Optional<? extends LivingEntity> func_242349_a(AbstractColorEntity p_242349_0_) {
      Optional<LivingEntity> optional = BrainUtil.getTargetFromMemory(p_242349_0_, MemoryModuleType.ANGRY_AT);
      if (optional.isPresent() && func_242347_a(optional.get())) {
         return optional;
      } else {
         Optional<? extends LivingEntity> optional1 = func_242351_a(p_242349_0_, MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER);
         return optional1.isPresent() ? optional1 : p_242349_0_.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_NEMESIS);
      }
   }

   private static boolean func_242347_a(LivingEntity p_242347_0_) {
      return EntityPredicates.CAN_HOSTILE_AI_TARGET.test(p_242347_0_);
   }

   private static Optional<? extends LivingEntity> func_242351_a(AbstractColorEntity p_242351_0_, MemoryModuleType<? extends LivingEntity> p_242351_1_) {
      return p_242351_0_.getBrain().getMemory(p_242351_1_).filter((p_242357_1_) -> {
         return p_242357_1_.isEntityInRange(p_242351_0_, 12.0D);
      });
   }

   protected static void func_242353_a(ColorEntity p_242353_0_, LivingEntity p_242353_1_) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      if (!(p_242353_1_ instanceof AbstractColorEntity)) {
		  //ColorEntityBrain.method.setAccessible(true);
		  ColorEntityBrain.method.invoke(PiglinTasks.class, p_242353_0_, p_242353_1_);
		  //PiglinTasks.func_234509_e_(p_242353_0_, p_242353_1_);
      }
   }

   protected static void func_242360_c(ColorEntity p_242360_0_) {
      if ((double)p_242360_0_.world.rand.nextFloat() < 0.0125D) {
         func_242363_d(p_242360_0_);
      }
   }

   private static void func_242363_d(ColorEntity p_242363_0_) {
      p_242363_0_.getBrain().getTemporaryActivity().ifPresent((p_242355_1_) -> {
         if (p_242355_1_ == Activity.FIGHT) {
            p_242363_0_.func_242345_eT();
         }
      });
   }
}
