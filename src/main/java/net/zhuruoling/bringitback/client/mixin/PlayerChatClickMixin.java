package net.zhuruoling.bringitback.client.mixin;

import com.mojang.logging.LogUtils;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.logging.ErrorManager;

import static net.minecraft.client.gui.screen.Screen.hasShiftDown;

@Mixin(net.minecraft.client.gui.screen.Screen.class)
public abstract class PlayerChatClickMixin {
    private static final Logger LOGGER = LogUtils.getLogger();
    @Shadow @Nullable protected MinecraftClient client;

    @Inject(method = "handleTextClick", at = @At("HEAD"), cancellable = true)
    public void handleTextClick(Style style, CallbackInfoReturnable<Boolean> cir){
        if (style == null){
            cir.setReturnValue(null);
        }
        else {
            ClickEvent clickEvent = style.getClickEvent();
            if (clickEvent != null && clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND) {
                String string2 = SharedConstants.stripInvalidChars(clickEvent.getValue());
                if (string2.startsWith("/")) {
                    if (this.client != null && this.client.player != null && !this.client.player.sendCommand(string2.substring(1))) {
                        LOGGER.error("Not allowed to run command with signed argument from click event: '{}'", (Object) string2);
                    }
                } else {
                    if (this.client != null && this.client.player != null) {
                        this.client.player.sendChatMessage(clickEvent.getValue(), Text.of(clickEvent.getValue()));
                    }
                    cir.setReturnValue(true);
                }
            }
        }
    }
}
