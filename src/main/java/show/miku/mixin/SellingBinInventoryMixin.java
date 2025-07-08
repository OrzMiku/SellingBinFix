package show.miku.mixin;

import bigchadguys.sellingbin.SellingBinInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.LinkedHashMap;
import java.util.Map;

@Mixin(value = SellingBinInventory.class)
public abstract class SellingBinInventoryMixin {
    @Shadow
    private DefaultedList<ItemStack> inventory;

    @Shadow
    public abstract void setCapacity(int size);

    @Inject(method = "readNbt(Lnet/minecraft/nbt/NbtCompound;)V", at = @At("HEAD"), cancellable = true)
    private void fixReadNbt(NbtCompound nbt, CallbackInfo ci) {
        ci.cancel();

        if (!nbt.contains("slots", 9)) { // 9 = NbtElement.LIST_TYPE
            setCapacity(0);
            return;
        }

        NbtList nbtList = nbt.getList("slots", 10); // 10 = NbtElement.COMPOUND_TYPE

        Map<Integer, ItemStack> items = new LinkedHashMap<>();
        int maxSlot = -1;

        for (int i = 0; i < nbtList.size(); i++) {
            NbtCompound itemTag = nbtList.getCompound(i);
            int slot = itemTag.getInt("slot");
            ItemStack stack = ItemStack.fromNbt(itemTag);

            if (slot > maxSlot) {
                maxSlot = slot;
            }
            items.put(slot, stack);
        }

        int inventorySize = maxSlot + 1;

        setCapacity(inventorySize);

        for (Map.Entry<Integer, ItemStack> entry : items.entrySet()) {
            int slot = entry.getKey();
            ItemStack stack = entry.getValue();
            if (!stack.isEmpty() && slot >= 0 && slot < this.inventory.size()) {
                this.inventory.set(slot, stack);
            }
        }
    }
}