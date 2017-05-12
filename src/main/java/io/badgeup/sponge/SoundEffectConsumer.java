package io.badgeup.sponge;

import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;

import java.util.function.Consumer;

public class SoundEffectConsumer implements Consumer<Task> {

    private final int ITERATIONS = 10;
    private Player player;
    private SoundType soundType;
    private int counter;

    public SoundEffectConsumer(Player player, SoundType soundType) {
        this.player = player;
        this.soundType = soundType;
        this.counter = 0;
    }

    @Override
    public void accept(Task task) {
        this.player.playSound(this.soundType, this.player.getLocation().getPosition(), 1);

        this.counter++;

        if (this.counter >= this.ITERATIONS) {
            task.cancel();
        }
    }

}
