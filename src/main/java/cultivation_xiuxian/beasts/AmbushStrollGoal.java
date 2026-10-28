package cultivation_xiuxian.beasts;

import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;

public class AmbushStrollGoal extends WaterAvoidingRandomStrollGoal {
    private final CommonSnakeBeast snake;

    public AmbushStrollGoal(CommonSnakeBeast snake) {
        super(snake, 0.7);
        this.snake = snake;
    }

    @Override
    public boolean canUse() {
        if (this.snake.isResting() || this.snake.isInShed()) {
            return false;
        }
        return this.snake.getRandom().nextBoolean() && super.canUse();
    }
}
