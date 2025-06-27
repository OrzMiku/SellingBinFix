package show.miku.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import bigchadguys.sellingbin.SellingBinInventory;

@Mixin(value = SellingBinInventory.class)
public abstract class SellingBinInventoryMixin {
    @Shadow
    private DefaultedList<ItemStack> inventory;

    @Inject(method = "readNbt(Lnet/minecraft/nbt/NbtCompound;)V", at = @At("HEAD"), cancellable = true)
    private void fixReadNbtCompletely(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("slots", 9)) {
            this.inventory.clear();
            NbtList nbtList = nbt.getList("slots", 10);
            for (int i = 0; i < nbtList.size(); ++i) {
                NbtCompound itemTag = nbtList.getCompound(i);
                int slot = itemTag.getInt("slot");
                ItemStack itemStack = ItemStack.fromNbt(itemTag);
                if (slot >= 0 && slot < this.inventory.size()) {
                    this.inventory.set(slot, itemStack);
                }
            }
        }
        ci.cancel();
    }
}