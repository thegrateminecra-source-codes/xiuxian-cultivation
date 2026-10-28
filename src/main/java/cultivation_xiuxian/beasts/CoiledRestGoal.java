package cultivation_xiuxian.beasts;

import net.minecraft.world.entity.ai.goal.Goal;

public class CoiledRestGoal extends Goal {
    private final CommonSnakeBeast snake;
    private int restTicks;
    private int cooldown;

    public CoiledRestGoal(CommonSnakeBeast snake) {
        this.snake = snake;
    }

    @Override
    public boolean canUse() {
        if (this.cooldown > 0) {
            this.cooldown--;
            return false;
        }
        if (this.snake.threatened() || !this.snake.onGround() || this.snake.isInWater()) {
            return false;
        }
        if (this.snake.isInShed()) {
            // shed snakes rest far more often
            return this.snake.getRandom().nextInt(20) == 0;
        }
        return this.snake.getRandom().nextInt(300) == 0;
    }

    @Override
    public boolean canContinueToUse() {
        return this.restTicks-- > 0 && !this.snake.threatened();
    }

    @Override
    public void start() {
        this.restTicks = 400 + this.snake.getRandom().nextInt(600);
        this.snake.setResting(true);
        this.snake.getNavigation().stop();
    }

    @Override
    public void stop() {
        this.snake.setResting(false);
        this.cooldown = 200;
    }
}
