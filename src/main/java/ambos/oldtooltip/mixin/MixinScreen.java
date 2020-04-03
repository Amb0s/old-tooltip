package ambos.oldtooltip.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Iterator;
import java.util.List;

@Mixin(Screen.class)
public abstract class MixinScreen extends AbstractParentElement {
    private static final int TRANSPARENT_GREY = -1073741824;
    private static final int TOOLTIP_MARGIN = 3;

    @Shadow
    protected TextRenderer font;

    @Shadow
    public int width;

    @Shadow
    public int height;

    @Shadow
    protected ItemRenderer itemRenderer;

    @Overwrite
    public void renderTooltip(List<String> list, int i, int j) {
        if (!list.isEmpty()) {
            RenderSystem.disableRescaleNormal();
            RenderSystem.disableDepthTest();
            int k = 0;
            Iterator var5 = list.iterator();

            while(var5.hasNext()) {
                String string = (String)var5.next();
                int l = this.font.getStringWidth(string);
                if (l > k) {
                    k = l;
                }
            }

            int m = i + 12;
            int n = j - 12;
            int p = 8;
            if (list.size() > 1) {
                p += 2 + (list.size() - 1) * 10;
            }

            if (m + k > this.width) {
                m -= 28 + k;
            }

            if (n + p + 6 > this.height) {
                n = this.height - p - 6;
            }

            this.setBlitOffset(300);
            this.itemRenderer.zOffset = 300.0F;
            this.fillGradient(m - TOOLTIP_MARGIN, n - TOOLTIP_MARGIN, m + k + TOOLTIP_MARGIN,
                    n + p + TOOLTIP_MARGIN, TRANSPARENT_GREY, TRANSPARENT_GREY); // Draws tooltip.
            MatrixStack matrixStack = new MatrixStack();
            VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
            matrixStack.translate(0.0D, 0.0D, (double)this.itemRenderer.zOffset);
            Matrix4f matrix4f = matrixStack.peek().getModel();

            for(int t = 0; t < list.size(); ++t) {
                String string2 = (String)list.get(t);
                if (string2 != null) {
                    this.font.draw(string2, (float)m, (float)n, -1, true, matrix4f, immediate, false, 0, 15728880);
                }

                if (t == 0) {
                    n += 2;
                }

                n += 10;
            }

            immediate.draw();
            this.setBlitOffset(0);
            this.itemRenderer.zOffset = 0.0F;
            RenderSystem.enableDepthTest();
            RenderSystem.enableRescaleNormal();
        }
    }
}
