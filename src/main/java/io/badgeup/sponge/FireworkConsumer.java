package io.badgeup.sponge;

import com.google.common.collect.Lists;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.FireworkEffect;
import org.spongepowered.api.item.FireworkShapes;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Color;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class FireworkConsumer implements Consumer<Task> {

    private final int ITERATIONS = 10;
    private Player player;
    private int counter;

    public FireworkConsumer(Player player) {
        this.player = player;
        this.counter = 0;
    }

    @Override
    public void accept(Task task) {

        List<Color> colors = Lists.newArrayList(Color.BLACK, Color.BLUE, Color.CYAN, Color.DARK_CYAN, Color.DARK_GREEN, Color.DARK_MAGENTA,
                Color.GRAY, Color.GREEN, Color.LIME, Color.MAGENTA, Color.NAVY, Color.PINK, Color.PURPLE, Color.RED, Color.WHITE, Color.YELLOW);
        Collections.shuffle(colors);

        FireworkEffect fireworkEffect = FireworkEffect.builder()
                .colors(colors.get(0), colors.get(1), colors.get(2))
                .shape(FireworkShapes.STAR)
                .build();

        Entity firework = this.player.getWorld().createEntity(EntityTypes.FIREWORK, this.player.getLocation().getPosition());
        firework.offer(Keys.FIREWORK_EFFECTS, Lists.newArrayList(fireworkEffect));
        firework.offer(Keys.FIREWORK_FLIGHT_MODIFIER, 2);

        this.player.getWorld().spawnEntity(firework);

        this.counter++;

        if (this.counter >= this.ITERATIONS) {
            task.cancel();
        }
    }

}
