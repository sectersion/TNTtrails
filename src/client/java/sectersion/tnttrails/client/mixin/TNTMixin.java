package sectersion.tnttrails.client.mixin;

import net.minecraft.world.entity.item.PrimedTnt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sectersion.tnttrails.client.TNTTracker;

@Mixin(PrimedTnt.class)
public class TNTMixin {
    private boolean tnttrails$markedExplosion;
    @Inject(at = @At("HEAD"), method = "tick")
    private void onTick(CallbackInfo ci) {
        PrimedTnt tnt = (PrimedTnt) (Object) this;
        TNTTracker.getInstance().updatePosition(tnt.getId(), tnt.getX(), tnt.getY(), tnt.getZ());
        if (!tnttrails$markedExplosion && tnt.getFuse() <= 1) {
            TNTTracker.getInstance().addExplosion(tnt.getX(), tnt.getY(), tnt.getZ());
            tnttrails$markedExplosion = true;
        }
    }
}
