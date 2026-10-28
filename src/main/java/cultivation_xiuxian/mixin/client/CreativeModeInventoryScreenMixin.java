package cultivation_xiuxian.mixin.client;

import java.util.Set;
import java.util.stream.Collectors;

import cultivation_xiuxian.tabs.TabPages;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen.ItemPickerMenu;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeModeInventoryScreenMixin extends AbstractContainerScreen<ItemPickerMenu> {
    @Shadow
    private static CreativeModeTab selectedTab;
    @Unique
    private static final Set<String> COMMON_PATHS = Set.of("search", "inventory", "hotbar", "op_blocks");
    @Unique
    private static int currentPage = 0;
    @Unique
    private static Set<CreativeModeTab> commonTabs;

    public CreativeModeInventoryScreenMixin(ItemPickerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Shadow
    protected abstract void selectTab(CreativeModeTab tab);

    @Unique
    private static Set<CreativeModeTab> cultivation$commonTabs() {
        Set<CreativeModeTab> set = commonTabs;
        if (set == null) {
            set = BuiltInRegistries.CREATIVE_MODE_TAB.registryKeySet().stream()
                    .filter(key -> COMMON_PATHS.contains(key.identifier().getPath()))
                    .map(key -> BuiltInRegistries.CREATIVE_MODE_TAB.getValueOrThrow(key))
                    .collect(Collectors.toSet());
            commonTabs = set;
        }
        return set;
    }

    @Unique
    private boolean cultivation$isTabVisible(CreativeModeTab tab) {
        if (TabPages.FABRIC_TAB_API_PRESENT) {
            return tab.shouldDisplay();
        }
        return tab.shouldDisplay()
                && (cultivation$commonTabs().contains(tab) || TabPages.pageOf(tab) == currentPage);
    }

    @Unique
    private void cultivation$updateSelection() {
        if (!cultivation$isTabVisible(selectedTab)) {
            CreativeModeTabs.allTabs().stream()
                    .filter(this::cultivation$isTabVisible)
                    .min((a, b) -> Boolean.compare(a.isAlignedRight(), b.isAlignedRight()))
                    .ifPresent(this::selectTab);
        }
    }

    @Inject(method = "init", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/EditBox;setTextColor(I)V", shift = Shift.AFTER))
    private void cultivation$addPageButtons(CallbackInfo info) {
        if (TabPages.FABRIC_TAB_API_PRESENT || TabPages.pageCount() < 2) {
            return;
        }
        currentPage = TabPages.pageOf(selectedTab);
        int x = this.leftPos + 171;
        int y = this.topPos + 4;
        this.addRenderableWidget(new TabPageButton(x, y, false, () -> cultivation$switchPage(-1),
                () -> currentPage != 0, () -> currentPage, TabPages::pageCount));
        this.addRenderableWidget(new TabPageButton(x + 10, y, true, () -> cultivation$switchPage(1),
                () -> currentPage + 1 < TabPages.pageCount(), () -> currentPage, TabPages::pageCount));
    }

    @Inject(method = "selectTab", at = @At("HEAD"), cancellable = true)
    private void cultivation$selectTab(CreativeModeTab tab, CallbackInfo info) {
        if (!cultivation$isTabVisible(tab)) {
            info.cancel();
        }
    }

    @Inject(method = "checkTabHovering", at = @At("HEAD"), cancellable = true)
    private void cultivation$checkTabHovering(GuiGraphicsExtractor graphics, CreativeModeTab tab, int mouseX, int mouseY,
            CallbackInfoReturnable<Boolean> info) {
        if (!cultivation$isTabVisible(tab)) {
            info.setReturnValue(false);
        }
    }

    @Inject(method = "checkTabClicked", at = @At("HEAD"), cancellable = true)
    private void cultivation$checkTabClicked(CreativeModeTab tab, double mouseX, double mouseY,
            CallbackInfoReturnable<Boolean> info) {
        if (!cultivation$isTabVisible(tab)) {
            info.setReturnValue(false);
        }
    }

    @Inject(method = "extractTabButton", at = @At("HEAD"), cancellable = true)
    private void cultivation$extractTabButton(GuiGraphicsExtractor graphics, int mouseX, int mouseY,
            CreativeModeTab tab, CallbackInfo info) {
        if (!cultivation$isTabVisible(tab)) {
            info.cancel();
        }
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void cultivation$keyPressed(KeyEvent context, CallbackInfoReturnable<Boolean> info) {
        if (TabPages.FABRIC_TAB_API_PRESENT) {
            return;
        }
        if (context.key() == 75) { // K = previous page
            if (cultivation$switchPage(-1)) {
                info.setReturnValue(true);
            }
        } else if (context.key() == 78 && cultivation$switchPage(1)) { // N = next page
            info.setReturnValue(true);
        }
    }

    @Unique
    private boolean cultivation$hasTabOnPage(int page) {
        return CreativeModeTabs.tabs().stream()
                .anyMatch(tab -> TabPages.pageOf(tab) == page);
    }

    @Unique
    private boolean cultivation$switchPage(int delta) {
        int target = currentPage + delta;
        if (target < 0 || target >= TabPages.pageCount() || target == currentPage
                || !cultivation$hasTabOnPage(target)) {
            return false;
        }
        currentPage = target;
        cultivation$updateSelection();
        return true;
    }
}
