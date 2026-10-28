package cultivation_xiuxian.mixin.client;

import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

// small 10x12 "<" / ">" corner widget for switching tab pages
public class TabPageButton extends Button {
    private final boolean next;
    private final Runnable press;
    private final BooleanSupplier enabled;
    private final IntSupplier currentPage;
    private final IntSupplier pageCount;

    public TabPageButton(int x, int y, boolean next, Runnable press, BooleanSupplier enabled,
            IntSupplier currentPage, IntSupplier pageCount) {
        super(x, y, 10, 12, next ? Component.literal(">") : Component.literal("<"),
                bw -> press.run(), Button.DEFAULT_NARRATION);
        this.next = next;
        this.press = press;
        this.enabled = enabled;
        this.currentPage = currentPage;
        this.pageCount = pageCount;
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        this.active = enabled.getAsBoolean();
        Minecraft mc = Minecraft.getInstance();
        String glyph = next ? ">" : "<";
        graphics.centeredText(mc.font, glyph, this.getX() + 5, this.getY() + 2,
                this.active ? 0xFFFFFFFF : 0xFFA0A0A0);
        if (this.isHovered()) {
            graphics.setTooltipForNextFrame(mc.font,
                    Component.translatable("cultivation_xiuxian.gui.page",
                            currentPage.getAsInt() + 1, pageCount.getAsInt()),
                    mouseX, mouseY);
        }
    }
}
