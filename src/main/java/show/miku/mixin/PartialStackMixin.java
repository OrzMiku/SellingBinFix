package show.miku.mixin;

import bigchadguys.sellingbin.data.item.PartialItem;
import bigchadguys.sellingbin.data.item.PartialStack;
import bigchadguys.sellingbin.data.nbt.PartialCompoundNbt;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(value = PartialStack.class, remap = false)
public abstract class PartialStackMixin {

    @Shadow protected PartialItem item;
    @Shadow protected PartialCompoundNbt nbt;

    @Inject(method = "generate(I)Ljava/util/Optional;", at = @At("HEAD"), cancellable = true)
    private void onGenerate(int count, CallbackInfoReturnable<Optional<ItemStack>> cir) {
        cir.cancel();
        Optional<ItemStack> result = this.item.generate(count).map((stack) -> {
            this.nbt.asWhole().ifPresent(nbtToApply -> {
                if (!nbtToApply.isEmpty()) {
                    stack.getOrCreateNbt().copyFrom(nbtToApply);
                }
            });
            return stack;
        });
        cir.setReturnValue(result);
    }
}
