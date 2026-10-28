package cultivation_xiuxian.beasts;

import java.util.Comparator;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.rabbit.Rabbit;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import cultivation_xiuxian.blocks.CultivationBlocks;
import cultivation_xiuxian.items.CultivationItems;

public class CommonSnakeBeast extends AgeableMob {
    private static final EntityDataAccessor<Boolean> RESTING = SynchedEntityData.defineId(CommonSnakeBeast.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> TONGUE_FLICK = SynchedEntityData.defineId(CommonSnakeBeast.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IN_SHED = SynchedEntityData.defineId(CommonSnakeBeast.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SHEARED = SynchedEntityData.defineId(CommonSnakeBeast.class, EntityDataSerializers.BOOLEAN);

    private static final int SHED_WARNING_TICKS = 3000;
    private static final int LAY_INTERVAL_TICKS = 12000;
    private static final float LAY_CHANCE = 0.35F;
    private int shedTimer;
    private int layTimer = LAY_INTERVAL_TICKS;
    private int tongueTimer = 300;
    private int tongueTicksLeft;
    private int restCooldown;
    private int trampleCheckCooldown;
    private int biteCooldown;

    public CommonSnakeBeast(EntityType<? extends CommonSnakeBeast> type, Level level) {
        super(type, level);
        this.shedTimer = this.nextShedTime();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.ATTACK_DAMAGE, 3.0)
                .add(Attributes.FOLLOW_RANGE, 12.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.6)
                .add(Attributes.STEP_HEIGHT, 1.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.5));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.4, true));
        this.goalSelector.addGoal(4, new CoiledRestGoal(this));
        this.goalSelector.addGoal(5, new AmbushStrollGoal(this));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Rabbit.class, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Chicken.class, false));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Frog.class, false));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(RESTING, false);
        builder.define(TONGUE_FLICK, false);
        builder.define(IN_SHED, false);
        builder.define(SHEARED, false);
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (this.level() instanceof ServerLevel serverLevel && this.isAlive()) {
            boolean inShed = this.shedTimer <= SHED_WARNING_TICKS;
            if (!this.isBaby()) {
                if (--this.shedTimer <= 0) {
                    this.shedTimer = this.nextShedTime();
                    this.setSheared(false); // new cycle, hide regrown
                    this.spawnAtLocation(serverLevel, new ItemStack(CultivationItems.SHED_SNAKE_SKIN));
                    if (this.random.nextInt(3) == 0) {
                        this.spawnAtLocation(serverLevel, new ItemStack(CultivationItems.SHED_SNAKE_SKIN));
                    }
                }
                this.setInShed(inShed);

                this.layEgg(serverLevel);
            }

            if (this.restCooldown > 0) {
                this.restCooldown--;
            }
            if (this.biteCooldown > 0) {
                this.biteCooldown--;
            }

            // trampled snakes strike at once
            if (--this.trampleCheckCooldown <= 0) {
                this.trampleCheckCooldown = 5;
                List<LivingEntity> intruders = this.level().getEntitiesOfClass(LivingEntity.class,
                        this.getBoundingBox().inflate(0.4, 0.1, 0.4),
                        e -> e.isAlive() && !e.isSpectator() && !(e instanceof CommonSnakeBeast)
                                && !(e instanceof Player player && player.isCreative()));
                if (!intruders.isEmpty()) {
                    LivingEntity intruder = intruders.stream()
                            .min(Comparator.comparingDouble(this::distanceToSqr))
                            .orElse(null);
                    if (intruder != null) {
                        this.setTarget(intruder);
                        if (this.biteCooldown <= 0) {
                            this.biteCooldown = 20;
                            this.playSound(CommonSounds.SNAKE_ALARM, 1.0F, 1.0F);
                            intruder.hurtServer(serverLevel, this.damageSources().mobAttack(this),
                                    (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
                        }
                        this.setResting(false);
                    }
                }
            }

            // When Snake Rest - its Toung Flicks More
            if (--this.tongueTimer <= 0) {
                this.tongueTimer = this.isResting() ? 140 + this.random.nextInt(200) : 300 + this.random.nextInt(400);
                this.tongueTicksLeft = 25;
            }
            if (this.tongueTicksLeft > 0) {
                this.tongueTicksLeft--;
            }
            this.setTongueFlick(this.tongueTicksLeft > 0);
        }
    }

    private int nextShedTime() {
        return 24000 + this.random.nextInt(24000);
    }

    private void layEgg(ServerLevel level) {
        if (--this.layTimer > 0) {
            return;
        }
        this.layTimer = LAY_INTERVAL_TICKS;
        if (this.random.nextFloat() >= LAY_CHANCE) {
            return;
        }
        BlockPos pos = BlockPos.containing(this.position());
        if (level.getBlockState(pos).isAir()) {
            level.setBlock(pos, CultivationBlocks.COMMON_SNAKE_EGG.defaultBlockState(), 3);
            this.playSound(CommonSounds.SNAKE_LAY, 1.0F, 1.0F);
        }
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return new CommonSnakeBeast(CommonBeasts.COMMON_SNAKE_BEAST, level);
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        boolean hurt = super.hurtServer(level, source, amount);
        if (hurt) {
            this.setResting(false);
            this.restCooldown = 600;
        }
        return hurt;
    }

    public boolean isResting() {
        return this.entityData.get(RESTING);
    }

    public void setResting(boolean resting) {
        this.entityData.set(RESTING, resting);
    }

    public boolean isTongueFlicking() {
        return this.entityData.get(TONGUE_FLICK);
    }

    private void setTongueFlick(boolean flicking) {
        this.entityData.set(TONGUE_FLICK, flicking);
    }

    public boolean isInShed() {
        return this.entityData.get(IN_SHED);
    }

    private void setInShed(boolean inShed) {
        this.entityData.set(IN_SHED, inShed);
    }

    int restCooldown() {
        return this.restCooldown;
    }

    @Override
    public void addAdditionalSaveData(ValueOutput out) {
        super.addAdditionalSaveData(out);
        out.putInt("ShedTimer", this.shedTimer);
        out.putInt("LayTimer", this.layTimer);
        out.putBoolean("Resting", this.isResting());
        out.putBoolean("Sheared", this.isSheared());
    }

    @Override
    public void readAdditionalSaveData(ValueInput in) {
        super.readAdditionalSaveData(in);
        this.shedTimer = in.getIntOr("ShedTimer", this.nextShedTime());
        this.layTimer = in.getIntOr("LayTimer", LAY_INTERVAL_TICKS);
        this.setResting(in.getBooleanOr("Resting", false));
        this.setSheared(in.getBooleanOr("Sheared", false));
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(Items.SHEARS)) {
            if (this.level() instanceof ServerLevel level && this.readyForShearing()) {
                level.playSound(null, this, SoundEvents.SHEEP_SHEAR, SoundSource.PLAYERS, 1.0F, 1.0F);
                this.spawnAtLocation(level, new ItemStack(CultivationItems.SHED_SNAKE_SKIN));
                this.gameEvent(GameEvent.SHEAR, player);
                stack.hurtAndBreak(1, player, hand.asEquipmentSlot());
                this.setSheared(true);
                return InteractionResult.SUCCESS_SERVER;
            }
            return InteractionResult.CONSUME;
        }
        return super.mobInteract(player, hand);
    }

    public boolean isSheared() {
        return this.entityData.get(SHEARED);
    }

    public void setSheared(boolean sheared) {
        this.entityData.set(SHEARED, sheared);
    }

    public boolean readyForShearing() {
        return !this.isSheared();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return CommonSounds.SNAKE_SLITHER;
    }

    @Override
    public void playAmbientSound() {
        if (this.navigation.isInProgress() || this.getDeltaMovement().horizontalDistanceSqr() > 0.0016) {
            super.playAmbientSound();
        }
    }

    @Override
    public int getAmbientSoundInterval() {
        return 30;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return CommonSounds.SNAKE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return CommonSounds.SNAKE_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 0.9F;
    }

    // Disable Fall Damage For Beast
    @Override
    public boolean causeFallDamage(double fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    public static boolean checkSnakeSpawnRules(
            EntityType<CommonSnakeBeast> type, LevelAccessor level, EntitySpawnReason reason, BlockPos pos, net.minecraft.util.RandomSource random) {
        return level.getFluidState(pos).isEmpty()
                && level.getBlockState(pos.below()).isValidSpawn(level, pos.below(), type)
                && level.getRawBrightness(pos, 0) > 7;
    }

    boolean threatened() {
        LivingEntity lastHurtBy = this.getLastHurtByMob();
        return (lastHurtBy != null && lastHurtBy.isAlive())
                || this.isAggressive()
                || this.tickCount - this.getLastHurtByMobTimestamp() < 200;
    }

    static List<Class<? extends LivingEntity>> preyClasses() {
        return List.of(Rabbit.class, Chicken.class, Frog.class);
    }
}
